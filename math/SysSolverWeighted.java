/*
 c  SysSolverWeighted.java
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

import com.motekew.vse.enums.Decomposition;

/**
 * This is used to find the solution to systems of linear equations of the
 * form:
 * <P>
 *   x = (A'WA)^-1 A'Wy
 * <P>
 * It is initialized with the desired solution method (immutable) and
 * initial Jacobian.  After initialization, the solve() method can be
 * called repeatedly with different <code>Tuple</code> observations.
 * While the solution method can't be changed, the normal equations may
 * be updated.  The dimensions of all matrices and vectors must remain
 * the same.  Matrices used for inputs are not modified internally.
 *
 * @author   Kurt Motekew
 * @since    20130511
 */
public class SysSolverWeighted {
  private final Decomposition method;
  private final Matrix aTW_qT;        // aMat'W or Q' from QR(sqrt(wm)*aMat)
  private final Matrix wm;            // Weighting matrix (or sqrt(wm))
  private final Matrix f_r;           // Fisher info A'WA or R for QR 
  private final Tuple aTWy_qTy;       // aMat'W*y

  /**
   * Initialize the solver.
   *
   * @param   meth   Decomposition method to use
   * @param   aMat   Initial Jacobian to use.  It can be modified
   *                 later, but the size must stay the same.  Values
   *                 copied.
   * @param   wMat   Measurement weighting matrix - the content may
   *                 also be modified, but not the dimensions.  Values
   *                 copied.
   */
  public SysSolverWeighted(Decomposition meth, Matrix aMat, Matrix wMat) {
    method = meth;

      // Set the weighting matrix value here
    wm = new Matrix(wMat);
      // Only allocate space here
    aTW_qT   = new Matrix(aMat.numCols(), aMat.numRows());
    f_r      = new Matrix(aMat.numCols());
    aTWy_qTy = new Tuple(aTW_qT.numRows());

    setup(aMat);
  }

  /**
   * Configures this solver to use a different Jacobian with the previously
   * set weighting matrix.
   *
   * @param   aMat   New Jacobian for which to form the information matrix.
   *                 Values copied.
   */
  public void set(Matrix aMat) {
    setup(aMat);
  }

  /**
   * Configures this solver to use a different Jacobian and weighting
   * matrix.
   *
   * @param   aMat   New Jacobian for which to form the information matrix.
   * @param   wMat   New weighting matrix
   */
  public void set(Matrix aMat, Matrix wMat) {
    wm.set(wMat);
    setup(aMat);
  }

  /**
   * @return   The number of parameters that will be solved for,
   *           convenient for initializing space for the solve
   *           for parameters Tuple.
   */
  public int numSolveFor() {
    return aTWy_qTy.length();
  }

  /**
   * Given measurements/observations, compute the associated solve
   * for parameters.
   *
   * @param   yin   Input observations
   * @param   xout  Output solve for parameters
   */
  public void solve(Tuple yin, Tuple xout) {
      // For QR, set observations to product of the square root
      // of the weighting matrix and the inputs.  Otherwise, just
      // point the observations Tuple to the inputs.
    Tuple obs;
    if (method == Decomposition.QR) {
      obs = new Tuple(yin.length());
      obs.mult(wm, yin);
    } else {
      obs = yin;
    }

    aTWy_qTy.mult(aTW_qT, obs);
    switch (method) {
      case CHOLESKY:
        f_r.solveCH(aTWy_qTy, xout);
        break;
      case CROUT:
        f_r.solve(aTWy_qTy, xout);
        break;
      case QR:
        // Perform simple backwards substitution here for QR method
      for (int ii=f_r.M; ii>=1; ii--) {
        xout.put(ii, aTWy_qTy.get(ii));
        for (int jj=(ii+1); jj<=f_r.N; jj++) {
          xout.put(ii, xout.get(ii) - f_r.get(ii, jj)*xout.get(jj));
        }
        xout.put(ii, xout.get(ii)/f_r.get(ii,ii));
      }
    }
  }

  /*
   * @param   a   Jacobian to form into a decomposed information matrix.
   *              The square root of the weighting matrix will be taken for
   *              QR decomposition.
   */
  private void setup(Matrix a) {
      // Get square root of weighting if using QR
    if (method == Decomposition.QR) {
      wm.cholesky();
      wm.zeroUpper();
      Matrix tmp = new Matrix(a);
      a.mult(wm, tmp);
    }

    Matrix tmp;
    switch (method) {
      case CHOLESKY:
        tmp = new Matrix(a.numCols(), a.numRows());
        tmp.transpose(a);
        aTW_qT.mult(tmp, wm);
        f_r.mult(aTW_qT, a);
        f_r.cholesky();
        break;
      case CROUT:
        tmp = new Matrix(a.numCols(), a.numRows());
        tmp.transpose(a);
        aTW_qT.mult(tmp, wm);
        f_r.mult(aTW_qT, a);
        f_r.croutLU();
        break;
      case QR:
        tmp = new Matrix(a.numRows(), a.numCols());
        a.getQR(tmp, f_r);
        aTW_qT.transpose(tmp);
    }
  }
}
