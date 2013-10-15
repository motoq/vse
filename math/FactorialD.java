/*
 c  FactorialD.java
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

package com.motekew.vse.math;

/**
 * This class computes the factorial of non-negative integers:
 * <P>
 * n! = n * (n-1) * (n-2) * ... * 1
 * <P>
 * where 0! = 1 and 1! = 1.
 * <P>
 * Upon instantiation, space is allocated to hold values up to
 * a desired magnitude.  These values are then computed and stored.
 * requests for values in excess of the maximum will return 0.
 * Factorial values are stored as doubles - they will obviously not
 * be correct to full precision with for large values.
 *
 * @author   Kurt Motekew
 * @since    20120204
 */
public class FactorialD {
  /** Maximum allowable double precision factorial, IEEE */
  public static final int MAXN = 170;
  
  private final int N;

  private double[] fvals;

  /**
   * @param   nmax   Maximum value for which the factorial can be computed
   *                 by this object.  MAXN is the maximum allowable.  For
   *                 values less than 1 and greater than MAXN, MAXN will
   *                 be stored.
   */
  public FactorialD(int nmax) {
    if (nmax < 1  ||  nmax > MAXN) {
      N = MAXN;
    } else {
      N = nmax;
    }

    fvals = new double[N+1];
    fvals[0] = 1.;
    fvals[1] = 1.;
    for (int ii=2; ii<=N; ii++) {
      fvals[ii] = ii*fvals[ii-1];
    }
  }

  /**
   * @return    The size of the factorial argument.
   */
  public int getMaxFactorialArg() {
    return N;
  }

  /**
   * @param   x     The number for which the factorial is to be computed.
   *                If it is greater than the maximum allowable size, or
   *                less than zero, then a 0 will be returned.
   *
   * @return        if (0 >= x <= N), x!
   */
  public double get(int x) {
    if (x < 0  ||  x > N) {
      return 0.;
    } else {
      return fvals[x];
    }
  }

}
