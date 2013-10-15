/*
 c  StateKepEuler.java
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

import com.motekew.vse.enums.EulerA;
import com.motekew.vse.enums.KepEuler;
import com.motekew.vse.enums.Keplerian;
import com.motekew.vse.math.Tuple;

/**
 * <code>StateKepEuler</code> is a <code>VectorSpace</code> representing
 * the position, velocity, and attitude of an object orbiting a central
 * body in an intuitive manner (vs a Cartesian state with quaternion
 * attitude).
 * <P>
 * Any attempt to access an element of this <code>VectorSpace</code>
 * that is out of range will result in a VectorSpaceIndexOutOfBoundsException
 * being thrown.  Note that integer indexing starts with 1, not zero.
 *
 * @author Kurt Motekew
 * @since  20110507
 */
public final class StateKepEuler extends Tuple {

    // Used to see if labels for this extension of the Tuple have
    // been initialized.  Don't bother wasting the memory setting
    // unless the names have been asked for via getLabels();
  private boolean needToInitLabels = true;

  /**
   * Default constructor - all elements set to zero.
   */
  public StateKepEuler() {
    super(KepEuler.values().length);
  }

  /**
   * Returns an array of String labels for each element of this StateKepEuler
   * Tuple.
   *
   * @return         A new arrray of String labels - not the pointer to the
   *                 internally stored labels.                             
   */
  @Override
  public String[] getLabels() {
    if (needToInitLabels) {
      needToInitLabels = false;
      String[] names = new String[KepEuler.values().length];
      for (KepEuler ii : KepEuler.values()) {
        names[ii.ordinal()] = ii.toString();
      }
      super.setLabels(names);
      return names;
    } else {
      return super.getLabels();
    }
  }

  /**
   * Sets an array of labels that are to be associated with this StateKepEuler.
   * It is probably best to use the default labels.
   *                     
   * @param  lbl[]   An array of labels that are to be copied into to this
   *                 StateKepEuler.
   */
  @Override
  public void setLabels(String[] lbls) {
    needToInitLabels = false;
    super.setLabels(lbls);
  }

  /**
   * Sets the values of this Tuple given a KeplerianOE and
   * EulerAngles.
   */
  public void set(KeplerianOE kep, EulerAngles att) {
    put(KepEuler.A, kep.get(Keplerian.A));
    put(KepEuler.E, kep.get(Keplerian.E));
    put(KepEuler.I, kep.get(Keplerian.I));
    put(KepEuler.O, kep.get(Keplerian.O));
    put(KepEuler.W, kep.get(Keplerian.W));
    put(KepEuler.V, kep.get(Keplerian.V));
    put(KepEuler.BANK, att.get(EulerA.BANK));
    put(KepEuler.ELEV, att.get(EulerA.ELEV));
    put(KepEuler.HEAD, att.get(EulerA.HEAD));
  }

  /**
   * Sets the value of the StateKepEuler given a <code>KepEuler</code>
   * enum index.
   *
   * @param  ndx   A KepEuler index for the row
   * @param  val   A double value to be set for the StateKepEuler
   */
  public void put(KepEuler ndx, double val) {
    super.put(ndx.ordinal()+1, val);
  }

  /**
   * Gets the value of the StateKepEuler given a <code>KepEuler</code>
   * enum index.
   *
   * @param  ndx   A KepEuler index for the row
   *
   * @return       A double value for the requested element
   */
  public double get(KepEuler ndx) {
    return super.get(ndx.ordinal()+1);
  }
}
