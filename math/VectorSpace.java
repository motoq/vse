/*
 c  VectorSpace.java
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
 * <code>VectorSpace</code> is an abstract class defining the basic
 * methods (and as a result, attributes) of a VectorSpace.
 *
 * The concept of blades exist for potential future flexibility in case
 * it is necessary to make this class a subclass that is compatible with
 * geometric algebra.  A quick note on blades:  A scalar is a 0-blade,
 * a vector (of dimension 2 or greater - otherwise it is a scalar) is
 * a 1-blade.  And, a matrix is a 2-blade.
 *
 * This class assumes the VectorSpace will consist of the field of real
 * numbers.
 *
 * All indexing using integers (vs. enums) for subclass should start
 * with the value '1', and not '0'.
 *
 * @author Kurt Motekew
 * @since  20070529
 */

public abstract class VectorSpace {
  private final int BLADES;

  private String label = "";

  /**
   * The constructor sets the number of blades in this vectorspace.
   *
   * @param   numBlades    The int number of blades in this vectorspace.
   */
  public VectorSpace(int numBlades) {
    BLADES = numBlades;
  }

  /**
   * The number of blades in this VectorSpace.  A scalar is 0-blade.
   * A tuple is 1-blade, and a matrix is 2-blade....
   *
   * @return    int number of blades in this VectorSpace
   */
  public int getNumBlades() {
    return BLADES;
  }

  /**
   * Sets a label that is to be associated with this VectorSpace.
   *
   * @param  lbl     A String label that is to be applied to this
   *                 VectorSpace.
   */
  public void setLabel(String lbl) {
    label = lbl;
  }

  /**
   * Returns a label that is associated with this VectorSpace.
   *
   * @return         A String label for this VectorSpace
   */
  public String getLabel() {
    return label;
  }

  /**
   * Returns the dimension of the VectorSpace.  This would be the
   * number of rows for a vector, and the number of rows times the
   * number of columns for a matrix.  Just one for a scalar.
   *
   * @return    int number of dimensions
   */
  public abstract int getDimension();

  /**
   * Zeros all elements of this Vectorspace.
   */
  public abstract void zero();
}
