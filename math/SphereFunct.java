/*
 c  SphereFunct.java
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
 * Mathematical model of a sphere.
 *
 * @author   Kurt Motekew
 * @since    20090131
 */
public class SphereFunct implements ISpherical {
  private double radius = 1.0;

  /**
   * Initizlize with the radius of the sphere defined by this function.
   */
  public SphereFunct(double r0) {
    radius = r0;
  }

  /**
   * Sets the radius of the sphere defined by this function.
   */
  public void setR(double r0) {
    radius = r0;
  }

  /**
   * Since this is a sphere, the radius value will be constant, unless changed
   * by the setR() accessor method.
   * 
   * @param   elevation   Elevation.  Any number will return the stored radius.
   * @param   azimuth     Azumuth.  Any number will return the stored radius
   *
   * @return              Radius.
   */
  @Override
  public double getR(double elevation, double azimuth) {
    return radius;
  }

  /**
   * Returns the Cartesian coordinates on the sphere with given elevation and
   * azimuth values.
   * 
   * @param   elevation   Elevation, in radians, positive up from XY plane.
   * @param   azimuth     Azimuth, in radians, positive from the X axis in the
   *                      direction of Y (right hand rule about Z).
   * @param   xyz         X, Y, and Z coordinates.  Most likely computed
   *                      by first finding the radius, and then converting
   *                      to spherical.  Uses a conversion method that is
   *                      consistent with the definition of elevation and
   *                      azimuth in this class.
   */
  @Override
  public void getXYZ(double elevation, double azimuth, Tuple3D xyz) {
    SphericalUtil.rLatLon2xyz(radius, elevation, azimuth, xyz);
  }
}
