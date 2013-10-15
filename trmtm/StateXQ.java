/*
 c  StateXQ.java
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

package com.motekew.vse.trmtm;

import com.motekew.vse.enums.Basis3D;
import com.motekew.vse.enums.Q;
import com.motekew.vse.enums.XQ;
import com.motekew.vse.math.*;

/**
 * <code>StateXQ</code> is a <code>VectorSpace</code> representing
 * a three dimensional position vector and attitude quaternion.  It is a 
 * is a 7-tuple with accessor methods that employ the <code>XQ</code>
 * enum.  This makes it easier to keep track of what element is what when
 * accessing those elements directly (instead of looping on all elements).
 * <P>
 * Quaternions, instead of Euler angles, are used to track attitude.
 * <P>
 * Any attempt to access an element of this <code>VectorSpace</code>
 * that is out of range will result in a VectorSpaceIndexOutOfBoundsException
 * being thrown.  Note that integer indexing starts with 1, not zero.
 *
 * @author Kurt Motekew
 * @since  20101108
 */
public final class StateXQ extends Tuple {
  private static final int DIM = 7;

    // Used to see if labels for this extension of the Tuple have
    // been initialized.  Don't bother wasting the memory setting
    // unless the names have been asked for via getLabels();
  private boolean needToInitLabels = true;

  /**
   * Default constructor - all elements set to zero except for
   * attitude quaternion scalar component which should default to
   * a value of 1.0.
   */
  public StateXQ() {
    super(DIM);
    this.put(XQ.Q0, 1.0);
  }

  /**
   * Initialize the StateXQ using an array.  If the array is
   * not DIM elements long, a <code>VectorSpaceArgumentException</code>
   * will be thrown.
   *
   * @param   v_m    A double[] used to initialize this StateXQ
   */
  public StateXQ(double[] v_m) {
    super(v_m);
    if (v_m.length != DIM) {
     throw new VectorSpaceArgumentException("StateXQ must be initialized " +
                  "with an array of " + DIM + " elements, not:  " + v_m.length);
    }
  }

  /**
   * Returns an array of String labels for each element of this StateXQ
   * Tuple.
   *
   * @return         A new arrray of String labels - not the pointer to the
   *                 internally stored labels.                             
   */
  @Override
  public String[] getLabels() {
    if (needToInitLabels) {
      needToInitLabels = false;
      String[] names = new String[XQ.values().length];
      for (XQ ii : XQ.values()) {
        names[ii.ordinal()] = ii.toString();
      }
      super.setLabels(names);
      return names;
    } else {
      return super.getLabels();
    }
  }

  /**
   * Sets an array of labels that are to be associated with this StateXQ.
   * It is probably best to use the default labels.
   *                     
   * @param  lbl[]   An array of labels that are to be copied into to this
   *                 StateXQ.
   */
  @Override
  public void setLabels(String[] lbls) {
    needToInitLabels = false;
    super.setLabels(lbls);
  }

  /**
   * Sets the value of the StateXQ given a <code>XQ</code>
   * enum index.
   *
   * @param  ndx   A XQ index for the row
   * @param  val   A double value to be set for the StateXQ
   */
  public void put(XQ ndx, double val) {
    super.put(ndx.ordinal()+1, val);
  }

  /**
   * Gets the value of the StateXQ given a <code>XQ</code>
   * enum index.
   *
   * @param  ndx   A XQ index for the row
   *
   * @return       A double value for the requested element
   */
  public double get(XQ ndx) {
    return super.get(ndx.ordinal()+1);
  }

  /**
   * Sets the position elements of this class to those of the input
   * Tuple3D.
   *
   * @param    pos    Position vector with which to set the elements
   *                  of this state vector.
   */
  public void setPosition(Tuple3D pos) {
    this.put(XQ.X, pos.get(Basis3D.I));
    this.put(XQ.Y, pos.get(Basis3D.J));
    this.put(XQ.Z, pos.get(Basis3D.K));
  }

  /**
   * Retrieves the position elements of this class and places them in
   * the input Tuple3D.
   *
   * @param    pos   Position vector to set equal to the position of
   *                 this state vector.
   */
  public void getPosition(Tuple3D pos) {
    pos.put(Basis3D.I, this.get(XQ.X));
    pos.put(Basis3D.J, this.get(XQ.Y));
    pos.put(Basis3D.K, this.get(XQ.Z));
  }

  /**
   * Sets the quaternion attitude elements of this class to
   * those of the input quaternion.
   *
   * @param    att    Attitude with which to set the elements
   *                  of this state vector.
   */
  public void setAttitude(Quaternion att) {
    this.put(XQ.Q0, att.get(Q.Q0));
    this.put(XQ.QI, att.get(Q.QI));
    this.put(XQ.QJ, att.get(Q.QJ));
    this.put(XQ.QK, att.get(Q.QK));
  }

  /**
   * Retrieves the quaternion attitude elements of this class
   * and places then in the input quaternion.
   *
   * @param    att    Quaternion attitude to set equal to the
   *                  attitude of this state vector.
   */
  public void getAttitude(Quaternion att) {
    att.put(Q.Q0, this.get(XQ.Q0));
    att.put(Q.QI, this.get(XQ.QI));
    att.put(Q.QJ, this.get(XQ.QJ));
    att.put(Q.QK, this.get(XQ.QK));
  }

}
