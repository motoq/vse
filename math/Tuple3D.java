/*
 c  Tuple3D.java
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

import com.motekew.vse.enums.IGetBasis3D;
import com.motekew.vse.enums.Basis3D;
import com.motekew.vse.enums.Q;

/**
 * <code>Tuple3D</code> is a <code>Tuple</code> representing
 * a 3-tuple.  Any attempt to access an element of this 3-tuple
 * that is out of range (three elements) will result in a
 * VectorSpaceIndexOutOfBoundsException being thrown.  Note that integer
 * indexing starts with 1, not zero.
 * <P>
 * Many operations are explicitly written out to eliminate loops.  This used
 * to give an order of magnitude increase in speed, but the compiler and
 * run time environment have become smart enough to make this type of
 * optimization unnecessary.  These methods are still here only because
 * there is no reason to remove them....
 *
 * @author Kurt Motekew
 * @since  20070531
 * @since  20130508   Made things a bit more efficient.
 * @since  20131109   Added IGetBasis3D interface and related changes.
 */
public class Tuple3D extends Tuple implements IGetBasis3D {
  private final double[] vals;

  /**
   * Default constructor
   */
  public Tuple3D() {
    super(3);
    vals = valuesPtr();
  }

  /**
   * Initialize with the following three elements.
   *
   * @param  x    First element
   * @param  y    Second element
   * @param  z    Third element
   */
  public Tuple3D(double x, double y, double z) {
    this();
    vals[0] = x;
    vals[1] = y;
    vals[2] = z;
  }

  /**
   * Initialize the Tuple3D using an array.  If the array is
   * not 3 elements long, a <code>VectorSpaceArgumentException</code>
   * will be thrown.
   *
   * @param   t3d    A double[] used to initialize this Tuple3D
   */
  public Tuple3D(double[] t3d) {
    this();
    if (t3d.length != 3) {
     throw new VectorSpaceArgumentException("Tuple3D must be initialized " +
               "with an array of " + 3 + " elements, not:  " + t3d.length);
    } else {
      System.arraycopy(t3d, 0, vals, 0, super.N);
    }
  }

  /**
   * Sets the value of the Tuple3D given a <code>Basis3D</code>
   * enum index.
   *
   * @param  ndx   A Basis3D index for the row
   * @param  val   A double value to be set for the Tuple3D
   */
  public void put(Basis3D ndx, double val) {
    vals[ndx.ordinal()] = val;
  }

  /**
   * Sets values for all three elements of the Tuple3D given scalars.
   *
   * @param  i   First element value.
   * @param  j   Second element value.
   * @param  k   Third element value.
   */
  public void set(double i, double j, double k) {
    vals[0] = i;
    vals[1] = j;
    vals[2] = k;
  }

  /**
   * Sets the values of this Tuple3D given a <code>Tuple</code> and a start
   * index.  The value at the start index, and the two subsequent values
   * in the input Tuple will be copied to the 1st, 2nd, and 3rd elements of
   * this Tuple3D.  If the input Tuple isn't long enough to supply all three
   * values, then a VectorSpaceIndexOutOfBoundsException will be thrown.
   * 
   * @param    <code>Tuple</code> from which to copy elements.
   * @param    Start index from where to begin copying elements (1 based).
   * 
   * @throws   Thrown if the input <code>Tuple</code> can't be accessed at the
   *           supplied index, and two subsequent values (input Tuple not long
   *           enough).
   */
  public void set(Tuple tin, int ndx) {
    int ndxend = ndx + 2;

    if (tin.N < ndxend) {
      throw new VectorSpaceIndexOutOfBoundsException(
          "Setting Tuple: index out of bounds:  (" + ndxend + ")");
    } else {
      put(1, tin.get(ndx));
      put(2, tin.get(ndx+1));
      put(3, tin.get(ndxend));
    }
  }

  /**
   * Necessary to avoid ambiguity with the <code>Tuple</code> set
   * method and the Tuple3D set method below using IGetBasis3D.
   *
   * @param   t3d   Input 3-tuple with new values to copy.
   */
  public void set(Tuple3D t3d) {
    super.set(t3d);
  }

  /**
   * Sets this Tuple3D to the values associated with the input
   * object implementing the <code>IGetBasis3D</code> interface.
   *
   * @param   b3d   Input 3-tuple with new values to adopt.
   */
  public void set(IGetBasis3D b3d) {
    this.put(1, b3d.get(Basis3D.I));
    this.put(2, b3d.get(Basis3D.J));
    this.put(3, b3d.get(Basis3D.K));
  }

