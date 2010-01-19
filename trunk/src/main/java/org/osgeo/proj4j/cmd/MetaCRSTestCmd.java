package org.osgeo.proj4j.cmd;


import java.io.*;
import java.util.List;

import org.osgeo.proj4j.*;
import org.osgeo.proj4j.io.*;

/**
 * A command-line application which runs test files 
 * in MetaCRS Transformation Test format.
 * <p>
 * Usage:
 * <pre>
 *   MetaCRSTestCmd <test-file-name>
 * </pre>
 * 
 * @author Martin Davis
 *
 */
public class MetaCRSTestCmd 
{

  public static void main(String args[]) 
  {
    MetaCRSTestCmd cmd = new MetaCRSTestCmd();
    cmd.parseArgs(args);
    try {
      cmd.execute();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static String usage()
  {
    return "Usage: MetaCRSTestCmd <test-file-name>";
  }
  private static final int TESTS_PER_LINE = 50;
  
  private static CRSFactory csFactory = new CRSFactory();

  private String filename;
  private boolean verbose = true;
  
  public MetaCRSTestCmd() 
  {
  }
  
  private void parseArgs(String[] args)
  {
    //TODO: error handling
    if (args.length <= 0) {
      System.err.println(usage());
      System.exit(1);
    }
    filename = args[0];
  }

  private void execute()
  throws IOException
  {
    File file = new File(filename);
    MetaCRSTestFileReader reader = new MetaCRSTestFileReader(file);
    List<MetaCRSTestCase> tests = reader.readTests();
    int count = 0;
    int failCount = 0;
    int errCount = 0;
    
    for (MetaCRSTestCase test : tests) 
    {
      count++;
      System.out.print(".");
      boolean isOk = test.execute(csFactory);
      if (! isOk) {
        failCount++;
        System.out.print("F");
      }
      if (verbose) {
        System.out.println();
        System.out.println("Name:    " + test.getName());
        System.out.println("Src CRS: " + test.getSourceCrsName()
            + "  ::  " + test.getSourceCS().getParameterString());
        System.out.println("Tgt CRS: " + test.getTargetCrsName()
            + "  ::  " + test.getTargetCS().getParameterString());
        System.out.println("Src Coord:    " + toString(test.getSourceCoordinate()));
        System.out.println("Tgt Coord:    " + toString(test.getTargetCoordinate()));
        System.out.println("Result Coord: " + toString(test.getResultCoordinate()));
        System.out.println();
      }

      if (count % TESTS_PER_LINE == 0)
        System.out.println();
    }
    System.out.println();
    System.out.println("Tests run: " + count
        + ",  Failures: " + failCount
        + ",  Errors: " + errCount);
    
  }
  
  private static String toString(ProjCoordinate p)
  {
    if (p.hasValidZOrdinate()) {
      return p.x + ", " + p.y + ", " + p.z;
    }
    return p.x + ", " + p.y;
  }

}
