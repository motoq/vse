/*
 c  TimeOfDay.java
 c
 c  Copyright (C) 2000, 2013 Kurt Motekew
 c
 c  This library is free software; you can redistribute it and/or
 c  modify it under the terms of the GNU Lesser General Public
 c  License as published by the Free Software Foundation; either
 c  version 2.1 of the License, or (at your option) any later version.
 c
 c  This library is distributed in the hope that it will be useful,
 c  but WITHOUT ANY WARRANTY; without even the implied warranty of
 c  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 c  Lesser General Public License for more details.
 c
 c  You should have received a copy of the GNU Lesser General Public
 c  License along with this library; if not, write to the Free Software
 c  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 c  02110-1301 USA
 */

package com.motekew.vse.envrm;

import java.util.*;
import java.text.*;

/**
 * The class <code>TimeOfDay</code> is a class that represents the time of day.
 *
 * @author Kurt Motekew
 * @since  20010308
 */

 /*
  * The time is stored using days, hours, minutes, seconds, and total
  * seconds.  If useDays is true, then the days variable may be nonzero, and 
  * the hh variable will be less than 24.  mm will range between zero and
  * 59 inclusive.  Seconds will be less than 60.  days, hh, mm, ss will
  * always be zero or greater.  Only ss will contain a frational value
  * days, hh, mm, are double values as a convenience, but will carry no 
  * fractional part.  Hopefully truncation error will not play too much 
  * of an issue here.
  */

public class TimeOfDay {

  /*
   * Is the time represented by days, hh, mm, ss positive or
   * negative?
   */
  private static final int POSITIVE =  1;
  private static final int NEGATIVE = -1;

    //  days formatter
  private static final DecimalFormat df0 = new DecimalFormat("0");
    //  hours, minutes formatter
  private static final DecimalFormat df1 = new DecimalFormat("00");
    //  seconds formatter
  private static final DecimalFormat df2 = new DecimalFormat("00.00");

  /*
   * Indicates if the days value should be used If true, days will 
   * aways be zero and hh can equal or exceed 24.
   */
  private boolean useDays = true;

  /*
   * Positive or negative time (forward or backward).  It will always
   * match the sign of totSec.
   */
  private int    sign = POSITIVE;

  /*
   * Time in days.  Always zero if useDays is false.
   */
  private double days = 0.0;

  private double hh;   // hours:  useDays ? (0 <= hh < 24) : (hh >= 0)
  private double mm;   // minutes:  0 <= mm < 60
  private double ss;   // seconds:  0 <= ss < 60   (can have a fractional part)

  /*
   * Total time, represented in seconds.  The sign of totSec will match
   * that of sign.
   */
  private double totSec;


  /*
   * Constructors
   */

  /**
   * Initialize with time set to QUADZERO.
   */
  public TimeOfDay() {
    this(0.0, 0.0, 0.0);
  }

  /**
   * Calls <code>TimeOfDay</code> with the day value set to
   * zero.
   *
   * @param hh0 hour
   * @param mm0 minute
   * @param ss0 second
   */
  public TimeOfDay(double hh0, double mm0, double ss0) {
    this(0.0, hh0, mm0, ss0);
  }

  /**
   * Calls <code>setTime</code> with provided values.
   *
   * @param days0  day
   * @param hh0    hour
   * @param mm0    minute
   * @param ss0    second
   */
  public TimeOfDay(double days0, double hh0, double mm0, double ss0) {
    setTime(days0, hh0, mm0, ss0);
  }

  /**
   * Initialize the time with the given string representation
   * of a time.
   *
   * @param str Time in a string format.  Acceptable formats are:
   *            HHMMSS, HHMM, HH:MM:SS, HH:MM, and MM.  The previous 
   *            examples may also be preceeded by a number of days
   *            and a blank (ex:  dd hhmmss).
   */
  public TimeOfDay(String str) throws BadTimeException {
    try {
      setTime(str);
    } catch(BadTimeException bte) {
      throw bte;
    }
  }


  /*
   * Public accessor methods.
   */

  /**
   * Sets the time of day given a total time in seconds.
   *
   * @param  seconds  Total seconds, positive or negative
   */
  public void setTime(double seconds) {
    totSec = seconds;
    totSec2time();
  }

  /**
   * Calls <code>setTime</code> with a value of zero for days.
   *
   * @param hh0    hour
   * @param mm0    minute
   * @param ss0    second
   */
  public void setTime(double hh0, double mm0, double ss0) {
    setTime(0.0, hh0, mm0, ss0);
  }

