package org.osgeo.proj4j;

import org.osgeo.proj4j.io.Proj4FileReader;
import org.osgeo.proj4j.parser.Proj4Parser;

/**
 * Creates {@link CoordinateReferenceSystem}s from a variety of ways
 * of specifying them.
 * <p>
 * <tt>CoordinateReferenceSystem</tt>s can be used to
 * define {@link CoordinateTransform}s to perform transformations
 * on {@link ProjCoordinate}s. 
 * 
 * @author Martin Davis
 *
 */
public class CRSFactory 
{
  private static Proj4FileReader csReader = new Proj4FileReader();
  
  private static Registry registry = new Registry();

	// TODO: add method to allow reading from arbitrary PROJ4 CS file
	
  /**
   * Creates a new factory.
   */
	public CRSFactory()
	{
		
	}
	
  /**
   * Gets the {@link Registry} used by this factory.
   * @return the Registry
   */
  public Registry getRegistry()
  {
    return registry;
  }
  
  /**
   * Creates a {@link CoordinateReferenceSystem} from a well-known name.
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
   * @return a {@link CoordinateReferenceSystem}
   * @throws UnsupportedOperationException if a PROJ.4 parameter is not supported
   */
  public CoordinateReferenceSystem createFromName(String name)
  throws UnsupportedOperationException
  {
    String[] params = csReader.getParameters(name);
    return createFromParameters(name, params);
  }
  
  /**
   * Creates a {@link CoordinateReferenceSystem} from a PROJ.4 parameter string.
   * 
   * @param name a name for this coordinate system (may be null)
   * @param paramStr a PROJ.4 parameter string
   * @return a {@link CoordinateReferenceSystem}
   * @throws UnsupportedOperationException if a PROJ.4 parameter is not supported
   */
  public CoordinateReferenceSystem createFromParameters(String name, String paramStr)
  {
    return createFromParameters(name, splitParameters(paramStr));
  }
  

  /**
   * Creates a {@link CoordinateReferenceSystem} from a set of PROJ.4 parameters.
   * 
   * @param name a name for this coordinate system (may be null)
   * @param args an array of PROJ.4 parameters
   * @return a {@link CoordinateReferenceSystem}
    * @throws UnsupportedOperationException if a PROJ.4 parameter is not supported
  */
  public CoordinateReferenceSystem createFromParameters(String name, String[] args)
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
