/*
 c  Matrix.java
 c
 c  Copyright (C) 2000, 2013 Kurt Motekew
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

import java.util.Arrays;

/**
 * <code>Matrix</code> is a <code>VectorSpace</code> representing
 * a matrix.  Any attempt to access an element of this matrix
 * that is out of range will result in a VectorSpaceIndexOutOfBoundsException
 * being thrown.  Note that indexing starts with 1, not zero.
 *
 * @author Kurt Motekew
 * @since  20071011
 * @since  20120225   Added croutLU(), cholesky(), det(), invert(), solveCH,
 *                    and associated methods.  Also fixed a bug in mult(A,B)!
 * @since  20121101   Added getQR() via modified Gram-Schmidt method
 * @since  20130507   Cleaned up some stuff, made use of valuesPtr() calls
 *                    along with arraycopy and fill methods.  Some things are
 *                    now package protected vs. private.  Eliminated public
 *                    getM() and getN() since they don't have obvious meanings
 *                    outside this package.
 */
public class Matrix extends VectorSpace {
  /** Rows X Columns */
  final int DIM;
  /** Number of rows in Matrix */
  final int M;
  /** Number of Columns in Matrix */
  final int N;

  private final boolean mxm;  // True if this is a square Matrix

    // Indicates this is a temporary matrix used inside another Matrix
    // object for recursive stuff.
  private boolean recursiveMat = false;

  /*
   * [MxN] with [M][N] indexing, Matrix values
   */
  private final double[][] vals;

    // Used for pivoting during decomposition.  See croutLU
  private int[] row_order = null;
  private double[] row_scale = null;

  /**
   * Values considered too small to divide by - results in a
   * SingularMatrixException.
   */
  public static final double EPS = 1.0e-100;

  /**
   * Default constructor - just initializes the size of this Matrix
   *
   * @param  m    A int for the number of rows
   * @param  n    A int for the number of columns
   */
  public Matrix(int m, int n) {
    super(2);               // two blade
    this.M   = (m > 0) ? m : 1;
    this.N   = (n > 0) ? n : 1;
    this.DIM = M*N;
    if (M == N) {
      mxm = true;
    } else {
      mxm = false;
    }
    vals = new double[this.M][this.N];
  }

  /**
   * Initialize as a square matrix
   * 
   * @param   m   The size of this square mxm matrix
   */
  public Matrix(int m) {
    this(m, m);             // nXn matrix
  }

  /**
   * Initialize the Matrix using a 2D array of doubles.
   * The 
   *
   * @param   mtrx    A Matrix used to initialize this Matrix.
   *                  Values are copied.
   */
  public Matrix(Matrix mtrx) {
    this(mtrx.M, mtrx.N);
    set(mtrx);
  }

  /**
   * Initialize the Matrix using a 2D array of doubles.
   * The 
   *
   * @param   mtrx    A double[][] used to initialize this Matrix
   *                  It should be a block double array, not jagged.
   *                  Values are copied into this matrix.
   */
  public Matrix(double[][] mtrx) {
    this(mtrx.length, mtrx[0].length);
    for (int ii=0; ii<M; ii++) {
      System.arraycopy(mtrx[ii], 0, this.vals[ii], 0, N);
    }
  }

  /**
   * Supports internal use for in-place decomposition related
   * functions where it is not desired to change the internally
   * stored values.  Use with caution!
   * 
   * @param   tmpMtx     A pointer to values used by this matrix.
   *                     This memory is used - no copies are made.
   * @param   tmpFlag    Flag indicating this matrix is for internal
   *                     (recursive) use.  Setting to true should flag
   *                     an end to recursion.
   */
  Matrix(double[][] tmpMtx, boolean tmpFlag) {
    super(2);
    this.M = tmpMtx.length;
    this.N = tmpMtx[0].length;
    this.DIM = M*N;
    if (M == N) {
      mxm = true;
    } else {
      mxm = false;
    }
    recursiveMat = tmpFlag;
    vals = tmpMtx;
  }

  /**
   * Returns the dimension of the VectorSpace
   *
   * @return    int number of dimensions
   */
  @Override
  public int getDimension() {
    return DIM;
  }

  /**
   * Returns the number of rows in this matrix
   *
   * @return   int number of rows
   */
  public int numRows() {
    return M;
  }

  /**
   * Returns the number of columns in this matrix
   *
   * @return   int number of columns
   */
  public int numCols() {
    return N;
  }

  public boolean isSquare() {
    return mxm;
  }

  /**
   * @return    Two element array of integers representing the number of rows
   *            [0] and the colums [1], starting from [0][0], that don't
   *            contain zeros.  Useful if a larger than needed matrix was
   *            originally allocated and there is a desire to trim a new one.
   *            <PRE>
   *            --        --
   *            | X X X 0 0 |
   *            | X X X 0 0 |  ->  [2][3]
   *            | 0 0 0 0 0 |
   *            --         --
   *            </PRE>
   */
  public int[] trimToMN() {
    int[] nm = new int[2];

      // Want to know number of rows and columns, not offsets, so start
      // counting from 1.
    for (int ii=1; ii<=M; ii++) {
      for (int jj=1; jj<=N; jj++) {
        if (vals[ii-1][jj-1] != 0.0) {
          if (ii > nm[0]) {
            nm[0] = ii;
          }
          if (jj > nm[1]) {
            nm[1] = jj;
          }
        }
      }
    }
    return nm;
  }

