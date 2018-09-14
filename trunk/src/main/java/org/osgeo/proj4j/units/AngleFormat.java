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

package org.osgeo.proj4j.units;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import org.osgeo.proj4j.util.ProjectionMath;

/**
 * A NumberFormat for formatting Angles in various commonly-found mapping styles.
 */
public class AngleFormat extends NumberFormat {

  public static final char CH_MIN_SYMBOL = '\'';
  public static final String STR_SEC_SYMBOL = "\"";
  public static final char CH_DEG_SYMBOL = '\u00b0';
  public static final char CH_DEG_ABBREV = 'd';
  public static final char CH_MIN_ABBREV = 'm';
  public static final String STR_SEC_ABBREV = "s";
  
  public static final char CH_N = 'N';
  public static final char CH_E = 'E';
  public static final char CH_S = 'S';
  public static final char CH_W = 'W';

	public final static String ddmmssPattern = "DdM";
	public final static String ddmmssPattern2 = "DdM'S\"";
	public final static String ddmmssLongPattern = "DdM'S\"W";
	public final static String ddmmssLatPattern = "DdM'S\"N";
	public final static String ddmmssPattern4 = "DdMmSs";
	public final static String decimalPattern = "D.F";

	private DecimalFormat format;
	private String pattern;
	private boolean isDegrees;

	public AngleFormat() {
		this(ddmmssPattern);
	}
	
	public AngleFormat(String pattern) {
		this(pattern, false);
	}
	
	public AngleFormat(String pattern, boolean isDegrees) {
		this.pattern = pattern;
		this.isDegrees = isDegrees;
		format = new DecimalFormat();
		format.setMaximumFractionDigits(0);
		format.setGroupingUsed(false);
	}
	
	public StringBuffer format(long number, StringBuffer result, FieldPosition fieldPosition) {
		return format((double)number, result, fieldPosition);
	}

