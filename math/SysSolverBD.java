/*
 c  SysSolverBD.java
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
 * This class aids in the solution to systems of linear equations of the
 * form:
 * <P>
 *   x = (A'WA)^-1 A'Wy
 * <P>
 * where the WLS solver is used in an accumulation mode.  When errors
 * between measurement sets are uncorrelated, the measurement weighting
 * matrix becomes block diagonal, allowing measurements to be
 * accumulated.  Cholesky decomposition method is used.
 *
 * The class is initialized with the number of measurements per measurement
 * set and the number of solve for parameters.  After initialization,
 * the accumulate() method is used to add observations.  The number of
 * measurements per set and solve for parameters must remain the same.  The
 * solve() method will then output the solution.
 *
 * There are two accumulate methods.  One is for the weighted least squares
 * approach and the other is for a standard least squares solution.  It is
 * probably best to only switch between accumulation methods between calls
 * to reset().
 *
 * Be sure to call reset() when used in an iterative least squares solution.
 *
 * @author   Kurt Motekew
 * @since    20130516
 */
public class SysSolverBD {
  private final int NP;          // Number of solve for parameters

    // Individual measurement set - memory retained for efficiency
  private final Matrix at;
  private final Matrix atw;
  private final Matrix atwa;
  private final Matrix infoM;
  private final Tuple atwy;
    // Accumulated normal equations
  private final Matrix satwa;
  private final Tuple satwy;

  /**
   * Initialize this solver with the Jacobian dimensions.
   *
   * @param   nY   The number of measurements per measurement set
   * @param   nP   The number of solve for parameters
   */
  public SysSolverBD(int nY, int nP) {
    NP = nP;
      // Update cached values
    at   = new Matrix(NP, nY);
    atw  = new Matrix(NP, nY);
    atwa = new Matrix(NP);
    atwy = new Tuple(NP);
      // Update accumulated normal equations
    infoM = new Matrix(NP);
    satwa = new Matrix(NP);
    satwy = new Tuple(NP);
  }

  /**
   * Add a measurement set to the accumulated normal equations.
   *
   * @param   aMat   Jacobian, relates measurements to solve for parameters.
   * @param   wMat   Measurement weighting matrix.
   * @param   yTup   New observations (or residuals).
   */
  public void accumulate(Matrix aMat, Matrix wMat, Tuple yTup) {
      // Normal equations for this measurement set
    at.transpose(aMat);
    atw.mult(at, wMat); 
    atwa.mult(atw, aMat);
    atwy.mult(atw, yTup);
      // Accumulated normal equations
    satwa.plus(atwa);
    satwy.plus(atwy);
  }

  /**
   * Add a measurement set to the accumulated normal equations
   * with no measurement weighting.
   *
   * @param   aMat   Jacobian, relates measurements to solve for parameters.
   * @param   yTup   New observations (or residuals).
   */
  public void accumulate(Matrix aMat, Tuple yTup) {
      // Normal equations for this measurement set, w = I
    at.transpose(aMat);
    atwa.mult(at, aMat);
    atwy.mult(at, yTup);
      // Accumulated normal equations
    satwa.plus(atwa);
    satwy.plus(atwy);
  }

  /**
   * Given the current set of accumulated normal equations, solve for
   * the unknown parameters.  More measurements may be accumulated
   * afterwards or reset() may be called to start over from scratch.
   *
   * @param   xout   Output solve for parameters based on previously
   *                 accumulated observations.
   */
  public void solve(Tuple xout) {
    infoM.set(satwa);
    infoM.cholesky();
    infoM.solveCH(satwy, xout);
  }

  /**
   * This method is a convenience routine used to return a matrix
   * of dimensions matching that of the estimated parameter covariance.
   * It can be used with the covariance() method.
   *
   * @return   A properly sized matrix to be used for covariance
   *           requests.
   */
  public Matrix emptyCovariance() {
    return new Matrix(NP);
  }

  /**
   * Computes the covariance based on the current set of accumulated
   * normal equations.
   *
   * @param   cov   Output estimate covariance.
   */
  public void covariance(Matrix cov) {
    cov.set(satwa);
    cov.invert();
  }

  /**
   * Zeros the normal equations - afterwards the object is ready
   * to begin accumulating a new set of measurements.  However,
   * calling solve right after a reset will lead to a matrix
   * decomposition error.  This  method is useful when performing
   * iterative least squares - reset() should be called between
   * each refinement of the estimate.
   */
  public void reset() {
    satwa.zero();
    satwy.zero();
  }

  /**
   * @return   The number of parameters that will be solved for,
   *           convenient for initializing space for the solve
   *           for parameters Tuple.
   */
  public int numSolveFor() {
    return NP;
  }
}