  /**
   * Creates a matrix of new dimensions based on the input matrix.
   *
   * @param   mTrim   Number of rows to trim or expand this matrix to.
   * @param   nTrim   Number of columns to trim this matrix to.
   *
   * @return          A matrix either trimmed or expanded to the input
   *                  dimensions, with this matrix's values copied from
   *                  row = 1 to mTrim and column = 1 to nTrim.  If the
   *                  requested matrix is bigger than this one, then
   *                  the expanded values will be zero.  If the requested
   *                  matrix is smaller, the excess components will be
   *                  dropped.
   */
  public Matrix trimToMN(int mTrim, int nTrim) {
    Matrix newMat = new Matrix(mTrim, nTrim);
    mTrim = (M < mTrim) ? M : mTrim;
    nTrim = (N < nTrim) ? N : nTrim;

    for (int ii=1; ii<=mTrim; ii++) {
      for (int jj=1; jj<=nTrim; jj++) {
        newMat.put(ii, jj, this.get(ii, jj));
      }
    }
    return newMat;
  }

  /**
   * Set all elements of this Matrix to zero.
   */
  @Override
  public void zero() {
    for (int ii=0; ii<M; ii++) {
      Arrays.fill(this.vals[ii], 0.0);
    }
  }

  /**
   * Converts this matrix to a lower diagonal by zero'ing
   * components above the diagonal.
   */
  public void zeroUpper() {
    for (int ii=0; ii<M-1; ii++) {
      for (int jj=N-1; jj>ii; jj--) {
        vals[ii][jj] = 0.0;
      }
    }
  }

  /**
   * Sets this Matrix to the identity Matrix.  Must be a square
   * matrix.
   * 
   * @throws         VectorSpaceArgumentException if dimension are wrong
   */
  public void identity() {
    if (!mxm) {
      throw new VectorSpaceArgumentException(
                     "Not a square matrix - can't create an identity matrix");
    }
    zero();
    for (int ii=0; ii<M; ii++) {
      vals[ii][ii] = 1.0;
    }
  }

  /**
   * Sets the value of the (i,j) element.
   * A bad index will throw a VectorSpaceIndexOutOfBoundsException.
   *
   * @param  i     An int index for the row:    1 < i < M
   * @param  j     An int index for the column: 1 < j < N
   * @param  val   A double value to be inserted into the (i,j) element
   */
  public void put(int i, int j, double val) {
    if (i > M  ||  j > N  ||  i < 1  ||  j < 1) {
      throw new VectorSpaceIndexOutOfBoundsException(
                   "Matrix indices out of bounds:  (" + i + "," + j + ")"); 
    } else {
      vals[i-1][j-1] = val;
    }
  }

  /**
   * Gets the value of the i'th element.
   * A bad index will throw a VectorSpaceIndexOutOfBoundsException.
   *
   * @param  i     An int index for the row:    1 < i < M
   * @param  j     An int index for the column: 1 < j < N
   *
   * @return       A double value for the (i,j) element
   */
  public double get(int i, int j) {
    if (i > M  ||  j > N  ||  i < 1  ||  j < 1) {
      throw new VectorSpaceIndexOutOfBoundsException(
                   "Matrix indices out of bounds:  (" + i + "," + j + ")"); 
    } else {
      return vals[i-1][j-1];
    }
  }

  /**
   * Copy elements one for one from the passed Matrix into this one.
   * A <code>VectorSpaceArgumentException.java</code> will be thrown if
   * the dimensions do no match.
   *
   * @param  mtrx   A Matrix for which this Matrix's values are to be
   *                set.
   */
  public void set(Matrix mtrx) {
    int ii;
    if (mtrx.M != M  ||  mtrx.N != N) {
     throw new VectorSpaceArgumentException("Matrix must be set " +
                "with a Matrix of (" + M + ", " + N + ") elements, not:  (" +
                 mtrx.M + ", " + mtrx.N + ")");
    } else {
      double[][] mtrxvals = mtrx.valuesPtr();
      for (ii=0; ii<M; ii++) {
        System.arraycopy(mtrxvals[ii], 0, this.vals[ii], 0, N);
      }
    }
  }

  /**
   * Set this matrix with the sub matrix starting at the input row and
   * column.  Note that existing values will remain - call zero() first if
   * original supermatrix components should be zeroed.
   *
   * @param   row    Row in this matrix into which values will start
   *                 being copied
   * @param   col    Column in this matrix into which values will start
   *                 being copied.
   * @param   mtrx   Matrix from which to copy values - must fit from
   *                 start index to end of this matrix.
   *
   * @throws  VectorSpaceArgumentException  If the input matrix is too big
   *                                        to fit in this matrix starting
   *                                        at the given indices.
   *                 
   */
  public void set(int row, int col, Matrix mtrx) {
    if ((M + 1 - row) < mtrx.M  ||  (N + 1 - col) < mtrx.N) {
      throw new VectorSpaceArgumentException("Sub Matrix must be (" +
                 (M+1-row) + ", " + (N+1-col) + " or smaller.");
    } else {
      for (int ii=0; ii < mtrx.M; ii++) {
        for (int jj=0; jj < mtrx.N; jj++) {
          this.put(row+ii, col+jj, mtrx.get(ii+1, jj+1));
        }
      }
    }
  }

