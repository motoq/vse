/*
 c  TheDate.java
 c
 c  Copyright (C) 2000, 2007 Kurt Motekew
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

/**
 * <code>TheDate</code> class represents a date in the Gregorian calendar.
 * This class offers date arithmetic and Julian Date conversions.
 *
 * @author Kurt Motekew
 * @since  20020105
 */
public class TheDate {
  /*
   * References:
   * 1.  Vallado, David A.  1997.  "Fundamentals Of Astrodynamics and 
   *     Applications".  New York:  McGraw-Hill.
   * 2.  Fliegel, H.F. and van Flandern, T.C. (1968).  Communications of the 
   *     ACM, Vol. 11, No. 10 (October, 1968).
   */

  /*
   * Public static final constants
   */

  /** integer representation of January */
  public static final int JAN = 1;
  /** integer representation of February */
  public static final int FEB = 2;
  /** integer representation of March */
  public static final int MAR = 3;
  /** integer representation of April */
  public static final int APR = 4;
  /** integer representation of May */
  public static final int MAY = 5;
  /** integer representation of June */
  public static final int JUN = 6;
  /** integer representation of July */
  public static final int JUL = 7;
  /** integer representation of August */
  public static final int AUG = 8;
  /** integer representation of September */
  public static final int SEP = 9;
  /** integer representation of October */
  public static final int OCT = 10;
  /** integer representation of November */
  public static final int NOV = 11;
  /** integer representation of December */
  public static final int DEC = 12;

  /*
   * Private static final constants
   */

  /*
   * When the Gregorian calendar came to be - for the most part....
   */
  private static final int GREGYEAR = 1582;

  /*
   * Smallest allowable year due to epoch of Gregorian calendar.
   * No limit on largest year.
   */
  private static final int MINYEAR = GREGYEAR;

  /*
   * Days in each month.  Does not include the extra day for
   * leap year.  The imaginary 0th month exists for a reason.
   * But it is also useful to keep the month conatants in sync
   * with this array... ie... nDaysInMonth[JAN] = 31.
   *
   * Only the month variable or numbers between JAN-1 and
   * DEC should be used to access the elements in this array.
   */
  private static final int nDaysInMonth[] = { 0,
                                              31, 28, 31, 30,
                                              31, 30, 31, 31,
                                              30, 31, 30, 31 };


  /*
   * Cumulative number of days in each month.  Each index marks the
   * number of days up to and including the previous month.  Similar
   * use of 0th month as in nDaysInMinth.
   *
   * Oh yea, doesn't include the extra day for leap year.
   *
   * Only the month variable or numbers between JAN-1 and
   * DEC should be used to access the elements in this array.
   */
  /* _not_needed_now_
  private static final int cumDaysInMonth[] = {0,
                                               31,   59,  90, 120,
                                               151, 181, 212, 243,
                                               273, 304, 334, 365  };
  */


  /*
   * Private internal variables
   */

  /*
   * Gregorian Date format:  year, month, day
   *
   * The following three variables, and the Julian Date discussed below,
   * are the primary values used to keep track of the date.  All other date 
   * forms should be calculated from these values, with the exception of those
   * times the <code>TheDate</code> class is initialized or modifed with a 
   * value of a different form (say, YYYYDDD).  In that case, the initialization
   * routine must be sure to update all four of these values.
   *
   * NOTE:  throughout the code, this form of year, month, and day is
   * referred to as ymd in method calls.
   */
  private int year;     // YYYY
  private int month;    // mm, JAN-> DEC (1-12)
  private int day;      // 0->28,29,30,31

  /*
   * Julian Date format:  jd
   *
   * The Julian Date is to be kept in sync with the year, month, and day
   * values.  Any time year, month, or day is updated, so should jd.
   * This is because the transformation between the Gregorian format, and
   * the Julian Date format is computationally intensive.  All other forms
   * of the date will be computed on the fly using either the Gregorian
   * format or the Julian Date.
   *
   * The convention will be to update both the Gregorian form and the
   * Julian Date form in the same code block.
   */
  private int jd;        // Julian Date at noon of the current day.


  /*
   * Constructors
   */

  /**
   * Initializes <code>TheDate</code> to October 4th, 1957.  It's up to you to 
   * figure out why this date is important.
   */
  public TheDate() {
    year  = 1957;
    month = OCT;
    day   = 4;
    ymdToJD();
  }

  /**
   * Initializes the <code>TheDate</code> to the given year, month, and day.
   *
   * @param yearIn  <code>integer</code> representation of the year in YYYY
   *                format.
   * @param monthIn  <code>integer</code> representation of the month in MM
   *                 format (values of JAN->DEC or 1->12)
   * @param dayIn   <code>integer</code> representation of the day in DD
   *                 format (values of 1->(28)(29)(30)31)
   *
   * @throws BadDateException if the <code>yearIn</code>, <code>monthIn</code>
   *                          <code>dayIn</code> values do not make sense.  For
   *                          instance, Feb 29, 2002.
   */
  public TheDate(int yearIn, int monthIn, int dayIn) throws BadDateException {
    try {
      setYearMonthDay(yearIn, monthIn, dayIn);
    } catch(BadDateException bde) {
      throw(bde);
    }
  }


