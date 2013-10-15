/*
 c  StateX3D.java
 c
 c  Copyright (C) 2011 Kurt Motekew
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

import com.motekew.vse.enums.X3D;
import com.motekew.vse.math.Tuple3D;
import com.motekew.vse.math.VectorSpaceArgumentException;

/**
 * <code>StateX3D</code> is a <code>VectorSpace</code> representing
 * a three dimensional state vector of position.
 * <P>
 * Any attempt to access an element of this <code>VectorSpace</code>
 * that is out of range will result in a VectorSpaceIndexOutOfBoundsException
 * being thrown.  Note that integer indexing starts with 1, not zero.
 *
 * @author Kurt Motekew
 * @since  20070602
 */
public final class StateX3D extends Tuple3D {
  private static int DIM;

    // Used to see if labels for this extension of the Tuple3D have
    // been initialized.  Don't bother wasting the memory setting
    // unless the names have been asked for via getLabels();
  private boolean needToInitLabels = true;

  /**
   * Default constructor - all elements set to zero.
   */
  public StateX3D() {
    super();
    DIM = getDimension();
  }

  /**
   * Initialize the StateX3D using an array.  If the array is
   * not DIM elements long, a <code>VectorSpaceArgumentException</code>
   * will be thrown.
   *
   * @param   v_m    A double[] used to initialize this StateX3D
   */
  public StateX3D(double[] v_m) {
    super(v_m);
    if (v_m.length != DIM) {
     throw new VectorSpaceArgumentException("StateX3D must be initialized " +
                  "with an array of " + DIM + " elements, not:  " + v_m.length);
    }
  }

  /**
   * Returns an array of String labels for each element of this StateX3D
   * Tuple.
   *
   * @return         A new arrray of String labels - not the pointer to the
   *                 internally stored labels.                             
   */
  @Override
  public String[] getLabels() {
    if (needToInitLabels) {
      needToInitLabels = false;
      String[] names = new String[X3D.values().length];
      for (X3D ii : X3D.values()) {
        names[ii.ordinal()] = ii.toString();
      }
      super.setLabels(names);
      return names;
    } else {
      return super.getLabels();
    }
  }

  /**
   * Sets an array of labels that are to be associated with this StateX3D.
   * It is probably best to use the default labels.
   *                     
   * @param  lbl[]   An array of labels that are to be copied into to this
   *                 StateX3D.
   */
  @Override
  public void setLabels(String[] lbls) {
    needToInitLabels = false;
    super.setLabels(lbls);
  }

  /**
   * Sets the value of the StateX3D given a <code>X3D</code>
   * enum index.
   *
   * @param  ndx   A X3D index for the row
   * @param  val   A double value to be set for the StateX3D
   */
  public void put(X3D ndx, double val) {
    super.put(ndx.ordinal()+1, val);
  }

  /**
   * Gets the value of the StateX3D given a <code>X3D</code>
   * enum index.
   *
   * @param  ndx   A X3D index for the row
   *
   * @return       A double value for the requested element
   */
  public double get(X3D ndx) {
    return super.get(ndx.ordinal()+1);
  }
}