  /**
   * Set values of selected column of this Matrix to those of the input
   * Tuple
   *
   * @param   colNum   Column to set - 1->numCol
   * @param   tpl      Tuple of values copied into selected column.  Length
   *                   must be the same as the number of rows in this Matrix
   */
  public void setColumn(int colNum, Tuple tpl) {
    if (tpl.N != M) {
      throw new VectorSpaceArgumentException("Your Tuple isn't big enough");
    } else if (colNum > N  ||  colNum < 1) {
      throw new VectorSpaceArgumentException("Invalid column number request");
    }
   
    colNum--;                            // Internal storeage 0 based
    double[] tplvals = tpl.valuesPtr();
    for (int ii=0; ii<M; ii++) {
      vals[ii][colNum] = tplvals[ii];
    }
  }

  /**
   * Copy elements from the 2D array passed in, into this Matrix.
   * A <code>VectorSpaceArgumentException.java</code> will be thrown if
   * the dimensions do no match.
   *
   * @param  mtrx    A double[][] for which this Matrix's values are to be
   *                 set.  It should be a block 2D array, not jagged.
   */
  public void set(double[][] mtrx) {
    if (mtrx.length != M  ||  mtrx[0].length != N) {
     throw new VectorSpaceArgumentException("Matrix must be set " +
              "with a double[][] of (" + M + ", " + N + ") elements, not:  (" +
               mtrx.length + ", " + mtrx[0].length + ")");
    } else {
      for (int ii=0; ii<M; ii++) {
        System.arraycopy(mtrx[ii], 0, this.vals[ii], 0, N);
      }
    }
  }

  /**
   * Copy elements one for one from the passed Tuple into this Matrix.
   * The incoming Tuple is treated as an Mx1 matrix.
   * A <code>VectorSpaceArgumentException.java</code> will be thrown if
   * the dimensions do no match.
   *
   * @param  tpl    A Tuple for which this Matrix's values are to be
   *                set.
   */
  public void set(Tuple tpl) {
    int ii;
    if (N != 1  ||  tpl.N != M) {
     throw new VectorSpaceArgumentException("Matrix must be set " +
                "with a Matrix of (" + M + ", " + N + ") elements, not:  (" +
                 tpl.N + ", " + 1 + ")");
    } else {
      double[] tplvals = tpl.valuesPtr();
      for (ii=0; ii<M; ii++) {
        vals[ii][0] = tplvals[ii];
      }
    }
  }

  /**
   * Returns a 2D array representation of this matrix.  The 2D array will
   * have the same dimensions as the matrix.
   *
   * @return      A double[] filled with this tuple's values.
   */
  public double[][] values() {
    double[][] mtrx = new double[M][N];
    for (int ii=0; ii<M; ii++) {
      System.arraycopy(this.vals[ii], 0, mtrx[ii], 0, N);
    }
    return mtrx;
  }

  /**
   * @return    A pointer to a 2D array of the internally stored values.
   *            Use care.  Changes to this array affect the internally
   *            stored values!
   */
  double [][] valuesPtr() {
    return vals;
  }

  /**
   * Returns a string representation of the matrix.  Probably won't
   * be pretty for a big matrix....
   */
  public String toString() {
    String output = "";
    int ii, jj;
    for (ii=0; ii<M; ii++) {
      for (jj=0; jj<N; jj++) {
        output = output + "  " + vals[ii][jj];
      }
      output = output + '\n';
    }
    return output;
  }

  /**
   * Adds the input Matrix to this.
   *
   * @param   aMat    Matrix to be added to this.  Must be of the same
   *                  dimensions as this Matrix.
   */
  public void plus(Matrix aMat) {
    int ii, jj;
    if (M == aMat.M  &&  N == aMat.N) {
      double[][] avals = aMat.valuesPtr();
      for (ii=0; ii<M; ii++) {
        for (jj=0; jj<N; jj++) {
          vals[ii][jj] += avals[ii][jj];
        }
      }
    } else {
     throw new VectorSpaceArgumentException("Wrong dimensions for " +
                                            "Matrix.plus(Matrix aMat)");
    }
  }

  /**
   * Multiplies this matrix by num
   *
   * @param  num    number to multiply this matrix by
   */
  public void mult(double num) {
    int ii, jj;
    for (ii=0; ii<M; ii++) {
      for (jj=0; jj<N; jj++) {
        vals[ii][jj] *= num;
      }
    }
  }

  /**
   * Divides this matrix by num
   *
   * @param  num    number to divide this matrix by
   */
  public void div(double num) {
    this.mult(1.0/num);
  }

