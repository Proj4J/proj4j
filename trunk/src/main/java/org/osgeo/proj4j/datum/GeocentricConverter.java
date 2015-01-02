package org.osgeo.proj4j.datum;

import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.util.ProjectionMath;

/**
 *  Provides conversions between Geodetic coordinates 
 *  (latitude, longitude in radians and height in meters) 
 *  and Geocentric coordinates
 *  (X, Y, Z) in meters.
 *  <p>
 *  Provenance: Ported from GEOCENTRIC by the U.S. Army Topographic Engineering Center via PROJ.4
 */
public class GeocentricConverter 
{
  /*
   * 
   * REFERENCES
   *    
   *    An Improved Algorithm for Geocentric to Geodetic Coordinate Conversion,
   *    Ralph Toms, February 1996  UCRL-JC-123138.
   *
   *    Further information on GEOCENTRIC can be found in the Reuse Manual.
   *
   *    GEOCENTRIC originated from : U.S. Army Topographic Engineering Center
   *                                 Geospatial Information Division
   *                                 7701 Telegraph Road
   *                                 Alexandria, VA  22310-3864
   *
   *    Manual of Photogrammetry - TODO add reference
   *
   * LICENSES
   *
   *    None apply to this component.
   *
   * RESTRICTIONS
   *
   *    GEOCENTRIC has no restrictions.
   */

  double a;
  double b;
  double a2;
  double b2;
  double e2;
  double ep2;

  private static final double ITERATION_THRESHOLD = 4.8481368110953599e-08;

  public GeocentricConverter(Ellipsoid ellipsoid) {
    this(ellipsoid.getA(), ellipsoid.getB());
  }
  public GeocentricConverter(double a, double b) {
    this.a = a;
    this.b = b;
    a2 = a * a;
    b2 = b * b;
    e2 = (a2 - b2) / a2;
    ep2 = (a2 - b2) / b2;
  }

  /**
   * Converts geodetic coordinates
   * (latitude, longitude, and height) to geocentric coordinates (X, Y, Z),
   * according to the current ellipsoid parameters.
   *
   *    Latitude  : Geodetic latitude in radians                     (input)
   *    Longitude : Geodetic longitude in radians                    (input)
   *    Height    : Geodetic height, in meters                       (input)
   *    
   *    X         : Calculated Geocentric X coordinate, in meters    (output)
   *    Y         : Calculated Geocentric Y coordinate, in meters    (output)
   *    Z         : Calculated Geocentric Z coordinate, in meters    (output)
   *
   */
  public void convertGeodeticToGeocentric(ProjCoordinate p)
  {
    double Longitude = p.x;
    double Latitude = p.y;
    double Height = p.hasValidZOrdinate() ? p.z : 0;   //Z value not always supplied
    double X;  // output
    double Y;
    double Z;

    double Rn;            /*  Earth radius at location  */
    double Sin_Lat;       /*  Math.sin(Latitude)  */
    double Sin2_Lat;      /*  Square of Math.sin(Latitude)  */
    double Cos_Lat;       /*  Math.cos(Latitude)  */

    /*
    ** Don't blow up if Latitude is just a little out of the value
    ** range as it may just be a rounding issue.  Also removed longitude
    ** test, it should be wrapped by Math.cos() and Math.sin().  NFW for PROJ.4, Sep/2001.
    */
    if( Latitude < -ProjectionMath.HALFPI && Latitude > -1.001 * ProjectionMath.HALFPI ) {
        Latitude = -ProjectionMath.HALFPI;
    } else if( Latitude > ProjectionMath.HALFPI && Latitude < 1.001 * ProjectionMath.HALFPI ) {
        Latitude = ProjectionMath.HALFPI;
    } else if ((Latitude < -ProjectionMath.HALFPI) || (Latitude > ProjectionMath.HALFPI)) {
      /* Latitude out of range */
      throw new IllegalStateException("Latitude is out of range: " + Latitude);
    }

    if (Longitude > ProjectionMath.PI) Longitude -= (2*ProjectionMath.PI);
    Sin_Lat = Math.sin(Latitude);
    Cos_Lat = Math.cos(Latitude);
    Sin2_Lat = Sin_Lat * Sin_Lat;
    Rn = a / (Math.sqrt(1.0e0 - e2 * Sin2_Lat));
    X = (Rn + Height) * Cos_Lat * Math.cos(Longitude);
    Y = (Rn + Height) * Cos_Lat * Math.sin(Longitude);
    Z = ((Rn * (1 - e2)) + Height) * Sin_Lat;

    p.x = X;
    p.y = Y;
    p.z = Z;
  }

  public void convertGeocentricToGeodetic(ProjCoordinate p)
  {
    convertGeocentricToGeodeticIter(p);
  }

  public void convertGeocentricToGeodeticIter(ProjCoordinate p) {
    final double x = p.x;
    final double y = p.y;
    final double z = p.z;

    final double eccFactor = (1 - this.e2);

    // Explicitly calculate longitude
    final double lon = Math.atan2(y, x);

    // Form initial estimates of latitude and height.
    final double U = Math.sqrt(x * x + y * y);
    double estLat = Math.atan(z / U);

    double estPrimeVertROC = primeVerticalRadiusOfCurvature(estLat);
    double estHt = Math.sqrt(x * x + y * y + z * z) - estPrimeVertROC;

    double deltaU;
    double deltaZ;

    short numIter = 0;

    double cLat;
    double sLat;
    double estMeridianROC;
    double deltaLat;
    double deltaHt;
    do {
      cLat = Math.cos(estLat);
      sLat = Math.sin(estLat);

      estPrimeVertROC = primeVerticalRadiusOfCurvature(estLat);

      deltaU = U - (estPrimeVertROC + estHt) * cLat;
      deltaZ = z - (estPrimeVertROC * eccFactor + estHt) * sLat;
      estMeridianROC = meridianRadiusOfCurvature(estLat);

      // Calculate iteration step.
      deltaLat = (-deltaU * sLat + deltaZ * cLat) / (estMeridianROC + estHt);
      deltaHt = deltaU * cLat + deltaZ * sLat;

      // Update the estimates.
      estLat += deltaLat;
      estHt += deltaHt;

      numIter++;

    } while (((Math.abs(deltaU) > ITERATION_THRESHOLD) ||
            (Math.abs(deltaZ) > ITERATION_THRESHOLD)) && numIter < 100);

    p.x = lon;
    p.y = estLat;
    p.z = estHt;
  }

  //TODO: port non-iterative algorithm????
  private double primeVerticalRadiusOfCurvature(double lat) {
    final double denomTerm = Math.sqrt(this.e2) * Math.sin(lat);
    final double n = this.a / Math.sqrt(1 - denomTerm * denomTerm);

    return n;
  }

  private double meridianRadiusOfCurvature(double lat) {
    double r;
    final double slat = Math.sin(lat);
    final double den = 1 - this.e2 * slat * slat;

    r = a * (1 - this.e2) / Math.sqrt(den * den * den);

    return r;
  }
}
