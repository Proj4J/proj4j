package org.osgeo.proj4j;

import org.osgeo.proj4j.proj.Projection;
import org.osgeo.proj4j.units.*;

/**
 * Represents a projected or geodetic geospatial coordinate system,
 * to which coordinates may be referenced.
 * A coordinate system is defined by the following things:
 * <ul>
 * <li>an {@link Ellipsoid} specifies how the shape of the Earth is approximated
 * <li>a {@link Datum} provides the mapping from the ellipsoid to 
 * actual locations on the earth
 * <li>a {@link Projection} method maps the ellpsoidal surface to a planar space.
 * (The projection method may be null in the case of geodetic coordinate systems).
 * <li>a {@link Unit} indicates how the ordinate values 
 * of coordinates are interpreted
 * </ul>
 * 
 * @author Martin Davis
 * 
 * @see CRSFactory
 *
 */
public class CoordinateReferenceSystem 
{
  // allows specifying transformations which convert to/from Geographic coordinates on the same datum
  public static final CoordinateReferenceSystem CS_GEO = new CoordinateReferenceSystem("CS_GEO", null, null, null);

	//TODO: add metadata like authority, id, name, parameter string, datum, ellipsoid, datum shift parameters
	
	private String name;
  private String[] params;
  private Datum datum;
	private Projection proj;
	
	public CoordinateReferenceSystem(String name, String[] params, Datum datum, Projection proj)
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
