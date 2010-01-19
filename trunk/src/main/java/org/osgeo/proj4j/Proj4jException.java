package org.osgeo.proj4j;

/**
 * Signals that a situation or data state has been encountered
 * which prevents computation from proceeding,
 * or which would lead to erroneous results.
 * 
 * @author mbdavis
 *
 */
public class Proj4jException extends RuntimeException 
{
	public Proj4jException() {
		super();
	}

	public Proj4jException(String message) {
		super(message);
	}
}
