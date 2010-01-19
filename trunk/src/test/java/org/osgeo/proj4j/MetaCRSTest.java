package org.osgeo.proj4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.osgeo.proj4j.io.MetaCRSTestCase;
import org.osgeo.proj4j.io.MetaCRSTestFileReader;

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

  static CRSFactory csFactory = new CRSFactory();
  
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
