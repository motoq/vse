/*
 c  Tuple.java
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

import com.motekew.vse.enums.Q;

/**
 * <code>Tuple</code> is a <code>VectorSpace</code> representing
 * a M-tuple.  Any attempt to access an element of this M-tuple
 * that is out of range will result in a VectorSpaceIndexOutOfBoundsException
 * being thrown.  Note that indexing starts with 1, not zero.
 *
 * @author Kurt Motekew
 * @since  20070531
 * @since  20130508   Simplified and sped a few things up.
 */
public class Tuple extends VectorSpace {
  /** Size of this vector */
  final int N;
  /** Dimension of this vector */
  final int DIM;

  private final double[] vals;                 // Tuple values
  private String[] labels = { "" };      // Labels associated with values

  /**
   * Default constructor - just initializes the size of this Tuple
   *
   * @param  m    A int for the length of the array.
   */
  public Tuple(int n) {
    super(1);               // one blade
    this.DIM = (n > 0) ? n : 1;
    this.N   = DIM;
    vals = new double[this.N];
  }

  /**
   * Initialize the Tuple using an array of doubles.  Values are
   * copied in.
   *
   * @param   tpl    A double[] used to initialize this Tuple
   */
  public Tuple(double[] tpl) {
    this(tpl.length);
    System.arraycopy(tpl, 0, this.vals, 0, N);
  }

  /**
   * Initialize the Tuple using another Tuple.  Values are
   * copied in.
   *
   * @param   tpl    Input Tuple to copy.
   */
  public Tuple(Tuple tpl) {
    this(tpl.length());
    this.set(tpl);
  }

