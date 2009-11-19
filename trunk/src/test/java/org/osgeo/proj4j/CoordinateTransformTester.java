package org.osgeo.proj4j;

import java.awt.geom.Point2D;

public class CoordinateTransformTester {

  boolean verbose = true;
  
  CoordinateSystemFactory csFactory = new CoordinateSystemFactory();

  static final String WGS84_PARAM = "+title=long/lat:WGS84 +proj=longlat +ellps=WGS84 +datum=WGS84 +units=degrees";
  CoordinateSystem WGS84 = csFactory.createFromParameters("WGS84",WGS84_PARAM);

  public CoordinateTransformTester(boolean verbose) {
    this.verbose = verbose;
  }

  Point2D.Double p = new Point2D.Double();
  Point2D.Double p2 = new Point2D.Double();

  boolean checkTransformFromGeo(String name, double lon, double lat, double x, double y)
  {
    return checkTransformFromGeo(name, lon, lat, x, y, 0.0001);
  }
  
  boolean checkTransformFromGeo(String name, double lon, double lat, double x, double y, double tolerance)
  {
    CoordinateSystem cs = csFactory.createFromName(name);
    return checkTransformFromGeo(cs, lon, lat, x, y, tolerance);
  }
  
  boolean checkTransformFromGeo(CoordinateSystem cs, double lon, double lat, double x, double y, double tolerance)
  {
    if (cs != null) {
      System.out.println(cs + " ( " + cs.getProjection() + " ) - " + cs.getParameterString());
    }
    else {
      System.out.println(cs + " - NOT DEFINED");
    }

    if (cs == null) return false;
    
    boolean ok = checkTransform(cs, lon, lat, x, y, tolerance);
    return ok;
  }
  

  private boolean checkTransform(CoordinateSystem cs, double lon, double lat, double x, double y, double tolerance)
  {
    p.x = lon;
    p.y = lat;
    CoordinateTransformation trans = new CoordinateTransformation(
        WGS84, // CoordinateSystem.CS_GEO
        cs);
    trans.transform(p, p2);
    
    if (verbose) {
      System.out.println(ProjectionUtil.toString(p) 
          + " -> " + ProjectionUtil.toString(p2)
          + " ( expected: " + x + ", " + y + " )"
          );
      System.out.println();
    }
    
    double dx = Math.abs(p2.x - x);
    double dy = Math.abs(p2.y - y);
    
    boolean isInTol =  dx <= tolerance && dy <= tolerance;
   
    if (verbose && ! isInTol) {
      System.out.println(cs.getParameterString());
      System.out.println("FAIL: "
          + ProjectionUtil.toString(p) 
          + " -> " + ProjectionUtil.toString(p2) 
          );
    }

    return isInTol;
  }

}
