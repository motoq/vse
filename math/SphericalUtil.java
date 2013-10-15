/*
 c  SphericalUtil.java
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

import com.motekew.vse.enums.Basis3D;

/**
 * This class contains utilities related to great circle geometry, spherical
 * harmonics, and related functions. 
 */
public class SphericalUtil {
  public double cel = 1.0;
  public double sel = 0.0;

  /**
   * Used when the elevation value is held constant, and iteration is done for
   * multiple azimuth values.  Set the elevation with this method, and call the
   * appropriate version of a utility method that takes advantage of these
   * parameters.  Utilities that accept elevation as a parameter should
   * NOT change the locally stored elevation - only this method should make
   * changes to the locally stored elevation value.
   * <P>
   * See rLatLon2xyz(double, double, double, Tuple3D) and
   * rLatLon2xyz(double, double, Tuple3D) for an example....
   */ 
  public void setElevation(double el) {
    cel = Math.cos(el);
    sel = Math.sin(el);
  }

  /**
   * Converts a radius, elevation, and azimuth to X, Y, and Z Cartesian
   * coordinates.
   *
   * @param   radius   Radius, or range value, in spherical coordinates.
   * @param   el       Elevation, in radians.  Measured positive up from the
   *                   XY plane.
   * @param   az       Azimuth, measure positive from x axis towards y (positive
   *                   about Z in right hand sense), in radians.
   * @param   xyz      Output x, y, and z coordinates.
   */
  public static void rLatLon2xyz(double radius, double el, double az,
                                                         Tuple3D xyz) {
    double localCel = Math.cos(el);

    xyz.put(Basis3D.I, radius*localCel*Math.cos(az));
    xyz.put(Basis3D.J, radius*localCel*Math.sin(az));
    xyz.put(Basis3D.K, radius*Math.sin(el));
  }

  /**
   * Converts a radius, elevation, and azimuth to X, Y, and Z Cartesian
   * coordinates.  Makes use of previously stored elevation values.  Make
   * sure setElevation() has been called before making use of this method;
   *
   * @param   radius   Radius, or range value, in spherical coordinates.
   * @param   az       Azimuth, measure positive from x axis towards y (positive
   *                   about Z in right hand sense), in radians.
   * @param   xyz      Output x, y, and z coordinates.
   */
  public void rLatLon2xyz(double radius, double az, Tuple3D xyz) {
    xyz.put(Basis3D.I, radius*cel*Math.cos(az));
    xyz.put(Basis3D.J, radius*cel*Math.sin(az));
    xyz.put(Basis3D.K, radius*sel);
  }
}
