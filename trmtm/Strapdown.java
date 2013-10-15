/*
 c  Strapdown.java
 c
 c  Copyright (C) 2000, 2013 Kurt Motekew
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
import com.motekew.vse.math.Quaternion;
import com.motekew.vse.math.Tuple3D;

/**
 * This class contains static methods that compute "inertial" body
 * attitude rates given body relative attitude rates.  This is often
 * refered to as the "strapdown equation".
 * <P>
 * Basically, the "inertial" attitude, and body relative roll, pitch,
 * and yaw rates, are combined to produce the "inertial" attitude
 * rate.
 * <P>
 * Only the Quaternion version of the strapdown equation has been
 * implemented at this time.
 *
 * @author  Kurt Motekew
 * @since   20081214
 */
public final class Strapdown {

  /**
   * Default constructor - don't allow it to be instantiated
   */
  private Strapdown() {
  }

  /**
   * Given the body relative roll pitch and yaw rates, along with the current
   * body attitude in some inertial frame, compute the attitude rate in
   * that frame.
   * 
   * @param   pqr              Body relative roll, pitch, and yaw rates
   *                           (rad/time unit)
   * @param   attitude         Body attitude in inertial/computational reference
   *                           frame.
   * @param   attitude_rate    Rate of change in attitude in inertial/computational
   *                           reference frame.  Same time units as pqr.
   */
  public static void quaternionStrapdown(Tuple3D pqr, Quaternion attitude,
                                                 Quaternion attitude_rate) {
      // body attitude rates in body frame
    double p  = pqr.get(Basis3D.I);  // P, dphi,   roll rate
    double q  = pqr.get(Basis3D.J);  // Q, dtheta, pitch rate
    double r  = pqr.get(Basis3D.K);  // R, dpsi,   yaw rate        
      // Quaternion attitude
    double q0 = attitude.get(Q.Q0);
    double q1 = attitude.get(Q.QI);
    double q2 = attitude.get(Q.QJ);
    double q3 = attitude.get(Q.QK);

    attitude_rate.put(Q.Q0,  -0.5*( p*q1 + q*q2 + r*q3) );           
    attitude_rate.put(Q.QI,  -0.5*(-p*q0 - r*q2 + q*q3) );
    attitude_rate.put(Q.QJ,  -0.5*(-q*q0 + r*q1 - p*q3) );
    attitude_rate.put(Q.QK,  -0.5*(-r*q0 - q*q1 + p*q2) );
  }
}
