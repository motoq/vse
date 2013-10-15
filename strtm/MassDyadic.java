/*
 c  MassDyadic.java
 c
 c  Copyright (C) 2000, 2010 Kurt Motekew
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

package com.motekew.vse.strtm;

import com.motekew.vse.enums.Basis3D;
import com.motekew.vse.math.Matrix3X3;

/**
 * <code>MassDyadic</code> is a <code>Matrix3X3</code> representing
 * the total mass of a body and its inertia dyadic (subset of tensors).
 * The putJ() method exists to make it easier to enter components.
 * <P>
 * The inverse of the inertia matrix is stored internally.  Use of the
 * putJ() method automatically flags this object to refresh this inverse
 * before use.  If a superclass is used to modify the elements of this
 * object, it is important that the setInvRefresh() be called.
 *
 * @author Kurt Motekew
 * @since  20090126
 */

public class MassDyadic extends Matrix3X3 {
  private double mass = 1.0;

    // Since the inverse of the dyadic is often used in rotational
    // dynamics, store it locally for easy use.  Use a flag to
    // indicate if a new inverse needs to be computed.
  private Matrix3X3 inverse = new Matrix3X3();                   
  private boolean mustRefreshInverse = true;

  /**
   * Default constructor - just call the superclass constructor....
   */
  public MassDyadic() {
    super();                 // 3X3 matrix
    
      // Default mass.
    setMass(1.0);
      // Set principle moments to 1.  Leave others zero setting two
      // planes of symmetry by default (like a missile).
    putJ(Basis3D.I,Basis3D.I, 1.0);
    putJ(Basis3D.J,Basis3D.J, 1.0);
    putJ(Basis3D.K,Basis3D.K, 1.0);
  }

  /**
   * @return         Mass of the object.
   */
  public double getMass() {
    return mass;
  }

  /**
   * Sets the mass of the object.
   *
   * @param    newM  New mass of the object.
   */
  public void setMass(double newM) {
    mass = newM;
  }

  /**
   * Sets elements of the object's inertia matrix.
   * <P>
   * Symmetry is forced with this put() method.  Don't override
   * superclass put()!
   * <P>
   * This is the best way to update elements of the inertia matrix.
   * The class will automatically flag the need to recompute the
   * inverse of this matrix.
   *
   * @param    row  Row of element of inertia matrix to set.
   * @param    col  Column of element of inertia matrix to set.
   * @param    val  New element value.  Note that (row,col) will
   *                be set to (col,row) if row != col (symmetrix
   *                matrix).  Also note that off diagonal elements
   *                are negated.                                  
   */
  public void putJ(Basis3D row, Basis3D col, double val) {
    mustRefreshInverse = true;
    if (row == col  ||  val == 0.0) {
      put(row, col, val);
    } else {
      putSym(row, col, -val);
    }
  }

  /**
   * Gets the elements of the object's inertial matrix.  Off diagonal
   * elements are negated!  The superclass get() method can still be
   * used to get the values as they exist internally.
   *
   * @param    row  Row of element of inertia matrix to get.
   * @param    col  Column of element of inertia matrix to get.
   *
   * @return        Element value.  Off diagonal elements are negated
   *                before being returned.
   */
  public double getJ(Basis3D row, Basis3D col) {
    double val = get(row, col);

    if (row == col  ||  val == 0.0) {
      return val;
    } else {
      return -1.0*val;
    }
  }

  /**
   * Flags this class to recompute the inverse of the inertia matrix.
   */
  public void setInvRefresh() {
    mustRefreshInverse = true;
  }

  /**
   * Returns a pointer to the locally stored inverse of this
   * inertia matrix.  If a superclass method was used to modify the
   * elements of this inertia matrix, be sure to call
   * setInvRefresh() to force the local computation of a new
   * inverse.  The safest thing is to simply use putJ().
   *
   * @return    Inverse of this.
   */
  public Matrix3X3 getInv() {
    checkInv();
    return inverse;
  }

  /*
   * Refreshes the dyadic inverse if the dyadic matrix has been
   * changed.                                                
   */
  private void checkInv() {
    if (mustRefreshInverse) {
      inverse.invert(this);
      mustRefreshInverse = false;
    }
  }

}
