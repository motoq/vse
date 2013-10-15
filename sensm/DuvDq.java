/*
 c  DuvDq.java
 c
 c  Copyright (C) 2012 Kurt Motekew
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

package com.motekew.vse.sensm;

import com.motekew.vse.enums.*;
import com.motekew.vse.math.*;

/**
 * Jacobian relating the quaternion transformation from a unit pointing
 * vector in one reference frame to another.
 * 
 * @author   Kurt Motekew
 * @since    20120908
 */
public class DuvDq extends Matrix {
  private Matrix3X3 body2Sensor = new Matrix3X3();
  private Matrix dpdb = new Matrix(2,3);             // pointing to body
  private Matrix dbdi = new Matrix(3,4);             // body to computational

  /**
   * Instantiate as a [2x4] matrix with all partials set to zero.
   */
  public DuvDq() {
    super(2,4);
  }

  /**
   * Computes the partials of a unit pointing vector to a reference point
   * in the sensor reference frame to the quaternion used to transform from
   * the inertial/computational to the body reference frame under a quaternion
   * frame rotation operation (q'*r*q).  Since only the 1st and 2nd elements of
   * the pointing vector provide useful partials info, only they are returned.
   * The first element of the transformed pointing vector is 'u' and the 2nd
   *  element is 'v'.
   *
   * @param   xyz    Unit pointing vector to the reference point in the
   *                 inertial/computational reference frame.
   * @param   qbs    Body to sensor reference frame transformation quaternion.
   * @param   q      Inertial/computational to body reference frame
   *                 transformation quaternion.  This is what the partial is
   *                 taken w.r.t.
   *
   * @return         A pointer to this Jacobian relating u & v to q
   *                        <P>
   *                                  -                -
   *                           dpdq = | du/dq0  dv/dq0 |
   *                                  | du/dq1  dv/dq1 |
   *                                  | du/dq2  dv/dq2 |
   *                                  | du/dq3  dv/dq3 |
   *                                  -                -
   */
  public Matrix partials(Tuple3D xyz, Quaternion qbs, Quaternion q) {
      // Partial of sensor reference frame to body reference frame
    qbs.rotMat(body2Sensor);
    dpdb.put(1, 1, body2Sensor.get(1,1));
    dpdb.put(1, 2, body2Sensor.get(1,2));
    dpdb.put(1, 3, body2Sensor.get(1,3));
    dpdb.put(2, 1, body2Sensor.get(2,1));
    dpdb.put(2, 2, body2Sensor.get(2,2));
    dpdb.put(2, 3, body2Sensor.get(2,3));

    double x = xyz.get(Basis3D.I);
    double y = xyz.get(Basis3D.J);
    double z = xyz.get(Basis3D.K);

    double q0 = q.get(Q.Q0);
    double q1 = q.get(Q.QI);
    double q2 = q.get(Q.QJ);
    double q3 = q.get(Q.QK);

      // Partial of body pointing w.r.t. inertial
    dbdi.put(1,1, 4*x*q0 + 2*y*q3 - 2*z*q2);
    dbdi.put(1,2, 4*x*q1 + 2*y*q2 + 2*z*q3);
    dbdi.put(1,3, 2*y*q1 - 2*z*q0);
    dbdi.put(1,4, 2*z*q1 + 2*y*q0);
      // Row 2
    dbdi.put(2,1, 4*y*q0 + 2*z*q1 - 2*x*q3);
    dbdi.put(2,2, 2*x*q2 + 2*z*q0);
    dbdi.put(2,3, 2*x*q1 + 4*y*q2 + 2*z*q3);
    dbdi.put(2,4, 2*z*q2 - 2*x*q0);
      // Row 3
    dbdi.put(3,1, 4*z*q0 + 2*x*q2 - 2*y*q1);
    dbdi.put(3,2, 2*x*q3 - 2*y*q0);
    dbdi.put(3,3, 2*y*q3 + 2*x*q0);
    dbdi.put(3,4, 2*x*q1 + 2*y*q2 + 4*z*q3);

      // Finally, partial of sensor to inertial
    this.mult(dpdb, dbdi);

    return this;
  }

}