  /**
   * Gets the value of the Tuple3D given a <code>Basis3D</code>
   * enum index.
   *
   * @param  ndx   A Basis3D index for the row
   *
   * @return       A double value for the requested element
   */
  @Override
  public double get(Basis3D ndx) {
    return vals[ndx.ordinal()];
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the
   * sum of a and b:  this = a + b
   *
   * @param a     First Tuple3D
   * @param b     Second Tuple3D
   */
  public void plus(Tuple3D a, Tuple3D b) {
    double[] aptr = a.valuesPtr();
    double[] bptr = b.valuesPtr();

    vals[0] = aptr[0] + bptr[0];
    vals[1] = aptr[1] + bptr[1];
    vals[2] = aptr[2] + bptr[2];
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the
   * sum of itself and the input tuple:  this = this + a
   *
   * @param a     Tuple to add to this one
   */
  public void plus(Tuple3D a) {
    double[] aptr = a.valuesPtr();

    vals[0] += aptr[0];
    vals[1] += aptr[1];
    vals[2] += aptr[2];
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the
   * difference of a and b:  this = a - b
   *
   * @param a     First Tuple3D
   * @param b     Second Tuple3D
   */
  public void minus(Tuple3D a, Tuple3D b) {
    double[] aptr = a.valuesPtr();
    double[] bptr = b.valuesPtr();

    vals[0] = aptr[0] - bptr[0];
    vals[1] = aptr[1] - bptr[1];
    vals[2] = aptr[2] - bptr[2];
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the
   * difference of this and a:  this = this - a
   *
   * @param a     Tuple to subtract from this one
   */
  public void minus(Tuple3D a) {
    double[] aptr = a.valuesPtr();

    vals[0] -= aptr[0];
    vals[1] -= aptr[1];
    vals[2] -= aptr[2];
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the
   * cross product of a and b:  this = aXb
   *
   * @param a     First Tuple3D (aXb)
   * @param b     Second Tuple3D (aXb)
   */
  public void cross(Tuple3D a, Tuple3D b) {
    double[] aptr = a.valuesPtr();
    double[] bptr = b.valuesPtr();

    vals[0] = aptr[1]*bptr[2] - aptr[2]*bptr[1];
    vals[1] = aptr[2]*bptr[0] - aptr[0]*bptr[2];
    vals[2] = aptr[0]*bptr[1] - aptr[1]*bptr[0];
  }

  /**
   * qvq*   (quaternion multiplication performed left to right)
   *
   * Uses the input unit quaternion to perform a point rotation on the
   * input <code>Tuple3D</code>.
   *
   * @param   q   Unit quaternion
   * @param   r   Vector to be rotated
   */
  public void vRot(Quaternion q, Tuple3D r) {
    double q0 = q.get(Q.Q0);
    double qi = q.get(Q.QI);
    double qj = q.get(Q.QJ);
    double qk = q.get(Q.QK);
      //
    double q0q0 = q0*q0;
    double q0qi = q0*qi;
    double q0qj = q0*qj;
    double q0qk = q0*qk;
    double qiqj = qi*qj;
    double qiqk = qi*qk;
    double qjqk = qj*qk;
      //
    double q11 = 2.0*(q0q0 + qi*qi) - 1.0;
    double q21 = 2.0*(qiqj + q0qk);
    double q31 = 2.0*(qiqk - q0qj);
    double q12 = 2.0*(qiqj - q0qk);
    double q22 = 2.0*(q0q0 + qj*qj) - 1.0;
    double q32 = 2.0*(qjqk + q0qi);
    double q13 = 2.0*(qiqk + q0qj);
    double q23 = 2.0*(qjqk - q0qi);
    double q33 = 2.0*(q0q0 + qk*qk) - 1.0;
      //
    double[] ivals = r.valuesPtr();            
    double[] ovals = this.valuesPtr();

    ovals[0] = q11*ivals[0] + q12*ivals[1] + q13*ivals[2];
    ovals[1] = q21*ivals[0] + q22*ivals[1] + q23*ivals[2];
    ovals[2] = q31*ivals[0] + q32*ivals[1] + q33*ivals[2];
  }

  /**
   * q*vq   (quaternion multiplication performed left to right)
   *
   * Uses the input unit quaternion to perform a reference frame rotation on
   * the input <code>Tuple3D</code>.
   *
   * @param   q   Unit quaternion
   * @param   r   Vector to be subjected to a reference frame transformation.
   */
  public void fRot(Quaternion q, Tuple3D r) {
    double q0 = q.get(Q.Q0);
    double qi = q.get(Q.QI);
    double qj = q.get(Q.QJ);
    double qk = q.get(Q.QK);
      //
    double q0q0 = q0*q0;
    double q0qi = q0*qi;
    double q0qj = q0*qj;
    double q0qk = q0*qk;
    double qiqj = qi*qj;
    double qiqk = qi*qk;
    double qjqk = qj*qk;
      //
    double q11 = 2.0*(q0q0 + qi*qi) - 1.0;
    double q21 = 2.0*(qiqj + q0qk);
    double q31 = 2.0*(qiqk - q0qj);
    double q12 = 2.0*(qiqj - q0qk);
    double q22 = 2.0*(q0q0 + qj*qj) - 1.0;
    double q32 = 2.0*(qjqk + q0qi);
    double q13 = 2.0*(qiqk + q0qj);
    double q23 = 2.0*(qjqk - q0qi);
    double q33 = 2.0*(q0q0 + qk*qk) - 1.0;
      //
    double[] ivals = r.valuesPtr();            
    double[] ovals = this.valuesPtr();

    ovals[0] = q11*ivals[0] + q21*ivals[1] + q31*ivals[2];
    ovals[1] = q12*ivals[0] + q22*ivals[1] + q32*ivals[2];
    ovals[2] = q13*ivals[0] + q23*ivals[1] + q33*ivals[2];
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the scalar
   * product of itself and the input scalar.
   *
   * @param  s     Scalar to multiply this Tuple3D by
   */
  public void mult(double s) {
    vals[0] *= s;
    vals[1] *= s;
    vals[2] *= s;
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the scalar
   * product of itself and the inverse of the input scalar.
   *
   * @param  s     Scalar to divide this Tuple3D by
   */
  public void div(double s) {
    mult(1.0/s);
  }

  /**
   * Returns the dot product of this Tuple and another
   *
   * @param   tup   The tuple to dot with this tuple
   *
   * @return        The dot product of this tuple and another
   */
  public double dot(Tuple3D tup) {
    double[] tupvals = tup.valuesPtr();
    return vals[0]*tupvals[0] + vals[1]*tupvals[1] + vals[2]*tupvals[2];
  }

  /**
   * Returns the magnitude of this Tuple
   *
   * @return     the magintude of this Tuple
   */
  public double mag() {
    return( Math.sqrt(this.dot(this)) );
  }

  /**
   * Converts this vector into a unit vector by dividing each element
   * by its magnitude.
   */
  public void unitize() {
    this.mult(1.0/this.mag());
  }
 
  /**
   * Generates a Tuple3D unit vector using the first and 2nd
   * components of the input Tuple2D.  If the Tuple2D is already
   * larger than 1 in magnitude, then an exception will be
   * thrown (doing nothing could result in a hard to find bug -
   * better to have the system complain with a runtime exception).
   * Note the sign from the last call to set(Tuple3D) is preserved.
   *
   * @param   uv      2D vector from which to derive a 3D unit vector.
   *
   * @throws         If this vector has a magnitude greater than 1.
   */
  public void setUnitVec(Tuple2D uv) {
    double w2 = 1.0 - uv.dot(uv);

    if (w2 < 0.0) {
      throw new VectorSpaceArgumentException(
        "Magnitude of input Tuple2D must be <= 1:  " + uv);      
    } else {
      set(uv.getU(), uv.getV(), uv.getWSign()*Math.sqrt(w2));
    }
  }

  /**
   * Prints the elements of this <code>Tuple3D</code>
   */
  public String toString() {
    return("x y z:  " + get(1) + " " + get(2) + " " + get(3));
  }
}

/* Works but has issues under some conditions
  public void fRot(Quaternion q, Tuple3D r) {
    double[] rvals = r.valuesPtr();          
    double ri = rvals[0];          
    double rj = rvals[1];
    double rk = rvals[2];
    double qs = q.get(Q.Q0);
    double qi = q.get(Q.QI);
    double qj = q.get(Q.QJ);
    double qk = q.get(Q.QK);
    double rdq = ri*qi + rj*qj + rk*qk;
      //
    double[] ovals = this.valuesPtr();

    ovals[0] = 2.0*(qs*(ri*qs + rj*qk - rk*qj) + qi*rdq) - ri;
    ovals[1] = 2.0*(qs*(rj*qs + rk*qi - ri*qk) + qj*rdq) - rj;
    ovals[2] = 2.0*(qs*(rk*qs + ri*qj - rj*qi) + qk*rdq) - rk;
  }
 */