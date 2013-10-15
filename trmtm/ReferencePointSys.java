/*
 c  ReferencePointSys.java
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

import com.motekew.vse.enums.*;
import com.motekew.vse.intxm.*;
import com.motekew.vse.math.*;
import com.motekew.vse.servm.StringUtils;

/**
 * This class represents a reference point defined by Cartesian
 * coordinates relative to either inertial space or an object implementing
 * IPosVelAtt.  No dynamics are involved - only returning its position
 * in inertial space.
 * <P>
 * Keep in mind, the state of the object is with respect to the
 * computational (inertial) reference frame.  These values are available
 * through the <code>ISysEqns</code> accessor methods.
 * <P>
 * The setPointOfOrigin method must be used if this reference point is
 * to be modeled relative to a reference frame other than the inertial
 * one.
 * 
 * @author  Kurt Motekew
 * @since   20111111
 */
public class ReferencePointSys implements ISysEqns, IPosition {

  /**
   * Number of parameters representing the state of this system.
   */
  public final int ORDER;

    // Need to keep track of time since it may be necessary to
    // pass time to point of origin.
  double t  = 0.0;
  double dt = 0.0;

    // Need to call step(0.0) after updating either relativePostion
    // or pointOfOrigin to update state.
  Tuple3D relativePosition = new Tuple3D();
  StateX3D state = new StateX3D();  // Relative to pointOfOrigin
  IPosVelAtt pointOfOrigin = null;

  /**
   * Initialize this system.  Set the order.
   */
  public ReferencePointSys() {
    ORDER = relativePosition.length();
  }

  /**
   * Initialize this system.  Set the order and initial position.
   *
   * @param  x   X-axis position relative to point of origin.
   * @param  y   Y-axis position relative to point of origin.
   * @param  z   Z-axis position relative to point of origin.
   */
  public ReferencePointSys(double x, double y, double z) {
    this();
    relativePosition.set(x, y, z);
    step(0.0);
  }

  /**
   * Initialize this system.  Set the order and initial position.
   *
   * @param  xyz   Position relative to point of origin.
   */
  public ReferencePointSys(Tuple3D xyz) {
    this(xyz.get(Basis3D.I), xyz.get(Basis3D.J), xyz.get(Basis3D.K));
  }

  /**
   * Update the state (location) of the reference point.  If a point
   * of origin exists, then this is the location relative to that
   * position.
   *
   * @param   pos    Copy position values into local state vector.
   */
  public void setRelativePosition(Tuple3D pos) {
    relativePosition.set(pos);
    step(0.0);
  }

  /**
   * @param   pos   Output: Copy relative position values here.
   * 
   * @return        Pointer to pos
   */
  public Tuple3D getRelativePostion(Tuple3D pos) {
    pos.set(relativePosition);
    return pos;
  }
  
  /**
   * Defines the reference frame position and attitude with which
   * relativePosition is defined.
   *
   * @param   poo    Sets the internal point of origin object to
   *                 point to this one.
   */
  public void setPointOfOrigin(IPosVelAtt poo) {
    pointOfOrigin = poo;
    step(0.0);
  }

  /**
   * Returns the number of elements composing the state vector.
   *
   * @return       A int representing the number elements in this
   *               objects state vector.
   */
  @Override
  public int getOrder() {
    return ORDER;
  }

  /**
   * There are no outputs generated along with the state.
   *
   * @return    A zero
   */
  @Override
  public int getNumOutputs() {
    return 0;
  }

  /**
   * There are no controls.
   *                                                           
   * @return       A zero.
   */
  @Override
  public int getNumControls() {
    return 0;
  }

  /**
   * Returns the time associated with the current state of the model.
   *
   * @return       Model time.
   */
  @Override
  public double getT() {
    return t;
  }

  /**
   * Sets the time to be be used to retrive state for ISysEqns accessor
   * methods.
   *
   * @param t0     The new model time.
   */
  @Override
  public void setT(double t0) {
    t = t0;
  }

  /**
   * Gets values for the state of this system.  The X3D enum makes
   * array indexing checkable at compile time.  Position is relative
   * to the inertial frame.
   *
   * @param   i      A X3D index to the state vector.
   *
   * @return         Output double value of the requested state vector
   *                 parameter.
   */
  public double getX(X3D i) {
    return state.get(i);
  }

