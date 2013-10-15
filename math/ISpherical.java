/*
 c  ISpherical.java
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

package com.motekew.vse.math;

/**
 * Defines a function that returns a radius/range value given
 * an azimuth and elevation.
 *
 * @author   Kurt Motekew
 * @since    20090131
 */
public interface ISpherical {

  /**
   * Returns radius values as a function of elevation and azimuth.
   * 
   * @param   elevation   Elevation.  could be a latitude, or
   *                      co-latitude, depending on the function.
   *                      Be sure to define from where this parameter
   *                      is measured when implementing this function.
   *                      In general, it will be assumed to be between
   *                      -PI/2 and +PI/2.
   * @param   azimuth     Azimuth.  Once again, define range and what is
   *                      meant.  This could be a longitude.
   *
   * @return              Radius/range that is a function of
   *                      the input azimuth and elevation value.
   */
  public double getR(double elevation, double azimuth);

  /**
   * Creates vector in Cartesian coordinates from elevation and azimuth.
   * 
   * @param   elevation   Elevation, as defined above.
   * @param   azimuth     Azimuth, as defined above.
   * @param   xyz         X, Y, and Z coordinates.  Most likely computed
   *                      by first finding the radius, and then converting
   *                      to Cartesian.  Uses a conversion method that is
   *                      consistent with the definition of elevation and
   *                      azimuth in this class.
   */
  public void getXYZ(double elevation, double azimuth, Tuple3D xyz);
}
