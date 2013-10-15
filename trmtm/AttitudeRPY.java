/*
 c  AttitudeRPY.java
 c
 c  Copyright (C) 2000, 2011 Kurt Motekew
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

import com.motekew.vse.enums.Keplerian;
import com.motekew.vse.math.Matrix3X3;
import com.motekew.vse.math.Quaternion;
import com.motekew.vse.math.Tuple3D;

/**
 * This class contains methods to compute a transformation from
 * the computational (inertial) reference frame to what is often called
 * a RPY frame, in which bank, elevation, and heading (roll, pitch, yaw)
 * are conveniently measured.  See each method for a more detailed explanation.
 *
 * @author  Kurt Motekew
 * @since   20111008
 */
public class AttitudeRPY {

    // Used to compute rotation transformations
  private static final Tuple3D ihat = new Tuple3D(1.0, 0.0, 0.0);
  private static final Tuple3D jhat = new Tuple3D(0.0, 1.0, 0.0);
  private static final Tuple3D khat = new Tuple3D(0.0, 0.0, 1.0);

    // Stop instantiation
  private AttitudeRPY() {
  }

  /**
   * Given an osculating Keplerian element set, compute
   * the instantaneous conversion to a RPY (roll, pitch,
   * yaw) reference frame.  The RPY reference frame is
   * oriented such that the z-axis points towards nadir.
   * The x-axis is perpendicular, in the orbital plane,
   * and in the direction of the velocity vector.  The
   * y-axis completes a right handed system.  The x-y
   * plane will always be tangent to a sphere originating
   * at the primary focus of the orbit.
   *
   * @param    koe    IN, Osculating Keplerian element
   *                  set.
   * @param    qOut   OUT, Transformation from the
   *                  inertial reference frame consistent
   *                  with the input Keplerian element set
   *                  to the RPY reference frame, as defined
   *                  above.
   */
  public static void orbitalElem2Quat(KeplerianOE koe, Quaternion qOut) {
    Quaternion q2 = new Quaternion();
    Quaternion q12 = new Quaternion();
      // Back out RAAN
    qOut.set(koe.get(Keplerian.O), khat);
      // Tilt "down" from vertical into orbital plane (inclination is tilted
      // "up" from x-y plane).
    double angle = koe.get(Keplerian.I) - Math.PI/2.0;
    q2.set(angle, ihat);
    q12.mult(qOut, q2);
      // Rotate x-axis up about y-axis by argument of perigee.  Then move to
      // current location via true anomaly.  Now, X points from focus out
      // (radial), and z-axis in direction of velocity vector.  Finally,
      // rotate an additional 90 deg to point x-axis "forward" and Z towards
      // nadir.
    angle = -1.0*(koe.get(Keplerian.W) + koe.get(Keplerian.V) + Math.PI/2.0);
    q2.set(angle, jhat);
    qOut.mult(q12, q2);
  }

  /**
   * Given a state vector (position & velocity) compute
   * the conversion to a RPY (roll, pitch, * yaw) reference frame.
   * If the state vector represents an orbit in an inertial frame,
   * then the computed transformation should match
   * <code>orbitalElem2Quat()</code> provided gravitational parameters
   * are consistent with the model used to generate the position and
   * velocity vectors.
   * <P>
   * Instead of a quaternion, this method computes a rotation
   * matrix transforming from the state vector's reference frame
   * to the (instantanious) RPY frame.   The basis vectors are as follows:
   *   Z-axis (Yaw) is in the negative direction of the position vector.
   *   Y-axis (Pitch) is the Z-axis crossed with the velocity vector.
   *   X-axis completes the orthogonal system (Y-axis cross Z-axis).
   *
   * @param    pos    IN, Position vector.
   * @param    vel    IN, Velocity vector.
   * @param    dcm    OUT, Inertial to RPY reference frame DCM
   */
  public static void posVel2DCM(Tuple3D pos, Tuple3D vel, Matrix3X3 dcm) {
    Tuple3D xHat = new Tuple3D();
    Tuple3D yHat = new Tuple3D();
    Tuple3D zHat = new Tuple3D();

    /*
     * Unitize vectors first to avoid large vs. small vectors.
     * Final basis vectors are not unitized again.
     */

      // Z-axis first
    zHat.set(pos);
    zHat.mult(-1.0);
    zHat.unitize();

      // Temporary, to unitize velocity
    xHat.set(vel);
    xHat.unitize();

      // Now Y-axis
    yHat.cross(zHat, xHat);

      // Finally, X-axis
    xHat.cross(yHat, zHat);

      // Set basis
    dcm.setRows(xHat, yHat, zHat);
  }

}