  /**
   * Sets values for the state of this system.  The X3D enum makes
   * array indexing checkable at compile time.  Position is relative
   * to the inertial frame.
   *
   * @param i      A X3D index to the state vector.
   * @param x0     The double value to set the state parameter to.
   */
  public void setX(X3D i, double x0) {
    state.put(i, x0);
  }

  /**
   * Copies values for the state of this system into the passed in
   * Tuple.  The dimensions must match for the Tuple.get() method
   * to not complain.  Use the getOrder() method to determine how
   * big the passed in Tuple needs to be.  The state is relative to
   * the inertial frame.
   *
   * @param   out    Tuple into which state vector values are to be copied.
   *
   * @return         Time associated with this state vector
   */
  @Override
  public double getX(Tuple out) {
    out.set(state);
    return t;
  }

  /**
   * Copies values from the input Tuple into the state vector of this system.
   * The dimensions must match for the Tuple.set() method to not complain.  Use
   * the getOrder() method to determine how big the passed in Tuple needs to be
   * is recomended.
   *
   * @param   in     Tuple from which state vector values are to be set.
   */
  @Override
  public void setX(Tuple in) {
    state.set(in);
  }

  /**
   * Returns an array of Strings that are appropriate labels for the
   * state vector values in this system.
   *
   * @return      Array of String labels representing the state vector
   *              element names.
   */
  @Override
  public String[] getXNames() {
    return state.getLabels();
  }

  /**
   * There are no outputs to return.  The input Tuple will be zero'ed
   * since this best represents the request being made.
   *
   * @param   out    Tuple which will be zero'ed.
   */
  @Override
  public void getY(Tuple out) {
   out.zero();
  }

  /**
   * Returns an empty array of Strings.  See 
   * <code>StringUtils.blankStringArray</code>.
   *
   * @return      StringUtils.blankStringArray()
   */
  @Override
  public String[] getYNames() {
    return StringUtils.blankStringArray();
  }

  /**
   * There are no controls to return.  The input Tuple will be zero'ed
   * since this best represents the request being made.
   *
   * @param   out    Tuple which will be zero'ed.
   */
  @Override
  public void getU(Tuple out) {
    out.zero();
  }

  /**
   * Does nothing since there are no controls to set.
   */
  @Override
  public void setU(Tuple in) {
    ;
  }

  /**
   * Returns an empty array of Strings.  See 
   * <code>StringUtils.blankStringArray</code>.
   *
   * @return      StringUtils.blankStringArray()
   */
  @Override
  public String[] getUNames() {
    return StringUtils.blankStringArray();
  }

  /**
   * Sets pout to be the reference point's inertial position at
   * time t.
   * 
   * @param    tloc    Time for which to return position.
   * @param    pout    Output:  position will be copied into this
   *                            Tuple3D
   *
   * @return           If no external point of origin has been defined,
   *                   then the returned time is equal to the requested
   *                   time.  Otherwise it will be equal to the time at
   *                   which the point of origin last updated its state.
   */
  @Override
  public double getPosition(double tloc, Tuple3D pout) {
    double time = tloc;

      // Compute inertial position given point of origin pos & attitude
    if (pointOfOrigin != null) {
      Tuple3D origin = new Tuple3D();
      Tuple3D inertialFromOrigin = new Tuple3D();
      Quaternion originAtt = new Quaternion();
        // Get origin & attitude, then rotate relative position
        // to inertial
      pointOfOrigin.getPosition(tloc, origin);
      time = pointOfOrigin.getAttitude(tloc, originAtt);
      originAtt.vecRot(relativePosition, inertialFromOrigin);
      pout.plus(origin, inertialFromOrigin);
    } else {
      pout.set(relativePosition);
    }
    return time;
  }

  /**
   * See the other step method....
   */
  @Override
  public void step() {
    step(dt);
  }

  /**
   * Update time and state.  State will only depend on time if a
   * point of origin has been created and if it isn't static in
   * nature.
   *
   * @param user_delta   Time increment to update by.
   */
  @Override
  public void step(double user_delta) {
    dt = user_delta;
    t += user_delta;
    if (pointOfOrigin != null) {
      this.getPosition(t, state);
    }
  }
}
