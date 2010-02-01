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

package org.osgeo.proj4j.datum;


/**
 * A class representing a geographic ellipsoid.
 */
public class Ellipsoid implements Cloneable {

	public String name;
	public String shortName;
	public double equatorRadius = 1.0;
	public double poleRadius = 1.0;
	public double eccentricity = 1.0;
	public double eccentricity2 = 1.0;

	// From: USGS PROJ package.
	public final static Ellipsoid SPHERE = new Ellipsoid("sphere", 6371008.7714, 6371008.7714, 0.0, "Sphere");
	public final static Ellipsoid BESSEL = new Ellipsoid("bessel", 6377397.155, 0.0, 299.1528128, "Bessel 1841");
	public final static Ellipsoid CLARKE_1866 = new Ellipsoid("clrk66", 6378206.4, 6356583.8, 0.0, "Clarke 1866");
	public final static Ellipsoid CLARKE_1880 = new Ellipsoid("clrk80", 6378249.145, 0.0, 293.4663, "Clarke 1880 mod.");
  public final static Ellipsoid AIRY = new Ellipsoid("airy", 6377563.396, 6356256.910, 0.0, "Airy 1830");
  public final static Ellipsoid MOD_AIRY = new Ellipsoid("airy", 6377340.189, 6356034.446, 0.0, "Modified Airy");
	public final static Ellipsoid WGS60 = new Ellipsoid("WGS60", 6378165.0, 0.0, 298.3, "WGS 60");
	public final static Ellipsoid WGS66 = new Ellipsoid("WGS66", 6378145.0, 0.0, 298.25, "WGS 66");
	public final static Ellipsoid WGS72 = new Ellipsoid("WGS72", 6378135.0, 0.0, 298.26, "WGS 72");
	public final static Ellipsoid WGS84 = new Ellipsoid("WGS84", 6378137.0, 0.0, 298.257223563, "WGS 84");
	public final static Ellipsoid KRASOVSKY = new Ellipsoid("krass", 6378245.0, 298.3, 0.0, "Krassovsky, 1942");
	public final static Ellipsoid EVEREST = new Ellipsoid("evrst30", 6377276.345, 0.0, 300.8017, "Everest 1830");
  public final static Ellipsoid INTERNATIONAL = new Ellipsoid("intl", 6378388.0, 0.0, 297.0, "International 1909 (Hayford)");
  public final static Ellipsoid INTERNATIONAL_1967 = new Ellipsoid("new_intl", 6378157.5, 6356772.2, 0.0, "New International 1967");
	public final static Ellipsoid GRS80 = new Ellipsoid("GRS80", 6378137.0, 0.0, 298.257222101, "GRS 1980 (IUGG, 1980)");
	public final static Ellipsoid AUSTRALIAN = new Ellipsoid("australian", 6378160.0, 6356774.7, 298.25, "Australian");
	

	public Ellipsoid() {
	}
	
	/**
	 * Creates a new Ellipsoid.
	 * One of of poleRadius or reciprocalFlattening must be specified, the other must be zero
	 */ 
	public Ellipsoid(String shortName, double equatorRadius, double poleRadius, double reciprocalFlattening, String name) {
		this.shortName = shortName;
		this.name = name;
		this.equatorRadius = equatorRadius;
		this.poleRadius = poleRadius;
		
		if (poleRadius == 0.0 && reciprocalFlattening == 0.0)
			throw new IllegalArgumentException("One of poleRadius or reciprocalFlattening must be specified");
		// don't check for only one of poleRadius or reciprocalFlattening to be specified,
		// since some defs actually supply two
		
		// reciprocalFlattening takes precedence over poleRadius
		if (reciprocalFlattening != 0) {
			double flattening = 1.0 / reciprocalFlattening;
			double f = flattening;
			eccentricity2 = 2 * f - f * f;
			this.poleRadius = equatorRadius * Math.sqrt(1.0 - eccentricity2);
		} else {
			eccentricity2 = 1.0 - (poleRadius * poleRadius) / (equatorRadius * equatorRadius);
		}
		eccentricity = Math.sqrt(eccentricity2);
	}

	public Ellipsoid(String shortName, double equatorRadius, double eccentricity2, String name) {
		this.shortName = shortName;
		this.name = name;
		this.equatorRadius = equatorRadius;
		setEccentricitySquared(eccentricity2);
	}

	public Object clone() {
		try {
			Ellipsoid e = (Ellipsoid)super.clone();
			return e;
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public void setEquatorRadius(double equatorRadius) {
		this.equatorRadius = equatorRadius;
	}

	public double getEquatorRadius() {
		return equatorRadius;
	}
  public double getA()
  {
    return equatorRadius;
  }
  public double getB()
  {
    return poleRadius;
  }

	public void setEccentricitySquared(double eccentricity2) {
		this.eccentricity2 = eccentricity2;
		poleRadius = equatorRadius * Math.sqrt(1.0 - eccentricity2);
		eccentricity = Math.sqrt(eccentricity2);
	}

	public double getEccentricitySquared() {
		return eccentricity2;
	}

  public boolean isEqual(Ellipsoid e)
  {
    return equatorRadius == e.equatorRadius
    &&  eccentricity2 == e.eccentricity2;
  }
  
	public String toString() {
		return name;
	}

}
