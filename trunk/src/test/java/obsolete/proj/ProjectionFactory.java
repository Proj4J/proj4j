/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package obsolete.proj;

import java.io.*;
import java.util.*;
import java.awt.geom.*;

import org.osgeo.proj4j.*;
import org.osgeo.proj4j.units.*;
import org.osgeo.proj4j.util.ProjectionMath;
import org.osgeo.proj4j.datum.Ellipsoid;
import org.osgeo.proj4j.proj.*;

/**
 * This class is essentially obsolete.
 * Use {@link CRSFactory} instead.
 * 
 * @author Martin Davis
 * 
 * @deprecated
 * @see CRSFactory
 *
 */
public class ProjectionFactory 
{
	private final static double SIXTH = .1666666666666666667; /* 1/6 */
	private final static double RA4 = .04722222222222222222; /* 17/360 */
	private final static double RA6 = .02215608465608465608; /* 67/3024 */
	private final static double RV4 = .06944444444444444444; /* 5/72 */
	private final static double RV6 = .04243827160493827160; /* 55/1296 */

	private static AngleFormat format = new AngleFormat( AngleFormat.ddmmssPattern, true );

  private static Registry registry = new Registry();
  
  
	/**
	 * Return a projection initialized with a PROJ.4 argument list.
	 */
	public static Projection fromPROJ4Specification( String[] args ) {
		Projection projection = null;
		Ellipsoid ellipsoid = null;
		double a = 0, b = 0, es = 0;

		Map params = new HashMap();
		for ( int i = 0; i < args.length; i++ ) {
			String arg = args[i];
			if ( arg.startsWith("+") ) {
				int index = arg.indexOf( '=' );
				if ( index != -1 ) {
					String key = arg.substring( 1, index );
					String value = arg.substring( index+1 );
					params.put( key, value );
				}
			}
		}

		String s;
		s = (String)params.get( "proj" );
		if ( s != null ) {
			projection = registry.getProjection( s );
			if ( projection == null )
				throw new ProjectionException( "Unknown projection: "+s );
		}

		s = (String)params.get( "init" );
		if ( s != null ) {
			projection = getNamedPROJ4CoordinateSystem( s );
			if ( projection == null )
				throw new ProjectionException( "Unknown projection: "+s );
            a = projection.getEquatorRadius();
            es = projection.getEllipsoid().getEccentricitySquared();
		}

		// Set the ellipsoid
		String ellipsoidName = "";
		s = (String)params.get( "R" );
		if ( s != null ) 
			a = Double.parseDouble( s );
		else {
			s = (String)params.get( "ellps" );
            if ( s == null )
                s = (String)params.get( "datum" );
			if ( s != null ) {
        ellipsoid = registry.getEllipsoid(s);
				if ( ellipsoid == null )
					throw new ProjectionException( "Unknown ellipsoid: "+s );
				es = ellipsoid.eccentricity2;
				a = ellipsoid.equatorRadius;
				ellipsoidName = s;
			} else {
				s = (String)params.get( "a" );
				if ( s != null ) 
					a = Double.parseDouble( s );
				s = (String)params.get( "es" );
				if ( s != null ) {
					es = Double.parseDouble( s );
				} else {
					s = (String)params.get( "rf" );
					if ( s != null ) {
						es = Double.parseDouble( s );
						es = es * (2. - es);
					} else {
						s = (String)params.get( "f" );
						if ( s != null ) {
							es = Double.parseDouble( s );
							es = 1.0 / es;
							es = es * (2. - es);
						} else {
							s = (String)params.get( "b" );
							if ( s != null ) {
								b = Double.parseDouble( s );
								es = 1. - (b * b) / (a * a);
							}
						}
					}
				}
				if ( b == 0 )
					b = a * Math.sqrt(1. - es);
			}

			s = (String)params.get( "R_A" );
			if ( s != null && Boolean.getBoolean( s ) ) {
				a *= 1. - es * (SIXTH + es * (RA4 + es * RA6));
			} else {
				s = (String)params.get( "R_V" );
				if ( s != null && Boolean.getBoolean( s ) ) {
					a *= 1. - es * (SIXTH + es * (RV4 + es * RV6));
				} else {
					s = (String)params.get( "R_a" );
					if ( s != null && Boolean.getBoolean( s ) ) {
						a = .5 * (a + b);
					} else {
						s = (String)params.get( "R_g" );
						if ( s != null && Boolean.getBoolean( s ) ) {
							a = Math.sqrt(a * b);
						} else {
							s = (String)params.get( "R_h" );
							if ( s != null && Boolean.getBoolean( s ) ) {
								a = 2. * a * b / (a + b);
								es = 0.;
							} else {
								s = (String)params.get( "R_lat_a" );
								if ( s != null ) {
									double tmp = Math.sin( parseAngle( s ) );
									if ( Math.abs(tmp) > ProjectionMath.HALFPI )
										throw new ProjectionException( "-11" );
									tmp = 1. - es * tmp * tmp;
									a *= .5 * (1. - es + tmp) / ( tmp * Math.sqrt(tmp));
									es = 0.;
								} else {
									s = (String)params.get( "R_lat_g" );
									if ( s != null ) {
										double tmp = Math.sin( parseAngle( s ) );
										if ( Math.abs(tmp) > ProjectionMath.HALFPI )
											throw new ProjectionException( "-11" );
										tmp = 1. - es * tmp * tmp;
										a *= Math.sqrt(1. - es) / tmp;
										es = 0.;
									}
								}
							}
						}
					}
				}
			}
		}
		projection.setEllipsoid( new Ellipsoid( ellipsoidName, a, es, ellipsoidName ) );
		
		// Other arguments
//		projection.setProjectionLatitudeDegrees( 0 );
//		projection.setProjectionLatitude1Degrees( 0 );
//		projection.setProjectionLatitude2Degrees( 0 );
		s = (String)params.get( "lat_0" );
		if ( s != null ) 
			projection.setProjectionLatitudeDegrees( parseAngle( s ) );
		s = (String)params.get( "lon_0" );
		if ( s != null ) 
			projection.setProjectionLongitudeDegrees( parseAngle( s ) );
		s = (String)params.get( "lat_1" );
		if ( s != null ) 
			projection.setProjectionLatitude1Degrees( parseAngle( s ) );
		s = (String)params.get( "lat_2" );
		if ( s != null ) 
			projection.setProjectionLatitude2Degrees( parseAngle( s ) );
		s = (String)params.get( "lat_ts" );
		if ( s != null ) 
			projection.setTrueScaleLatitudeDegrees( parseAngle( s ) );
		s = (String)params.get( "x_0" );
		if ( s != null ) 
			projection.setFalseEasting( Double.parseDouble( s ) );
		s = (String)params.get( "y_0" );
		if ( s != null ) 
			projection.setFalseNorthing( Double.parseDouble( s ) );

		s = (String)params.get( "k_0" );
		if ( s == null ) 
			s = (String)params.get( "k" );
		if ( s != null ) 
			projection.setScaleFactor( Double.parseDouble( s ) );

		s = (String)params.get( "units" );
		if ( s != null ) {
			Unit unit = Units.findUnits( s );
			if ( unit != null )
				projection.setFromMetres( 1.0 / unit.value );
		}
		s = (String)params.get( "to_meter" );
		if ( s != null ) 
			projection.setFromMetres( 1.0/Double.parseDouble( s ) );

    //TODO: implement these parameters
    
//  towgs84
//  alpha
//  datum
//  lat_ts
//  azi
//  lonc
//  rf
//  pm
    
    // check for unsupported features
    s = (String)params.get( "towgs84" );
    if ( s != null ) 
      throw new UnsupportedOperationException("towgs84");

    s = (String)params.get( "lonc" );
    if ( s != null ) 
      throw new UnsupportedOperationException("lonc");

    s = (String)params.get( "south" );
    if ( s != null ) 
      throw new UnsupportedOperationException("south");

    // TODO: refine this test.  +datum is supported, but not for datum transformation at this point
    /*
    s = (String)params.get( "datum" );
    if ( s != null ) 
      throw new UnsupportedOperationException("datum");
*/
    
		if ( projection instanceof TransverseMercatorProjection ) {
            s = (String)params.get( "zone" );
            if ( s != null ) 
                ((TransverseMercatorProjection)projection).setUTMZone( Integer.parseInt( s ) );
        }



		projection.initialize();

		return projection;
	}

