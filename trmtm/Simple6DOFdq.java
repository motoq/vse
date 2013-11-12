/*
 c  Simple6DOFdq.java
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
import com.motekew.vse.intxm.IDiffQ;
import com.motekew.vse.math.*;
import com.motekew.vse.strtm.MassDyadic;

/**
 * This class contains the differential equations for a 6 degree
 * of freedom model subject to translational and rotational
 * accelerations.  See <code>Simple6DOFSys</code> for more info.
 *
 * @author  Kurt Motekew
 * @since   20090108
 */
public class Simple6DOFdq implements IDiffQ {
  private final int ORDER;

  /*
   * Indexes used to make code more readable.
   */
  private static final Basis3D X = Basis3D.I;
  private static final Basis3D Y = Basis3D.J;
  private static final Basis3D Z = Basis3D.K;

  private Simple6DOFSys system = null;

  public Simple6DOFdq(Simple6DOFSys sys_in) {
    ORDER = sys_in.getOrder();
    system = sys_in;
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
    * This method computes the derivative values based on the model
    * of the system of equations.  This is where the real work is done.
    *
    * 
    * @param t        A double time for which the functions (derivatives)
    *                 are to be evaluated.
    * @param x_tup    A Tuple state vector at time t.
    * @param xd_tup   A Tuple to insert the computed derivative values into.
    *                 This is an Output.
    */
  private InfluencesCache iC = new InfluencesCache();
  private EquationsOfMotionCache eomC = new EquationsOfMotionCache();
  @Override
  public void getXDot(double t, Tuple x_tup, Tuple xd_tup) {
    double m;
    MassDyadic jMat = null;

      // Convert to 6DOF state vectors to ease indexing
    eomC.x_6dq.set(x_tup);
    eomC.x_6dq.setTime(t);
    eomC.xd_6dq.set(xd_tup);
    eomC.xd_6dq.setTime(t);

    /*
     * Compute total body torques/forces and inertial forces & accelerations
     * given time, current vehicle state, & current control forces/torques.
     * All forces returned should be resolve about the vehicle center of mass.
     * First clear values from previous call.
     */
    iC.bodyForceCG.zero();
    iC.bodyTorqueCG.zero();
    iC.inertialForceCG.zero();
    iC.inertialFieldAccel.zero();
    system.finishModel(t, eomC.x_6dq, iC.bodyForceCG, iC.bodyTorqueCG,
                                      iC.inertialForceCG, iC.inertialFieldAccel);
    jMat = system.getJMat();
    m = jMat.getMass();

    /*
     * Convert forces from Body to Inertial reference
     * frame.  Then update diffQs with the new force
     * and torque values.
     *
     * This is an inertial to body frame rotation quaternion,
     * so do a vector rotation to convert the body vector to an
     * inertial one.
     */
    eomC.x_6dq.getAttitude(t, eomC.i2bQ);
    eomC.forceBI.vRot(eomC.i2bQ, iC.bodyForceCG);
      // total force through cg!  
    iC.inertialForceCG.plus(eomC.forceBI);

    /*
     * Now compute derivatives - start with translational motion
     */
    eomC.xd_6dq.put(XdX6DQ.X, eomC.x_6dq.get(XdX6DQ.DX));       // DX:1,4
    eomC.xd_6dq.put(XdX6DQ.Y, eomC.x_6dq.get(XdX6DQ.DY));       // DY:2,5
    eomC.xd_6dq.put(XdX6DQ.Z, eomC.x_6dq.get(XdX6DQ.DZ));       // DZ:3,6
    eomC.xd_6dq.put(XdX6DQ.DX, iC.inertialFieldAccel.get(X) +
                               iC.inertialForceCG.get(X)/m);    // DDX:4
    eomC.xd_6dq.put(XdX6DQ.DY, iC.inertialFieldAccel.get(Y) +
                               iC.inertialForceCG.get(Y)/m);    // DDY:5
    eomC.xd_6dq.put(XdX6DQ.DZ, iC.inertialFieldAccel.get(Z) +
                               iC.inertialForceCG.get(Z)/m);    // DDZ:6

    /*
     *  rotational motion (a bit more involved)
     */
      // now update the body attitude rate tuple. These
      // are the roll, pitch, yaw rates relative to the body
      // reference frame:
      //   x(11):  P, dphi,   roll  rate
      //   x(12);  Q, dtheta, pitch rate
      //   x(13);  R, dpsi,   yaw   rate  
    eomC.wBody.set(eomC.x_6dq,XdX6DQ.P.ordinal()+1);
      // Convert from body rates to inertial attitude rates.
      // First get attitude quaternion, then strapdown equation:
      // q0 = x(7);  q1 = x(8);  q2 = x(9);  q3 = x(10);
    eomC.i2bQ.set(eomC.x_6dq, XdX6DQ.Q0.ordinal()+1);
    eomC.di2bQ.set(eomC.wBody, eomC.i2bQ);
      // Update quaternion attitude derivative values:
    eomC.xd_6dq.put(XdX6DQ.Q0, eomC.di2bQ.get(Q.Q0));
    eomC.xd_6dq.put(XdX6DQ.QI, eomC.di2bQ.get(Q.QI));
    eomC.xd_6dq.put(XdX6DQ.QJ, eomC.di2bQ.get(Q.QJ));
    eomC.xd_6dq.put(XdX6DQ.QK, eomC.di2bQ.get(Q.QK));

      // now determine body attitude accelerations
    eomC.jwb.mult(jMat, eomC.wBody);
    eomC.wjwb.cross(eomC.wBody, eomC.jwb);
    eomC.tqc.minus(iC.bodyTorqueCG, eomC.wjwb);
      // Now find rate of change in rotational velocity.
    eomC.wBodyDot.mult(jMat.getInv(), eomC.tqc);
      // Set the body roll, pitch, and yaw accelerations.
    eomC. xd_6dq.put(XdX6DQ.P, eomC.wBodyDot.get(X)); // Pdot, ddphi, roll acc
    eomC.xd_6dq.put(XdX6DQ.Q, eomC.wBodyDot.get(Y));  // Qdot, ddtheta, pitch acc
    eomC.xd_6dq.put(XdX6DQ.R, eomC.wBodyDot.get(Z));  // Rdot, ddpsi, yaw acc

      // Don't forget to update the normal Tuple output
    xd_tup.set(eomC.xd_6dq);
  }

  /*
   * These 4 Tuples contain the composite forces, torques, and 
   * accelerations acting on the body needed to propagate the vehicle.
   * Vehicle state and control settings can be used to compute these  
   * values, which then get used in propagating the state.          
   *
   * All forces assumed acting through the CG, allowing for the
   * separating of translational and angular motion computations.
   */
    // getXDot() Cache - influences on the body
  private class InfluencesCache {
      // Total body forces acting on the vehicle through the cg
    Tuple3D bodyForceCG = new Tuple3D();
      // Total torques acting about the body.
    Tuple3D bodyTorqueCG = new Tuple3D();
      // Total force acting on body in inertial reference frame
    Tuple3D inertialForceCG = new Tuple3D();           
      // Total field acceleration (in inertial reference frame).
    Tuple3D inertialFieldAccel = new Tuple3D();
  }
    // getXDot() Cache - intermediate computations
  private class EquationsOfMotionCache {
      // Holds conversion of body to inertial forces, added to total
      // inertial forces.
    Tuple3D forceBI = new Tuple3D();
      // Used to hold attitude of body relative to inertial ref frame
      // for transformations - pulled from current state.
      // Derivative used for equations of motion.        
    Quaternion  i2bQ = new Quaternion(); 
    Strapdown di2bQ = new Strapdown();

    State6DQ x_6dq  = new State6DQ();
    State6DQ xd_6dq = new State6DQ();
    Tuple3D wBody    = new Tuple3D();  // body attitude rates
    Tuple3D jwb      = new Tuple3D();
    Tuple3D wjwb     = new Tuple3D();
    Tuple3D tqc      = new Tuple3D();  // coriollis corrected torque
    Tuple3D wBodyDot = new Tuple3D();  // body attitude angular accel
  }

}