	public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition) {
		int length = pattern.length();
		boolean negative = false;

		if (number < 0) {
			for (int i = length-1; i >= 0; i--) {
				char c = pattern.charAt(i);
				if (c == 'W' || c == 'N') {
					number = -number;
					negative = true;
					break;
				}
			}
		}
		
		int sign = 1;
		if(number < 0.0)
		{
			sign = -1;
			number = -number;
		}

		// The previous method of converting an angle from decimal degrees
		// to integer degrees, minutes, and seconds relied on a mixture of
		// casting doubles as integers and using Math.round() in way that was
		// numerically unstable. Examples of failure cases for all defined
		// formats are given below.
		//
		// AngleFormat.ddmmssPattern
		// Previous failure case: 29.99999999 --> 29d00
		//
		// AngleFormat.ddmmssPattern2
		// Previous failure case: 29.99999999 --> 29d00'59"
		//
		// AngleFormat.ddmmssLongPattern
		// Previous failure case: 29.99999999 --> 29d00'59"E
		//
		// AngleFormat.ddmmssLatPattern
		// Previous failure case: 29.99999999 --> 29d00'59"N
		//
		// AngleFormat.ddmmssPattern4
		// Previous failure case: 29.99999999 --> 29d00m59s
		//
		// AngleFormat.Test case: decimalPattern
		// Previous failure case: 29.99999999 --> 29.1000000

		double          degrees = isDegrees ? number : Math.toDegrees(number);

		// This is necessary for cases like this: 128.99999999999997
		// The problem occurs when the decimal degrees are within 1e-12 or less
		// of an integer value because case 'F' below will automatically round
		// the decimal degrees to 1.0
		if(Math.abs(Math.round(degrees) - degrees) <= 1e-12)
			degrees = Math.round(degrees);

		int     integer_degrees = (int)Math.floor(degrees);
		double  decimal_degrees = degrees - integer_degrees;

		double          minutes = decimal_degrees * 60.0;
		int     integer_minutes = (int)Math.floor(minutes);
		double  decimal_minutes = minutes - integer_minutes;

		double          seconds = decimal_minutes * 60.0;
		int     integer_seconds = (int)Math.floor(seconds);
//		double  decimal_seconds = seconds - integer_seconds;

		for (int i = 0; i < length; i++) {
			char c = pattern.charAt(i);
			switch (c) {
			case 'R':
				result.append(number);
				break;
			case 'D':
				// This handles the case where the integer degrees equal zero,
				// but the angle is negative
				//
				// AngleFormat.ddmmssPattern
				// Previous failure case: -0.999722 --> 0d59
				//
				// AngleFormat.ddmmssPattern2
				// Previous failure case: -0.999722 --> 0d59'59"
				//
				// AngleFormat.ddmmssPattern4
				// Previous failure case: -0.999722 --> 0d59m59s
				result.append(String.format("%s%d", ( sign == -1 ? "-" : "" ), integer_degrees));
				break;
			case 'M':
				// Ensures two digits are used and avoids rounding errors
				result.append(String.format("%02d", integer_minutes));
				break;
			case 'S':
				// Ensures two digits are used and avoids rounding errors
				result.append(String.format("%02d", integer_seconds));
				break;
			case 'F':
				// Ensures all available decimal digits, up to 12 decimal
				// places, will be output if non-zero
				String string = String.format("%.12f", decimal_degrees);
				int end = string.length();
				for(int j=string.length()-1;j>=3;j--) {
					if(string.charAt(j) == '0')
						end--;
					else
						break;
				}
				result.append(string.substring(2,end));
				break;
			case 'W':
				if (negative)
					result.append(CH_W);
				else
					result.append(CH_E);
				break;
			case 'N':
				if (negative)
					result.append(CH_S);
				else
					result.append(CH_N);
				break;
			default:
				result.append(c);
				break;
			}
		}
		return result;
	}
	
  /**
   * 
   * @param s
   * @return
   * @deprecated
   * @see Angle#parse(String)
   */
	public Number parse(String text, ParsePosition parsePosition) {
		double d = 0, m = 0, s = 0;
		double result;
		boolean negate = false;
		int length = text.length();
		if (length > 0) {
			char c = Character.toUpperCase(text.charAt(length-1));
			switch (c) {
			case 'W':
			case 'S':
				negate = true;
				// Fall into...
			case 'E':
			case 'N':
				text = text.substring(0, length-1);
				break;
			}
		}
		int i = text.indexOf('d');
		if (i == -1)
			i = text.indexOf('\u00b0');
		if (i != -1) {
			String dd = text.substring(0, i);
			String mmss = text.substring(i+1);
			d = Double.valueOf(dd).doubleValue();
			i = mmss.indexOf('m');
			if (i == -1)
				i = mmss.indexOf('\'');
			if (i != -1) {
				if (i != 0) {
					String mm = mmss.substring(0, i);
					m = Double.valueOf(mm).doubleValue();
				}
				if (mmss.endsWith("s") || mmss.endsWith("\""))
					mmss = mmss.substring(0, mmss.length()-1);
				if (i != mmss.length()-1) {
					String ss = mmss.substring(i+1);
					s = Double.valueOf(ss).doubleValue();
				}
				if (m < 0 || m > 59)
					throw new NumberFormatException("Minutes must be between 0 and 59");
				if (s < 0 || s >= 60)
					throw new NumberFormatException("Seconds must be between 0 and 59");
			} else if (i != 0)
				m = Double.valueOf(mmss).doubleValue();
			if (isDegrees)
				result = ProjectionMath.dmsToDeg(d, m, s);
			else
				result = ProjectionMath.dmsToRad(d, m, s);
		} else {
			result = Double.parseDouble(text);
			if (!isDegrees)
				result = Math.toRadians(result);
		}
		if (parsePosition != null)
			parsePosition.setIndex(text.length());
		if (negate)
			result = -result;
		return new Double(result);
	}
}

