package org.osgeo.proj4j;

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
