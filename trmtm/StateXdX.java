/*
 c  StateXdX.java
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

import com.motekew.vse.enums.XdX;
import com.motekew.vse.math.*;

/**
 * <code>StateXdX</code> is a <code>VectorSpace</code> representing
 * a one dimensional state vector of a state, and its derivative (say,
 * position and velocity).  It is essentially a 2-tuple with accessor
 * methods that employ the <code>XdX</code> enum.  This makes it easier
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
public final class StateXdX extends Tuple {
  private static final int DIM = 2;

    // Used to see if labels for this extension of the Tuple have
    // been initialized.  Don't bother wasting the memory setting
    // unless the names have been asked for via getLabels();
  private boolean needToInitLabels = true;

  /**
   * Default constructor - all elements set to zero.
   */
  public StateXdX() {
    super(DIM);
  }

  /**
   * Initialize the StateXdX using an array.  If the array is
   * not DIM elements long, a <code>VectorSpaceArgumentException</code>
   * will be thrown.
   *
   * @param   t2d    A double[] used to initialize this StateXdX
   */
  public StateXdX(double[] t2d) {
    super(t2d);
    if (t2d.length != DIM) {
     throw new VectorSpaceArgumentException("StateXdX must be initialized " +
                  "with an array of " + DIM + " elements, not:  " + t2d.length);
    }
  }

  /**
   * Returns an array of String labels for each element of this StateXdX
   * Tuple.
   *
   * @return         A new arrray of String labels - not the pointer to the
   *                 internally stored labels.                             
   */
  @Override
  public String[] getLabels() {
    if (needToInitLabels) {
      needToInitLabels = false;
      String[] names = new String[XdX.values().length];
      for (XdX ii : XdX.values()) {
        names[ii.ordinal()] = ii.toString();
      }
      super.setLabels(names);
      return names;
    } else {
      return super.getLabels();
    }
  }

  /**
   * Sets an array of labels that are to be associated with this StateXdX.
   * It is probably best to use the default labels.
   *                     
   * @param  lbl[]   An array of labels that are to be copied into to this
   *                 StateXdX.
   */
  @Override
  public void setLabels(String[] lbls) {
    needToInitLabels = false;
    super.setLabels(lbls);
  }

  /**
   * Sets the value of the StateXdX given a <code>XdX</code>
   * enum index.
   *
   * @param  ndx   A XdX index for the row
   * @param  val   A double value to be set for the StateXdX
   */
  public void put(XdX ndx, double val) {
    super.put(ndx.ordinal()+1, val);
  }

  /**
   * Gets the value of the StateXdX given a <code>XdX</code>
   * enum index.
   *
   * @param  ndx   A XdX index for the row
   *
   * @return       A double value for the requested element
   */
  public double get(XdX ndx) {
    return super.get(ndx.ordinal()+1);
  }
}