  /**
   * Sets the value of this Matrix to the product of the two entered
   * matrices.  Matrix A must be a MxN, and Matrix B must be a NxQ.
   * This Matrix must be a MxQ
   * [MXQ] = [MXN] * [NXQ]
   * <P>
   * If these dimensions don't match, a VectorSpaceArgumentException
   * will be thrown.
   *
   * @param    aMat     First Matrix:   [MXN]
   * @param    bMat     Second Matrix:  [NXQ]
   */
  public void mult(Matrix aMat, Matrix bMat) {
    int ii,   jj,   kk;

    if (M == aMat.M  &&  N == bMat.N  &&  aMat.N == bMat.M) {
      this.zero();
      double[][] avals = aMat.valuesPtr();
      double[][] bvals = bMat.valuesPtr();
      for (ii=0; ii<M; ii++) {
        for (kk=0; kk<aMat.N; kk++) {
          for (jj=0; jj<N; jj++) {
            vals[ii][jj] += avals[ii][kk] * bvals[kk][jj];
          }
        }
      }
    } else {
     throw new VectorSpaceArgumentException("Wrong dimensions for " +
                               "Matrix.mult(Matrix aMat, Matrix bMat)");
    }
  }

  /**
   * Sets the value of this Matrix to the product of the first entered
   * <code>Tuple</code> and the transpose of the second entered
   * <code>Tuple</code>.
   * <P>
   * [MXM] = [MX1] * [1XM]
   * M     = AAT
   *
   * @param    a     Tuple 1:   [MX1]
   * @param    b     Tuple to transpose, will be a: [1XM]
   */
  public void mult(Tuple a, Tuple b) {    
    int ii,   jj;

    if (M == a.N  &&  M == b.N  &&  M == N) {
      double[] avals = a.valuesPtr();
      double[] bvals = b.valuesPtr();
      for (ii=0; ii<M; ii++) {
        for (jj=0; jj<M; jj++) {
          vals[ii][jj] = avals[ii] * bvals[jj];
        }
      }
    } else {
     throw new VectorSpaceArgumentException("Wrong dimensions for " +
                                         "Matrix.mult(Tuple a, Tuple b)");
    }
  }

  /**
   * Sets the value of this matrix to the product of the transpose
   * of the input matrix times the input matrix:  this = A'*A
   *
   * @param   a   The number of columns must match the number of rows
   *              in this matrix:  this.numRows() = a.numCols()
   */
  public void normalEqn(Matrix a) {
    Matrix aT = new Matrix(a.N, a.M);
    aT.transpose(a);
    this.mult(aT, a);
  }

  /**
   * Sets the value of this matrix to the product of the transpose
   * of the input matrix times the 2nd input matrix times the first
   * matrix:  this = A'*W*A
   *
   * @param   a   The number of columns must match the number of rows
   *              in this matrix:  this.numRows() = a.numCols()
   * @param   w   the number of rows and columns must match the 
   *              number of rows in the a matrix.
   */
  public void normalEqn(Matrix a, Matrix w) {
    Matrix aT = new Matrix(a.numCols(), a.numRows());
    aT.transpose(a);
    Matrix aTw = new Matrix(aT.numCols());
    aTw.mult(aT, w);
    this.mult(aTw, a);
  }

  /**
   * Sets the value of this matrix to the product of the first
   * matrix times the 2nd matrix times the transpose of the first.
   * Typically used as a matrix transformation operation where A
   * is a Jacobian:  this = A*S*A'
   *
   * @param   a      The number of rows must match the number of rows
   *                 in this matrix:  this.numRows() = a.numRows()
   * @param   cov    the number of rows and columns must match the 
   *                 number of columns in the a matrix.
   */
  public void transform(Matrix a, Matrix cov) {
    Matrix aT = new Matrix(a.numCols(), a.numRows());
    aT.transpose(a);
    Matrix aw = new Matrix(a.numRows(), a.numCols());
    aw.mult(a, cov);
    this.mult(aw, aT);
  }

  /**
   * Returns trace of this matrix if it is square - throws a 
   * VectorSpaceArgumentException otherwise.
   * 
   * @return   Trace of this matrix trace
   */
  public double trace() {
    double retVal = 0.0;

    if (!mxm) {
      throw new VectorSpaceArgumentException(
                  "Can't find trace of non-square " + "matrix " + this);
    }

    for (int ii=0; ii<M; ii++) {
      retVal += vals[ii][ii];
    }

    return retVal;
  }

  /**
   * Performs in-place transpose - requires this matrix be square otherwise
   * an exception will be thrown.
   */
  public void transpose() {
    double tmp;
    
    if (!mxm) {
      throw new VectorSpaceArgumentException(
                  "Can't in-place transpose non-square " + "matrix " + this);
    }
 
    for (int ii=0; ii<M; ii++) {
      for (int jj=0; jj<ii; jj++) {
        tmp = vals[jj][ii];
        vals[jj][ii] = vals[ii][jj];
        vals[ii][jj] = tmp;
      }
    }
  }

  /**
   * Sets this Matrix to the transpose of the input Matrix.
   * If the input matrix is a NxM, then this matrix must be a
   * MxN.
   * 
   * @param   aMat   A NxM matrix to be transposed (given this
   *                 matrix is a MxN)
   *                 
   * @throws         VectorSpaceArgumentException if dimension are wrong
   */
  public void transpose(Matrix aMat) {
    if (M != aMat.N  ||  N != aMat.M) {
      throw new VectorSpaceArgumentException(
                     "Can't transpose - dimensions of input matrix incorrect");
    }
    double[][] avals = aMat.valuesPtr();
    for (int ii=0; ii<M; ii++) {
      for (int jj=0; jj<N; jj++) {
        vals[ii][jj] = avals[jj][ii];
      }
    }
  }

