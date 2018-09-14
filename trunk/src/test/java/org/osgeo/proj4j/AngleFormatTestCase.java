package org.osgeo.proj4j;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;

import org.osgeo.proj4j.units.Angle;
import org.osgeo.proj4j.units.AngleFormat;

/**
 * Unit test class for angle format generation and parsing in PROJ4J.
 * Each angle format defined in the AngleFormat class is tested through
 * a full range of angles by first performing a forward format conversion
 * from decimal degrees to the specified format and then performing the
 * inverse conversion. The end result is compared to the starting value
 * subject to an error threshold that depends upon the particular format.
 * In most cases the error threshold is 1 arcsecond, but in the case of the 
 * ddmmssPattern, which does not display seconds, an error of up to 59 
 * arcseconds could result from the forward and inverse conversion calculations,
 * so its error threshold must take this into account. To further mitigate decimal 
 * inaccuracies in the calculations, a safety margin of 2 has been factored into
 * the error thresholds.
 * 
 * @author jgyorfi
 */
public class AngleFormatTestCase extends TestCase implements TestListener
{
	public AngleFormatTestCase(String name)
	{
		super(name);
	}
	
	/**
	 * Despite this pattern's name, it doesn't actually include
	 * seconds. The maximum error is therefore 59 seconds of arc.
	 * The factor of 2 on the error 
	 * 
	 * Example: -123.456 degrees is formatted -123d27
	 */
	public void ddmmssPattern()
	{
		performTest(AngleFormat.ddmmssPattern, 2.0 * 59.0 / 3600.0);
	}
	
	/**
	 * This pattern extends the one above by including seconds using
	 * the minutes symbol ' and the seconds symbol ".
	 * 
	 * Example: -123.456 degrees is formatted -123d27'21"
	 */
	public void ddmmssPattern2()
	{
		performTest(AngleFormat.ddmmssPattern2, 2.0 / 3600.0);
	}

	/**
	 * This pattern is a variation on the ddmmssPattern2 that formats
	 * longitudes with the east suffix (E) if positive and the west
	 * suffix (W) if negative.
	 * 
	 * Example: -123.456 degrees is formatted 123d27'21"W
	 */
	public void ddmmssLongPattern()
	{
		performTest(AngleFormat.ddmmssLongPattern, 2.0 / 3600.0);
	}
	
	/**
	 * This pattern is a variation on the ddmmssPattern2 that formats
	 * latitudes with the north suffix (N) if positive and the south
	 * suffix (S) if negative.
	 * 
	 * Example: -123.456 degrees is formatted 123d27'21"S
	 *          (Yes, I know latitudes don't go that low, but
	 *           this is a good stress test for the formatter)
	 */
	public void ddmmssLatPattern()
	{
		performTest(AngleFormat.ddmmssLatPattern, 2.0 / 3600.0);
	}

	/**
	 * This pattern is the same as ddmmssPattern2 except that
	 * it uses the minutes abbreviation m and the seconds
	 * abbreviation s.
	 * 
	 * Example: -123.456 degrees is formatted -123d27m21s 
	 */
	public void ddmmssPattern4()
	{
		performTest(AngleFormat.ddmmssPattern4, 2.0 / 3600.0);
	}
	
	/**
	 * This pattern displays angles in decimal degrees. The choice
	 * of 6 decimal places is arbitrary and can be changed in the
	 * AngleFormat class. The maximum error at this resolution is
	 * 0.0036 seconds of arc.
	 * 
	 * Example: -123.456 degrees is formatted -123.456000 
	 */
	public void decimalPattern()
	{
		performTest(AngleFormat.decimalPattern, 1.0 / 3600.0);
	}

	/**
	 * Each of the test cases above calls this method to perform
	 * forward and inverse angle format conversions, comparing the
	 * initial angle value to the final result. The test angles are
	 * incremented from -180 degrees to +180 degrees in 1 arcsecond
	 * increments. The error threshold depends upon the test. In most
	 * cases, a threshold of 2 arcseconds is used whereas for the
	 * ddmmssPattern, which ignores seconds, a threshold of 2 minutes
	 * is used.
	 * 
	 * @param angleFormatPattern
	 * @param eps
	 */
	private void performTest(String angleFormatPattern, double eps)
	{
		final StringBuffer sb = new StringBuffer();
		final AngleFormat af = new AngleFormat(angleFormatPattern, true);
		
		for(int deg=-180; deg<=180; deg++) 
		{
			for(int min=0; min<=59; min++)
			{
				for(int sec=0; sec<=59; sec++)
				{
					double theta1 = 
						(double)deg + 
						(double)min / 60.0 + 
						(double)sec / 3600.0;

					// Forward conversion (decimal degrees to specified format)
					sb.delete(0,sb.length());
					af.format(theta1, sb, null); 
					
					// Inverse conversion (specified format to decimal degrees)
					double theta2 = Angle.parse(sb.toString());

					// Output error case, if any
					String string = String.format("%12.6f --> %s --> %12.6f", theta1, sb.toString(), theta2);
					assertTrue(string, Math.abs(theta2 - theta1) <= eps);
				}
			}
		}
	}
	
	private boolean failed = false;
	
	@Override
	public void addError(Test test, Throwable throwable)
	{
	}

	@Override
	public void addFailure(Test test, AssertionFailedError error)
	{
		failed = true;
		System.out.println("Failure: " + error.getMessage());
	}

	@Override
	public void endTest(Test arg0)
	{
		if(!failed)
			System.out.println("Success");
		System.out.println();
	}

	@Override
	public void startTest(Test test)
	{
		System.out.println("Test case: " + ((AngleFormatTestCase)test).getName());
	}

	/**
	 * Each angle format is run as a separate test case.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		AngleFormatTestCase testCase;
		TestResult testResult;
		
		testCase = new AngleFormatTestCase("ddmmssPattern");
		testResult = new TestResult();
		testResult.addListener(testCase);
		testCase.run(testResult);

		testCase = new AngleFormatTestCase("ddmmssPattern2");
		testResult = new TestResult();
		testResult.addListener(testCase);
		testCase.run(testResult);

		testCase = new AngleFormatTestCase("ddmmssLongPattern");
		testResult = new TestResult();
		testResult.addListener(testCase);
		testCase.run(testResult);

		testCase = new AngleFormatTestCase("ddmmssLatPattern");
		testResult = new TestResult();
		testResult.addListener(testCase);
		testCase.run(testResult);
		
		testCase = new AngleFormatTestCase("ddmmssPattern4");
		testResult = new TestResult();
		testResult.addListener(testCase);
		testCase.run(testResult);

		testCase = new AngleFormatTestCase("decimalPattern");
		testResult = new TestResult();
		testResult.addListener(testCase);
		testCase.run(testResult);
	}
}
