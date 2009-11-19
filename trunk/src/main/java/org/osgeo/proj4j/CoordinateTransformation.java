package org.osgeo.proj4j;

import java.awt.geom.Point2D;

/**
 * A reification of the operation of transforming 
 * a coordinate from one {@link CoordinateSystem} into another.
 * Allows caching constant values for efficiency.
 * 
 * 
 * @author Martin Davis
 *
 */
public class CoordinateTransformation 
{
	private CoordinateSystem srcCS;
	private CoordinateSystem destCS;
	
  // temporary variable for intermediate results
	private Point2D.Double geoPt = new Point2D.Double();
  private ProjCoordinate geoCoord = new ProjCoordinate(0,0);
	
	private boolean doInverseProjection = true;
	private boolean doForwardProjection = true;
  private boolean doDatumTransform = false;
  private boolean transformViaGeocentric = false;
  private GeocentricConverter srcGeoConv; 
  private GeocentricConverter destGeoConv; 
	
	public CoordinateTransformation(CoordinateSystem srcCS, CoordinateSystem destCS)
	{
		this.srcCS = srcCS;
		this.destCS = destCS;
		
		// compute strategy for transformation at initialization time, to make transformation more efficient
		// this may include precomputing sets of parameters
		
		doInverseProjection = (srcCS != null && srcCS != CoordinateSystem.CS_GEO);
		doForwardProjection = (destCS != null && destCS != CoordinateSystem.CS_GEO);
    doDatumTransform = doInverseProjection && doForwardProjection
      && srcCS.getDatum() != destCS.getDatum();
    
    if (doDatumTransform) {
      
      boolean isEllipsoidEqual = srcCS.getEllipsoid().isEqual(destCS.getEllipsoid());
      if (! isEllipsoidEqual) 
          transformViaGeocentric = true;
      if (srcCS.getDatum().isTransform() 
          || destCS.getDatum().isTransform())
          transformViaGeocentric = true;
      
      if (transformViaGeocentric) {
        srcGeoConv = new GeocentricConverter(srcCS.getEllipsoid());
        destGeoConv = new GeocentricConverter(destCS.getEllipsoid());
      }
    }
	}
	
	/**
   * Tranforms a coordinate from one {@link CoordinateSystem} to another.
   * 
   * @param src
   * @param dest
   * @return
   * @throws ProjectionException if a computation error is encountered
	 */
	public Point2D.Double transform( Point2D.Double src, Point2D.Double dest )
  throws ProjectionException
	{
		// NOTE: this method may be called many times, so needs to be as efficient as possible
    
		if (! doInverseProjection) {
			geoPt.x = src.x;
			geoPt.y = src.y;
		}
		else {
			// inverse project to geo
			srcCS.getProjection().inverseTransformRadians(src, geoPt);
		}
		
    //TODO: adjust src Prime Meridian if specified
    
    if (doDatumTransform) {
      geoCoord.x = geoPt.x;
      geoCoord.y = geoPt.y;
      geoCoord.z = 0.0; // for now
      datumTransform(geoCoord);
      geoPt.x = geoCoord.x;
      geoPt.y = geoCoord.y;
      // ignore Z for now
    }
		
    //TODO: adjust dest Prime Meridian if specified

		if (! doForwardProjection) {
			dest.x = geoPt.x;
			dest.y = geoPt.y;
		}
		else {
			// project from geo
			destCS.getProjection().transformRadians(geoPt, dest);
		}
		return dest;
	}
  
  /**
   * 
   * Input:  long/lat/z coordinates in radians in the source datum
   * Output: long/lat/z coordinates in radians in the destination datum
   * 
   * @param geoPt the point containing the input and output values
   */
  private void datumTransform(ProjCoordinate pt)
  {
    /* -------------------------------------------------------------------- */
    /*      Short cut if the datums are identical.                          */
    /* -------------------------------------------------------------------- */
    if (srcCS.getDatum().isEqual(destCS.getDatum()))
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
      if( srcCS.getDatum().isTransform() ) {
        srcCS.getDatum().transformFromGeocentricToWgs84( pt );
      }
      if( destCS.getDatum().isTransform() ) {
        destCS.getDatum().transformToGeocentricFromWgs84( pt );
      }

      /* -------------------------------------------------------------------- */
      /*      Convert back to geodetic coordinates.                           */
      /* -------------------------------------------------------------------- */
      destGeoConv.convertGeocentricToGeodetic( pt );
    }
    
    // TODO: grid shift if required

  }
  
}
