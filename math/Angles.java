/*
 c  Angles.java
 c
 c  Copyright (C) 2000, 2008 Kurt Motekew
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

package com.motekew.vse.math;

/**
 * Angle related utilities and constants.
 *
 * @author  Kurt Motekew
 * @since   20080827
 *
 */
public final class Angles {
  public static final double PIO2 = Math.PI/2.0;
  public static final double   PI = Math.PI;
  public static final double  TPI = PI*2.0;
  public static final double RAD_PER_DEG = PI/180.0;
  public static final double DEG_PER_RAD = 1.0/RAD_PER_DEG;
    //
  public static final double ARCSEC_PER_DEG = 3600.0;
  public static final double ARCSEC_PER_RAD = ARCSEC_PER_DEG*DEG_PER_RAD;
  public static final double DEG_PER_HOUR   = 15.0;
  public static final double RAD_PER_HOUR   = DEG_PER_HOUR*RAD_PER_DEG;
  public static final double RAD_PER_SEC    = RAD_PER_HOUR/3600.0;

    // No need to ever instantiate.
  private Angles() {
  }

  /**
   * Sets the value of an angle to be between +/- PI/2
   *
   * @param   ang    The angle to check and possibly correct
   *
   * @return         An angle between -PI/2 and +PI/2
   */
  public static double setPIo2(double ang) {
    if (ang > PIO2) {
      while (ang > PIO2) {
        ang -= PI;
      }
      return(ang);
    } else if (ang < -PIO2) {
      while (ang < -PIO2) {
        ang += PI;
      }
      return(ang);
    } else {
      return ang;
    }
  }
  
  /**
   * Sets the value of an angle to be between +/- PI
   *
   * @param   ang    The angle to check and possibly correct
   *
   * @return         An angle between -PI and +PI
   */
  public static double setPI(double ang) {
    if (ang > PI) {
      while (ang > PI) {
        ang -= TPI;
      }
      return(ang);
    } else if (ang < -PI) {
      while (ang < -PI) {
        ang += TPI;
      }
      return(ang);
    } else {
      return ang;
    }
  }

  /**
   * Sets the value of an angle to be between +/- 2PI.  If the
   * input angle is positive, then the resulting angle will be
   * between 0 and 2PI.  If it is negagive, then the resulting
   * angle will be between -0 and -2PI.
   *
   * @param   ang    The angle to check and possibly correct
   *
   * @return         An angle between -2PI and +2PI
   */
  public static double set2PI(double ang) {
    if (ang > TPI) {
      while (ang > TPI) {
        ang -= TPI;
      }
      return(ang);
    } else if (ang < -TPI) {
      while (ang < -TPI) {
        ang += TPI;
      }
      return(ang);
    } else {
      return ang;
    }
  }  

}
