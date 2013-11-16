/*
 c  QR.java
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
 * This class is essentially a container for thin QR Decomposition of
 * a full rank matrix.  The decomposed matrices are directly
 * accessible, emphasizing the point that they are not necessarily
 * new instantiations upon each request to decompose.
 */
public class QR {
  /** 
   * Computed MxN matrix with orthonormal columns,
   * given a MxN matrix to decompose.
   */
  public Matrix q = new Matrix(1);

  /** 
   * Output NxN upper triangular matrix,
   * given a MxN matrix to decompose.
   */
  public Matrix r = new Matrix(1);

  /**
   * Allow empty instantiation, default to scalar since throwing a
   * vector space argument exception is easier to deal with than
   * accessing a public null pointer.  No need to check for null
   * objects during set(), just dimensions.
   */ 
  public QR() {
  }

  /**
   * Instantiate with a matrix to be decomposed.
   *
   * @param   qr   A full rank MxN matrix to be decomposed via "thin" QR
   *               decomposition.  See <code>Matrix.getQR</code> for
   *               implementation details.
   */
  public QR(Matrix qr) {
    set(qr);
  }

  /**
   * Decompose input matrix.
   *
   * @param   qr   A full rank MxN matrix to be decomposed via "thin" QR
   *               decomposition.  See <code>Matrix.getQR</code> for
   *               implementation details.
   */
  public void set(Matrix qr) {
      // Check matrix size compatibility
    if (q.M != qr.M  ||  q.N != qr.N) {
      q = new Matrix(qr.M, qr.N);
    }
    if (r.M != qr.N  ||  r.N != qr.N) {
      r = new Matrix(qr.N);
    }

    qr.getQR(q, r);
  }
}
