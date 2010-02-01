package org.osgeo.proj4j;

public class CoordinateTransformFactory 
{
	public CoordinateTransformFactory()
	{
		
	}
	
	public CoordinateTransform createTransform(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS)
	{
		return new BasicCoordinateTransform(sourceCRS, targetCRS);
	}
}
