/*
 c  NumberUtil.java
 c
 c  Copyright (C) 2013 Kurt Motekew
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
 * Number utilitiy functions
 *
 * @author  Kurt Motekew
 * @since   20131210
 *
 */
public final class NumberUtil {

  /**
   * Given a double, zeros all portions of the fraction past a
   * given number of digits.  Can also completely truncate the
   * fraction or zero portions of the integer part of the number.
   *
   * @param   fullVal   The number to be truncated
   * @param   digits    The number of fractional digits to retain.
   *                    If zero, the integer portion is retained.
   *                    If negative, the number of integer digits,
   *                    right to left, to zero.
   */
  public static double truncate(double fullVal, int digits) {
    double factor = Math.pow(10.0, (double) digits);
    int itmp = (int) (factor * fullVal);

    return ((double) itmp)/factor;
  }
}
