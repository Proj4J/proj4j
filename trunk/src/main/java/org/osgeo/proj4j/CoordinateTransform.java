package org.osgeo.proj4j;

import org.osgeo.proj4j.datum.GeocentricConverter;

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
public class CoordinateTransform 
{
	private CoordinateReferenceSystem srcCRS;
	private CoordinateReferenceSystem tgtCRS;
	
  // temporary variable for intermediate results
  //private Point2D.Double srcPt = new Point2D.Double();
  //private Point2D.Double tgtPt = new Point2D.Double();
  //private Point2D.Double geoPt = new Point2D.Double();
  private ProjCoordinate geoCoord = new ProjCoordinate(0,0);
	
  // precomputed information
	private boolean doInverseProjection = true;
	private boolean doForwardProjection = true;
  private boolean doDatumTransform = false;
  private boolean transformViaGeocentric = false;
  private GeocentricConverter srcGeoConv; 
  private GeocentricConverter tgtGeoConv; 
	
  /**
   * Creates a transformation from a source {@link CoordinateReferenceSystem} 
   * to a target one.
   * 
   * @param srcCRS the source CRS to transform from
   * @param tgtCRS the target CRS to transform to
   */
	public CoordinateTransform(CoordinateReferenceSystem srcCRS, 
      CoordinateReferenceSystem tgtCRS)
	{
		this.srcCRS = srcCRS;
		this.tgtCRS = tgtCRS;
		
		// compute strategy for transformation at initialization time, to make transformation more efficient
		// this may include precomputing sets of parameters
		
		doInverseProjection = (srcCRS != null && srcCRS != CoordinateReferenceSystem.CS_GEO);
		doForwardProjection = (tgtCRS != null && tgtCRS != CoordinateReferenceSystem.CS_GEO);
    doDatumTransform = doInverseProjection && doForwardProjection
      && srcCRS.getDatum() != tgtCRS.getDatum();
    
    if (doDatumTransform) {
      
      boolean isEllipsoidEqual = srcCRS.getDatum().getEllipsoid().isEqual(tgtCRS.getDatum().getEllipsoid());
      if (! isEllipsoidEqual) 
          transformViaGeocentric = true;
      if (srcCRS.getDatum().hasTransformToWGS84() 
          || tgtCRS.getDatum().hasTransformToWGS84())
          transformViaGeocentric = true;
      
      if (transformViaGeocentric) {
        srcGeoConv = new GeocentricConverter(srcCRS.getDatum().getEllipsoid());
        tgtGeoConv = new GeocentricConverter(tgtCRS.getDatum().getEllipsoid());
      }
    }
	}
	
  public CoordinateReferenceSystem getSourceCRS()
  {
    return srcCRS;
  }
  
  public CoordinateReferenceSystem getTargetCRS()
  {
    return tgtCRS;
  }
  
  
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
  throws Proj4jException
	{
		// NOTE: this method may be called many times, so needs to be as efficient as possible
    
		if (! doInverseProjection) {
      geoCoord.x = src.x;
      geoCoord.y = src.y;
		}
		else {
			// inverse project to geo
			srcCRS.getProjection().inverseTransformRadians(src, geoCoord);
		}
    geoCoord.z = 0.0; // for now

    //TODO: adjust src Prime Meridian if specified
    
    if (doDatumTransform) {
//      geoCoord.x = geoPt.x;
//      geoCoord.y = geoPt.y;
//      geoCoord.z = 0.0; // for now
      datumTransform(geoCoord);
//      geoPt.x = geoCoord.x;
//      geoPt.y = geoCoord.y;
      // ignore Z for now
    }
		
    //TODO: adjust target Prime Meridian if specified

		if (! doForwardProjection) {
			tgt.x = geoCoord.x;
			tgt.y = geoCoord.y;
		}
		else {
			// project from geo
			tgtCRS.getProjection().transformRadians(geoCoord, tgt);
		}
		return tgt;
	}
  
  /**
   * 
   * Input:  long/lat/z coordinates in radians in the source datum
   * Output: long/lat/z coordinates in radians in the target datum
   * 
   * @param pt the point containing the input and output values
   */
  private void datumTransform(ProjCoordinate pt)
  {
    /* -------------------------------------------------------------------- */
    /*      Short cut if the datums are identical.                          */
    /* -------------------------------------------------------------------- */
    if (srcCRS.getDatum().isEqual(tgtCRS.getDatum()))
      return;
    
    // TODO: grid shift if required
    
    /* ==================================================================== */
    /*      Do we need to go through geocentric coordinates?                */
    /* ==================================================================== */
    if (transformViaGeocentric) {
      /* -------------------------------------------------------------------- */
      /*      Convert to geocentric coordinates.                              */
      /* -------------------------------------------------------------------- */
      srcGeoConv.convertGeodeticToGeocentric( pt );
      
      /* -------------------------------------------------------------------- */
      /*      Convert between datums.                                         */
      /* -------------------------------------------------------------------- */
      if( srcCRS.getDatum().hasTransformToWGS84() ) {
        srcCRS.getDatum().transformFromGeocentricToWgs84( pt );
      }
      if( tgtCRS.getDatum().hasTransformToWGS84() ) {
        tgtCRS.getDatum().transformToGeocentricFromWgs84( pt );
      }

      /* -------------------------------------------------------------------- */
      /*      Convert back to geodetic coordinates.                           */
      /* -------------------------------------------------------------------- */
      tgtGeoConv.convertGeocentricToGeodetic( pt );
    }
    
    // TODO: grid shift if required

  }
  
}
