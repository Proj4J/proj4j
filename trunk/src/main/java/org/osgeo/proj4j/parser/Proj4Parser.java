package org.osgeo.proj4j.parser;

import java.util.HashMap;
import java.util.Map;

import org.osgeo.proj4j.*;
import org.osgeo.proj4j.Registry;
import org.osgeo.proj4j.datum.Datum;
import org.osgeo.proj4j.datum.Ellipsoid;
import org.osgeo.proj4j.proj.Projection;
import org.osgeo.proj4j.proj.TransverseMercatorProjection;
import org.osgeo.proj4j.units.AngleFormat;
import org.osgeo.proj4j.units.Unit;
import org.osgeo.proj4j.units.Units;

public class Proj4Parser 
{
  private Registry registry;
  
  public Proj4Parser(Registry registry) {
    this.registry = registry;
  }
  
  public CoordinateReferenceSystem parse(String name, String[] args)
  {
    if (args == null)
      return null;
    
    Map params = createParameterMap(args);
    Proj4Keyword.checkUnsupported(params.keySet());
    DatumParameters datumParam = new DatumParameters();
    parseDatum(params, datumParam);
    parseEllipsoid(params, datumParam);
    Datum datum = datumParam.getDatum();
    Ellipsoid ellipsoid = datum.getEllipsoid(); 
    // TODO: this makes a difference - why?
    // which is better?
//    Ellipsoid ellipsoid = datumParam.getEllipsoid(); 
    Projection proj = parseProjection(params, ellipsoid);
    return new CoordinateReferenceSystem(name, args, datum, proj);
  }

  /*
  
  // not currently used
 private final static double SIXTH = .1666666666666666667; // 1/6 
 private final static double RA4 = .04722222222222222222; // 17/360 
 private final static double RA6 = .02215608465608465608; // 67/3024 
 private final static double RV4 = .06944444444444444444; // 5/72 
 private final static double RV6 = .04243827160493827160; // 55/1296 
 */

 private static AngleFormat format = new AngleFormat( AngleFormat.ddmmssPattern, true );

 /**
  * Creates a {@link Projection}
  * initialized from a PROJ.4 argument list.
  */
 private Projection parseProjection( Map params, Ellipsoid ellipsoid ) {
   Projection projection = null;

   String s;
   s = (String)params.get( Proj4Keyword.proj );
   if ( s != null ) {
     projection = registry.getProjection( s );
     if ( projection == null )
       throw new InvalidValueException( "Unknown projection: "+s );
   }

   projection.setEllipsoid(ellipsoid);

   // not sure what CSes use this??
   /*
   s = (String)params.get( "init" );
   if ( s != null ) {
     projection = createFromName( s ).getProjection();
     if ( projection == null )
       throw new ProjectionException( "Unknown projection: "+s );
           a = projection.getEquatorRadius();
           es = projection.getEllipsoid().getEccentricitySquared();
   }
   */
   
   
   //TODO: better error handling for things like bad number syntax.  
   // Should be able to report the original param string in the error message
   // Also should the exception be lib specific?  (Say ParseException)
   
   // Other parameters
//   projection.setProjectionLatitudeDegrees( 0 );
//   projection.setProjectionLatitude1Degrees( 0 );
//   projection.setProjectionLatitude2Degrees( 0 );
   s = (String)params.get( Proj4Keyword.alpha );
   if ( s != null ) 
     projection.setAlphaDegrees( Double.parseDouble( s ) );
   
   s = (String)params.get( Proj4Keyword.lonc );
   if ( s != null ) 
     projection.setLonCDegrees( Double.parseDouble( s ) );
   
   s = (String)params.get( Proj4Keyword.lat_0 );
   if ( s != null ) 
     projection.setProjectionLatitudeDegrees( parseAngle( s ) );
   
   s = (String)params.get( Proj4Keyword.lon_0 );
   if ( s != null ) 
     projection.setProjectionLongitudeDegrees( parseAngle( s ) );
   
   s = (String)params.get( Proj4Keyword.lat_1 );
   if ( s != null ) 
     projection.setProjectionLatitude1Degrees( parseAngle( s ) );
   
   s = (String)params.get( Proj4Keyword.lat_2 );
   if ( s != null ) 
     projection.setProjectionLatitude2Degrees( parseAngle( s ) );
   
   s = (String)params.get( Proj4Keyword.lat_ts );
   if ( s != null ) 
     projection.setTrueScaleLatitudeDegrees( parseAngle( s ) );
   
   s = (String)params.get( Proj4Keyword.x_0 );
   if ( s != null ) 
     projection.setFalseEasting( Double.parseDouble( s ) );
   
   s = (String)params.get( Proj4Keyword.y_0 );
   if ( s != null ) 
     projection.setFalseNorthing( Double.parseDouble( s ) );

   s = (String)params.get( Proj4Keyword.k_0 );
   if ( s == null ) 
     s = (String)params.get( Proj4Keyword.k );
   if ( s != null ) 
     projection.setScaleFactor( Double.parseDouble( s ) );

   s = (String)params.get( Proj4Keyword.units );
   if ( s != null ) {
     Unit unit = Units.findUnits( s );
     // TODO: report unknown units name as error
     if ( unit != null ) {
       projection.setFromMetres( 1.0 / unit.value );
       projection.setUnits( unit );
     }
   }
   
   s = (String)params.get( Proj4Keyword.to_meter );
   if ( s != null ) 
     projection.setFromMetres( 1.0/Double.parseDouble( s ) );

   if ( params.containsKey( Proj4Keyword.south ) ) 
     projection.setSouthernHemisphere(true);

   //TODO: implement some of these parameters ?
     
   // this must be done last, since behaviour depends on other params being set (eg +south)
   if (projection instanceof TransverseMercatorProjection) {
     s = (String) params.get("zone");
     if (s != null)
       ((TransverseMercatorProjection) projection).setUTMZone(Integer
           .parseInt(s));
   }

   projection.initialize();

   return projection;
 }

