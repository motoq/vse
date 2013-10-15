/*
 c  JulianDate.java
 c
 c  Copyright (C) 2012 Kurt Motekew
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
 * <code>JulianDate</code> class represents a Julian Date.  The Julian
 * date is stored internally as two numbers (the Julian Date and the
 * fraction of the day) for maximum precision in locating an instant
 * in time.  Convenience functions allow for easy adding, subtracting
 * and bookeeping.
 *
 * @author Kurt Motekew
 * @since  20121108
 */
public class JulianDate {
    /** Jan 1, 2000 12:00:00 TT  */
  public static final double J2000 = 2451545.0;
    /** Jan 1, 1900 12:00:00 UT1 */
  public static final double J1900 = 2415021.0;
    /** Jan 6, 1980 00:00:00 UT  */
  public static final double GPS0  = 2444244.5;
    /** Subtract from JD to get MJD */
  public static final double MJD   = 2400000.5;

    // The full Julian Date = jdhi + jdlow
    // jd does not need to be 00:00:00 (ending in 0.5) or 12:00:00,
    // although time increments should probably be handled through
    // updates to jdlow.
  private double jdHi;              // Julian Date
  private double jdLo;              // Fraction of the Day

  /**
   * Default constructor - initializes to J2000 epoch
   */
  public JulianDate() {
    jdHi = J2000;
  }

  /**
   * Initializes this Julian Date class given a date and time
   * of day.
   *
   * @param   gregDate   Date
   * @param   tod        Time of day
   */
  public JulianDate(TheDate gregDate, TimeOfDay tod) {
      // Remember to convert from noon to QUADZERO
    jdHi = (double) gregDate.getJD() - 0.5;
    jdLo = tod.days();
  }

    /**
     * @return   The full Julian Date in a single double.  Time can
     *           be reliably represented down to 100 ms.
     */
  public double getJD() {
     return jdHi + jdLo;
  }

  /**
   * @param   jdNew     Set this Julian Date equal to the input JD.
   */
  public void set(JulianDate jdNew) {
    jdHi = jdNew.getJDHi();
    jdLo = jdNew.getJDLo();
  }

  /**
   * @param   jdHiLo    Set the full Julian Date with a single double.
   *                    Time can only be reliably represented down to
   *                    100 ms (the fraction of the day is set to zero
   *                    with this method).
   */
  public void setJD(double jdHiLo) {
    jdHi = jdHiLo;
    jdLo = 0.0;
  }

    /** @return   The large portion of the Julian Date */
  public double getJDHi() { return jdHi;  }

    /** @param   jd1   Sets the large portion of the Julian Date */
  public void setJDHi(double jd1) { jdHi  = jd1; }

    /** @return   The smaller portion of the JD */
  public double getJDLo() { return jdLo; }

    /** @param   jd2   Sets  the smaller portion of the Jd */
  public void setJDLo(double jd2) { jdLo = jd2; }

  /** @return   Modified Julian Date */
  public double getMJD() { return  (jdHi - MJD) + jdLo; }

  /**
   * Sets this JD given the input MJD, preserving as much precision as
   * possible.
   *
   * @iparam   mjd   Modified Julian Date
   */
  public void setMJD(double mjd) {
    int imjd = (int) mjd;

    jdLo = mjd - (double) imjd - 0.5;
    jdHi  = MJD + (double) imjd;
  }

  /**
   * Resets the internal JD values such that the high value is a
   * whole day (noon of date).
   */
  public void normalize() {
    double tmp = Math.floor(jdHi);

    jdLo += (jdHi - tmp);
    jdHi = tmp;

    while (jdLo >= 1.0) {
      jdLo = jdLo - 1.0;
      jdHi = jdHi + 1.0;
    }
    while (jdLo < 0.0) {
      jdLo = jdLo + 1.0;
      jdHi = jdHi - 1.0;
    }
  }

  /**
   * Returns the difference, in days, between this and another Julian Date.
   *
   * @iparam   jdb   Julian Date to subtract from this
   *
   * @oparam         this - jdb
   */
  public double minus(JulianDate jdb) {
    return (jdHi - jdb.getJDHi()) + (jdLo - jdb.getJDLo());
  }
}
