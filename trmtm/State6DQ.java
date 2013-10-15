/*
 c  State6DQ.java
 c
 c  Copyright (C) 2000, 2012 Kurt Motekew
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
import com.motekew.vse.enums.XdX6DQ;
import com.motekew.vse.math.*;

/**
 * <code>State6DQ</code> is a <code>VectorSpace</code> representing
 * a three dimensional position vector of a state, and its derivative (say,
 * position and velocity), along with the attitude and attitude rates.  It
 * is a 13-tuple with accessor methods that employ the <code>XdX6DQ</code>
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
 * @since  20080902
 * @since  20121128   Updated with changes to IPosVelAtt
 */
public final class State6DQ extends Tuple implements IPosVelAtt {
  private static final int DIM = 13;

    // Used to see if labels for this extension of the Tuple have
    // been initialized.  Don't bother wasting the memory setting
    // unless the names have been asked for via getLabels();
  private boolean needToInitLabels = true;

    // Time associated with state vector - this value should be updated
    // whenever state is updated
  private double t = 0;

  /**
   * Default constructor - all elements set to zero except for
   * attitude quaternion scalar component which should default to
   * a value of 1.0.
   */
  public State6DQ() {
    super(DIM);
    this.put(XdX6DQ.Q0, 1.0);
  }

  /**
   * Initialize the State6DQ using an array.  If the array is
   * not DIM elements long, a <code>VectorSpaceArgumentException</code>
   * will be thrown.
   *
   * @param   v_m    A double[] used to initialize this State6DQ
   */
  public State6DQ(double[] v_m) {
    super(v_m);
    if (v_m.length != DIM) {
     throw new VectorSpaceArgumentException("State6DQ must be initialized " +
                  "with an array of " + DIM + " elements, not:  " + v_m.length);
    }
  }

  /**
   * Returns an array of String labels for each element of this State6DQ
   * Tuple.
   *
   * @return         A new arrray of String labels - not the pointer to the
   *                 internally stored labels.                             
   */
  @Override
  public String[] getLabels() {
    if (needToInitLabels) {
      needToInitLabels = false;
      String[] names = new String[XdX6DQ.values().length];
      for (XdX6DQ ii : XdX6DQ.values()) {
        names[ii.ordinal()] = ii.toString();
      }
      super.setLabels(names);
      return names;
    } else {
      return super.getLabels();
    }
  }

  /**
   * Sets an array of labels that are to be associated with this State6DQ.
   * It is probably best to use the default labels.
   *                     
   * @param  lbl[]   An array of labels that are to be copied into to this
   *                 State6DQ.
   */
  @Override
  public void setLabels(String[] lbls) {
    needToInitLabels = false;
    super.setLabels(lbls);
  }

  /**
   * @param   newT   New time to be associated with this state
   *                 vector.
   */
  public void setTime(double newT) {
    t = newT;
  }

  /**
   * Sets the value of the State6DQ given a <code>XdX6DQ</code>
   * enum index.
   *
   * @param  ndx   A XdX6DQ index for the row
   * @param  val   A double value to be set for the State6DQ
   */
  public void put(XdX6DQ ndx, double val) {
    super.put(ndx.ordinal()+1, val);
  }

  /**
   * Gets the value of the State6DQ given a <code>XdX6DQ</code>
   * enum index.
   *
   * @param  ndx   A XdX6DQ index for the row
   *
   * @return       A double value for the requested element
   */
  public double get(XdX6DQ ndx) {
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
    this.put(XdX6DQ.X, pos.get(Basis3D.I));
    this.put(XdX6DQ.Y, pos.get(Basis3D.J));
    this.put(XdX6DQ.Z, pos.get(Basis3D.K));
  }

