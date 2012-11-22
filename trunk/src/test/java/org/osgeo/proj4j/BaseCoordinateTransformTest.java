package org.osgeo.proj4j;

import junit.framework.TestCase;

/**
 * Tests correctness and accuracy of Coordinate System transformations.
 * 
 * @author Martin Davis
 *
 */
public abstract class BaseCoordinateTransformTest extends TestCase
{
  // ~= 1 / (2Pi * Earth radius) 
  // in code: 1.0 / (2.0 * Math.PI * 6378137.0);
  public static final double APPROX_METRE_IN_DEGREES = 2.0e-8; 
  
	static boolean debug = true;
	
  static CoordinateTransformTester tester = new CoordinateTransformTester(true);
	
  public BaseCoordinateTransformTest(String name) { super(name); }

  void checkTransformFromWGS84(String code, double lon, double lat, double x, double y)
  {
    assertTrue(tester.checkTransformFromWGS84(code, lon, lat, x, y, 0.0001));
  }
  void checkTransformFromWGS84(String code, double lon, double lat, double x, double y, double tolerance)
  {
    assertTrue(tester.checkTransformFromWGS84(code, lon, lat, x, y, tolerance));
  }
  void checkTransformToWGS84(String code, double x, double y, double lon, double lat, double tolerance)
  {
    assertTrue(tester.checkTransformToWGS84(code, x, y, lon, lat, tolerance));
  }
  void checkTransformFromGeo(String code, double lon, double lat, double x, double y)
  {
    assertTrue(tester.checkTransformFromGeo(code, lon, lat, x, y, 0.0001));
  }
  void checkTransformFromGeo(String code, double lon, double lat, double x, double y, double tolerance)
  {
    assertTrue(tester.checkTransformFromGeo(code, lon, lat, x, y, tolerance));
  }
  void checkTransformToGeo(String code, double x, double y, double lon, double lat, double tolerance)
  {
    assertTrue(tester.checkTransformToGeo(code, x, y, lon, lat, tolerance));
  }
  void checkTransformFromAndToGeo(String code, double lon, double lat, double x, double y, double tolProj, double tolGeo)
  {
    assertTrue(tester.checkTransformFromGeo(code, lon, lat, x, y, tolProj));
    assertTrue(tester.checkTransformToGeo(code, x, y, lon, lat, tolGeo));
  }

  void checkTransform(
  		String cs1, double x1, double y1, 
  		String cs2, double x2, double y2, 
  		double tolerance)
  {
    assertTrue(tester.checkTransform(cs1, x1, y1, cs2, x2, y2, tolerance));
  }
  void checkTransformAndInverse(
  		String cs1, double x1, double y1, 
  		String cs2, double x2, double y2, 
  		double tolerance,
  		double inverseTolerance)
  {
    assertTrue(tester.checkTransform(cs1, x1, y1, cs2, x2, y2, tolerance, inverseTolerance, true));
  }
 
}
