/*
 c  StateXdX3D.java
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

import com.motekew.vse.enums.XdX3D;
import com.motekew.vse.math.*;

/**
 * <code>StateXdX3D</code> is a <code>VectorSpace</code> representing
 * a three dimensional state vector of a state, and its derivative (say,
 * position and velocity).  It is essentially a 6-tuple with accessor
 * methods that employ the <code>XdX3D</code> enum.  This makes it easier
 * to keep track of what element is what when accessing those elements
 * directly (instead of looping on all elements).
 * <P>
 * Any attempt to access an element of this <code>VectorSpace</code>
 * that is out of range will result in a VectorSpaceIndexOutOfBoundsException
 * being thrown.  Note that integer indexing starts with 1, not zero.
 *
 * @author Kurt Motekew
 * @since  20070602
 */
public final class StateXdX3D extends Tuple {
  private static final int DIM = 6;

    // Used to see if labels for this extension of the Tuple have
    // been initialized.  Don't bother wasting the memory setting
    // unless the names have been asked for via getLabels();
  private boolean needToInitLabels = true;

  /**
   * Default constructor - all elements set to zero.
   */
  public StateXdX3D() {
    super(DIM);
  }

  /**
   * Initialize the StateXdX3D using an array.  If the array is
   * not DIM elements long, a <code>VectorSpaceArgumentException</code>
   * will be thrown.
   *
   * @param   v_m    A double[] used to initialize this StateXdX3D
   */
  public StateXdX3D(double[] v_m) {
    super(v_m);
    if (v_m.length != DIM) {
     throw new VectorSpaceArgumentException("StateXdX3D must be initialized " +
                  "with an array of " + DIM + " elements, not:  " + v_m.length);
    }
  }

  /**
   * Returns an array of String labels for each element of this StateXdX3D
   * Tuple.
   *
   * @return         A new arrray of String labels - not the pointer to the
   *                 internally stored labels.                             
   */
  @Override
  public String[] getLabels() {
    if (needToInitLabels) {
      needToInitLabels = false;
      String[] names = new String[XdX3D.values().length];
      for (XdX3D ii : XdX3D.values()) {
        names[ii.ordinal()] = ii.toString();
      }
      super.setLabels(names);
      return names;
    } else {
      return super.getLabels();
    }
  }

  /**
   * Sets an array of labels that are to be associated with this StateXdX3D.
   * It is probably best to use the default labels.
   *                     
   * @param  lbl[]   An array of labels that are to be copied into to this
   *                 StateXdX3D.
   */
  @Override
  public void setLabels(String[] lbls) {
    needToInitLabels = false;
    super.setLabels(lbls);
  }

  /**
   * Sets the value of the StateXdX3D given a <code>XdX3D</code>
   * enum index.
   *
   * @param  ndx   A XdX3D index for the row
   * @param  val   A double value to be set for the StateXdX3D
   */
  public void put(XdX3D ndx, double val) {
    super.put(ndx.ordinal()+1, val);
  }

  /**
   * Gets the value of the StateXdX3D given a <code>XdX3D</code>
   * enum index.
   *
   * @param  ndx   A XdX3D index for the row
   *
   * @return       A double value for the requested element
   */
  public double get(XdX3D ndx) {
    return super.get(ndx.ordinal()+1);
  }
}