  /**
   * If this is a full rank MxN matrix, then "thin" QR decomposition will
   * be performed by the modified Gram-Schmidt method as described
   * in "Optimal Estimation of Dynamic Systems" by Crassidis and Junkins.
   * The method attempts to exit gracefully if this matrix isn't full rank
   * by throwing a SingularMatrixException.
   *
   * @param   qMat   Output MxN matrix with orthonormal columns.
   * @param   rMat   Output NxN upper triangular matrix
   *
   * @throws         VectorSpaceArgumentException if dimensions of supplied
   *                 output matrices are wrong.
   * @throws         SingularMatrixException if the matrix appears to not be
   *                 full rank.
   */
  void getQR(Matrix qMat, Matrix rMat) {
      // Make sure R is NxN
    if (N != rMat.M  ||  N != rMat.N) {
      throw new VectorSpaceArgumentException(    
                     "R needs to be NxN " + rMat);
    }
      // Setting qMat to this will throw an exception if the dimensions
      // don't match - no need to explicitly perform check here...
    rMat.zero();
    qMat.set(this);
    Tuple qk = new Tuple(M);
    Tuple qj = new Tuple(M);
    double rkk;
    for (int kk=1; kk<=N; kk++) {
      qk.setColumn(kk, qMat);
      rkk = qk.mag();
      if (rkk < EPS) {
        throw new SingularMatrixException(
            "This matrix does not appear to be full rank:  " + this);
      }
      rMat.put(kk, kk, rkk);                  // R's diagonal
      qk.div(rkk);
      qMat.setColumn(kk, qk);                 // Done with Q's kth column
      for (int jj=(kk+1); jj<=N; jj++) {
        qk.setColumn(kk, qMat);               // Get final version of qk
        qj.setColumn(jj, qMat);               // Get next q column
        rMat.put(kk, jj, qk.dot(qj));         // Set R's kth row for this j
        qk.mult(rMat.get(kk, jj));
        qj.minus(qk);
        qMat.setColumn(jj, qj);               // Forward modify column -
                                              // finish next pass
      }
    }
  }

  /**
   * Solve for x where y = Ax.  <code>croutLU</code> <B>MUST</B>
   * be called before this method to prepare the matrix for solving.
   * It is similar to first inverting this matrix and multiplying by
   * y to get the answer:
   * <P>
   * x = A.crouteLU()*y   vs.   x = A.invert()*y
   * <P>
   * This method is more efficient because this matrix only needs to
   * be decomposed to solve for x, vs. finding the full matrix inverse.
   * <P>
   * An exception will be thrown if this isn't a square matrix or if the
   * y and x dimension values don't match.
   *
   * See <code>SysSolver</code> and related classes for access to solver
   * functionality outside of this package.
   * 
   * @param   y    Array of function values
   * @param   x    Array of values to be solved for
   * 
   * @throws       VectorSpaceArgumentException   If y and x length don't match
   *                                              matrix dimension.
   * @throws       VectorSpaceArgumentException   If not square 
   */
  void solve(Tuple y, Tuple x) {
    if (!mxm  ||  M < 1) {
      throw new VectorSpaceArgumentException(
                      "Can't solve equations with non-square Matrix " + this);
    }
    if (y.N != N  ||  x.N != N) {
      throw new VectorSpaceArgumentException(
               "Cant solve for x " + x + " given y (Dimensions wrong)" + y);
    }
    if (row_order == null) {
      return;
    }
    
    double[] yvals = y.valuesPtr();
    double[] xvals = x.valuesPtr();
    solve(yvals, xvals);
  }

  /**
   * Solve for x where y = Ax.  <code>cholesky</code> <B>MUST</B>
   * be called before this method to prepare the matrix for solving.
   * The matrix <B>MUST</B> by symmetric positive definite.
   * <P>
   * It is similar to first inverting this matrix and multiplying by
   * y to get the answer:
   * <P>
   * x = A.cholesky()*y   vs.   x = A.invert()*y
   * <P>
   * When this Matrix meets the criteria, this method of solving for
   * unknowns is more efficient than Crout LU decomposition.
   * <P>
   * An exception will be thrown if this isn't a square matrix or if the
   * y and x dimension values don't match.
   *
   * See <code>SysSolver</code> and related classes for access to solver
   * functionality outside of this package.
   * 
   * @param   y    Array of function values
   * @param   x    Array of values to be solved for
   * 
   * @throws       VectorSpaceArgumentException   If y and x length don't match
   *                                              matrix dimension.
   * @throws       VectorSpaceArgumentException   If not square  
   * 
   * 
   * 
   */
  void solveCH(Tuple y, Tuple x) {
    int ii, jj;
    double sum;

    if (!mxm  ||  M < 1) {
      throw new VectorSpaceArgumentException(
                      "Can't solve equations with non-square Matrix " + this);
    }
    if (y.N != N  ||  x.N != N) {
      throw new VectorSpaceArgumentException(
               "Cant solve for x " + x + " given y (Dimensions wrong)" + y);
    }

      // Solve for b where y = Lb
    double[] yvals = y.valuesPtr();
    double[] xvals = x.valuesPtr();
    double[] bvals = new double[x.N];
    for (ii=0; ii<M; ii++) {
      sum = 0.0;
      for (jj=0; jj<ii; jj++) {
        sum += bvals[jj]*vals[ii][jj];
      }
      bvals[ii] = (yvals[ii] - sum)/vals[ii][ii];
    }

      // Now solve for x where b = L'x
    for (ii=(M-1); ii>=0; ii--) {
      sum = 0.0;
      for (jj=ii+1; jj<M; jj++) {
          // Note reverse order for indexing vals - L' vs. L
        sum += xvals[jj]*vals[jj][ii];
      }
      xvals[ii] = (bvals[ii] - sum)/vals[ii][ii];
    }
  }

