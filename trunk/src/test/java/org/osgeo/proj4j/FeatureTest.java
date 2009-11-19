package org.osgeo.proj4j;

import java.awt.geom.Point2D;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * Tests for implementation of PROJ4 features.
 * 
 * @author Martin Davis
 *
 */
public class FeatureTest extends TestCase
{
  CoordinateTransformTester tester = new CoordinateTransformTester(true);
	
  public static void main(String args[]) {
    TestRunner.run(FeatureTest.class);
  }

  public FeatureTest(String name) { super(name); }

  public void testDatumConversion()
  {
//  datum conversions not yet supported
//  +proj=tmerc +lat_0=0 +lon_0=6 +k=1 +x_0=2500000 +y_0=0 +ellps=bessel +datum=potsdam +units=m +no_defs     
    checkTransformFromGeo("EPSG:31466",   6.685, 51.425, 2547685.01212,5699155.7345   );
  }
  public void testTowgs84()
  {
    //# MGI / M31
    //<31285> +proj=tmerc +lat_0=0 +lon_0=13.33333333333333 +k=1.000000 +x_0=450000 +y_0=0 +ellps=bessel +towgs84=577.326,90.129,463.919,5.137,1.474,5.297,2.4232 +units=m +no_defs  <>
    // towgs not implemented
    checkTransformFromGeo("EPSG:31285",    13.33333333333, 47.5, 450055.70, 5262356.33   );
  }
  public void testLambertEqualArea()
  {
    // laea - not implemented
    //runTransform("EPSG:3035",     11.0, 53.0, 4388138.60, 3321736.46);  //laea
    checkTransformFromGeo("EPSG:3573",     9.84375, 61.875,  2923052.02009, 1054885.46559  );
  }
  
  void checkTransformFromGeo(String code, double lon, double lat, double x, double y)
  {
    assertTrue(tester.checkTransformFromGeo(code, lon, lat, x, y, 0.0001));
  }
  
}
