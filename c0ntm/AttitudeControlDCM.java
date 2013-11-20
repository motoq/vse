/*
 c  AttitudeControlDCM.java
 c
 c  Copyright (C) 2011, 2013 Kurt Motekew
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

package com.motekew.vse.c0ntm;

import com.motekew.vse.math.Matrix3X3;
import com.motekew.vse.math.Quaternion;
import com.motekew.vse.math.Tuple3D;

/**
 * This <code>Tuple3D</code> is an attitude feedback controller with
 * the the following control law (where 'u' is this Tuple3D):
 * <P>
 *   {u} = -[Kv]*{w} - [Kp]*{OmegaA([R])}
 * <P>
 * as described in "Rigid-Body Attitude Control Using Rotation
 * Matrices for Continuous Singularity-Free Control Laws," by
 * Nalin A. Chaturvedi, Amit K. Sanyal, and N. Harris McClamroch,
 * June 2011 IEEE Control Systems Magazine.  This version includes a
 * change I made to computing the potential portion of the control law
 * in the OmegaA formula listed below.  As described in the paper,
 * the method appears to be unstable for dynamic attitude control (the
 * subject of the paper was rest-to-rest attitude control).  I also
 * can 't figure out how the form described in the paper was derived -
 * multiplying the current attitude DCM by the transpose of the desired
 * DCM (as implemented below) makes more sense for either case.
 * <P>
 * 'this' is the resulting vector of torques about three axes at a given
 * moment with the objective of transforming the attitude of the object
 * being modeled from its current state to a desired one.  The resulting
 * torques are derived from the sum of a kinetic and potential contribution.
 * The potential portion is based on how far the current attitude is from
 * the desired attitude - it drives the change in attitude.  The kinetic
 * portion is based on the body relative angular velocity and drives
 * the attitude rates to zero.  When the attitude is far from the desired
 * state, the potential portion overpowers the kinetic.  As the attitude
 * converges to the desired state, the kinetic portion continues to
 * gain more weight until it overpowers the potential portion (which
 * becomes zero once reaching the desired attitude).  When used alone, the
 * kinetic portion can be used to "apply the brakes" to a rotating body.
 * <P>
 * [Kv] is the positive definite gain matrix controlling the kinetic
 * portion of the control law.  {w} is the vector of attitude rates about
 * the body axes.  [Kp] controls the potential portion.  [R] is the
 * direction cosine matrix representing the current attitude.  If
 * [Rd] is the desired attitude DCM, [e1 e2 e3] vectors forming the
 * identity matrix, and [a1, a2, a3] distinct positive integers, then
 * the vector function {OmegaA()} is:
 * <P>
 *   {OmegaA([R])} = sum(ai*ei X [R]'*[Rd]'*ei), i = {1, 2, 3}
 * <P>
 * where 'X' is the vector cross product and [Rd]' represents
 * the transpose of [Rd].  The values of the gain parameters
 * determine the behavior of the control system (critically/over/under
 * damped).  
 * <P>
 * More info on inputs can be found in the documentation for each
 * respective "control()" method below.
 * <P>
 * This class is not thread safe.
 * 
 * @author  Kurt Motekew
 * @since   20110609
 * @since   20131112   Implemented IAttitudeControl interface and extended
 *                     Tuple3D class.
 */
public class AttitudeControlDCM extends Tuple3D implements IAttitudeControl {

    // Default gain values to null -> control provides no outpt
  private Matrix3X3 kv = null;                      // potential gain
  private Matrix3X3 kp = null;                      // kinetic gain
  private Tuple3D ai = new Tuple3D(1.0, 1.0, 1.0);  // axes weighting

    // computeU Cache - quaternion inputs.
  private class ControlQCache {
    Matrix3X3 currentAtt = new Matrix3X3();
    Matrix3X3 desiredAtt = new Matrix3X3();
  }
  private ControlQCache cntQC = new ControlQCache();

    // computeU Cache - DCM inputs
  private class ControlCache {
    Matrix3X3 rotX180 = new Matrix3X3();
    Matrix3X3 desiredAttT = new Matrix3X3();
    Matrix3X3 deltaAtt = new Matrix3X3();
    Tuple3D omegaA = new Tuple3D();
    Tuple3D uvec_k = new Tuple3D();
    ControlCache() {
      rotX180.identity();
      rotX180.put(2,2, -1.0);
      rotX180.put(3,3, -1.0);
    }
  }
  private ControlCache cntC = new ControlCache();

  /**
   * Default constructor.  All gain matrices are set to zero by
   * default, resulting in no control outputs.
   */
  public AttitudeControlDCM() {
  }

  /**
   * Sets the potential and kinetic gain matrices, along with the
   * axes weighting vector (affects potential portion).  Internal
   * values are set to point to these - they are not copied in place.
   *
   * @param    kvin    New attitude kiniteic gain matrix to point to.
   *                   Positive definite 3X3.  Pointer is set to this
   *                   matrix, values are not copied.
   * @param    kpin    New attitude potential gain matrix to point to.
   *                   Positive definite 3X3.  Pointer is set to this
   *                   matrix, values are not copied.
   * @param    ain     New axes weighting vector values.  Pointer is
   *                   set to this Tuple3D, values are not copied.
   */
  public AttitudeControlDCM(Matrix3X3 kvin, Matrix3X3 kpin, Tuple3D ain) {
    kv = kvin;
    kp = kpin;
    ai = ain;
  }

