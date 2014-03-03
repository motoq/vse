/*
 c  AttitudeQuatdq.java
 c
 c  Copyright (C) 2013 Kurt Motekew
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
import com.motekew.vse.enums.QdQ;
import com.motekew.vse.intxm.IDiffQ;
import com.motekew.vse.math.*;
import com.motekew.vse.strtm.MassDyadic;

/**
 * This class contains the differential equations to solve
 * for quaternion attitude and attitude rates.
 *
 * @author  Kurt Motekew
 * @since   20131223
 */
public class AttitudeQuatdq implements IDiffQ {
  private final int ORDER = 7;
  
  private MassDyadic jMat;

  /**
   * @param   mt   Point to this mass properties object.
   */
  public AttitudeQuatdq(MassDyadic mt) {
    jMat = mt;
  }

  /**
   *
   * @return   The number of 1st order differential equations in this system.
   */
  @Override
  public int getOrder() {
    return ORDER;
  }

   /**
    * This method computes the derivative values based on the model
    * of the system of equations.
    *
    * @param t        A double time for which the functions (derivatives)
    *                 are to be evaluated.
    * @param x_tup    A Tuple state vector at time t, attitude and attitude
    *                 rates.
    * @param xd_tup   A Tuple to insert the computed derivative values into
    *                 Attitude rate and angular acceleration.
    *                 This is an Output.
    */
  private EquationsOfMotionCache eomC = new EquationsOfMotionCache();
  @Override
  public void getXDot(double t, Tuple x_tup, Tuple xd_tup) {
    Tuple3D bodyTorqueCG = new Tuple3D();

      // Convert to 6DOF state vectors to ease indexing
    eomC.x_qdq.set(x_tup);
    eomC.xd_qdq.set(xd_tup);
    eomC.x_qdq.getAttitude(eomC.i2bQ);

    eomC.wBody.set(eomC.x_qdq,QdQ.P.ordinal()+1);
      // Convert from body rates to inertial attitude rates.
      // Get et attitude quaternion, then strapdown equation:
    eomC.i2bQ.set(eomC.x_qdq, QdQ.Q0.ordinal()+1);
    eomC.di2bQ.set(eomC.wBody, eomC.i2bQ);
      // Update quaternion attitude derivative values:
    eomC.xd_qdq.put(QdQ.Q0, eomC.di2bQ.get(Q.Q0));
    eomC.xd_qdq.put(QdQ.QI, eomC.di2bQ.get(Q.QI));
    eomC.xd_qdq.put(QdQ.QJ, eomC.di2bQ.get(Q.QJ));
    eomC.xd_qdq.put(QdQ.QK, eomC.di2bQ.get(Q.QK));

      // now determine body attitude accelerations
    eomC.jwb.mult(jMat, eomC.wBody);
    eomC.wjwb.cross(eomC.wBody, eomC.jwb);
    eomC.tqc.minus(bodyTorqueCG, eomC.wjwb);
      // Now find rate of change in rotational velocity.
    eomC.wBodyDot.mult(jMat.getInv(), eomC.tqc);
      // Set the body roll, pitch, and yaw accelerations.
    eomC. xd_qdq.put(QdQ.P, eomC.wBodyDot.get(Basis3D.I));
    eomC.xd_qdq.put(QdQ.Q, eomC.wBodyDot.get(Basis3D.J));
    eomC.xd_qdq.put(QdQ.R, eomC.wBodyDot.get(Basis3D.K));

      // Don't forget to update the normal Tuple output
    xd_tup.set(eomC.xd_qdq);
  }

    // getXDot() Cache - intermediate computations
  private class EquationsOfMotionCache {
      // Used to hold attitude of body relative to inertial ref frame
      // for transformations - pulled from current state.
      // Derivative used for equations of motion.        
    Quaternion  i2bQ = new Quaternion(); 
    Strapdown di2bQ = new Strapdown();

    StateQdQ x_qdq   = new StateQdQ();
    StateQdQ xd_qdq  = new StateQdQ();
    Tuple3D wBody    = new Tuple3D();  // body attitude rates
    Tuple3D jwb      = new Tuple3D();
    Tuple3D wjwb     = new Tuple3D();
    Tuple3D tqc      = new Tuple3D();  // coriollis corrected torque
    Tuple3D wBodyDot = new Tuple3D();  // body attitude angular accel
  }

}
