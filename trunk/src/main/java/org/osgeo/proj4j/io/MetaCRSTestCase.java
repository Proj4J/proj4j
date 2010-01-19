package org.osgeo.proj4j.io;

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.ProjCoordinate;
import org.osgeo.proj4j.util.ProjectionUtil;

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

  CoordinateReferenceSystem srcCS;
  CoordinateReferenceSystem tgtCS;

  ProjCoordinate srcPt = new ProjCoordinate();
  ProjCoordinate resultPt = new ProjCoordinate();

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
  
  public CoordinateReferenceSystem getSourceCS() { return srcCS; }
  
  public CoordinateReferenceSystem getTargetCS() { return tgtCS; }
  
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
  
  public boolean execute(CRSFactory csFactory)
  {
    srcCS = createCS(csFactory, srcCrsAuth, srcCrs);
    tgtCS = createCS(csFactory, tgtCrsAuth, tgtCrs);

    return executeTransform(srcCS, tgtCS);
  }
  
  public static String csName(String auth, String code)
  {
    return auth + ":" + code;
  }
  
  public static CoordinateReferenceSystem createCS(CRSFactory csFactory, String auth, String code)
  {
    CoordinateReferenceSystem cs = csFactory.createFromName(csName(auth, code));
    return cs;
  }
  
  private boolean executeTransform(
      CoordinateReferenceSystem srcCS,
      CoordinateReferenceSystem tgtCS)
  {
    // Testing: flip axis order to test SS sample file
    //srcPt.x = srcOrd1;
    //srcPt.y = srcOrd2;
    srcPt.x = srcOrd2;
    srcPt.y = srcOrd1;
    
    CoordinateTransform trans = new CoordinateTransform(
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
