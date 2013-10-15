/*
 c  Simple6DOFSys.java
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

import com.motekew.vse.enums.*;
import com.motekew.vse.intxm.*;
import com.motekew.vse.math.*;
import com.motekew.vse.servm.StringUtils;
import com.motekew.vse.strtm.MassDyadic;

/**
 * This is an abstract class representing a 6DOF simulation of a vehicle
 * A System of first order differential equations representing a simple
 * rigid vehicle that is subject to translational and angular accelerations.
 * The vehicle center of gravity is used as the reference point for forces
 * and torques allowing the separation of translational and rotational
 * dynamics.
 * <P>
 * Torques can be applied about each axis.  Torques are positive in the
 * right hand sense.
 * <P>
 * Roll is defined about the X-axis, with pitch about the Y-axis, and
 * yaw about the Z-axis.  The orientation of these axes w.r.t. the vehicle
 * body is dependent on the class implementing this class.
 * <P>
 * Regarding attitude:  Attitude is held in a unit quaternion.  However,
 * roll, pitch, and yaw rates (angular velocities:  P, Q, R) are computed
 * from angular accelerations of the body w.r.t. the body frame.  P, Q, and
 * R are used with the attitude quaternion to compute the inertial attitude
 * rate (remember, P, Q, R are w.r.t. the body reference frame) of change
 * via the quaternion strapdown equation using the standard Aerospace 3 2 1
 * sequence with Z-axis rotation first, followed by the Y-axis rotation, and
 * finally the X-axis rotation (yaw, pitch, bank).
 * <P>
 * Note that there is a distinction between bank, elevation, and heading,
 * vs. roll, pitch, and yaw.  The former represent the actual attitude of
 * the vehicle relative to the simulation reference frame.  The latter refer
 * to changes or perturbations in attitude about the vehicle body axis.
 * <P>
 * A quaternion is used to track attitude given that quaternions
 * are easier to display in graphics packages, and because they
 * eliminate gimbal lock issues.  The quaternion strapdown equation
 * used in the code below relates body attitude rates, to quaternion
 * rates, which orient the vehicle in the simulation reference frame.
 * <P>
 * Normalization of the quaternion attitude to compensate for limitations
 * inherit in the numerical methods used to propagate the vehicle's state
 * is carried out when necessary.  Over time, this quaternion can drift from
 * unity, causing it to scale in addition to rotate.
 * <P>
 * This class contains two abstract methods.  The first allows computation
 * of accelerations, torques, and forces (both in the inertial and body
 * reference frames) acting on the vehicle using the vehicle's current state,
 * other properties, and control settings.  Once this is done, body relative
 * forces are transformed to the inertial reference frame, and added to the
 * forces acting on the vehicle that are already in the inertial reference
 * frame.  The system is then propagated forward, and outputs are finally
 * updated.  These two abstract classes should provide enough flexibility
 * to allow this class to be used as the basis for a large variety of
 * modeling problems.
 * <P>
 * Two reference frames exist in this model.  There is the inertial
 * (computational) reference frame in which Newton's laws hold without
 * complicated modifications.  There is also the body relative reference
 * frame described above.
 * <P>
 * This system of equations consists of a state vector, control vector,
 * and an output vector.
 * 
 * @author  Kurt Motekew
 * @since   20090108
 * @since   20121128   Updated with changes to IPosVelAtt
 *
 */
public abstract class Simple6DOFSys implements ISysEqns, IPosVelAtt {
  /**
   *  Number of 1st Order diffQs.
   */
  public static final int ORDER = 13;

  /*
   * Indexes used to make code more readable.
   */
  private static final Basis3D X = Basis3D.I;
  private static final Basis3D Y = Basis3D.J;
  private static final Basis3D Z = Basis3D.K;

    // Number of controls, and state derived outputs - based on size
    // of initializing Tuples
  private Tuple controls = null;     // Controls acting on the system.
  private int ncont = 0;
  private Tuple outputs = null;      // Outputs computed from system state.
  private int nout = 0;