	private static double parseAngle( String s ) {
		return format.parse( s, null ).doubleValue();
	}
	
	public static Projection readProjectionFile( String file, String name ) 
  throws IOException 
  {
    // TODO: read comment preceding CS string as CS description
    // TODO: use simpler parser than StreamTokenizer for speed and flexibility
    // TODO: parse CSes line-at-a-time (this allows preserving CS param string for later access)
    
		BufferedReader reader = new BufferedReader( new InputStreamReader( ProjectionFactory.class.getResourceAsStream( "/nad/"+file ) ) );
		StreamTokenizer t = new StreamTokenizer( reader );
		t.commentChar( '#' );
		t.ordinaryChars( '0', '9' );
		t.ordinaryChars( '.', '.' );
		t.ordinaryChars( '-', '-' );
		t.ordinaryChars( '+', '+' );
		t.wordChars( '0', '9' );
		t.wordChars( '\'', '\'' );
		t.wordChars( '"', '"' );
		t.wordChars( '_', '_' );
		t.wordChars( '.', '.' );
		t.wordChars( '-', '-' );
		t.wordChars( '+', '+' );
		t.wordChars( ',', ',' );
		t.nextToken();
		while ( t.ttype == '<' ) {
			t.nextToken();
			if ( t.ttype != StreamTokenizer.TT_WORD )	
				throw new IOException( t.lineno()+": Word expected after '<'" );
			String cname = t.sval;
			t.nextToken();
			if ( t.ttype != '>' )
				throw new IOException( t.lineno()+": '>' expected" );
			t.nextToken();
			Vector v = new Vector();
			String values = "";
			while ( t.ttype != '<' ) {
				if ( t.ttype == '+' )
					t.nextToken();
				if ( t.ttype != StreamTokenizer.TT_WORD )	
					throw new IOException( t.lineno()+": Word expected after '+'" );
				String key = t.sval;
				t.nextToken();
				if ( t.ttype == '=' ) {
					t.nextToken();
					//Removed check to allow for proj4 hack +nadgrids=@null 
					//if ( t.ttype != StreamTokenizer.TT_WORD )	
					//	throw new IOException( t.lineno()+": Value expected after '='" );
					String value = t.sval;
					t.nextToken();
					if ( key.startsWith("+") )
						v.add( key+"="+value );
					else
						v.add( "+"+key+"="+value );
				}
			}
			t.nextToken();
			if ( t.ttype != '>' )
				throw new IOException( t.lineno()+": '<>' expected" );
			t.nextToken();
			if ( cname.equals( name ) ) {
				String[] args = new String[v.size()];
				v.copyInto( args );
				reader.close();
				return fromPROJ4Specification( args );
			}
		}
		reader.close();
		return null;
	}
	
