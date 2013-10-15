/*
 c  State6DOF.java
 c
 c  Copyright (C) 2000, 2008 Kurt Motekew
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

package com.motekew.vse.trmtm;

import com.motekew.vse.enums.XdX6DOF;
import com.motekew.vse.math.*;

/**
 * <code>State6DOF</code> is a <code>VectorSpace</code> representing
 * a three dimensional position vector of a state, and its derivative (say,
 * position and velocity), along with the attitude and attitude rates.  It
 * is a 12-tuple with accessor methods that employ the <code>XdX6DOF</code>
 * enum.  This makes it easier to keep track of what element is what when
 * accessing those elements directly (instead of looping on all elements).
 * <P>
 * Any attempt to access an element of this <code>VectorSpace</code>
 * that is out of range will result in a VectorSpaceIndexOutOfBoundsException
 * being thrown.  Note that integer indexing starts with 1, not zero.
 *
 * @author Kurt Motekew
 * @since  20080824
 */
public final class State6DOF extends Tuple {
  private static final int DIM = 12;
  /**
   * Default constructor - all elements set to zero.
   */
  public State6DOF() {
    super(DIM);
  }

  /**
   * Initialize the State6DOF using an array.  If the array is
   * not DIM elements long, a <code>VectorSpaceArgumentException</code>
   * will be thrown.
   *
   * @param   v_m    A double[] used to initialize this State6DOF
   */
  public State6DOF(double[] v_m) {
    super(v_m);
    if (v_m.length != DIM) {
     throw new VectorSpaceArgumentException("State6DOF must be initialized " +
                   "with an array of " + DIM + " elements, not:  " + v_m.length);
    }
  }

  /**
   * Sets the value of the State6DOF given a <code>XdX6DOF</code>
   * enum index.
   *
   * @param  ndx   A XdX6DOF index for the row
   * @param  val   A double value to be set for the State6DOF
   */
  public void put(XdX6DOF ndx, double val) {
    super.put(ndx.ordinal()+1, val);
  }

  /**
   * Gets the value of the State6DOF given a <code>XdX6DOF</code>
   * enum index.
   *
   * @param  ndx   A XdX6DOF index for the row
   *
   * @return       A double value for the requested element
   */
  public double get(XdX6DOF ndx) {
    return super.get(ndx.ordinal()+1);
  }
}