  private double   t     = 0.0;               // System time
  private double   delta = 0.1;               // default step size
  private State6DQ x     = new State6DQ();    // State at time t

    // The actual differential equations for this model.
  private Simple6DOFdq dqs = null;
    // The integrator that will propagate the differential equations
  private IIntegrator integ8r = null;

  /*
   * Mass and moment properties.
   */
  private MassDyadic jMat    = new MassDyadic();

    // step - Cache
    // Inertial to body transformation.  Used for initialization
    // and normalization of attitude when necessary.
  private Quaternion  i2bQ = new Quaternion();

  /**
   * Initialize this system.  Set vehicle properties and initialize
   * integrator.
   */
  public Simple6DOFSys() {
      // Set vehicle mass
    jMat.setMass(1.0);
      // Set principle axes inertia moments
    jMat.putJ(X,X, 1.0);
    jMat.putJ(Y,Y, 1.0);
    jMat.putJ(Z,Z, 1.0);

      // Initialize attitude quaternion - it should be updated before
      // the first integration step with the initial simulation state,
      // but do it here just for a good default.
    x.getAttitude(t, i2bQ);

    dqs = new Simple6DOFdq(this);
    integ8r = new RK4(ORDER);
  }

  /**
   * Returns the number of 1st order differential equations in this system.
   * This is also the number of state vectors.
   *
   * @return       A int representing the number of 1st order differential
   *               equations associated with this 6DOF simulation.
   */
  @Override
  public int getOrder() {
    return ORDER;
  }

  /**
   * Initialize the Tuple of controls acting on the model.
   *
   * @param   cs   Controls Tuple.  Pointers set, values not copied.
   */
  public void enableControls(Tuple cs) {
    controls = cs;
    ncont = controls.length(); 
  }

  /**
   * Disable the controls acting on the model.
   */
  public void disableControls() {
    ncont = 0;
  }

  /**
   * Returns the number of controls in this system of equations.
   *                                                           
   * @return       An int representing the number of controls affecting
   *               this system.
   */
  @Override
  public int getNumControls() {
    return ncont;
  }

  /**
   * Initialize the Tuple of outputs generated by this model.
   *
   * @param   ys   Outputs Tuple.  Pointers set, values not copied.
   */
  public void enableOutputs(Tuple ys) {
    outputs = ys;
    nout = outputs.length();
  }

  /**
   * Disable the Tuple of outputs generated by this model.
   */
  public void disableOutputs() {
    nout = 0;
  }

  /**
   * The number of outputs generated from the state vector.
   * 
   * @return    number of outputs
   */
  @Override
  public int getNumOutputs() {
    return nout;
  }  

  /**
   * Returns the time associated with the current state of the model.
   *
   * @return       A double representing current time.
   */
  @Override
  public double getT() {
    return t;
  }

  /**
   * Sets the current time associated with the state of the model.
   * Should really only be used on initialization.
   * 
   * @param t0     A double representing the current simulation time.
   */
  @Override
  public void setT(double t0) {
    t = t0;
  }

  /**
   * Gets values for the state of this system.  The XdX6DQ enum makes
   * array indexing checkable at compile time.
   *
   * @param   i      A XdX6DQ index to the state vector.
   *
   * @return         Output double value of the requested state vector
   *                 parameter.
   */
  public double getX(XdX6DQ i) {
    return x.get(i);
  }

