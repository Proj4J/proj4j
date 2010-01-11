/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.osgeo.proj4j;


/**
 * A geodetic datum. 
 */
public class Datum 
{
  public static final int TYPE_UNKNOWN = 0;
  public static final int TYPE_WGS84 = 1;
  public static final int TYPE_3PARAM = 2;
  public static final int TYPE_7PARAM = 3;
  public static final int TYPE_GRIDSHIFT = 4;
  
  public static final Datum WGS84 = new Datum("WGS84", 0,0,0, Ellipsoid.WGS84, "WGS84"); 
  public static final Datum GGRS87 = new Datum("GGRS87", -199.87,74.79,246.62, Ellipsoid.GRS80, "Greek_Geodetic_Reference_System_1987");
  public static final Datum NAD83 = new Datum("NAD83", 0,0,0, Ellipsoid.GRS80,"North_American_Datum_1983");
  //public static final Datum NAD27 = new Datum("NAD27", "@conus,@alaska,@ntv2_0.gsb,@ntv1_can.dat", Ellipsoid.CLARKE_1866,"North_American_Datum_1927");
  public static final Datum POTSDAM = new Datum("potsdam", 606.0,23.0,413.0, Ellipsoid.BESSEL, "Potsdam Rauenberg 1950 DHDN");
  public static final Datum CARTHAGE = new Datum("carthage",-263.0,6.0,431.0, Ellipsoid.CLARKE_1880, "Carthage 1934 Tunisia");
  public static final Datum HERMANNSKOGEL = new Datum("hermannskogel", 653.0,-212.0,449.0, Ellipsoid.BESSEL, "Hermannskogel");
  public static final Datum IRE65 = new Datum("ire65", 482.530,-130.596,564.557,-1.042,-0.214,-0.631,8.15, Ellipsoid.MOD_AIRY, "Ireland 1965");
  public static final Datum NZGD49 = new Datum("nzgd49", 59.47,-5.04,187.44,0.47,-0.1,1.024,-4.5993, Ellipsoid.INTERNATIONAL, "New Zealand Geodetic Datum 1949");
  public static final Datum OSEB36 = new Datum("OSGB36", 446.448,-125.157,542.060,0.1502,0.2470,0.8421,-20.4894, Ellipsoid.AIRY, "Airy 1830");

  String code;
	String name;
	Ellipsoid ellipsoid;
	double[] transform;
	
  public Datum(String code, 
      double deltaX, double deltaY, double deltaZ, 
      Ellipsoid ellipsoid,
      String name) {
    this(code, new double[] { deltaX, deltaY, deltaZ },ellipsoid, name);
  }
  
  public Datum(String code, 
      double deltaX, double deltaY, double deltaZ,
      double rx, double ry, double rz, double mbf,
      Ellipsoid ellipsoid,
      String name) {
    this(code, new double[] { deltaX, deltaY, deltaZ, rx, ry, rx, mbf },ellipsoid, name);
  }
  
  public Datum(String code, 
      double[] transform, 
      Ellipsoid ellipsoid,
      String name) {
    this.code = code;
    this.name = name;
    this.ellipsoid = ellipsoid;
    this.transform = transform;
  }
  
  public String getCode() { return code; }
  
  public double[] getTransform()
  {
    return transform;
  }
  
  public int getType()
  {
    if (transform  == null) return TYPE_WGS84;
    
    //TODO: check for transform = all zeroes and return TYPE_WGS84 if so
    
    if (transform.length  == 3) return TYPE_3PARAM;
    if (transform.length  == 7) return TYPE_7PARAM;
    return TYPE_WGS84;
  }
  
  public boolean isTransform()
  {
    return getType() == TYPE_3PARAM || getType() == TYPE_7PARAM;
  }
  
  public boolean isEqual(Datum datum)
  {
    if( getType() != datum.getType()) {
      return false; // false, datums are not equal
    } 
    if( ellipsoid.getEquatorRadius() != ellipsoid.getEquatorRadius()) {
      if (Math.abs(ellipsoid.getEccentricitySquared() 
           - datum.ellipsoid.getEccentricitySquared() )  > 0.000000000050)
      return false;
    } 
    if( getType() == TYPE_3PARAM || getType() == TYPE_3PARAM) {
      for (int i = 0; i < transform.length; i++) {
        if (transform[i] != datum.transform[i])
          return false;
      }
      return true;
    } 
    /* 
     //TODO: complete
    else if( this.datum_type == Proj4js.common.PJD_GRIDSHIFT ) {
      return strcmp( pj_param(this.params,"snadgrids").s,
                     pj_param(dest.params,"snadgrids").s ) == 0;
    }
    */ 
    return true; // datums are equal

  }

  
  public void transformFromGeocentricToWgs84(ProjCoordinate p) 
  {
    if( transform.length == 3 )
    {
      p.x += transform[0];
      p.y += transform[1];
      p.z += transform[2];

    }
    else if (transform.length == 7)
    {
      double Dx_BF = transform[0];
      double Dy_BF = transform[1];
      double Dz_BF = transform[2];
      double Rx_BF = transform[3];
      double Ry_BF = transform[4];
      double Rz_BF = transform[5];
      double M_BF  = transform[6];
      
      double x_out = M_BF*(       p.x - Rz_BF*p.y + Ry_BF*p.z) + Dx_BF;
      double y_out = M_BF*( Rz_BF*p.x +       p.y - Rx_BF*p.z) + Dy_BF;
      double z_out = M_BF*(-Ry_BF*p.x + Rx_BF*p.y +       p.z) + Dz_BF;

      p.x = x_out;
      p.y = y_out;
      p.z = z_out;
    }
  }
  public void transformToGeocentricFromWgs84(ProjCoordinate p) 
  {
    if( transform.length == 3 )
    {
      p.x -= transform[0];
      p.y -= transform[1];
      p.z -= transform[2];

    }
    else if (transform.length == 7)
    {
      double Dx_BF = transform[0];
      double Dy_BF = transform[1];
      double Dz_BF = transform[2];
      double Rx_BF = transform[3];
      double Ry_BF = transform[4];
      double Rz_BF = transform[5];
      double M_BF  = transform[6];
      
      double x_tmp = (p.x - Dx_BF) / M_BF;
      double y_tmp = (p.y - Dy_BF) / M_BF;
      double z_tmp = (p.z - Dz_BF) / M_BF;

      p.x =        x_tmp + Rz_BF*y_tmp - Ry_BF*z_tmp;
      p.y = -Rz_BF*x_tmp +       y_tmp + Rx_BF*z_tmp;
      p.z =  Ry_BF*x_tmp - Rx_BF*y_tmp +       z_tmp;
    }
  }
}
