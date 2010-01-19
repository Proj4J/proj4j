package org.osgeo.proj4j.util;

import java.awt.geom.Point2D;

import org.osgeo.proj4j.ProjCoordinate;

public class ProjectionUtil 
{
  public static String toString(Point2D.Double p)
  {
    return "[" + p.x + ", " + p.y + "]";
  }

  public static String toString(ProjCoordinate p)
  {
    return "[" + p.x + ", " + p.y + "]";
  }

}
