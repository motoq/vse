/*
 c  AttitudeStateTransition.java
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

import com.motekew.vse.math.Quaternion;
import com.motekew.vse.math.Tuple3D;

/**
 * This extension of the <code>Quaternion</code> class represents the
 * linear state transition quaternion given a duration and angular velocity
 * vector.  If the angular velocity vector is constant over time, then
 * this linear approximation will match the actual change in attitude.
 * Based on "Strapdown Inertial Navigation Technology", Chapt 11, by
 * Titterton and Weston.
 *
 * @author  Kurt Motekew
 * @since   20131112
 */
public class AttitudeStateTransition extends Quaternion {

  /**
   * Sets this quaternion to be the (linear) state transition
   * quaternion given a duration and angular velocity vector, which
   * is assumed to be fixed in time.
   *
   * @param   dt      Propagation time
   * @param   omega   Angular velocity vector, units consistent with dt
   */
  public void set(double dt, Tuple3D omega) {
    double ang = dt*omega.mag();
    Tuple3D evec = new Tuple3D();
    evec.set(omega);
    evec.unitize();
    set(ang, evec);
  }
}