  /**
   * Performs Crout LU decomposition on this matrix.  This matrix is then
   * suitable for solving systems of linear equations through the
   * <code>solve</code> method.  By itself, this method is probably not
   * of much use.  It is meant for square matrices in its current form.
   * <P>
   * Use <code>getRowOrder</code> to view correctly - rows are not swapped
   * during the decomposition, a list of indices track these changes.
   * <P>
   * Note that once decomposed, the determinant method det() will not generate
   * the correct determinant given the row swapping.
   * 
   * @throws    VectorSpaceArgumentException   If not square 
   * @throws    SingularMatrixException        If there is a row of zeros.
   */
  public void croutLU() {
    if (!mxm  ||  N < 1) {
      throw new VectorSpaceArgumentException(
                                  "Can't decompose non-square Matrix " + this);
    }

      // Catch obvious matrix singularity while ordering rows
    boolean singular = order();
    if (singular) {
      throw new SingularMatrixException(
          "Can't decompose Matrix:  Row of zeros " + this);
    }
      // Decompose matrix (vals) into a form suitable for inversion
    decomp();
  }

  /**
   * This method is used to decompose a square positive definite symmetric
   * matrix for use by the <code>solveCH</code> method.  Lower elements
   * are modified - those above the diagonal remain the same (they are not
   * set to zero).  Call zeroUpper() to convert this to a true lower
   * diagonal matrix (such that A = L*L').
   * <P>
   * Algorithm based on "Numerical Methods for Engineers", 2nd ed. Steven C.
   * Chapra and Raymond P. Canale, 1988 with checks added.  If this Matrix
   * is processed without throwing an exeption, then chances are good it is
   * symmetric positive definite.
   * <P>
   * NOTE:  If a SingularMatrixException is thrown, this Matrix may have
   * been only partially decomposed and should therefore be reset before
   * other processing is done.
   * 
   * @throws    VectorSpaceArgumentException   If not square 
   * @throws    SingularMatrixException        If there are indications that
   *                                           this matrix is not invertible,
   *                                           symmetric, or positive definite.
   *                                           If this exception is thrown, then
   *                                           this matrix may be partially
   *                                           decomposed (corrupt - must
   *                                           reset values).
   */
  public void cholesky() {
    if (!mxm  ||  N < 1) {
      throw new VectorSpaceArgumentException(
                                  "Can't decompose non-square Matrix " + this);
    }
      // Determine L
    double sum;
    double tmp;
    for (int kk=0; kk<M; kk++) {
      for (int ii=0; ii<=kk-1; ii++) {
        if (vals[ii][ii] < EPS) {
          throw new SingularMatrixException(
              "Can't decompose Matrix:  Diagonal elements must be positive " +
                                                                          this);
        }
        sum = 0.0;
        for (int jj=0; jj<=ii-1; jj++) {
          sum += vals[ii][jj]*vals[kk][jj];
        }
        vals[kk][ii] = (vals[kk][ii] - sum)/vals[ii][ii];
      }
      sum = 0.0;
      for (int jj=0; jj<=kk-1; jj++) {
        sum += vals[kk][jj]*vals[kk][jj];
      }
      tmp = vals[kk][kk] - sum;
      if (tmp < EPS) {
        throw new SingularMatrixException(
         "Can't decompose Matrix:  Not symmetric positive definite " + this);
      } else {
        vals[kk][kk] = Math.sqrt(tmp);
      }
    }
  }

  /**
   * During croutLU decomposition, rows themselves are not swapped, only
   * indices to those rows.  This method returns the true row number of
   * the matrix given a stored row number.  The input index will equal the
   * output index if no decomposition has been performed.  Indices are 1
   * based like the rest of the matrix accessor functions.
   * 
   * @param   ii   Row for which the row order is desired.  0 < ii <= M
   * 
   * @return       True row order for requested row.
   */
  public int getRowOrder(int ii) {
    int retVal = 0;

    if (ii > 0  &&  ii <= M) {
        // If no decomposition has been performed, row order is 1,2...M
      if (row_order != null) {
        retVal = row_order[ii-1] + 1;
      } else {
        retVal = ii;
      }
    }
    return retVal;
  }

