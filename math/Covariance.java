/*
 c  Covariance.java
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
 * This <code>Matrix</code> extension accesses the covariance from
 * objects in this package that solve for unknown parameters.  It
 * must be initialized to a fixed size.  Any supported estimators
 * passed in must solve for the same number of parameters.
 */
public class Covariance extends Matrix {
  private final int NP;

  /**
   * @param  numSolveFor   Size of the covariance to initialize
   */
  public Covariance(int n) {
    super(n);
    NP = n;
  }

  /**
   * An estimator of type <code>SysSolverBD</code> from which to
   * extract covariance.
   */
  public void set(SysSolverBD ss) {
    ss.covariance(this);
  }

  /**
   * @return   The number of parameters represented by this
   *           covariance.
   */
  public int numSolveFor() {
    return NP;
  }
}