  /*
   * public accessor methods.
   */

  /**
   * Update <code>TheDate</code> with the <code>year</code>, <code>month</code>,
   * and <code>day</code>.
   *
   * @param yearIn  <code>integer</code> representation of the year in YYYY
   *                format.
   * @param monthIn  <code>integer</code> representation of the month in MM
   *                 format (values of JAN->DEC or 1->12)
   * @param dayIn   <code>integer</code> representation of the day in DD
   *                 format (values of 1->(28)(29)(30)31)
   *
   * @throws BadDateException if the <code>yearIn</code>, <code>monthIn</code>
   *                          <code>dayIn</code> values do not make sense.  For
   *                          instance, Feb 29, 2002.
   */
  public void setYearMonthDay(int yearIn, int monthIn, int dayIn)
                                                throws BadDateException {
    year  = yearIn;
    month = monthIn;
    day   = dayIn;
    if (!looksLikeDate()) {
      throw(new BadDateException("TheDate.TheDate(y, m, d)" + 
                                  year + month + day));
    }
    ymdToJD();
  }

  /**
   * Update <code>TheDate</code> with a date in the form of Julian Date.
   *
   * @param jdIn <code>int</code> values of the date in Julian date format.
   *
   * @throws BadDateException
   */
  public void setJD(int jdIn) throws BadDateException {
    jd = jdIn;
    jdToYmd();
    if (jd < 0  ||  !looksLikeDate()) {
      throw(new BadDateException("TheDate.setJD(jdIn) " + jdIn));
    }
  }

  /**
   * Return the Julian date representation.  The integer value is
   * at noon of the given date.  Subtract 0.5 to get the JD for the
   * beginning of the day.
   *
   * @return <code>double</code> value of the Julian Date.
   */
  public int getJD() {
    return jd;
  }

  /**
   * Update <code>TheDate</code> with a date in the form of YYYYMMDD.
   *
   * @param yyyymmddIn <code>integer</code> values of the date in
   *                   YYYYMMDD format.
   *
   * @throws BadDateException
   */
  public void setYYYYMMDD(int yyyymmddIn) throws BadDateException {
    try {
      yyyymmddToYmd(yyyymmddIn);
    } catch(BadDateException bde) {
      throw(bde);
    }
  }

  /**
   * Update <code>TheDate</code> with a <code>String</code> with text
   * in the form of YYYYMMDD.
   *
   * @param dateStr <code>String</code> value of the date in
   *                   YYYYMMDD format.
   *
   * @throws BadDateException
   */
  public void setYYYYMMDD(String dateStr) throws BadDateException {
    try {
      int yyyymmddIn = Integer.parseInt(dateStr);
      yyyymmddToYmd(yyyymmddIn);
    } catch(NumberFormatException nfe) {
      throw(new BadDateException("TheDate.setYYYYMMDD(dateStr)" + dateStr));
    } catch(BadDateException bde) {
      throw(bde);
    }
  }


  /*
   *  Public accessor methods that return the date in a format
   *  that needs to be computed from either the internally stored
   *  Gregorian or Julian Date formats.
   */

  /**
   * Return the year, month, and day in YYYYMMDD format
   *
   * @return <code>integer</code> value of the date in YYYYMMDD
   *         format
   */
  public int getYYYYMMDD() {
    return (year*10000 + month*100 + day);
  }


  /*
   * Other public methods (static and instance specific).
   */

  /**
   * Adds the given number of days to the date.
   *
   * @param <code>integer</code> number of days to add to the current date.
   */
  public void addDays(int ndays) {
    jd += ndays;
    jdToYmd();
  }

  /**
   * Subtracts the given number of days from the date.
   *
   * @param <code>integer</code> number of days to subtract to the current date.
   */
  public void subtractDays(int ndays) {
    addDays(-ndays);
  }

  /**
   * Determines the number of days between this date and the entered date.
   * It is computed as this date minus the date entered to be subracted.
   *
   * @param <code>TheDate</code> to subtract from this date.
   */
  public int deltaDays(TheDate otherDate) {
    return(jd - otherDate.getJD());
  }

  /**
   * Returns a string representation of the object.  Essentially
   * just the string of YYYYMMDD.
   */
  public String toString() {
    return ("" + getYYYYMMDD());
  }

