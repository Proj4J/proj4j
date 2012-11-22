package org.osgeo.proj4j;

import junit.textui.TestRunner;

/**
 * Tests from the PROJ4 testvarious file.
 * 
 * @author Martin Davis
 *
 */
public class Proj4VariousTest extends BaseCoordinateTransformTest
{ 
  public static void main(String args[]) {
    TestRunner.run(Proj4VariousTest.class);
  }

  public Proj4VariousTest(String name) { super(name); }

  public void testRawEllipse()
  {
    checkTransform("+proj=latlong +ellps=clrk66", p("79d58'00.000\"W 37d02'00.000\"N"), 
        "+proj=latlong +ellps=bessel", p("79d58'W 37d2'N"), 0.01 );
    checkTransform("+proj=latlong +ellps=clrk66", p("79d58'00.000\"W 36d58'00.000\"N"), 
        "+proj=latlong +ellps=bessel", p("79d58'W 36d58'N"), 0.01 );
  }

  public void testNAD27toRawEllipse()
  {
    checkTransform("+proj=latlong +datum=NAD27", p("79d00'00.000\"W 35d00'00.000\"N"), 
        "+proj=latlong +ellps=bessel", p("79dW 35dN"), 0.01 );

  }
  
}
  