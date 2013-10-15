/*
 c  NR_RootFinder.java
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

package com.motekew.vse.intxm;

/**
 * <code>NR_RootFinder</code> implements the Newton-Rapson method
 * to find roots of equations of one variable.
 *
 * @author  Kurt Motekew
 * @since   20000709
 *
 */

public class NR_RootFinder implements IRootFinder {

  /**
   * Integer representing maximum number of allowed iterations.  If a
   * solution is not found within this number of iterations, a 
   * NonConvergenceException will be thrown.
   */
  public static final int MAXITR = 100;

  private FofXable  fx = null;
  private FofXable dfx = null;
  private double   tol = 0.01;     // set a default tolerance

  /**
   * Initialize <code>NR_RootFinder</code>.
   *
   * @param funct      <code>FofXable</code> fuction representing
   *                   f(x), the function for which the root is to
   *                   be found.
   * @param dfunct     <code>FofXable</code> fuction representing
   *                   the derivative of the function for which the
   *                   root is to be found.
   * @param tolerance  <code>double</code> abs(x-x0) which must be met to
   *                   satisfy convergence.  If the entered tolerance is
   *                   <= 0, then the default tolerance of 0.01 will be used.
   */
  public NR_RootFinder(FofXable funct, FofXable dfunct, double tolerance) {
    fx  = funct;
    dfx = dfunct;
    if (tolerance > 0.0) {
      tol = tolerance;
    }
  }

  /**
   * Compute the root of the function given a guess.
   *
   * @param guess      <code>double</code> starting value for the root-
   *                   finder.  Initial guess.
   * @return           <code>double</code> If an exception is not thrown,
   *                   the computed root:  x such that f(x) = 0
   * @throws           <code>NonConvergenceException</code>
   */
  @Override
  public double solve(double guess) throws NonConvergenceException {

    int i;
    double  x0;              // previous estimated value of x
    double  x;               // new estimate of x
    double  correction;      // update to x
    boolean badnumber;
    double  err;

    badnumber = false;
    err = 1.1*tol;        // force initial change to be greater than the tol
    x0 = guess;
    x  = guess;
    i = 0;
    do {
      x0 = x;                        // update guess value
      correction = fx.f(x0)/dfx.f(x0);

      if (Double.isNaN(correction)) {
        badnumber = true;
        i = MAXITR;
      } else {
        i++;
        x = x0 - correction;
        err = Math.abs(x - x0);
      }
    } while (err > tol  &&  i < MAXITR);

      /* check for lack of convergence */
    if (badnumber) {
      throw new NonConvergenceException("Non Convergence due to NaN");
    } else if (err > tol) {
      throw new NonConvergenceException("Non Convergence due to MAXITR");
    }

    return x;
  } 
}