  /*
   * Determins if the given year is a leapyear.  The method used to determine
   * this is as follows.  If the year is divisible by 4 but not divisible by
   * 100 then the year is a leap year.  If the year is divisible by 4, and
   * divisible by 100, then it is a leap year only if it is also divisible by 
   * 400.  This method should be valid from the year 1582 forward since that is
   * when Pope Gregory XIII decided that this would be the method used to 
   * determie leap years as opposed to the previous method that only required 
   * the year be divisible by 4.  If the year is before 1582 then the year will
   * be considered a leap year if it is divisible by 4.  Not sure how far back 
   * that will work, but hey, nothing important happended back then.
   */
  private boolean isLeapYear() {
    boolean leap = false;

    if (year < GREGYEAR) {
      if (year%4 == 0) {
        leap = true;
      }
    } else {
      if (year%4 == 0) {
        if (year%100 == 0) {
          if (year%400 == 0) {
            leap = true;
          }
        } else {
          leap = true;
        }
      }
   }
    return leap;
  }


  /*
   * private methods
   */

  /*
   * This method looks at the year, month, and date values to determine if the
   * date appears to be valid.  These values should always be up to date.
   *
   * The month and day is pretty straight forward.  Currently, as far as the 
   * year goes, it only checks to make sure it is greater than 
   * <code>MINYEAR</code>.
   *
   * Note that the checking of the year month day is done in a specific order 
   * for a reason.  First, make sure the year makes sense.  Then determine if 
   * the month makes sense.  Then if it is FEB, check to see if it is a leap 
   * year.  Then, using the leapyear offset, and knowing that the month value 
   * is OK (as in not out of bounds of the month array) we can determine the 
   * number of days for that month, and make sure the given month makes sense.
   * If all methods that set the <code>year</code>, <code>month</code>, and 
   * <code>date</code> values call this method and pass, then there should be 
   * no worries about using <code>month</code> to access elements of 
   * <code>nDaysInMonth</code>.
   */
  private boolean looksLikeDate() {
    boolean looksGood = true;

    if (year < MINYEAR) {
      looksGood = false;
    } else if (month < JAN  ||  month > DEC) {
        looksGood = false;
    } else {
      int offset = 0;
      int dim = 0;

      if (month == FEB  &&  isLeapYear()) {
        offset = 1;
      }
      dim = nDaysInMonth[month] + offset;
      if (day < 1  || day > dim) {
        looksGood = false;
      }
    }

    return looksGood;
  }

  /*
   * Updates the year, month, and day using the date in
   * yyyymmdd format (YYYYMMDD to YYYY, MM, DD).  Check that
   * the date looks good, then update the Julian Date.
   */
  private void yyyymmddToYmd(int yyyymmddIn) throws BadDateException {
    int itmp;

    year = yyyymmddIn / 10000;
    itmp = yyyymmddIn - (year*10000);

    month = itmp / 100;

    day = itmp - (month*100);

    if (!looksLikeDate()) {
      throw(new BadDateException("TheDate.yyyymmddToYmd(yyyymmddIn)" 
                                 + yyyymmddIn));
    }

    ymdToJD();  // Snyc up jd
  }

  /*
   * Calculates the Julan Date jd from the year, month, and day values.
   * This formula comes from the U.S. Naval Observatory website, the
   * astronomical Applications Department.  They got the formula from the
   * Fliegel reference.  It should be valid for any  dates that result in 
   * a Julian date greater than zero.
   *
   * Notice all the integer arithmethic.  This truncation is taken advantage of
   * to get the right answers.
   */
  private void ymdToJD() {

    jd = day - 32075 
             + 1461*(year+4800+(month-14)/12)/4+367*(month-2-(month-14)/12*12)
             / 12-3*((year+4900+(month-14)/12)/100)/4;
  }

  /*
   * Takes the jd and updates the year, month, day.
   * This formula comes from the U.S. Naval Observatory website, the
   * astronomical Applications Department.  They got the formula from the
   * Fliegel reference.
   */
  private void jdToYmd() {

    int i, j, k, m, n;

    m = jd+68569;
    n = 4*m/146097;
    m = m-(146097*n+3)/4;
    i = 4000*(m+1)/1461001;
    m = m-1461*i/4+31;
    j = 80*m/2447;
    k = m-2447*j/80;
    m = j/11;
    j = j+2-12*m;
    i = 100*(n-49)+i+m;

    year  = i;
    month = j;
    day   = k;
  }

  /*
   * Test Code
   */
  public static void main(String[] args) {
    TheDate td = null;
    TheDate td2 = null;
    try {
      td = new TheDate(1996, 10, 26);
      td2 = new TheDate(2015, 8, 17);
    } catch(BadDateException bde) {
      System.out.println("" + bde);
      return;
    }
    System.out.println("td:  " + td);
    System.out.println("td2:  " + td2);
    System.out.println("td.getJD:  " + td.getJD());
    System.out.println("td2.getJD:  " + td2.getJD());
    try {
      td.setJD(td2.getJD());
      td2.setJD(td.getJD());
    } catch(BadDateException bde) {
      System.out.println("" + bde);
      System.out.println("" + bde);
    }
    System.out.println("Now Switch td and td2 using JD values");
    System.out.println("td:  " + td);
    System.out.println("td2:  " + td2);
    System.out.println("td - td2:  " + (td.deltaDays(td2)));
  }
}
