/*
 c  RotatingCentralBody.java
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
package com.motekew.vse.envrm;

import com.motekew.vse.enums.DDX3D;
import com.motekew.vse.intxm.IStepper;
import com.motekew.vse.math.Quaternion;
import com.motekew.vse.math.Tuple3D;
import com.motekew.vse.trmtm.RotatingBodySys;

/**
 * Represents a Central Body exerting a gravitational influence,
 * rotating about a fixed axis.  A <code>RotatingBodySys</code>
 * object is used for the dynamics of this object (position and
 * attitude), while a <code>Gravity</code> is used to compute the
 * gravitational potential (useful for generating a surface of equal
 * potential) and gravitational acceleration (for the dynamics of
 * objects this influences).
 * 
 * This class contains methods allowing the state to be pulled from the
 * current time or a requested time.  Gravitational  effects are all
 * computed given inputs relative to the central body's reference frame,
 * so there is no need for a time parameter.  For example, if this central
 * body represented the earth, it wouldn't need to know what time it is to
 * compute the gravitational acceleration because the reference vector would
 * be supplied in earth centered earth fixed coordinates.
 * 
 * @author  Kurt Motekew
 * @since   20101111
 */
public class RotatingCentralBody implements ICentralBody, IStepper {
  private RotatingBodySys rbs  = null;
  private Gravity grav = null;
  private GravitationalAcceleration accel = null;

  /**
   * Intialize with a <code>RotatingBodySys</code> to model the position
   * and attitude of this object, and a <code>Gravity</code> for central
   * body effects.
   * 
   *  @param    rbs_in    Models central body's position and attitude.
   *  @param    g_in      Models gravitational effects.
   */
  public RotatingCentralBody(RotatingBodySys rbs_in, Gravity g_in) {
    rbs  = rbs_in;
    grav = g_in;
    accel = new GravitationalAcceleration(grav);
  }

  /**
   * @return   Acceleration components computed via gravt, relative to the
   *           body.  See <code>Gravity</code>.
   */
  @Override
  public double get(DDX3D ndx) {
    return accel.get(ndx);
  }

  /**
   * @return    Reference (scaling) radius for potential computations.
   */
  @Override
  public double getRefRadius() {
    return grav.getRefRadius();
  }

  /**
   * @return    Gravitational Parameter (distance_units^3/time_units^2)
   */
  @Override
  public double getGravParam() {
    return grav.getGravParam();
  }

  /**
   * @return    The degree and order of this gravity model.
   */
  @Override
  public int getDegreeOrder() {
    return grav.getDegreeOrder();
  }

  /**
   * Computes gravitational potential given r - see
   * <code>Gravity</code>.
   */
  @Override
  public double getPotential(double r, double lat, double lon) {
    return grav.getPotential(r, lat, lon);
  }

  /**
   * Computes gravitational potential given r - see
   * <code>Gravity</code>.
   */
  @Override
  public double getPotential(int degree, double r, double lat,
                                                   double lon) {
    return grav.getPotential(degree, r, lat, lon);
  }

  /**
   * Computes gravitational potential - see <code>Gravity</code>
   */
  @Override
  public double getR(double lat, double lon) {
    return grav.getR(lat, lon);
  }

  /**
   * Computes gravitational acceleration.  See <code>Gravity</code>.
   */
  @Override
  public void gravt(double r, double lat, double lon) {
    accel.gravt(r, lat, lon);
  }

  /**
   * Computes gravitational acceleration.  See <code>Gravity</code>.
   */
  @Override
  public void gravt(int degree, double r, double lat, double lon) {
    accel.gravt(degree, r, lat, lon);
  }

  /**
   * Computes gravitational acceleration.  See <code>Gravity</code>.
   */  
  @Override
  public void gravt(Tuple3D pos) {
    accel.gravt(pos);
  }

  /**
   * Computes gravitational acceleration.  See <code>Gravity</code>.
   */  
  @Override
  public void gravt(int degree, Tuple3D pos) {
    accel.gravt(degree, pos);
  }

  /**
   * Sets pout to be the central body's position at time
   * t.
   * 
   * @param    tloc    Requested time for position vector
   * @param    pout    Output:  position will be copied into this
   *                           Tuple3D
   *
   * @return           Actual time associated with output position
   */
  @Override
  public double getPosition(double t, Tuple3D pos) {
    return rbs.getPosition(t, pos);
  }

  /**
   * Sets q to be the central body's attitude at time
   * t.
   * 
   * @param    tloc    Time for which attitude should be
   *                   retrieved
   * @param    q       Output - attitude will be copied into this
   *                            Quaternion
   *
   * @return           Actual time associated with output attitude
   */  
  @Override
  public double getAttitude(double t, Quaternion q) {
    return rbs.getAttitude(t, q);
  }

  /**
   * Same as step(double), but uses an interally set delta
   * value.  See <code>RotatingBodySys</code>.
   */
  @Override
  public void step() {
    rbs.step();
  }

  /**
   * Updates the state from the current time to a new time
   * incremented by delta.
   * 
   * @param    delta    Time delta for which to update
   *                    state.  See <code>RotatingBodySys</code>.
   */
  @Override
  public void step(double delta) {
    rbs.step(delta);
  }
}
