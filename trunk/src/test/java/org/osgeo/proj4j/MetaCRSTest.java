package org.osgeo.proj4j;

import java.io.*;
import java.util.*;

import java.awt.geom.Point2D;

import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.osgeo.proj4j.io.*;

/**
 * Test which serves as an example of using Proj4J.
 * @author mbdavis
 *
 */
public class MetaCRSTest extends TestCase
{
  public static void main(String args[]) {
    TestRunner.run(MetaCRSTest.class);
  }

  static CoordinateSystemFactory csFactory = new CoordinateSystemFactory();
  
  public MetaCRSTest(String name) { super(name); }

  public void test()
  throws IOException
  {
    File file = new File("C:/data/martin/proj/proj4j/osgeosvn/trunk/src/test/resources/TestData.csv");
    MetaCRSTestFileReader reader = new MetaCRSTestFileReader(file);
    List<MetaCRSTestCase> tests = reader.readTests();
    for (MetaCRSTestCase test : tests) {
      runTest(test);
    }
  }
  
  void runTest(MetaCRSTestCase crsTest)
  {
    crsTest.execute(csFactory);
  }

}
