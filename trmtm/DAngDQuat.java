/*
 c  DAngDQuat.java
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
import com.motekew.vse.math.*;

/**
 * Jacobian relating total rotation angle to quaternion components.
 * 
 * @author   Kurt Motekew
 * @since    20130922
 */
public class DAngDQuat extends Matrix {

  /**
   * Instantiate as a [1x4] matrix with all partials set to zero.
   */
  public DAngDQuat() {
    super(1,4);
  }

  /**
   * Set Jacobian
   *
   * @param   qAtt   Attitude <code>Quaternion</code>.
   *
   * @return         A pointer to this <code>Matrix</code>
   *                 a = total rotation angle (about eigenaxis)
   *
   *                          -                             -
   *                   dadq = | da/dqs da/dqi da/dqj da/dqk |
   *                          -                             -
   */
  public Matrix partials(Quaternion q) {
    Tuple3D eigAxis = new Tuple3D();
    double angleO2 = 0.5*q.axisAngle(eigAxis);
    double hcao2 = 0.5*Math.cos(angleO2);

    this.put(1, 1, -0.5*Math.sin(angleO2));
    this.put(1, 2, hcao2*eigAxis.get(Basis3D.I));
    this.put(1, 3, hcao2*eigAxis.get(Basis3D.J));
    this.put(1, 4, hcao2*eigAxis.get(Basis3D.K));

    return this;
  }

}