  /**
   * Initialize this Tuple using a Matrix.
   * A <code>VectorSpaceArgumentException.java</code> will be thrown if
   * the incoming matrix isn't a column matrix.
   *
   * @param  mtx    A Matrix for which this Tuples's values are initialized.
   */
  public Tuple(Matrix mtx) {
    this(mtx.M);
    this.set(mtx);
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
   * Returns the number of rows in this VectorSpace
   *
   * @return   int number of rows (elements in the vector)
   */
  public int numRows() {
    return N;
  }

  /**
   * Returns the length of this vector (number of rows)
   *
   * @return   int number of rows (elements in the vector)
   */
  public int length() {
    return N;
  }  

  /**
   * Set all elements of this Tuple to zero.
   */
  @Override
  public void zero() {
    Arrays.fill(this.vals, 0.0);
  }

  /**
   * Sets the value of the i'th element.  Being a N-tuple, 1 <= i <= N.
   * A bad index will throw a VectorSpaceIndexOutOfBoundsException.
   *
   * @param  i     An int index for the element to set.
   * @param  val   A double value to be inserted into the ith element
   */
  public void put(int i, double val) {
    if (i > N || i < 1) {
      throw new VectorSpaceIndexOutOfBoundsException(
                   "Tuple indices out of bounds:  (" + i + ")"); 
    } else {
      vals[i-1] = val;
    }
  }

  /**
   * Gets the value of the i'th element.  Being a N-tuple, 1 <= i <= N.
   * A bad index will throw a VectorSpaceIndexOutOfBoundsException.
   *
   * @param  i     A int index for the the element to return.
   *
   * @return       A double value for the i'th element
   */
  public double get(int i) {
    if (i > N || i < 1) {
      throw new VectorSpaceIndexOutOfBoundsException(
                   "Tuple indices out of bounds:  (" + i + ")");
    } else {
      return vals[i-1];
    }
  }

  /**
   * Copy elements one for one from the passed Tuple into this one.
   * A <code>VectorSpaceArgumentException.java</code> will be thrown if
   * the dimensions do no match.
   *
   * @param  tup    A Tuple for which this Tuple's values are to be
   *                set.
   */
  public void set(Tuple tup) {
    if (tup.N == N) {
      System.arraycopy(tup.valuesPtr(), 0, this.vals, 0, N);
    } else {
     throw new VectorSpaceArgumentException("Tuple must be set " +
                   "with a Tuple of " + N + " elements, not:  " + tup.N);
    }
  }

  /**
   * Starting with the ndx'th element, sets three values of this Tuple to
   * those of the input 3-tuple.
   *
   * @param   ndx   Start index from where to begin putting elements.
   *                The first element is '1'.
   * @param   tup3  3-tuple from which to copy values.
   *
   * @throws        If the input 3-tuple won't fit into this tuple given
   *                the requested offset (ndx).
   */
  public void set(int ndx, Tuple3D tup3) {
    int ndxend = ndx + 2;

    if (N < ndxend) {
      throw new VectorSpaceIndexOutOfBoundsException(
          "Tuple: index out of bounds:  (" + ndxend + ")");
    } else {
      vals[ndx-1] = tup3.get(1);
      vals[ndx]   = tup3.get(2);
      vals[ndx+1] = tup3.get(3);
    }  
  }

  /**
   * Starting with the ndx'th element, sets four values of this Tuple to
   * those of the input Quaternion, with the 1st element being the
   * Quaternion scalar.
   *
   * @param   ndx   Start index from where to begin putting elements.
   *                The first element is '1'.
   * @param   tup3  Quaternion from which to copy values.
   *
   * @throws        If the input Quaternion won't fit into this tuple given
   *                the requested offset (ndx).
   */
  public void set(int ndx, Quaternion quat) {
    int ndxend = ndx + 3;

    if (N < ndxend) {
      throw new VectorSpaceIndexOutOfBoundsException(
          "Tuple: index out of bounds:  (" + ndxend + ")");
    } else {
      vals[ndx-1] = quat.get(Q.Q0);
      vals[ndx]   = quat.get(Q.QI);
      vals[ndx+1] = quat.get(Q.QJ);
      vals[ndx+2] = quat.get(Q.QK);
    }  
  }

  /**
   * Copy elements from the array passed in, into this tuple.
   * A <code>VectorSpaceArgumentException.java</code> will be thrown if
   * the dimensions do no match.
   *
   * @param  tpl     A double[] for which this Tuple's values are to be
   *                 set.
   */
  public void set(double[] tpl) {
    if (tpl.length == N) {
      System.arraycopy(tpl, 0, this.vals, 0, N);
    } else {
     throw new VectorSpaceArgumentException("Tuple must be set " +
                   "with an array of " + N + " elements, not:  " + tpl.length);
    }
  }

  /**
   * Copy elements one for one from the passed Matrix into this Tuple.
   * The incoming Matrix must have dimensions of Mx1.
   * A <code>VectorSpaceArgumentException.java</code> will be thrown if
   * the dimensions do no match.
   *
   * @param  mtx    A Matrix for which this Tuples's
   *                set.
   */
  public void set(Matrix mtx) {
    if (mtx.N != 1  ||  mtx.M != N) {
     throw new VectorSpaceArgumentException("Tuple must be set " +
                "with a Matrix of (" + N + ", " + 1 + ") elements, not:  (" +
                 mtx.M + ", " + mtx.N + ")");
    } else {
      double[][] mtxvals = mtx.valuesPtr();
      for (int ii=0; ii<N; ii++) {
        vals[ii] = mtxvals[ii][0];
      }
    }
  }

  /**
   * Returns an array representation of this tuple.  The array will
   * have the same dimensions as the tuple.                        
   *
   * @return      A double[] filled with this tuple's values.
   */
  public double[] values() {
    double[] tpl = new double[N];
    System.arraycopy(this.vals, 0, tpl, 0, N);
    return tpl;
  }

  /**
   * @return    A pointer to an array of the internally stored values.
   *            Use care.  Changes to this array affect the internally
   *            stored values!
   */
  double [] valuesPtr() {
    return vals;
  }

  /**
   * Sets the value of this <code>Tuple</code> to be the
   * sum of a and b:  this = a + b                        
   * <P>
   * If these dimensions don't match, a VectorSpaceArgumentException
   * will be thrown.
   *                              
   * @param a     First Tuple
   * @param b     Second Tuple
   *
   * @return      Pointer to this (the resulting) vector
   */                           
  public Tuple plus(Tuple a, Tuple b) {
    if (N == a.N  &&  N == b.N) {
      double[] avals = a.valuesPtr();
      double[] bvals = b.valuesPtr();
      for (int ii=0; ii<N; ii++) {
        vals[ii] = avals[ii] + bvals[ii];
      }
    } else {
      throw new VectorSpaceArgumentException("Tuple.add(Tuple a, Tuple b):  "
                                                  + "Dimensions must match.");
    }
    return this;
  }

  /**
   * Sets the value of this <code>Tuple</code> to be the
   * sum of itself and the input tuple:  this = this + a
   * <P>
   * If these dimensions don't match, a VectorSpaceArgumentException
   * will be thrown.
   *                              
   * @param a     Tuple to add to this one
   *
   * @return      Pointer to this (the resulting) vector
   */                           
  public Tuple plus(Tuple a) {
    if (N == a.N) {
      double[] avals = a.valuesPtr();
      for (int ii=0; ii<N; ii++) {
        vals[ii] += avals[ii];
      }
    } else {
      throw new VectorSpaceArgumentException("Tuple.add(Tuple a):  Dimensions "
                                              + "must match.");
    }
    return this;
  }

  /**
   * Sets the value of this <code>Tuple</code> to be the
   * difference of a and b:  this = a - b                        
   * <P>
   * If these dimensions don't match, a VectorSpaceArgumentException
   * will be thrown.
   *                              
   * @param a     First Tuple
   * @param b     Second Tuple
   *
   * @return      Pointer to this (the resulting) vector
   */                           
  public Tuple minus(Tuple a, Tuple b) {
    if (N == a.N  &&  N == b.N) {
      double[] avals = a.valuesPtr();
      double[] bvals = b.valuesPtr();
      for (int ii=0; ii<N; ii++) {
        vals[ii] = avals[ii] - bvals[ii];
      }
    } else {
      throw new VectorSpaceArgumentException("Tuple.subtr(Tuple a, Tuple b):  "
                                                  + "Dimensions must match.");
    }
    return this;
  }

  /**
   * Sets the value of this <code>Tuple</code> to be the
   * difference of itself and the input tuple:  this = this - a
   * <P>
   * If these dimensions don't match, a VectorSpaceArgumentException
   * will be thrown.
   *                              
   * @param a     Tuple to subtract from this one
   *
   * @return      Pointer to this (the resulting) vector
   */                           
  public Tuple minus(Tuple a) {
    if (N == a.N) {
      double[] avals = a.valuesPtr();
      for (int ii=0; ii<N; ii++) {
        vals[ii] -= avals[ii];
      }
    } else {
      throw new VectorSpaceArgumentException("Tuple.subtr(Tuple a):  " +
                                                   "Dimensions must match.");
    }
    return this;
  }

  /**
   * Sets the value of this <code>Tuple</code> to be the scalar
   * product of itself and the input scalar.                     
   *                                        
   * @param  s     Scalar to multiply this Tuple by
   *
   * @return       Pointer to this (the resulting) vector
   */
  public Tuple mult(double s) {
    for (int ii=0; ii<N; ii++) {
      vals[ii] *= s;
    }
    return this;
  }

  /**
   * Sets the value of this <code>Tuple</code> to be the scalar
   * product of itself and the inverse of the input scalar.
   *                                        
   * @param  s     Scalar to divide this Tuple by
   *
   * @return       Pointer to this (the resulting) vector
   */
  public Tuple div(double s) {
    mult(1.0/s);

    return this;
  }

  /**
   * Sets the value of this <code>Tuple</code> to be the product of a
   * <code>Matrix</code> and another <code>Tuple</code>
   * <P>
   * [MX1] = [MXN] * [NX1]
   * <P>
   * If these dimensions don't match, a VectorSpaceArgumentException
   * will be thrown.
   *
   * @param  mtr     Matrix [MXN]
   * @param  tpl     Tuple  [MX1]
   *
   * @return         Pointer to this (the resulting) vector
   */
  public Tuple mult(Matrix mtr, Tuple tpl) {
    int ii, jj;

      // Check dimension
    if (mtr.N == tpl.N  &&  mtr.M == N) {
      double[]   tplvals = tpl.valuesPtr();
      double[][] mtrvals = mtr.valuesPtr();
      for (ii=0; ii<N; ii++) {
        vals[ii] = 0.0;
        for (jj=0; jj<tpl.N; jj++) {
          vals[ii] += mtrvals[ii][jj] * tplvals[jj];
        }
      }
    } else {
      throw new VectorSpaceArgumentException("Dimensions for" +
                                   "Matrix * Tuple don't match");
    }

    return this;
  }

  /**
   * Sets the value of this Tuple to the product of the transpose
   * of the input matrix times the input Tuple:  this = A'*b
   *
   * @param   a   The number of columns must match the number of
   *              elements in this Tuple:  this.length() = a.numCols()
   * @param   b   The number of elements must match the length of
   *              this Tuple:  this.length() = b.length()
   */
  public void normalEqn(Matrix a, Tuple b) {
    Matrix aT = new Matrix(a.numCols(), a.numRows());
    aT.transpose(a);
    this.mult(aT, b);
  }

  /**
   * Sets the value of this Tuple to the product of the transpose
   * of the input matrix times the 2nd input matrix times the
   * input Tuple:  this = A'*W*b
   *
   * @param   a   The number of columns must match the number of
   *              elements in this Tuple:  this.length() = a.numCols()
   * @param   w   The number of rows and columns must match the length
   *              of this Tuple.
   * @param   b   The number of elements must match the length of
   *              this Tuple:  this.length() = b.length()
   */
  public void normalEqn(Matrix a, Matrix w, Tuple b) {
    Matrix aT = new Matrix(a.numCols(), a.numRows());
    aT.transpose(a);
    Matrix aTw = new Matrix(aT.numRows(), aT.numCols());
    aTw.mult(aT, w);
    this.mult(aTw, b);
  }

  /**
   * Returns the dot product of this Tuple and another
   * <P>
   * If these dimensions don't match, a VectorSpaceArgumentException
   * will be thrown.
   *
   * @param   tup   The Tuple to dot with this Tuple
   *
   * @return        The dot product of this tuple and another
   */
  public double dot(Tuple tup) {
    double dotVal = 0.0;
    if (N == tup.N) {
      double[] tupvals = tup.valuesPtr();
      for (int ii=0; ii<N; ii++) {
        dotVal += vals[ii] * tupvals[ii];
      }
    } else {
      throw new VectorSpaceArgumentException("Dimensions for" +
                                   "dot(Tuple tup) don't match");
    }
    return dotVal;
  } 

  /**
   * Returns the magnitude of this Tuple
   *
   * @return     the magintude of this Tuple
   */
  public double mag() {
    return( Math.sqrt(dot(this)) );
  }

  /**
   * Converts this Tuple into a unit vector by dividing each element
   * by its magnitude.
   */
  public void unitize() {
    mult(1.0/mag());
  }

  /**
   * Sets an array of labels that are to be associated with this Tuple.  This
   * can be used to label each element of the array, or a shorter list of
   * labels could repeat.
   *
   * @param  lbl[]   An array of labels that are to be copied into to this
   *                 Tuple (note they are actually copied, pointers are not
   *                 set).
   */                            
  public void setLabels(String[] lbls) {
    if (lbls != null) {
      labels = new String[lbls.length];
      for (int ii=0; ii<lbls.length; ii++) {
        labels[ii] = lbls[ii];
      }
    }
  }             

  /**
   * Returns a an array of String labels that are associated with this Tuple.
   *
   * @return         A new arrray of String labels - not the pointer to the
   *                 internally stored labels.
   */
  public String[] getLabels() {
    String[] lbls = new String[labels.length];
    for (int ii=0; ii<labels.length; ii++) {
      lbls[ii] = labels[ii];
    }
    return lbls;
  }            

  /**
   * Returns a string representation of the Tuple, with a newline separating
   * values.
   */  
  public String toString() {
    String output = "";
    for (int ii=0; ii<N; ii++) {
      output = output + "  " + vals[ii] + '\n';
    }
    return output;
  }

}
