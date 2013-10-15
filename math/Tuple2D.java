/*
 c  Tuple2D.java
 c
 c  Copyright (C) 2012, 2013 Kurt Motekew
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
 * <code>Tuple2D</code> is a <code>Tuple</code> representing
 * a 2-tuple.  Any attempt to access an element of this 2-tuple
 * that is out of range (two elements) will result in a
 * VectorSpaceIndexOutOfBoundsException being thrown.  Note that integer
 * indexing starts with 1, not zero.
 *
 * @author Kurt Motekew
 * @since  20120911
 * @since  20130508   Pushed some fundamental functionality back up to
 *                    superclass.
 */

public class Tuple2D extends Tuple {
  private double wSign = 1.0;

  /**
   * Default constructor
   */
  public Tuple2D() {
    super(2);
  }

  /**
   * Initialize with the following two elements.
   *
   * @param  u    First element
   * @param  v    Second element
   */
  public Tuple2D(double u, double v) {
    super(2);
    put(1, u);
    put(2, v);
  }

  /**
   * Initialize the Tuple2D using an array.  If the array is
   * not 2 elements long, a <code>VectorSpaceArgumentException</code>
   * will be thrown.
   *
   * @param   t2d    A double[] used to initialize this Tuple2D
   */
  public Tuple2D(double[] t2d) {
    super(t2d);
    if (t2d.length != this.N) {
     throw new VectorSpaceArgumentException("Tuple2D must be initialized " +
               "with an array of " + this.N + " elements, not:  " + t2d.length);
    }
  }

  /**
   * Sets the U value of the Tuple2D.
   *
   * @param  val   Input double value
   */
  public void putU(double val) {
    super.put(1, val);
  }

  /**
   * Sets the V value of the Tuple2D.
   *
   * @param  val   Input double value
   */
  public void putV(double val) {
    super.put(2, val);
  }

  /**
   * Sets values for all two elements of the Tuple2D given scalars.
   *
   * @param  u   First element value.
   * @param  v   Second element value.
   */
  public void set(double u, double v) {
    this.put(1, u);
    this.put(2, v);
  }

  /**
   * Sets the 1st and 2nd elements of this Tuple2D with the first
   * and second elements of the input Tuple3D.  The input Tuple3D
   * should be a unit vector.  This set method is meant for use with
   * the getUnitVec() method.
   *
   * @param   t3d   3-element unit vector used to set this 2-element
   *                vector while preserving the sign.
   */
  public void set(Tuple3D t3d) {
    put(1, t3d.get(1));
    put(2, t3d.get(2));
    if (t3d.get(3) < 0.0) {
      wSign = -1.0;
    } else {
      wSign = 1.0;
    }
  }

  /**
   * Sets the values of this Tuple2D given a <code>Tuple</code> and a start
   * index.  The value at the start index, and the two subsequent values
   * in the input Tuple will be copied to the 1st, 2nd, and 3rd elements of
   * this Tuple2D.  If the input Tuple isn't long enough to supply all three
   * values, then a VectorSpaceIndexOutOfBoundsException will be thrown.
   * 
   * @param    <code>Tuple</code> from which to copy elements.
   * @param    Start index from where to begin copying elements.
   * 
   * @throws   Thrown if the input <code>Tuple</code> can't be accessed at the
   *           supplied index, and two subsequent values (input Tuple not long
   *           enough).
   */
  public void set(Tuple tin, int ndx) {
    int ndxend = ndx + 1;
    
    if (tin.N < ndxend) {
      throw new VectorSpaceIndexOutOfBoundsException(
          "Setting Tuple: index out of bounds:  (" + ndxend + ")");
    } else {
      put(1, tin.get(ndx));
      put(2, tin.get(ndx+1));
    }
  }

  /**
   * Gets the U value of the Tuple2D.
   *
   * @return       A double value for the requested element
   */
  public double getU() {
    return super.get(1);
  }

  /**
   * Gets the V value of the Tuple2D.
   *
   * @return       A double value for the requested element
   */
  public double getV() {
    return super.get(2);
  }

  /**
   * Generates a Tuple3D unit vector using the first and 2nd
   * components of this Tuple2D.  If this Tuple2D is already
   * larger than 1 in magnitude, then an exception will be
   * thrown (doing nothing could result in a hard to find bug -
   * better to have the system complain with a runtime exception).
   * Note the sign from the last call to set(Tuple3D) is preserved.
   *
   * @param   uvec    Output:  Unit vector derived from this
   *                  Tuple2D
   *
   * @throws         If this vector has a magnitude greater than 1.
   */
  public Tuple3D getUnitVec(Tuple3D uvec) {
    double w2 = 1.0 - this.dot(this);

    if (w2 < 0.0) {
      throw new VectorSpaceArgumentException(
        "Magnitude of input Tuple2D must be <= 1:  " + this);      
    } else {
      uvec.set(getU(), getV(), wSign*Math.sqrt(w2));
    }
    return uvec;
  }

  /**
   * Prints the elements of this <code>Tuple2D</code>
   */
  public String toString() {
    return("u v:  " + get(1) + " " + get(2));
  }
}