  /**
   * Sets the time.  Inputs can be any value, positive or negative.
   * If any values are negative, then the entire time period will be
   * considered negative.
   *
   * @param days0  day
   * @param hh0    hour
   * @param mm0    minute
   * @param ss0    second
   */
  public void setTime(double days0, double hh0, double mm0, double ss0) {

    days = days0;
    hh   = hh0;
    mm   = mm0;
    ss   = ss0;
    updateTime();
  }

  /**
   * Set the time with the given string representation of a time.
   *
   * @param str Time in a string format.  Acceptable formats are:
   *            HHMMSS, HHMM, HH:MM:SS, HH:MM, and MM.  The previous 
   *            examples may also be preceeded by a number of days
   *            and a blank (ex:  dd hhmmss).  In addition, the HH,
   *            MM, and SS fields may contain frational values, or
   *            values greater than 59 if ':'s are used to separate
   *            the values.
   *
   *            Negative signs are also parsed and can preceed any
   *            value (day, hour, minute, second).  A single negative
   *            will cause the entire value to be negative.  It would
   *            make the most sense for this value to preceed the first
   *            entry.
   */
  public void setTime(String str) throws BadTimeException {
    days = 0.0;
    hh   = 0.0;
    mm   = 0.0;
    ss   = 0.0;

    StringTokenizer stok = new StringTokenizer(str, " ");
    int             ntok = stok.countTokens();

    if (ntok < 1 || ntok > 2) {
      throw new BadTimeException("Wrong number of TimeOfDay Fields");
    }

    try {
      String parseStr = stok.nextToken();

         // check for a day field
      if (ntok == 2) {
        days = Double.parseDouble(parseStr);
        parseStr = stok.nextToken();
      }

         // now for the time field
      stok = new StringTokenizer(parseStr, ":");
      ntok = stok.countTokens();
      if (ntok == 1) {           // HHMMSS or HHMM or MM
        double time = Double.parseDouble(stok.nextToken());
        if (time > 5959.0  ||  time < -5959.0) {     // HHMMSS
          setHHMMSS(time);
        } else {                   // HHMM or MM
          setHHMM(time);
        }
      } else if (ntok > 1) {    // Get HH:MM
        hh = Double.parseDouble(stok.nextToken());
        mm = Double.parseDouble(stok.nextToken());
      }

         // Get :SS if there, ignore any other fields
      if (ntok > 2) {
        ss = Double.parseDouble(stok.nextToken());
      }

    } catch(NoSuchElementException nsee) {
      throw new BadTimeException("NoSuchElementException");
    } catch(NumberFormatException nfe) {
      throw new BadTimeException("NumberFormatException");
    }

    updateTime();  //  Very important to do!!!

  }

  /**
   * Toggle between using days for hours of 24 or more.
   *
   * @param option If true, use days.  Otherwise, store time
   *               cumulatively in hours
   */
  public void setUseDays(boolean option) {

    useDays = option;

    if (useDays) {
      int ihh   = (int) hh;
      int idays = (int) days;
      if (ihh >= 24) {
        idays = ihh / 24;
        days = idays;
        hh -= idays*24;
      }
    } else {
      if (days > 0.0) {    // days will always be positive
        hh += days*24.0;   // safer than checking for "== 0"
        days = 0.0;
      }
    }
  }

  /**
   * @return   Seconds into the day
   */
  public double seconds() {
    return totSec;
  }

  /**
   * @return   Minutes into the day
   */
  public double minutes() {
    return totSec/60.0;
  }

  /**
   * @return   Hours into the day
   */
  public double hours() {
    return totSec/3600.0;
  }

  /**
   * @return   Days
   */
  public double days() {
    return totSec/86400.0;
  }

  /**
   * Prints a string representation of TimeOfDay
   */
  public String toString() {
    String retStr = "";

       // only print the sign if negative.
    if (sign == NEGATIVE) {
      retStr += "-";
    }
       // do we need to print out the number of days?
    if (useDays  &&  days > 0.0) {
      retStr = retStr + df0.format(days) + " ";
    }

       // now finally print the HH:MM:SS.00 part
    retStr = retStr + df1.format(hh) +
                ":" + df1.format(mm) +
                ":" + df2.format(ss);

    return retStr;
  }


  /*
   * private methods
   */

  /*
   * NOTE that updateTime and totSec2time are closely related.
   * Some safety has been built into them, but they should both
   * be reviewed when modifications are made to either one.
   */

