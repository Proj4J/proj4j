package org.osgeo.proj4j.io;

import java.awt.geom.Point2D;
import org.osgeo.proj4j.*;

public class MetaCRSTestCase 
{
  private boolean verbose = false;
  
  String testName;
  String testMethod;
  
  String srcCrsAuth;
  String srcCrs;
  
  String tgtCrsAuth;
  String tgtCrs;
  
  double srcOrd1;
  double srcOrd2;
  double srcOrd3;
  
  double tgtOrd1;
  double tgtOrd2;
  double tgtOrd3;
  
  double tolOrd1;
  double tolOrd2;
  double tolOrd3;
  
  String using;
  String dataSource;
  String dataCmnts;
  String maintenanceCmnts;

  CoordinateSystem srcCS;
  CoordinateSystem tgtCS;

  Point2D.Double srcPt = new Point2D.Double();
  Point2D.Double resultPt = new Point2D.Double();

  public MetaCRSTestCase(
      String testName,
      String testMethod,
      String srcCrsAuth,
      String srcCrs,
      String tgtCrsAuth,
      String tgtCrs,
      double srcOrd1,
      double srcOrd2,
      double srcOrd3,
      double tgtOrd1,
      double tgtOrd2,
      double tgtOrd3,
      double tolOrd1,
      double tolOrd2,
      double tolOrd3,
      String using,
      String dataSource,
      String dataCmnts,
      String maintenanceCmnts
  )
  {
    this.testName = testName;
    this.testMethod = testMethod;
    this.srcCrsAuth = srcCrsAuth;
    this.srcCrs = srcCrs;
    this.tgtCrsAuth = tgtCrsAuth;
    this.tgtCrs = tgtCrs;
    this.srcOrd1 = srcOrd1;
    this.srcOrd2 = srcOrd2;
    this.srcOrd3 = srcOrd3;
    this.tgtOrd1 = tgtOrd1;
    this.tgtOrd2 = tgtOrd2;
    this.tgtOrd3 = tgtOrd3;
    this.tolOrd1 = tolOrd1;
    this.tolOrd2 = tolOrd2;
    this.tolOrd3 = tolOrd3;
    this.using = using;
    this.dataSource = dataSource;
    this.dataCmnts = dataCmnts;
    this.maintenanceCmnts = maintenanceCmnts;
  }

  public String getName() { return testName; }
  
  public String getSourceCrsName() { return csName(srcCrsAuth, srcCrs); }
  
  public String getTargetCrsName() { return csName(tgtCrsAuth, tgtCrs); }
  
  public CoordinateSystem getSourceCS() { return srcCS; }
  
  public CoordinateSystem getTargetCS() { return tgtCS; }
  
  public ProjCoordinate getSourceCoordinate()
  {
    return new ProjCoordinate(srcOrd1, srcOrd2, srcOrd3);
  }
  
  public ProjCoordinate getTargetCoordinate()
  {
    return new ProjCoordinate(tgtOrd1, tgtOrd2, tgtOrd3);
  }
  
  public ProjCoordinate getResultCoordinate()
  {
    return new ProjCoordinate(resultPt.x, resultPt.y);
  }
  
  public boolean execute(CoordinateSystemFactory csFactory)
  {
    srcCS = createCS(csFactory, srcCrsAuth, srcCrs);
    tgtCS = createCS(csFactory, tgtCrsAuth, tgtCrs);

    return executeTransform(srcCS, tgtCS);
  }
  
  public static String csName(String auth, String code)
  {
    return auth + ":" + code;
  }
  
  public static CoordinateSystem createCS(CoordinateSystemFactory csFactory, String auth, String code)
  {
    CoordinateSystem cs = csFactory.createFromName(csName(auth, code));
    return cs;
  }
  
  private boolean executeTransform(
      CoordinateSystem srcCS,
      CoordinateSystem tgtCS)
  {
    // Testing: flip axis order to test SS sample file
    //srcPt.x = srcOrd1;
    //srcPt.y = srcOrd2;
    srcPt.x = srcOrd2;
    srcPt.y = srcOrd1;
    
    CoordinateTransformation trans = new CoordinateTransformation(
        srcCS, tgtCS);

    trans.transform(srcPt, resultPt);
    
    if (verbose) {
      System.out.println(testName);
      System.out.println(ProjectionUtil.toString(srcPt) 
          + " -> " + ProjectionUtil.toString(resultPt)
          + " ( expected: " + tgtOrd1 + ", " + tgtOrd2 + " )"
          );
      System.out.println();
    }
    
    double dx = Math.abs(resultPt.x - tgtOrd1);
    double dy = Math.abs(resultPt.y - tgtOrd2);
    
    boolean isInTol =  dx <= tolOrd1 && dy <= tolOrd2;
   
    if (verbose && ! isInTol) {
      System.out.println(srcCS.getParameterString());
      System.out.println(tgtCS.getParameterString());
      System.out.println("FAIL: "
          + ProjectionUtil.toString(srcPt) 
          + " -> " + ProjectionUtil.toString(resultPt) 
          );
    }

    return isInTol;
  }

}
