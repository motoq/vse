/*
 c  DEulerDQuat.java
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

import com.motekew.vse.enums.Q;
import com.motekew.vse.math.*;

/**
 * Jacobian relating Euler angles to quaternion components.  The aerospace
 * sequence is followed (rotate by the heading, then the elevation, and
 * finally by the bank).
 * 
 * @author   Kurt Motekew
 * @since    20130903
 */
public class DEulerDQuat extends Matrix {

  /**
   * Instantiate as a [3x4] matrix with all partials set to zero.
   */
  public DEulerDQuat() {
    super(3,4);
  }

  /**
   * Set Jacobian
   *
   * @param   qAtt   Attitude <code>Quaternion</code>.
   *
   * @return         A pointer to this <code>Matrix</code>
   *                 (r,p,y) = (roll, pitch, yaw)
   *                          -                             -
   *                   dedq = | dr/dqs dr/dqi dr/dqj dr/dqk |
   *                          | dp/dqs dp/dqi dp/dqj dr/dqk |
   *                          | dy/dqs dy/dqi dy/dqj dy/dqk |
   */
  public Matrix partials(Quaternion q) {
    double q0 = q.get(Q.Q0);
    double q1 = q.get(Q.QI);
    double q2 = q.get(Q.QJ);
    double q3 = q.get(Q.QK);
    double coeff, p1, p2, p3, p4;

    /*
     * Partial of bank w.r.t. the quaternion
     */
    double phiNum = 2.0*(q2*q3 + q0*q1);
    double phiDen = 1.0/(2.0*(q0*q0 + q3*q3) - 1.0); 
    double phiDen2 = phiDen*phiDen;
    double tanPhi = phiNum*phiDen;

    coeff  = 2.0/(1.0 + tanPhi*tanPhi);
    p1 = coeff * (q1*phiDen - 2.0*q0*phiNum*phiDen2);
    p2 = coeff *  q0*phiDen;
    p3 = coeff *  q3*phiDen;
    p4 = coeff * (q2*phiDen - 2.0*q3*phiNum*phiDen2);
    this.put(1, 1, p1);
    this.put(1, 2, p2);
    this.put(1, 3, p3);
    this.put(1, 4, p4);

    /*
     * Partial of elevation w.r.t. the quaternion
     */
    double sinTheta = 2.0*(q0*q2 - q1*q3);
    coeff = 2.0/Math.sqrt(1.0 - sinTheta*sinTheta);
    p1 =  coeff * q2;
    p2 = -coeff * q3;
    p3 =  coeff * q0;
    p4 = -coeff * q1;
    this.put(2, 1, p1);
    this.put(2, 2, p2);
    this.put(2, 3, p3);
    this.put(2, 4, p4);

    /*
     * Partial of heading w.r.t. the quaternion
     */
    double psiNum = 2.0*(q1*q2 + q0*q3);
    double psiDen = 1.0/(2.0*(q0*q0 + q1*q1) - 1.0);
    double psiDen2 = psiDen*psiDen;
    double tanPsi = psiNum*psiDen;

    coeff = 2.0/(1.0 + tanPsi*tanPsi);
    p1 = coeff * (q3*psiDen - 2.0*q0*psiNum*psiDen2);
    p2 = coeff * (q2*psiDen - 2.0*q1*psiNum*psiDen2);
    p3 = coeff * q1*psiDen;
    p4 = coeff * q0*psiDen;
    this.put(3, 1, p1);
    this.put(3, 2, p2);
    this.put(3, 3, p3);
    this.put(3, 4, p4);

    return this;
  }

}
