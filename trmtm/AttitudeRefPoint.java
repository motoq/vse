/*
 c  AttitudeRefPoint.java
 c
 c  Copyright (C) 2011 Kurt Motekew
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

import com.motekew.vse.math.Matrix3X3;
import com.motekew.vse.math.Tuple3D;

/**
 * This class contains methods to compute a transformation from
 * the computational (inertial) reference frame to one relative to
 * a reference point in space.
 * <P>
 * These methods are not static due to caching of the objects used to
 * compute the transformations.  This may be updated in the near future
 * given the implementation of escape analysis in Java (and should lead to
 * thread safe utility functions).
 *
 * @author  Kurt Motekew
 * @since   20111116
 */
public class AttitudeRefPoint {

    // i2RefPnt cache - inertial pos/vel to RPY frame conversion
  private class I2RefPntCache {
    Tuple3D xHat = new Tuple3D();
    Tuple3D yHat = new Tuple3D();
    Tuple3D zHat = new Tuple3D();
  }
  private I2RefPntCache i2rpC = new I2RefPntCache();

  /**
   * Given a state vector of the object for which the attitude
   * is being computed and the reference point position, compute
   * the matrix transformation from inertial to the reference point
   * based reference frame.
   * <P>
   * The z-axis points towards the reference point, with the y-axis in the
   * same plane as the vector from the inertial origin to the reference
   * and the origin to the vehicle being modeled.  The positive y-axis points
   * towards (but not necessarily at) the origin.
   *
   * @param    pos    IN, Position vector, inertial ref frame
   * @param    rpnt   IN, Reference point, inertial ref frame
   * @param    dcm    OUT, Inertial to ref point frame DCM
   */
  public void posPnt2DCM(Tuple3D pos, Tuple3D rpnt, Matrix3X3 dcm) {
      // No need to unitize first since magnitudes of vectors should
      // be similar.
      // Start by setting z-axis to line from object to ref point. 
    i2rpC.zHat.minus(rpnt, pos);
    i2rpC.xHat.cross(i2rpC.zHat, rpnt);
    i2rpC.yHat.cross(i2rpC.zHat, i2rpC.xHat);
      // Form I2B DCM with row vectors
    i2rpC.xHat.unitize();
    i2rpC.yHat.unitize();
    i2rpC.zHat.unitize();
    dcm.setRows(i2rpC.xHat, i2rpC.yHat, i2rpC.zHat);
  }

}
