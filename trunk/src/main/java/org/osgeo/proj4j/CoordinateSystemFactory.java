package org.osgeo.proj4j;

import java.util.*;

import org.osgeo.proj4j.io.Proj4FileReader;
import org.osgeo.proj4j.parser.Proj4Parser;
import org.osgeo.proj4j.proj.TransverseMercatorProjection;
import org.osgeo.proj4j.units.AngleFormat;
import org.osgeo.proj4j.units.Unit;
import org.osgeo.proj4j.units.Units;

/**
 * Creates {@link CoordinateSystem}s from a variety of ways
 * of specifying them.
 * <p>
 * Once created, <tt>CoordinateSystem</tt>s can be used to
 * define a {@link CoordinateTransformation} to perform transformations
 * on {@link ProjCoordinate}s. 
 * 
 * @author Martin Davis
 *
 */
public class CoordinateSystemFactory 
{
  private static Proj4FileReader csReader = new Proj4FileReader();
  
  private static Registry registry = new Registry();

	// TODO: add method to allow reading from arbitrary PROJ4 CS file
	
  /**
   * Creates a new factory.
   */
	public CoordinateSystemFactory()
	{
		
	}
	
  /**
   * Gets the {@link Registry} used by this factory.
   * @return
   */
  public Registry getRegistry()
  {
    return registry;
  }
  
  /**
   * Creates a CoordinateSystem from a well-known name.
   * Names are of the form: <tt>authority:id</tt>.
   * <ul>
   * <li>The authority is a code for a namespace supported by
   * PROJ.4.  
   * Currently supported values are 
   * <tt>EPSG</tt>, <tt>ESRI</tt>, <tt>WORLD</tt>, <tt>NA83</tt>, <tt>NAD27</tt>.
   * If no authority is provided, <tt>EPSG</tt> will be assumed.
   * <li>The id is the id of a coordinate system in the authority namespace.
   * For example, in the <tt>EPSG</tt> namespace an id is an integer value.
   * </ul>
   * An example of a valid name is <tt>EPSG:3005</tt>.
   * 
   * @param name the name of a coordinate system, with optional authority prefix
   * @return a CoordinateSystem
   * @throws UnsupportedOperationException if a PROJ.4 parameter is not supported
   */
  public CoordinateSystem createFromName(String name)
  throws UnsupportedOperationException
  {
    String[] params = csReader.getParameters(name);
    return createFromParameters(name, params);
  }
  
  /**
   * Creates a CoordinateSystem from a PROJ.4 parameter string.
   * 
   * @param name a name for this coordinate system (may be null)
   * @param paramStr a PROJ.4 parameter string
   * @return a CoordinateSystem
   * @throws UnsupportedOperationException if a PROJ.4 parameter is not supported
   */
  public CoordinateSystem createFromParameters(String name, String paramStr)
  {
    return createFromParameters(name, splitParameters(paramStr));
  }
  

  /**
   * Creates a CoordinateSystem from a set of PROJ.4 parameters.
   * 
   * @param name a name for this coordinate system (may be null)
   * @param params an array of PROJ.4 parameters
   * @return a CoordinateSystem
    * @throws UnsupportedOperationException if a PROJ.4 parameter is not supported
  */
  public CoordinateSystem createFromParameters(String name, String[] args)
  {
    if (args == null)
      return null;
    
    Proj4Parser parser = new Proj4Parser(registry);
    return parser.parse(name, args);
  }

  private static String[] splitParameters(String paramStr)
  {
    String[] params = paramStr.split("\\s+");
    return params;
  }
  
}