  /**
   * Retrieves the position elements of this class and places them in
   * the input Tuple3D.
   *
   * @param    tReq  Time requested for position
   * @param    pos   Position vector to set equal to the position of
   *                 this state vector.
   *
   * @return         Actual time associated with output position vector.
   */
  @Override
  public double getPosition(double tReq, Tuple3D pos) {
    pos.put(Basis3D.I, this.get(XdX6DQ.X));
    pos.put(Basis3D.J, this.get(XdX6DQ.Y));
    pos.put(Basis3D.K, this.get(XdX6DQ.Z));

    return t;
  }

  /**
   * Sets the velocity elements of this class to those of the input
   * Tuple3D.
   *
   * @param    vel    Velocity vector with which to set the elements
   *                  of this state vector.
   */
  public void setVelocity(Tuple3D vel) {
    this.put(XdX6DQ.DX, vel.get(Basis3D.I));
    this.put(XdX6DQ.DY, vel.get(Basis3D.J));
    this.put(XdX6DQ.DZ, vel.get(Basis3D.K));
  }

  /**
   * Retrieves the velocity elements of this class and places them in
   * the input Tuple3D.
   *
   * @param    tReq  Time requested for velocity
   * @param    vel   Velocity vector to set equal to the velocity of
   *                 this state vector.
   *
   * @return         Actual time associated with output velocity vector.
   */
  @Override
  public double getVelocity(double tReq, Tuple3D vel) {
    vel.put(Basis3D.I, this.get(XdX6DQ.DX));
    vel.put(Basis3D.J, this.get(XdX6DQ.DY));
    vel.put(Basis3D.K, this.get(XdX6DQ.DZ));

    return t;
  }

  /**
   * Sets the quaternion attitude elements of this class to
   * those of the input quaternion.
   *
   * @param    att    Attitude with which to set the elements
   *                  of this state vector.
   */
  public void setAttitude(Quaternion att) {
    this.put(XdX6DQ.Q0, att.get(Q.Q0));
    this.put(XdX6DQ.QI, att.get(Q.QI));
    this.put(XdX6DQ.QJ, att.get(Q.QJ));
    this.put(XdX6DQ.QK, att.get(Q.QK));
  }

  /**
   * Retrieves the quaternion attitude elements of this class
   * and places then in the input quaternion.
   *
   * @param    tReq   Time requested for attitude
   * @param    att    Quaternion attitude to set equal to the
   *                  attitude of this state vector.
   *
   * @return         Actual time associated with output attitude.
   */
  @Override
  public double getAttitude(double tReq, Quaternion att) {
    att.put(Q.Q0, this.get(XdX6DQ.Q0));
    att.put(Q.QI, this.get(XdX6DQ.QI));
    att.put(Q.QJ, this.get(XdX6DQ.QJ));
    att.put(Q.QK, this.get(XdX6DQ.QK));

    return t;
  }

  /**
   * Sets the Euler angle attitude rate elements of this class to
   * those of the input rates.
   *
   * @param    pqr    Attitude rate with which to set the elements
   *                  of this state vector.  Roll rate, pitch rate,
   *                  and finally yaw rate.
   */
  public void setAttitudeRate(Tuple3D pqr) {
    this.put(XdX6DQ.P, pqr.get(Basis3D.I));
    this.put(XdX6DQ.Q, pqr.get(Basis3D.J));
    this.put(XdX6DQ.R, pqr.get(Basis3D.K));
  }

  /**
   * Retrieves the Euler angle attitude rate elements of this class
   * and places then in the input Tuple3D.
   *
   * @param    tReq   Time requested for attitude rate
   * @param    pqr    Attitude rate to set equal to the attitude of
   *                  this state vector (roll, pitch, and yaw rates).
   *
   * @return         Actual time associated with output attitude rate
   */
  public double getAttitudeRate(double tReq, Tuple3D pqr) {
    pqr.put(Basis3D.I, this.get(XdX6DQ.P));
    pqr.put(Basis3D.J, this.get(XdX6DQ.Q));
    pqr.put(Basis3D.K, this.get(XdX6DQ.R));

    return t;
  }
}