  /**
   * The determinant of this matrix.  Cofactor expansion is used for 3x3 and
   * smaller matrices, Gaussian elimination otherwise.  The current
   * implementation can be expensive for 3X3 and greater since a copy of
   * this matrix is created to compute the determinant without affecting
   * the values of this Matrix.
   * 
   * @return    The determinant of this matrix.
   * 
   * @throws    VectorSpaceArgumentException   If not square
   */
  public double det() {
    double retVal;

    if (!mxm  ||  M < 1) {
      throw new VectorSpaceArgumentException(
                      "Can't find determinant of non-square Matrix " + this);
    }

    if (M == 3) {
      retVal =   vals[0][0]*(vals[1][1]*vals[2][2] - vals[2][1]*vals[1][2])
               - vals[0][1]*(vals[1][0]*vals[2][2] - vals[2][0]*vals[1][2])
               + vals[0][2]*(vals[1][0]*vals[2][1] - vals[2][0]*vals[1][1]);
      return retVal;
    } else if (M == 2) {
      return (vals[0][0]*vals[1][1] - vals[1][0]*vals[0][1]);
    } else if (M == 1) {
      return vals[0][0];
    }

    if (recursiveMat) {
      boolean zeroRow = order();
      if (zeroRow) {
        retVal = 0.0;
      } else {
        double sign = gaussElim();
        retVal = diag_prod();
        retVal *= sign;
      }
    } else {
      Matrix detMatrix = new Matrix(this.values(), true);
      retVal = detMatrix.det();
    }

    return retVal;
  }

  /**
   * Performs in-place inversion via Crout LU decomposition for square matrices 
   * greater than 2 in size.  See notes in class private utility methods order,
   * decomp, and pivot.
   * <P>
   * For solving systems of linear equations, instead of using this method to
   * take the inverse and then multiplying by the vector of known values,
   * it is much more efficient to decompose this matrix via <code>croutLU</code>
   * and then use this class' <code>solve</code> method.  <code>cholesky</code>
   * and <code>solveCH</code> may also be more appropriate.
   *
   * @throws    VectorSpaceArgumentException   If not square (runtime)
   * @throws    SingularMatrixException        If not invertible (runtime)
   */
  public void invert() {
    if (!mxm  ||  N < 1) {
      throw new VectorSpaceArgumentException(
                                  "Can't invert non-square Matrix " + this);
    }

      // Manual inversion of 2x2 or scalar
    if (M < 3) {
      double d = this.det();
      if (Math.abs(d) < EPS) {
        throw new SingularMatrixException(
                 "Can't Invert Matrix:  Singular, or Near-Singular" + this);
      }
      if (M == 1) {
        vals[0][0] = 1.0/d;
      } else {
        double tmp = vals[1][1];
        vals[1][1] = vals[0][0]/d;
        vals[0][0] = tmp/d;
        vals[0][1] /= -d;
        vals[1][0] /= -d;
      }
      return;
    }

      // Decompose this matrix so it is suitable for inversion.
    croutLU();
    double absdet = Math.abs(diag_prod());
    if (absdet < EPS) {
      throw new SingularMatrixException(
               "Can't Invert Matrix:  Singular, or Near-Singular" + this);
    }

      // Each row of xmat will become a column of the inverted matrix.
    double xmat[][] = new double[N][N];
      // Unit vectors for solution with decomposed vals
    double ctup[] = new double[N];
    ctup[0] = 1.0;
      // Solve for each column of inverted matrix, then set local values
    solve(ctup, xmat[0]);
    for (int jj=1; jj<N; jj++) {
      ctup[jj-1] = 0.0;
      ctup[jj] = 1.0;
      solve(ctup, xmat[jj]);
    }
    set(xmat);
    transpose();
  }

  /*
   * Solve for x where c = Ax given decomp having been run (vals[][] = A).
   * <code>croutLU</code> MUST have already been called.
   * 
   * @param   cvals    Array of function values
   * @param   xvals    Array of values to be solved for
   */
  private void solve(double[] cvals, double[] xvals) {
    double sum;

    xvals[0] = cvals[row_order[0]]/vals[row_order[0]][0];
    for (int ii=1; ii<M; ii++) {
      sum = 0.0;
      for (int jj=0; jj<=ii-1; jj++) {
        sum += vals[row_order[ii]][jj]*xvals[jj];
      }
      xvals[ii] = (cvals[row_order[ii]] - sum)/vals[row_order[ii]][ii];
    }
    for (int ii=N-2; ii>=0; ii--) {
      sum = 0.0;
      for (int jj=ii+1; jj<N; jj++) {
        sum += vals[row_order[ii]][jj]*xvals[jj];
      }
      xvals[ii] -= sum;
    }
  }

  /*
   * Performs in-place Crout LU Decomposition.  The upper and lower
   * are combined into a single matrix knowing that the diagonal
   * elements of the upper matrix are 1's.  Employs partial pivoting 
   * to decomposes vals[][] into LU form.
   * <P>
   * Used for matrix inversion or solving systems of linear equations. 
   * <code>order</code> and other error checking must be done before this
   * method is called.
   * <P>
   * Algorithm based on "Numerical Methods for Engineers", 2nd ed. Steven C.
   * Chapra and Raymond P. Canale, 1988.
   */
  private void decomp() {
    int jj = 0;
    double sum;

      // For inversion, M should equal N
    pivot(jj);
    for (jj=1; jj<N; jj++) {
      vals[row_order[0]][jj] = vals[row_order[0]][jj]/vals[row_order[0]][0];
    }
    for (jj=1; jj<N-1; jj++) {
      for (int ii=jj; ii<M; ii++) {
        sum = 0.0;
        for (int kk=0; kk<=jj-1; kk++) {
          sum += vals[row_order[ii]][kk]*vals[row_order[kk]][jj];
        }
        vals[row_order[ii]][jj] -=  sum;
      }
      pivot(jj);
      for (int kk=jj+1; kk<M; kk++) {
        sum = 0.0;
        for (int ii=0; ii<=jj-1; ii++) {
          sum += vals[row_order[jj]][ii]*vals[row_order[ii]][kk];
        }
        vals[row_order[jj]][kk] = (vals[row_order[jj]][kk] - sum) /
                                   vals[row_order[jj]][jj];
      }
    }
    sum = 0.0;
    for (int kk=0; kk<N-1; kk++) {
      sum += vals[row_order[M-1]][kk]* vals[row_order[kk]][N-1];
    }
    vals[row_order[M-1]][N-1] = vals[row_order[M-1]][N-1] - sum;
  }

