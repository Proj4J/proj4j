/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.osgeo.proj4j.proj;

import org.osgeo.proj4j.ProjCoordinate;

/**
 * Implementation of forward and inverse projection equations taken
 * directly from Map Projections: A Working Manual by John Snyder, page 91.
 */
public class PlateCarreeProjection extends CylindricalProjection {
	
	public boolean hasInverse() {
		return true;
	}

	public boolean isRectilinear() {
		return true;
	}

	public String toString() {
		return "Plate Carr\u00e9e";
	}

	/**
	 * Computes the projection of a given point (i.e. from geographic to
	 * projection space). This should be overridden for all projections.
	 *
	 * @param x
	 *            the geographic x ordinate (in radians)
	 * @param y
	 *            the geographic y ordinate (in radians)
	 * @param dst
	 *            the projected coordinate (in coordinate system units)
	 * @return the target coordinate
	 */
	protected ProjCoordinate project(double x, double y, ProjCoordinate dst)
	{
		x *= Math.cos(getTrueScaleLatitude());
		dst.x = x;
		dst.y = y - getProjectionLatitude();
		return dst;
	}

	/**
	 * Computes the inverse projection of a given point (i.e. from projection
	 * space to geographic). This should be overridden for all projections.
	 *
	 * @param x
	 *            the projected x ordinate (in coordinate system units)
	 * @param y
	 *            the projected y ordinate (in coordinate system units)
	 * @param dst
	 *            the inverse-projected geographic coordinate (in radians)
	 * @return the target coordinate
	 */
	protected ProjCoordinate projectInverse(double x, double y, ProjCoordinate dst)
	{
		x /= Math.cos(getTrueScaleLatitude());
		dst.x = x;
		dst.y = y + getProjectionLatitude();
		return dst;
	}
}
