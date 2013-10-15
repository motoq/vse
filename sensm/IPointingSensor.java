/*
 c  IPointingSensor.java
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

import com.motekew.vse.math.*;

/**
 * Defines an interface for a sensor where the resulting measurement
 * is defined by two measurements.  For, example, the x & y components
 * of a pointing vector, or azimuth and elevation values.
 *
 * @author   Kurt Motekew
 * @since    20120913
 */
public interface IPointingSensor {

  /** Number of measurements taken */
  public int getNumMeasurements();

  /** Get the ith measurement - two components in the sensor reference frame */
  public void getPointing(int ith, Tuple2D uv);

  /** Get the ith 3 component measurement in the body reference frame */
  public void getDirection(int ith, Tuple3D obsDOA);

  /** Body/platform to sensor transformation */
  public void getOrientation(Quaternion q_b2s);

  /**
   * Indicates if sensor is able to provide a reading.  For example,
   * the FOV may be blocked, or the platform may be rotating too fast.
   */
  public boolean isValid();

  /** Get the random measurement error */
  public void getRandomError(Tuple2D sigmaUV);

  /** Get bias error */
  public void getMeasurementBias(Tuple2D biasUV);

}