 private void parseDatum(Map params, DatumParameters datumParam) 
 {
   String towgs84 = (String) params.get(Proj4Keyword.towgs84);
   if (towgs84 != null) {
     double[] datumConvParams = parseDatumTransform(towgs84); 
     datumParam.setDatumTransform(datumConvParams);
   }

   String code = (String) params.get(Proj4Keyword.datum);
   if (code != null) {
     Datum datum = registry.getDatum(code);
     if (datum == null)
       throw new InvalidValueException("Unknown datum: " + code);
     datumParam.setDatum(datum);
   }
   
 }
 
 private double[] parseDatumTransform(String paramList)
 {
   String[] numStr = paramList.split(",");
   
   if (! (numStr.length == 3 || numStr.length == 7)) {
     throw new InvalidValueException("Invalid number of values (must be 3 or 7) in +towgs84: " + paramList);
   }
   double[] param = new double[numStr.length];
   for (int i = 0; i < numStr.length; i++) {
     // TODO: better error reporting
     param[i] = Double.parseDouble(numStr[i]);
   }
   return param;
 }
 
 private void parseEllipsoid(Map params, DatumParameters datumParam) 
 {
   double b = 0;
   String s;

   /*
    * // not supported by PROJ4 s = (String) params.get(Proj4Param.R); if (s !=
    * null) a = Double.parseDouble(s);
    */

   String code = (String) params.get(Proj4Keyword.ellps);
   if (code != null) {
     Ellipsoid ellipsoid = registry.getEllipsoid(code);
     if (ellipsoid == null)
       throw new InvalidValueException("Unknown ellipsoid: " + code);
     datumParam.setEllipsoid(ellipsoid);
   }

   /*
    * Explicit parameters override ellps and datum settings
    */
   s = (String) params.get(Proj4Keyword.a);
   if (s != null) {
     double a = Double.parseDouble(s);
     datumParam.setA(a);
   }
   
   s = (String) params.get(Proj4Keyword.es);
   if (s != null) {
     double es = Double.parseDouble(s);
     datumParam.setES(es);
   }

   s = (String) params.get(Proj4Keyword.rf);
   if (s != null) {
     double rf = Double.parseDouble(s);
     datumParam.setRF(rf);
   }

   s = (String) params.get(Proj4Keyword.f);
   if (s != null) {
     double f = Double.parseDouble(s);
     datumParam.setF(f);
   }

   s = (String) params.get(Proj4Keyword.b);
   if (s != null) {
     b = Double.parseDouble(s);
     datumParam.setB(b);
   }

   if (b == 0) {
     b = datumParam.getA() * Math.sqrt(1. - datumParam.getES());
   }

   parseEllipsoidModifiers(params, datumParam);

   /*
    * // None of these appear to be supported by PROJ4 ??
    * 
    * s = (String)
    * params.get(Proj4Param.R_A); if (s != null && Boolean.getBoolean(s)) { a *=
    * 1. - es * (SIXTH + es * (RA4 + es * RA6)); } else { s = (String)
    * params.get(Proj4Param.R_V); if (s != null && Boolean.getBoolean(s)) { a *=
    * 1. - es * (SIXTH + es * (RV4 + es * RV6)); } else { s = (String)
    * params.get(Proj4Param.R_a); if (s != null && Boolean.getBoolean(s)) { a =
    * .5 * (a + b); } else { s = (String) params.get(Proj4Param.R_g); if (s !=
    * null && Boolean.getBoolean(s)) { a = Math.sqrt(a * b); } else { s =
    * (String) params.get(Proj4Param.R_h); if (s != null &&
    * Boolean.getBoolean(s)) { a = 2. * a * b / (a + b); es = 0.; } else { s =
    * (String) params.get(Proj4Param.R_lat_a); if (s != null) { double tmp =
    * Math.sin(parseAngle(s)); if (Math.abs(tmp) > MapMath.HALFPI) throw new
    * ProjectionException("-11"); tmp = 1. - es * tmp * tmp; a *= .5 * (1. - es +
    * tmp) / (tmp * Math.sqrt(tmp)); es = 0.; } else { s = (String)
    * params.get(Proj4Param.R_lat_g); if (s != null) { double tmp =
    * Math.sin(parseAngle(s)); if (Math.abs(tmp) > MapMath.HALFPI) throw new
    * ProjectionException("-11"); tmp = 1. - es * tmp * tmp; a *= Math.sqrt(1. -
    * es) / tmp; es = 0.; } } } } } } } }
    */
 }
 
 /**
  * Parse ellipsoid modifiers.
  * 
  * @param params
  * @param datumParam
  */
 private void parseEllipsoidModifiers(Map params, DatumParameters datumParam) 
 {
   /**
    * Modifiers are mutually exclusive, so when one is detected method returns
    */
   if ( params.containsKey( Proj4Keyword.R_A ) ) {
     datumParam.setR_A();
     return;
   }

 }
 
 private Map createParameterMap(String[] args) {
   Map params = new HashMap();
   for (int i = 0; i < args.length; i++) {
     String arg = args[i];
     if (arg.startsWith("+")) {
       int index = arg.indexOf('=');
       if (index != -1) {
         // params of form +pppp=vvvv
         String key = arg.substring(1, index);
         String value = arg.substring(index + 1);
         params.put(key, value);
       } else {
         // params of form +ppppp
         String key = arg.substring(1);
         params.put(key, null);
       }
     }
   }
   return params;
 }

 private static double parseAngle( String s ) {
   return format.parse( s, null ).doubleValue();
 }

}
