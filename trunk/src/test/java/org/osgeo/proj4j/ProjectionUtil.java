package org.osgeo.proj4j;

import java.awt.geom.Point2D;

public class ProjectionUtil 
{
	public static String toString(Point2D.Double p)
	{
		return "[" + p.x + ", " + p.y + "]";
	}

}
