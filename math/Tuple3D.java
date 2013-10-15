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

import com.motekew.vse.enums.Basis3D;

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
 */
public class Tuple3D extends Tuple {
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
   * Sets three subsequent values of the input <code>Tuple</code> starting with
   * the input index value, to be the values in this Tuple3D.  If the input Tuple
   * isn't long enough, a VectorSpaceIndexOutOfBoundsException will be thrown.
   * 
   * @param    Start index from where to begin putting elements.
   * @param    <code>Tuple</code> into which elements are to be copied.
   * 
   * @throws   Thrown if the input <code>Tuple</code> can't be accessed at the
   *           supplied, and two subsequent indices.
   */
  public void mget(int ndx, Tuple tout) {
    int ndxend = ndx + 2;
    
    if (tout.N < ndxend) {
      throw new VectorSpaceIndexOutOfBoundsException(
          "Tuple: index out of bounds:  (" + ndxend + ")");
    } else {
      tout.put(ndx, get(1));
      tout.put(ndx+1, get(2));
      tout.put(ndxend, get(3));
    }
  }

  /**
   * Gets the value of the Tuple3D given a <code>Basis3D</code>
   * enum index.
   *
   * @param  ndx   A Basis3D index for the row
   *
   * @return       A double value for the requested element
   */
  public double get(Basis3D ndx) {
    return vals[ndx.ordinal()];
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the
   * sum of a and b:  this = a + b
   *
   * @param a     First Tuple3D
   * @param b     Second Tuple3D
   * 
   * @return      Pointer to this (the resulting) vector
   */
  public Tuple3D plus(Tuple3D a, Tuple3D b) {
    double[] aptr = a.valuesPtr();
    double[] bptr = b.valuesPtr();

    vals[0] = aptr[0] + bptr[0];
    vals[1] = aptr[1] + bptr[1];
    vals[2] = aptr[2] + bptr[2];

    return this;
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the
   * sum of itself and the input tuple:  this = this + a
   *
   * @param a     Tuple to add to this one
   * 
   * @return      Pointer to this (the resulting) vector
   */
  public Tuple3D plus(Tuple3D a) {
    double[] aptr = a.valuesPtr();

    vals[0] += aptr[0];
    vals[1] += aptr[1];
    vals[2] += aptr[2];

    return this;
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the
   * difference of a and b:  this = a - b
   *
   * @param a     First Tuple3D
   * @param b     Second Tuple3D
   * 
   * @return      Pointer to this (the resulting) vector
   */
  public Tuple3D minus(Tuple3D a, Tuple3D b) {
    double[] aptr = a.valuesPtr();
    double[] bptr = b.valuesPtr();

    vals[0] = aptr[0] - bptr[0];
    vals[1] = aptr[1] - bptr[1];
    vals[2] = aptr[2] - bptr[2];

    return this;
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the
   * difference of this and a:  this = this - a
   *
   * @param a     Tuple to subtract from this one
   * 
   * @return      Pointer to this (the resulting) vector
   */
  public Tuple3D minus(Tuple3D a) {
    double[] aptr = a.valuesPtr();

    vals[0] -= aptr[0];
    vals[1] -= aptr[1];
    vals[2] -= aptr[2];

    return this;
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the
   * cross product of a and b:  this = aXb
   *
   * @param a     First Tuple3D (aXb)
   * @param b     Second Tuple3D (aXb)
   * 
   * @return      Pointer to this (the resulting) vector
   */
  public Tuple3D cross(Tuple3D a, Tuple3D b) {
    double[] aptr = a.valuesPtr();
    double[] bptr = b.valuesPtr();

    vals[0] = aptr[1]*bptr[2] - aptr[2]*bptr[1];
    vals[1] = aptr[2]*bptr[0] - aptr[0]*bptr[2];
    vals[2] = aptr[0]*bptr[1] - aptr[1]*bptr[0];

    return this;
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the scalar
   * product of itself and the input scalar.
   *
   * @param  s     Scalar to multiply this Tuple3D by
   * 
   * @return       Pointer to this (the resulting) vector
   */
  public Tuple3D mult(double s) {
    vals[0] *= s;
    vals[1] *= s;
    vals[2] *= s;

    return this;
  }

  /**
   * Sets the value of this <code>Tuple3D</code> to be the scalar
   * product of itself and the inverse of the input scalar.
   *
   * @param  s     Scalar to divide this Tuple3D by
   *
   * @return       Pointer to this (the resulting) vector
   */
  public Tuple3D div(double s) {
    mult(1.0/s);

    return this;
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
   * Prints the elements of this <code>Tuple3D</code>
   */
  public String toString() {
    return("x y z:  " + get(1) + " " + get(2) + " " + get(3));
  }
}
