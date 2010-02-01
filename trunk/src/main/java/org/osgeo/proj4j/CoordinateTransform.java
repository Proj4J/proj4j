package org.osgeo.proj4j;

import org.osgeo.proj4j.datum.*;

/**
 * Represents the operation of transforming 
 * a {@link ProjCoordinate} from one {@link CoordinateReferenceSystem} 
 * into a different one.
 * The transformation involves the following steps:
 * <ul>
 * <li>If the input coordinate is in a projected coordinate system,
 * it is inverse-projected into a geographic coordinate 
 * <li>If the source and target {@link Datum}s are different,
 * the geographic coordinate is converted from the source to the target datum
 * as accurately as possible
 * <li>If the target coordinate system is a projected coordinate system, 
 * the geographic coordinate is projected into a projected coordinate.
 * </ul>
 * <p>
 * A coordinate transformation object is stateful, and thus is not thread-safe.
 * However, it may be reused any number of times within a single thread.
 * <p>
 * Information about the transformation procedure is pre-computed
 * and cached in this object for efficiency
 * in computing tranformations.
 * 
 * @author Martin Davis
 *
 */
public interface CoordinateTransform 
{

  public CoordinateReferenceSystem getSourceCRS();
  
  public CoordinateReferenceSystem getTargetCRS();
  
  
	/**
   * Tranforms a coordinate from the source {@link CoordinateReferenceSystem} 
   * to the target one.
   * 
   * @param src the input coordinate
   * @param tgt the transformed coordinate
   * @return the target coordinate which was passed in
   * 
   * @throws Proj4jException if a computation error is encountered
	 */
	public ProjCoordinate transform( ProjCoordinate src, ProjCoordinate tgt )
  throws Proj4jException;
  

}