  /*
   * When the hours, minutes, and/or seconds have been changed,
   * this routine reformats them if necessary and updates the
   * total time variable totSec.
   *
   * The routine first checks to see if any values are negative.
   * If a single value is negative, then the entire time will
   * be considered negative and the <code>sign</code> of the time
   * will be changed to NEGATIVE.  Otherwise it will be set to
   * POSITIVE.  This is important to keep in mind when modifications
   * are being made to the time.  If the original time is negative,
   * then the new time component must be negative, or the new time
   * component will be positive.
   */
  private void updateTime() {
    if (days < 0.0 || hh < 0.0 || mm < 0.0 || ss < 0) {
      sign = NEGATIVE;
    } else {
      sign = POSITIVE;
    }
    if (days < 0.0) { days *= -1.0; }
    if (hh   < 0.0) { hh   *= -1.0; }
    if (mm   < 0.0) { mm   *= -1.0; }
    if (ss   < 0.0) { ss   *= -1.0; }

    //  First update the totSec time variable
    totSec = ss + 60.0*mm + 3600.0*hh + 86400.0*days;
    totSec *= sign;
    //  Then update days, hh, mm, ss
    totSec2time();
  }

  /*
   * Update the hours, minutes, and seconds values given a time in
   * seconds.  Also calls member function to update the hour format.
   *
   * member variables affected:  h, mm, ss, days, sign
   *
   * This function sets the sign variable and days values.  If this
   * method is called directly after updateTime, then the update to
   * sign is unecessary.  However, totSec2time is written to be a
   * standalone routine to be used for updating the time given the
   * total number of seconds.  It might make sense to just turn it
   * into a public accessor method - but I figured I'd separate out
   * the guts into a private method.
   */
  private void totSec2time() {
    double dtmp  = 0.0;
    int    idays = 0;
    int    ihh;

    double stime = totSec;

    if (stime < 0.0) {
      sign = NEGATIVE;
      stime *= -1.0;
    } else {
      sign = POSITIVE;
    }

       // Determine hours, minutes, and seconds.
    hh   = (double) (((int) stime)/3600);
    dtmp = stime - hh*3600.0;
    mm   = (double) (((int) dtmp)/60);
    ss   = dtmp - mm*60.0;

       // Set days field depending on useDays
    days = 0.0;
    ihh = (int) hh;

    if (useDays) {
      if (ihh >= 24) {
        idays = ihh / 24;
        days = idays;
        hh -= idays*24;
      }
    }
  }

  /*
   * The setHHMM member function attempts to set the hh and mm values and the
   * setHHMMSS member function attempts to set the hh, mm, and ss values using
   * a single input that should be in either hhmm or hhmmss format.  No error
   * checking is done.  Essentially, this tries it's best to fit the double into
   * the hh, mm, and ss slots.
   *
   * If the input time is negative, then the computed hour will be set to a
   * negative value.  This preserves the fact that the incomming time was
   * less than zero.
   *
   * Note that these methods exists as a convenience.  They should not be called
   * by themselves.  Anytime hh or mm, or ss are changed, updateTime() must be
   * called.  In othe words, any routine that calls these functions should 
   * also call updateTime() some time afterwards.
   *
   * Also note, the use of the term "attempt" in the above description implies
   * just that.  This routine is only called when a user enters a value and
   * decided to leave out the ":" delimiter.  The software then has to guess
   * as to what the user wants.  Hopefully, this method of entry will only
   * be used in an interactive fashion with the user so immediate feedback
   * can be given alerting the user to an incorrectly misinterpreted time.
   */
  private void setHHMM(double hhmm) {
    boolean negate = false;

    if (hhmm < 0.0) {
      hhmm *= -1.0;
      negate = true;
    }

    hh = (double) (((int) hhmm)/100);
    mm = hhmm - hh*100.0;

    if (negate) {
      hh *= -1.0;
    }
  }
  private void setHHMMSS(double hhmmss) {
    boolean negate = false;

    double dmmss;

    if (hhmmss < 0.0) {
      hhmmss *= -1.0;
      negate = true;
    }

    hh = (double) (((int) hhmmss)/10000);
    dmmss = hhmmss - hh*10000.0;
    mm = (double) (((int) dmmss)/100);
    ss = dmmss - mm*100.0;

    if (negate) {
      hh *= -1.0;
    }
  }

  /*
   * com.motekew.vse.test code
   */
  public static void main(String[] args) {
    TimeOfDay tod = null;
    TimeOfDay tod2 = null;
    try {
      tod = new TimeOfDay("3 05:32:15");
      tod2 = new TimeOfDay("-3 05:32:15");
    } catch(BadTimeException bte) {
      System.out.println("" + bte);
      return;
    }
    System.out.println("tod:  " + tod);
    System.out.println("tod2:  " + tod2);
  }

}