  /**
   * Sets the kinetic gain matrix.  Internal values are set to point
   * to these - they are not copied in place.
   * <P>
   * If this matrix is set and not the potential one, then only angular
   * velocity will be used by the control law, attempting to halt all angular
   * velocity.
   *
   * @param    kvin    New attitude kinetic gain matrix to point to.
   *                   Positive definite 3X3.
   */
  public void setKv(Matrix3X3 kvin) {
    kv = kvin;
  }

  /**
   * Removes the kinetic gain matrix.
   */
  public void nullKv() {
    kv = null;
  }

  /**
   * Sets the potential gain matrix.  Internal values are set to point
   * to these - they are not copied in place.
   *
   * @param    kpin    New attitude potential gain matrix to point to.
   *                   Positive definite 3X3.
   */
  public void setKp(Matrix3X3 kpin) {
    kp = kpin;
  }

  /**
   * Removes the potential gain matrix.
   */
  public void nullKp() {
    kp = null;
  }

  /**
   * Sets the axes weighting vector used for the potential component
   * of the control law.  These values weight the off diagonal elements
   * of the matrix formed by the product of the transpose of the desired
   * attitude DCM and the current (which are being driven to zero as this
   * product is driven to the identity matrix).
   * <P>
   * Only needs to be set when using potential gain matrix.
   *
   * @param    ain     New axes weighting vector to point to.
   */
  public void setA(Tuple3D a) {
    ai = a;
  }

  /**
   * Computes Attitude Control torques with the intention of bringing
   * the angular velocity vector to zero.  Only the kinetic portion
   * is implemented - not the potential.
   *
   * @param   wvec         Attitude rates, [roll_rate, pitch_rate, yaw_rate]'
   *                       relative to the body frame.  rad/time_unit
   */
  @Override
  public void control(Tuple3D wvec) {
      // Compute kinetic part if gain matrix has been set.  Make sure
      // to zero return vector just in case it isn't set.
    zero();
    if (kv != null) {
      mult(kv, wvec);   // Reuse omegaA for kinetic contribution
      mult(-1.0);
    }
  }

  /**
   * Same as control() below, except with quaternion attitudes (current
   * and desired) as inputs.
   *
   * @param   currentAtt   Current inertial attitude quaternion
   * @param   desiredAtt   Desired inertial attitude quaternion
   * @param   wvec         Attitude rates, [roll_rate, pitch_rate, yaw_rate]'
   *                       relative to the body frame.  rad/time_unit
   */
  @Override
  public void control(Quaternion currentAtt, Quaternion desiredAtt,
                                                      Tuple3D wvec) {
      // Simply convert current and desired attitudes to DCMs, then pass along.
    cntQC.currentAtt.set(currentAtt);
    cntQC.desiredAtt.set(desiredAtt);
    control(cntQC.currentAtt, cntQC.desiredAtt, wvec);
  }

  /**
   * Same as control() below, except with current attitude as a quaternion
   * and desired as a DCM.
   *
   * @param   currentAtt   Current inertial attitude quaternion
   * @param   desiredAtt   Desired inertial DCM
   * @param   wvec         Attitude rates, [roll_rate, pitch_rate, yaw_rate]'
   *                       relative to the body frame.  rad/time_unit
   */
  @Override
  public void control(Quaternion currentAtt, Matrix3X3 desiredAtt,
                                                     Tuple3D wvec) {
      // Simply convert current and desired attitudes to DCMs, then pass along.
    cntQC.currentAtt.set(currentAtt);
    control(cntQC.currentAtt, desiredAtt, wvec);
  }

  /**
   * Compute attitude control torques given the current and desired
   * attitude DCMs, along with the body relative angular velocities.
   * <P>
   * NOTE:  Currently adding a 180 deg rotation about the x-axis for the
   *        desired attitude.  Without this, the desired attitude appears
   *        to be an unstable equilibrium point - need to investigate why....
   *
   * @param   currentAtt   Current inertial attitude transformation matrix.
   * @param   desiredAtt   Desired inertial attitude transformation matrix.
   * @param   wvec         Attitude rates, [roll_rate, pitch_rate, yaw_rate]'
   *                       relative to the body frame.  rad/time_unit
   */
  @Override
  public void control(Matrix3X3 currentAtt, Matrix3X3 desiredAtt,
                                                    Tuple3D wvec) {
      // Compute kinetic portion and add to potential below
    control(wvec);
    cntC.uvec_k.set(this);
    zero();

      // Compute potential part if gain matrix has been set
    if (kp != null) {
        // Working:  Rotate desired about x-axis by 180 deg.
      cntC.desiredAttT.mult(cntC.rotX180, desiredAtt);
        // Mult current by desired_Transposed
      cntC.desiredAttT.transpose();
      cntC.deltaAtt.mult(currentAtt, cntC.desiredAttT);

      cntC.omegaA.put(1, ai.get(2) * cntC.deltaAtt.get(3,2) -
                         ai.get(3) * cntC.deltaAtt.get(2,3)   );
      cntC.omegaA.put(2, ai.get(3) * cntC.deltaAtt.get(1,3) -
                         ai.get(1) * cntC.deltaAtt.get(3,1)   );
      cntC.omegaA.put(3, ai.get(1) * cntC.deltaAtt.get(2,1) -
                         ai.get(2) * cntC.deltaAtt.get(1,2)   );

      mult(kp, cntC.omegaA);
      mult(-1.0);
    }

      // kinetic and potential contribution
    plus(cntC.uvec_k);
  }
}