	public static Projection getNamedPROJ4CoordinateSystem(String name) {
    String[] files = { "world", "nad83", "nad27", "esri", "epsg", };
    try {
      int p = name.indexOf(':');
      if (p >= 0)
        return readProjectionFile(name.substring(0, p), name.substring(p + 1));
      for (int i = 0; i < files.length; i++) {
        Projection projection = readProjectionFile(files[i], name);
        if (projection != null)
          return projection;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  // TODO: create new cmd-line interface similar to PROJ4 ones
	public static void main( String[] args ) {
		Projection projection = ProjectionFactory.fromPROJ4Specification( args );
		if ( projection != null ) {
			System.out.println( projection.getPROJ4Description() );
			for ( int i = 0; i < args.length; i++ ) {
				String arg = args[i];
				if ( !arg.startsWith("+") && !arg.startsWith("-") ) {
					try {
						BufferedReader reader = new BufferedReader( new FileReader( new File( args[i] ) ) );
						ProjCoordinate p = new ProjCoordinate();
						String line;
						while ( (line = reader.readLine()) != null ) {
							StringTokenizer t = new StringTokenizer( line, " " );
							String slon = t.nextToken();
							String slat = t.nextToken();
							p.x = format.parse( slon, null ).doubleValue();
							p.y = format.parse( slat, null ).doubleValue();
							projection.transform( p, p );
							System.out.println( p.x+" "+p.y );
						} 
					}
					catch ( IOException e ) {
						System.out.println( "IOException: "+args[i]+": "+e.getMessage() );
					}
				}
			}
		} else
			System.out.println( "Can't find projection "+args[0] );
	}
}
