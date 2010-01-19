package obsolete.proj;

import java.awt.geom.Point2D;

import obsolete.proj.ProjectionFactory;

import org.osgeo.proj4j.*;
import org.osgeo.proj4j.proj.Projection;
import org.osgeo.proj4j.util.ProjectionUtil;

import junit.framework.TestCase;
import junit.textui.TestRunner;

public class ProjectionValueTest extends TestCase
{
	static boolean debug = false;
	
  public static void main(String args[]) {
    TestRunner.run(ProjectionValueTest.class);
  }

  public ProjectionValueTest(String name) { super(name); }

  public void testFirst()
  {
    //runTransform("EPSG:31466",   6.685, 51.425, 2547685.01212,5699155.7345   );

    //runTransform("EPSG:2736",     34.0, -21.0, 603933.40, 7677505.64    );
  }

  public void testProj4js()
  {
    runTransform("EPSG:3031",    0, -75, 0, 1638783.238407   );
    runTransform("EPSG:3031",    -57.65625, -79.21875, -992481.633786, 628482.06328   );

    runTransform("EPSG:32612",    -113.109375, 60.28125, 383357.429537, 6684599.06392 );

    runTransform("EPSG:23030",    -3, 49.95,        500000, 5533182.925903  );
    runTransform("EPSG:3005",     -126.54, 54.15,   964813.103719, 1016486.305862  );
    //runInverseTransform("EPSG:27492",    25260.493584, -9579.245052,    -7.84, 39.58);
    //runInverseTransform("EPSG:27563",    653704.865208, 176887.660037,    3.005, 43.89);
    //runInverseTransform("EPSG:54003",    1223145.57,6491218.13,-6468.21,    11.0, 53.0);
    runTransform("EPSG:3573",     9.84375, 61.875,  2923052.02009, 1054885.46559  );
    
// datum not yet supported
// +proj=tmerc +lat_0=0 +lon_0=6 +k=1 +x_0=2500000 +y_0=0 +ellps=bessel +datum=potsdam +units=m +no_defs     
    //runTransform("EPSG:31466",   6.685, 51.425, 2547685.01212,5699155.7345   );
    
//    runTransform("EPSG:31467",   9, 51.165,       3500072.082451, 5670004.744777   );

    runTransform("EPSG:54008",    11.0, 53.0,     738509.49,5874620.38 );
    
    // laea - not implemented
    //runTransform("EPSG:3035",     11.0, 53.0, 4388138.60, 3321736.46);  //laea
    
    runTransform("EPSG:102026",   40.0, 40.0,     3000242.40, 5092492.64);
    runTransform("EPSG:54032",    -127.0, 52.11,  -4024426.19, 6432026.98 );
    
    // # NAD83(CSRS) / BC Albers
    runTransform("EPSG:3153",     -127.0, 52.11,  931625.9111828626, 789252.646454557 );
    
    runTransform("EPSG:32615",    -93, 42,        500000, 4649776.22482 );
    runTransform("EPSG:32612",    -113.109375, 60.28125, 383357.429537, 6684599.06392 );
    
    //# MGI / M31
    //<31285> +proj=tmerc +lat_0=0 +lon_0=13.33333333333333 +k=1.000000 +x_0=450000 +y_0=0 +ellps=bessel +towgs84=577.326,90.129,463.919,5.137,1.474,5.297,2.4232 +units=m +no_defs  <>
    // towgs not implemented
    //runTransform("EPSG:31285",    13.33333333333, 47.5, 450055.70, 5262356.33   );
    
    // +south not supported
    //runTransform("EPSG:2736",     34.0, -21.0, 603933.40, 7677505.64    );
    
    runTransform("EPSG:42304",    -99.84375, 48.515625,   -358185.267976, -40271.099023   );
    runTransform("EPSG:3785",     -76.640625, 49.921875,  -8531595.34908, 6432756.94421   );  // google
    runTransform("EPSG:42304",    -99.84375, 48.515625,   -358185.267976, -40271.099023  );
//    runInverseTransform("EPSG:28992",    148312.15, 457804.79, 698.48,    5.29, 52.11);
  }
  
  Point2D.Double p = new Point2D.Double();
  Point2D.Double p2 = new Point2D.Double();

  void runTransform(String code, double x, double y, double lon, double lat)
  {
    runTransformTol(code, x, y, lon, lat, 0.0001);
  }
  
  void runTransformTol(String code, double x, double y, double lon, double lat, double tolerance)
  {
    Projection proj = ProjectionFactory.getNamedPROJ4CoordinateSystem(code);
    System.out.println(proj);
    if (proj == null)
      return;
    boolean ok = checkTransform(proj, x, y, lon, lat, tolerance);
    assertTrue(ok);
  }
  

  private boolean checkTransform(Projection proj, double x, double y, double lon, double lat, double tolerance)
  {
    p.x = x;
    p.y = y;
    proj.transform(p, p2);
    
    if (debug) 
      System.out.println(ProjectionUtil.toString(p) 
          + " -> " + ProjectionUtil.toString(p2) 
          );
    
    double dx = Math.abs(p2.x - lon);
    double dy = Math.abs(p2.y - lat);
    
    boolean inTol =  dx <= tolerance && dy <= tolerance;
   
    if (! inTol) {
      System.out.println(proj.getPROJ4Description());
      System.out.println(ProjectionUtil.toString(p) 
          + " -> " + ProjectionUtil.toString(p2) 
          );
    }

    return inTol;
  }



}
