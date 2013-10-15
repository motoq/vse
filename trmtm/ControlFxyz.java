/*
 c  ControlFxyz.java
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

package com.motekew.vse.trmtm;

import com.motekew.vse.enums.Fxyz;
import com.motekew.vse.math.*;

/**
 * <code>ControlFxyz</code> is a <code>VectorSpace</code> representing
 * a control vector of a system that has forces acting in the X, Y, and
 * Z directions.  It is essentially a 3-tuple with accessor  methods that
 *  employ the <code>Fxyz</code> enum.  This makes it easier to keep track 
 * of what element is what when accessing those elements directly (instead
 * of looping on all elements).
 * <P>
 * Any attempt to access an element of this <code>VectorSpace</code>
 * that is out of range will result in a VectorSpaceIndexOutOfBoundsException
 * being thrown.  Note that integer indexing starts with 1, not zero.
 *
 * @author Kurt Motekew
 * @since  20070602
 */
public final class ControlFxyz extends Tuple {
  private static final int DIM = 3;
  /**
   * Default constructor - all elements set to zero.
   */
  public ControlFxyz() {
    super(DIM);
  }

  /**
   * Initialize the ControlFxyz using an array.  If the array is
   * not DIM elements long, a <code>VectorSpaceArgumentException</code>
   * will be thrown.
   *
   * @param   v_m    A double[] used to initialize this ControlFxyz
   */
  public ControlFxyz(double[] v_m) {
    super(v_m);
    if (v_m.length != DIM) {
     throw new VectorSpaceArgumentException("ControlFxyz must be initialized " +
                   "with an array of " + DIM + " elements, not:  " + v_m.length);
    }
  }

  /**
   * Sets the value of the ControlFxyz given a <code>Fxyz</code>
   * enum index.
   *
   * @param  ndx   A Fxyz index for the row
   * @param  val   A double value to be set for the ControlFxyz
   */
  public void put(Fxyz ndx, double val) {
    super.put(ndx.ordinal()+1, val);
  }

  /**
   * Gets the value of the ControlFxyz given a <code>Fxyz</code>
   * enum index.
   *
   * @param  ndx   A Fxyz index for the row
   *
   * @return       A double value for the requested element
   */
  public double get(Fxyz ndx) {
    return super.get(ndx.ordinal()+1);
  }
}
