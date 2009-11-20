package org.osgeo.proj4j;

import org.osgeo.proj4j.units.*;

/**
 * Represents a projected or geographic coordinate system,
 * to which coordinates are referenced.
 * A coordinate system is defined by a {@link Datum} 
 * on a specified {@link Ellipsoid},
 * and by a {@link Projection} method
 * (which may be null in the case of geographic coordinate systems.
 * The numeric values of coordinates in a coordinate system
 * are expressed in a specified kind of {@link Unit}s.
 * 
 * 
 * @author Martin Davis
 *
 */
public class CoordinateSystem 
{
  // allows specifying transformations which convert to/from Geographic coordinates on the same datum
  public static final CoordinateSystem CS_GEO = new CoordinateSystem("CS_GEO", null, null, null);

	//TODO: add metadata like authority, id, name, parameter string, datum, ellipsoid, datum shift parameters
	
	private String name;
  private String[] params;
  private Datum datum;
	private Projection proj;
	
	public CoordinateSystem(String name, String[] params, Datum datum, Projection proj)
	{
		this.name = name;
    this.params = params;
    this.datum = datum;
		this.proj = proj;
    
    
    if (name == null) {
      String projName = "null-proj"; 
      if (proj != null)
        projName = proj.getName();
      name = projName + "-CS";
    }
	}
	
  public String getName()
  {
    return name;
  }
  
  public String[] getParameters()
  {
    return params;
  }
  
  public Datum getDatum()
  {
    return datum;
  }
  
  public Ellipsoid getEllipsoid()
  {
    return proj.getEllipsoid();
  }
  
  public Projection getProjection()
  {
    return proj;
  }
  
  public String getParameterString()
  {
    if (params == null) return "";
    
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < params.length; i++) {
      buf.append(params[i]);
      buf.append(" ");
    }
    return buf.toString();
  }
  
	public String toString() { return name; }
}
