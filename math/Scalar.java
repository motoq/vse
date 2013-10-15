/*
 c  Scalar.java
 c
 c  Copyright (C) 2000, 2007 Kurt Motekew
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
 * <code>Scalar</code> is a <code>VectorSpace</code> representing
 * a simple scalar.
 *
 * @author Kurt Motekew
 * @since  20070530
 */
public class Scalar extends VectorSpace {
  private final int DIM    = 1;

  private double value     = 0.0;

  /**
   * Initializes this Scalar with a value.
   *
   * @param  val   A double intial value for this Scalar
   */
  public Scalar(double val) {
    super(0);       // 0-blade
    value = val;
  }

  /**
   * Default constructor with no arguments - just sets the
   * number of blades.
   */
  public Scalar() {
    super(0);      // 0-blade
  }

  /**
   * Returns the dimension of the VectorSpace:  1
   *
   * @return    int number of dimensions
   */
  @Override
  public int getDimension() {
    return DIM;
  }

  /**
   * Set the value of this Scalar to zero.
   */
  @Override
  public void zero() {
    value = 0.0;
  }

  /**
   * Sets the value of the Scalar.
   *
   * @param  val   A double value to be set for the Scalar
   */
  public void put(double val) {
    this.value = val;
  }

  /**
   * Gets the value of the Scalar.
   *
   * @return       The double value of the Scalar
   */
  public double get() {
    return value;
  }
}
