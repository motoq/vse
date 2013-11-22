/*
 c  SysSolver.java
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
 *   x = (A'A)^-1 A'y
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
 * @since    20131121   Extended Tuple
 */
public class SysSolver extends Tuple {
  private final Decomposition method;
  private final Matrix aT_qT;           // aMat' or Q' for QR decomp
  private final Matrix f_r;             // Fisher info or R for QR
  private final Tuple aTy_qTy;          // aMat'*y or Q'*y

  /**
   * Initialize the unweighted solver.
   *
   * @param   meth   Decomposition method to use.
   * @param   aMat   Initial Jacobian to use.  It can be modified
   *                 later, but the size must stay the same.  Values
   *                 copied.
   */
  public SysSolver(Decomposition meth, Matrix aMat) {
    super(aMat.N);
    method = meth;

    aT_qT = new Matrix(aMat.numCols(), aMat.numRows());
    f_r = new Matrix(aMat.numCols());
    aTy_qTy = new  Tuple(aT_qT.numRows());

    setup(aMat);
  }

  /**
   * Configures this solver to use a different Jacobian.
   *
   * @param   aMat   New Jacobian for which to form the information matrix.
   *                 Values copied.
   */
  public void setNormalEqns(Matrix aMat) {
    setup(aMat);
  }

  /**
   * @return   The number of parameters that will be solved for,
   *           convenient for initializing space for the solve
   *           for parameters Tuple.
   */
  public int numSolveFor() {
    return aTy_qTy.length();
  }

  /**
   * Given measurements/observations, compute the associated solve
   * for parameters (this Tuple).
   *
   * @param   yin   Input observations
   */
  public void solve(Tuple yin) {
    aTy_qTy.mult(aT_qT, yin);
    switch (method) {
      case CHOLESKY:
        f_r.solveCH(aTy_qTy, this);
        break;
      case CROUT:
        f_r.solve(aTy_qTy, this);
        break;
      case QR:
          // Perform simple backwards substitution here for QR method
        for (int ii=f_r.M; ii>=1; ii--) {
          put(ii, aTy_qTy.get(ii));
          for (int jj=(ii+1); jj<=f_r.N; jj++) {
            put(ii, get(ii) - f_r.get(ii, jj)*get(jj));
          }
          put(ii, get(ii)/f_r.get(ii,ii));
        }
    }
  }

  /*
   * @param   a   Jacobian to form into a decomposed information matrix.
   */
  private void setup(Matrix a) {
    switch (method) {
      case CHOLESKY:
        aT_qT.transpose(a);
        f_r.mult(aT_qT, a);
        f_r.cholesky();
        break;
      case CROUT:
        aT_qT.transpose(a);
        f_r.mult(aT_qT, a);
        f_r.croutLU();
        break;
      case QR:
        Matrix q = new Matrix(a.numRows(), a.numCols());
        a.getQR(q, f_r);
        aT_qT.transpose(q);
    }
  }
}