  /**
   * Sets values for the state of this system.  The XdX6DQ enum makes
   * array indexing checkable at compile time.
   *
   * @param i      A XdX6DQ index to the state vector.
   * @param x0     The double value to set the state parameter to.
   */
  public void setX(XdX6DQ i, double x0) {
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
   * Copies the position components of the state into tout.
   *
   * @param    tReq    Time for which position is desired
   * @param    tout    Output:  postion components of the state
   *
   * @return           Actual time associated with output position
   *                   vector
   */
  @Override
  public double getPosition(double tReq, Tuple3D tout) {
    x.getPosition(t, tout);

    return t;
  }

  /**
   * Copies the velocity components of the state into tout.
   *
   * @param    tReq    Time for which velocity is desired
   * @param    tout    Output:  velocity components of the state
   *
   * @return           Actual time associated with output velocity
   *                   vector
   */
  @Override
  public double getVelocity(double tReq, Tuple3D tout) {
    x.getVelocity(t, tout);
 
    return t;
  }

  /**
   * Copies the attitude components of the state into qout.
   *
   * @param    tReq    Time for which attitude is desired
   * @param    tout    Output:  attitude components of the state
   *
   * @return           Actual time associated with output attitude
   */
  @Override
  public double getAttitude(double tReq, Quaternion qout) {
    x.getAttitude(t, qout);

    return t;
  }

  /**
   * Gets outputs that are a function of the state/EOM, but not directly
   * solved for.  For this model, Bank, Elevation, and Heading are derived
   * from an attitude Quaternion, which was determined via integration of
   * the EOM.  Notice there is no method to directly set these values
   * since they are computed from other values.  Use the getNumOutputs()
   * method to size the passed in Tuple to avoid an exception.  If the
   * outputs have not been enabled (or have been disabled), the output
   * values are set to zero.
   *
   * @param   out    Tuple into which output values are to be copied.
   */
  @Override
  public void getY(Tuple out) {
    if (nout > 0) {
      out.set(outputs);
    } else {
      out.zero();
    }
  }

  /**
   * Returns an array of Strings that are appropriate labels for the
   * outputs to this system.
   *
   * @return      Array of String labels representing the output
   *              element names.
   */
  @Override
  public String[] getYNames() {
    if (nout > 0) {
      return outputs.getLabels();
    } else {
      return StringUtils.blankStringArray();
    }
  }

  /**
   * Copies values of the control vector of this system into the passed in
   * Tuple.  The dimensions must match for the Tuple.get() method
   * to not complain.  Use the getNumControls() method to determine how
   * big the passed in Tuple needs to be.  If the controls have not been
   * enabled (or have been disabled), then the output values are set to zero.
   *
   *  @param   out    Tuple into which control vector values are to be copied.
   */
  @Override
  public void getU(Tuple out) {
    if (ncont > 0) {
      out.set(controls);
    } else {
      out.zero();
    }
  }

  /**
   * Copies values from the input Tuple into the control
   * vector of this system.  The dimensions must match for the Tuple.set()
   * method to not complain.  Use of the getNumControls() method to determine
   * how big the passed in Tuple needs to be is recomended.  If the controls
   * have not been enabled (or have been disabled), then nothing is done.
   *
   *  @param   in     Tuple from which control vector values are to be copied.
   */
  @Override
  public void setU(Tuple in) {
    if (ncont > 0) {
      controls.set(in);
    }
  }

  /**
   * Returns an array of Strings that are appropriate labels for the
   * controls to this system.
   *
   * @return      Array of String labels representing the control
   *              element names.
   */
  @Override
  public String[] getUNames() {
    if (ncont > 0) {
      return controls.getLabels();
    } else {
      return StringUtils.blankStringArray();
    }
  }

  /**
   * Returns the mass of the vehicle/object.
   *
   * @return         Mass of the vehicle/object being modeled.
   */
  public double getMass() {
    return jMat.getMass();
  }

  /**
   * Set the mass of the vehicle.
   * 
   * @param   mass   New mass of the vehicle.  Expect bad things to
   *                 happen when set to zero....
   */
  public void setMass(double mass) {
    jMat.setMass(mass);
  }

  /**
   * Returns a pointer to the internal dyadic.  Note, this is the
   * inertia tensor, where off diagonal second area moments have been
   * negated!
   *
   * @return         Mass of the vehicle/object being modeled.
   */
  public MassDyadic getJMat() {
    return jMat;
  }

  /**
   * Returns elements of the vehicle/object inertia matrix.  This is
   * a symmetric matrix.
   *
   * @param    row  Row of element of inertia matrix to get.
   * @param    col  Column of element of inertia matrix to get.
   *
   * @return        Requested element of inertia matrix.  Off diagonal
   *                elements are the actual second area moments, not
   *                the negated values stored in the inertia tensor.
   */
  public double getJ(Basis3D row, Basis3D col) {
    return jMat.getJ(row, col);
  } 

  /**
   * Sets elements of the vehicle/object inertia matrix.
   *
   * @param    row  Row of element of inertia matrix to set.
   * @param    col  Column of element of inertia matrix to set.
   * @param    val  New element value.  Note that (row,col) will
   *                be set to (col,row) if row != col (symmetrix
   *                matrix).  Also note that off diagonal elements
   *                are negated.
   */
  public void putJ(Basis3D row, Basis3D col, double val) {
    jMat.putJ(row, col, val);
  } 

  /**
   * Use this method to complete the model.  Use the accessor methods
   * to this abstract class to access data necessary to generate the
   * the forces, torques, and accelerations acting on the vehicle.  All
   * forces must be resolved to act through the vehicle center of gravity -
   * torques are all in the body reference frame.  Both inertial and
   * body forces can be computed.
   * <P>
   * After this method is called, this class will transform the body relative
   * forces to the inertial frame, and then add them to the inertial forces.
   * This allows for the user of this class to leave the finished model
   * in terms of body and inertial forces, without having to repeat the
   * process of making the transformation.  But, there is no need to separate
   * the forces if the algorithms of this model results in all forces in
   * a single reference frame.
   * <P>
   * Note, all of the Tuple3D parameters are set to ZERO before being passed
   * in - they do not retain values from the previous time step.
   *
   * @param   time     Input:  Time for which to compute influences.
   * @param   state    The state of the system at the entered time.
   * @param   bForce   Output:  Forces acting on body through CG in body
   *                   reference frame.  Added to iForce after being transformed
   *                   to inertial outside of this method.  Reset to ZERO before
   *                   each call to this method.
   * @param   bTorque  Output:  Torques acting about body, in body reference
   *                   frame.  Reset to ZERO before each call to this method.
   * @param   iForce   Output:  Forces acting on body through CG in inertial
   *                   reference frame.  Reset to ZERO before each call to this
   *                   method.
   * @param   iAccel   Output:  Accelerations acting on body, say, from
   *                   gravity....  Reset to ZERO before each call to this
   *                   method.
   */
  protected abstract void finishModel(double time, State6DQ state,
                                      Tuple3D bForce, Tuple3D bTorque,
                                      Tuple3D iForce, Tuple3D iAccel);

  /**
   * After updating the vehicle state, call this method.  The subclass
   * can use accessor methods to derive outputs from the state vector,
   * and whatever other parameters are needed.
   */
  protected abstract void computeOutputs();  

  /**
   * Propagates the system by delta.
   */
  @Override
  public void step() {
    step(delta);
  }

  /**
   * Propagates the system by the input delta.
   *
   * @param user_delta   A double representing a user specified integration
   *                     step size.
   */
  @Override
  public void step(double user_delta) {
    
      // UPDATE STATE VECTOR
    t = integ8r.step(t, user_delta, x, dqs, x);
    x.setTime(t);

    /*
     * Clean up elements of the new state vector that may have
     * exceeded acceptable limits due to limitations in numerical
     * computations.
     *
     * For this simulation, only the quaternion attitude needs to
     * be normalized.  Get it from the state vector, put it into
     * a <code>Quaternion</code>, and then normalize it.
     */
    x.getAttitude(t, i2bQ);
    i2bQ.normalizeTOL();
    x.setAttitude(i2bQ);

    /*
     * Compute outputs based on new state vector.
     */
    computeOutputs();
  }

}
