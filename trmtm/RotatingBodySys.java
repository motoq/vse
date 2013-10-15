/*
 c  RotatingBodySys.java
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

import com.motekew.vse.enums.*;
import com.motekew.vse.intxm.*;
import com.motekew.vse.math.*;
import com.motekew.vse.servm.StringUtils;

/**
 * This class representing a rotating body.  No dynamics are
 * involved.  While the body's position is part of the state vector,
 * it is not computed in any way - simply set via accessor methods.
 * The attitude is computed based on an initial attitude (q0 at t = 0), 
 * a vector about which the body spins (originating at the body's center,
 * oriented relative to the body), and the angular velocity about that
 * vector.  The direction of the rotation axis along with the sign of
 * the angual velocity vector determine the spin direction based on the
 * right hand rule..
 * 
 * @author  Kurt Motekew
 * @since   20101107
 */
public class RotatingBodySys implements ISysEqns, IPosVelAtt {

  /**
   * Number of parameters representing the state of this system.
   */
  public final int ORDER;

  private Tuple3D spinAxis = null;       // Spin axis
  private double  alpha0 = 0.0;          // Intitial rotation angle
  private double  omega  = 0.0;          // rotation rate

  private Quaternion q  = null;
  private double     t  = 0.0;           // System time
  private double    dt  = 0.0;           // Keep track of prev step size
  private StateXQ    x  = null;          // State at time t

  /**
   * Initialize this system.  Set the order and initialize the state
   * vector.
   */
  public RotatingBodySys() {
    x        = new StateXQ();
    spinAxis = new Tuple3D();
    spinAxis.put(Basis3D.K, 1.0);    // Spin around z-axis
    q        = new Quaternion();

    ORDER = x.length();
    
      // Ensure x is consistent with q (just in case the
      // Constructor for one changes defaults.
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
   * Sets the current time associated with the state of the model.
   * Should really only be used on initialization.
   *
   * @param t0     The new model time.
   */
  @Override
  public void setT(double t0) {
    t = t0;
  }

  /**
   * Gets values for the state of this system.  The XQ enum makes
   * array indexing checkable at compile time.
   *
   * @param   i      A XQ index to the state vector.
   *
   * @return         Output double value of the requested state vector
   *                 parameter.
   */
  public double getX(XQ i) {
    return x.get(i);
  }

  /**
   * Sets values for the state of this system.  The XQ enum makes
   * array indexing checkable at compile time.
   *
   * @param i      A XQ index to the state vector.
   * @param x0     The double value to set the state parameter to.
   */
  public void setX(XQ i, double x0) {
    x.put(i, x0);
  }

  /**
   * Copies values for the state of this system into the passed in
   * Tuple.  The dimensions must match for the Tuple.get() method
   * to not complain.  Use the getOrder() method to determine how
   * big the passed in Tuple needs to be.
   *
   * @param   out    Tuple into which state vector values are to be copied.
   *
   * @return         Time associated with this state vector
   */
  @Override
  public double getX(Tuple out) {
    out.set(x);
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
    x.set(in);
  }

  /**
   * Sets the spin axis of this rotating body to be aligned with the input
   * <code>Tuple3D</code>.
   * 
   * @param    sa    Quaternion rotation axis.
   */
  public void setSpinAxis(Tuple3D sa) {
    spinAxis.set(sa);
    spinAxis.unitize();
      // Update state
    step(0.0);
  }

  /**
   * Sets the spin axis of this rotating body to be aligned with the input
   * <code>Tuple3D</code>, rotated by the amount a0.
   * 
   * @param    a0    Rotation angle (rad) at time t = 0.
   * @param    sa    Quaternion rotation axis.
   */
  public void setSpinAxis(double a0, Tuple3D sa) {
    alpha0 = a0;
    spinAxis.set(sa);
    spinAxis.unitize();
      // Update state
    step(0.0);
  }

  /**
   * Set the angular velocity;
   * 
   * @param    w0    Angular velocity in radians/time_unit
   */
  public void setAngularVelocity(double w) {
    omega = w;
  }

  /**
   * @return      Angular velocity of central body about spin
   *              axis in rad/time_unit
   */
  public double getAngularVelocity() {
    return omega;
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
    return x.getLabels();
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
   * Sets pout to be the central body's position at time
   * t.  Since the position of the central body isn't
   * dynamic, the time parameter is just there to satisfy
   * the interface.
   * 
   * @param    tloc    time - only used as a return value
   * @param    pout    Output:  position will be copied into this
   *                           Tuple3D
   *
   * @return           Time associated with output, equal to requested
   *                   time tloc.
   */
  @Override
  public double getPosition(double tloc, Tuple3D pout) {
    pout.put(Basis3D.I, getX(XQ.X));
    pout.put(Basis3D.J, getX(XQ.Y));
    pout.put(Basis3D.K, getX(XQ.Z));

    return tloc;
  }

  /**
   * Sets vout to zero - the model currently supports a stationary
   * central body.
   *
   * @param    tloc    time - only used as a return value
   * @param    vout    Output:  [0 0 0]'
   *
   * @return           Time associated with output, equal to requested
   *                   time tloc.
   */
  @Override
  public double getVelocity(double tloc, Tuple3D vout) {
    vout.zero();

    return tloc;
  }

  /**
   * Sets qout to be the central body's attitude at time
   * t.  Since this is a simple calculation, just recompute -
   * No needed for buffering.
   * 
   * @param    tloc    Time for which attitude should be
   *                   retrieved
   * @param    qout    Output - attitude will be copied into this
   *                            Quaternion
   *
   * @return           Time associated with output, equal to requested
   *                   time tloc.
   */  
  @Override
  public double getAttitude(double tloc, Quaternion qout) {
    double alpha = alpha0 + tloc*omega;
    alpha = Angles.set2PI(alpha);
    qout.set(alpha, spinAxis);

    return tloc;
  }

  /**
   * Update the model to t + delta
   */
  @Override
  public void step() {
    step(dt);
  }

  /**
   * Updates the system state to the current time plus delta.
   *
   * @param user_delta   Time increment to update by.
   */
  @Override
  public void step(double user_delta) {
    dt = user_delta;
    t += user_delta;
    
    double alpha = alpha0 + t*omega;
    alpha = Angles.set2PI(alpha);
    q.set(alpha, spinAxis);
    x.setAttitude(q);
  }
}
