/*
 c  IGravity.java
 c
 c  Copyright (C) 2000, 2009 Kurt Motekew
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

import com.motekew.vse.math.Tuple3D;

/**
 * This interface defines the expected behavior of an object
 * modeling gravitational potential an acceleration.
 * 
 *  @author  Kurt Motekew
 *  @since   20090330
 */
public interface IGravity {

  /**
   * @return    Gravitational Parameter (distance_units^3/time_units^2)
   */
  public double getGravParam();
  
  /**
   * @return    Scaling radius used to compute gravitational potential
   */
  public double getRefRadius();

  /**
   * @return    The degree and order of this gravity model.
   */
  public int getDegreeOrder();

  /**
   * getPotential without the option of specifying degree/order.
   */
  public double getPotential(double r, double elevation,
                                       double azimuth);

  /**
   * Returns the gravitational potential given a position relative to the
   * centroid of the body.
   *
   * @param   degree      The degree and order to be used in evaluating
   *                      the model.
   * @param   r           Distance from the centroid
   * @param   elevation   Elevation.  could be a latitude, or
   *                      co-latitude, depending on the function.
   *                      Be sure to define from where this parameter
   *                      is measured when implementing this function.
   *                      In general, it will be assumed to be between
   *                      -PI/2 and +PI/2.
   * @param   azimuth     Azimuth.  Once again, define range and what is
   *                      meant.  This could be a longitude.
   *
   * @return              Gravitational potential at the input position.
   */
  public double getPotential(int degree, double r, double elevation,
                                                   double azimuth);

  /**
   * gravt without the option of specifying the degree/order to be
   * used.
   */
  public void gravt(double r, double elevation, double azimuth,
                                                Tuple3D accel);

  /**
   * Returns the gravitational acceleration given a position relative to the
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
   * @param   accel       Output:  Acceleration, relative to the body.  X-axis
   *                      runs through lat=lon=0.0, and the Z-axis runs through
   *                      lat = 90.
   */
  public void gravt(int degree, double r, double elevation, double azimuth,
                                                            Tuple3D accel);

  /**
   * gravt without the option of specifying the degree/order to be used.
   */
  public void gravt(Tuple3D pos, Tuple3D accel);

  /**
   * This version of gravt accepts body relative Cartesian position as an
   * input.
   *
   * @param   degree   The degree and order to be used by this model.
   * @param   pos      Position relative to the centroid.
   * @param   accel    Output:  Acceleration, relative to the body centroid.
   */
  public void gravt(int degree, Tuple3D pos, Tuple3D accel);
}