  /*
   * Fills two arrays, one element per Matrix row, where
   * the order is the row number and the scale is the absolute
   * value of the largest element in each row.  Allows rows to be
   * normalized by their largest components to avoid division by
   * zero and minimize round-off error.
   * Class variable outputs:
   *          row_order   Must be of dimension M.  Each element is set
   *                      to its index:  row_order = [0 1 2 ... M-1]
   *          row_scale   Absolute value of largest element in
   *                      corresponding row.  Must also be of
   *                      dimension M
   * @return              If a row is composed of (essentially) zeros,
   *                      true indicates this matrix will be singular,
   *                      or close to singular
   */
  private boolean order() {
    if (row_order == null) {
      row_order = new int[M];
      row_scale = new double[M];
    }
    double stmp = 0.0;
    boolean singular = false;

    for (int ii=0; ii<M; ii++) {
      row_order[ii] = ii;
      row_scale[ii] = Math.abs(vals[ii][0]);
      for (int jj=1; jj<N; jj++) {
        stmp = Math.abs(vals[ii][jj]);
        if (stmp > row_scale[ii]) {
          row_scale[ii] = stmp;
        }    
      }
        // Can't decompose if one of the rows is all zeros
      if (row_scale[ii] < EPS) {
          singular = true;
      }
    }
    return singular; 
  }

  /* 
   * Finds the row order about which to pivot during elimination.
   *
   * Row order values initially from order() and modified here
   * are used in partial pivoting during decomposition.
   * <code>order</code> must already have been called.  If order
   * discovered any rows of all zeros (any row_scale == 0.0), then
   * this method will suffer a divide by zero.
   *
   * Class variable outputs:
   *          row_order   Updated 
   * 
   * @param   jj          Pivot column
   */
  private void pivot(int jj) {
    double dum, big;
    int idum;
    int pivit = jj;

    big = Math.abs(vals[row_order[jj]][jj]/row_scale[jj]);
      // This for loop down rows
    for (int ii=jj+1; ii<M; ii++) {
      dum = Math.abs( vals[row_order[ii]][jj]/row_scale[ii] );
      if (dum > big) {
        big = dum;
        pivit = ii;
      }
    }
    idum = row_order[pivit];
    row_order[pivit] = row_order[jj];
    row_order[jj] = idum;
  }

  /*
   * Performs Gaussian Elimination to support the <code>det</code>
   * function.  Recommend only using for this.  <code>order</code>
   * must already have been called.
   * 
   * Algorithm mostly from "Numerical Methods for Engineers",
   * 2nd ed. Steven C. Chapra and Raymond P. Canale, 1988.  Added
   * sign determination to support determinants.
   *
   * @return      The sign to be multiplied by the product of the
   *              triangular matrix resulting from this operation
   *              when solving for the determinant of this matrix.
   */
  private double gaussElim() {
    double factor;
    for (int kk=0; kk<N-1; kk++) {
      pivot(kk);
      for (int ii=kk+1; ii<M; ii++) {
        factor = vals[row_order[ii]][kk]/vals[row_order[kk]][kk];
        for (int jj=kk+1; jj<N; jj++) {
          vals[row_order[ii]][jj] -= factor*vals[row_order[kk]][jj];
        }
      }
    }
      // Find the number of rows that are out of place.  The number of
      // swaps to result in this is one less.
    int swaps = 0;
    for (int ii=0; ii<M; ii++) {
      if (ii != row_order[ii]) {
        swaps ++;
      }
    }
      // If any are out of place, then at least two are out of place.
      // If the number of swaps is odd, then the determinant needs to
      // be multiplied by a -1 when evaluated by the product of the
      // diagonal elements.
    double sign = 1.0;
    if (swaps != 0) {
      swaps--;
      int even = swaps % 2;
      if (even != 0) {
        sign = -1.0;
      }
    }
    return sign;
  }

  /*
   * If row_order is initialized it is used to compute the product of
   * the diagonal elements.  Otherwise, the diagonal product is
   * computed based on the actual order in vals[][].
   * 
   * @return    Product of diagonal elements
   */
  private double diag_prod() {
    double prod = 1.0;
    if (row_order == null) {
      for (int ii=0; ii<M; ii++) {
        prod *= vals[ii][ii];
      }
    } else {
      for (int ii=0; ii<M; ii++) {
        prod *= vals[row_order[ii]][ii];
      }
    }
    return prod;
  }

}
