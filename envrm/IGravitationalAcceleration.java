/*
 c  IGravitationalPotential.java
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

package com.motekew.vse.envrm;

import com.motekew.vse.enums.IGetDDX3D;
import com.motekew.vse.math.Tuple3D;

/**
 * This interface defines an interface to an object generating
 * gravitational acceleration.
 * 
 *  @author  Kurt Motekew
 *  @since   20131120
 */
public interface IGravitationalAcceleration extends IGetDDX3D {

  /**
   * gravt without the option of specifying the degree/order to be
   * used.
   */
  public void gravt(double r, double elevation, double azimuth);

  /**
   * Generates the gravitational acceleration given a position relative to the
   * centroid of the body.  Input position is in spherical (radius, ele, az),
   * and output is in Cartesian (x, y, z).
   *
   * @param   degree      The degree and order to be used by the model.
   * @param   r           Distance from the centroid
   * @param   elevation   Elevation.  could be a latitude, or
   *                      co-latitude, depending on the function.
   *                      Be sure to define from where this parameter
   *                      is measured when implementing this function.
   *                      In general, it will be assumed to be between
   *                      -PI/2 and +PI/2.
   * @param   azimuth     Azimuth.  Once again, define range and what is
   *                      meant.  This could be a longitude.
   */
  public void gravt(int degree, double r, double elevation, double azimuth);

  /**
   * gravt without the option of specifying the degree/order to be used.
   */
  public void gravt(Tuple3D pos);

  /**
   * This version of gravt accepts body relative Cartesian position as an
   * input.
   *
   * @param   degree   The degree and order to be used by this model.
   * @param   pos      Position relative to the centroid.
   * @param   accel    Output:  Acceleration, relative to the body centroid.
   */
  public void gravt(int degree, Tuple3D pos);

}
