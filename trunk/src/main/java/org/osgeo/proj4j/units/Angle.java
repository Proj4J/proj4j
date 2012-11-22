package org.osgeo.proj4j.units;

import org.osgeo.proj4j.util.ProjectionMath;

public class Angle
{
  /**
   * Parses a text representation of a degree angle in various formats.
   *  
   * @param text
   * @return the value of the angle, in degrees
   */
  public static double parse(String text) 
    throws NumberFormatException
  {
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
        if (mmss.length() == 0)
          m = 0;
        else
          m = Double.valueOf(mmss).doubleValue();
        result = ProjectionMath.dmsToDeg(d, m, s);
    } else {
      result = Double.parseDouble(text);
    }
    if (negate)
      result = -result;
    return result;
  }

}
