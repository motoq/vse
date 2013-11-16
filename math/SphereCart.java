/*
 c  SphereCart.java
 c
 c  Copyright (C) 2000, 2013 Kurt Motekew
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
 * Mathematical model of a sphere.  Setting the radius, elevation,
 * and azimuth values automatically updates the Cartesian
 * (Tuple3D) components.
 *
 * @author   Kurt Motekew
 * @since    20090131
 * @since    20131115 Modified SphereFunc, extended Tuple3D.
 */
public class SphereCart extends Tuple3D implements ISpherical {
  private double r = 0.0;
  private double el = 0.0;
  private double az = 0.0;

  private double cel = 1.0;
  private double sel = 0.0;

  /**
   * Set the radius, elevation, and azimuth on the sphere.
   *
   * @param   radius      New radius
   * @param   elevation   Elevation, radians
   * @param   azimuth     Azimuth, radians
   */
  public void setRElAz(double radius, double elevation, double azimuth) {
    r = radius;
    el = elevation;
    cel = Math.cos(el);
    sel = Math.sin(el);
    az = azimuth;

    update();
  }

  /**
   * Only change the elevation.  Cosine and Sine terms are buffered, allowing
   * for efficient conversions to Cartesian when setting only the Radius and
   * Azimuth.
   *
   * @param   elevation   New elevation value.
   */
  public void setElevation(double elevation) {
    el = elevation;
    cel = Math.cos(el);
    sel = Math.sin(el);

    update();
  }

  /**
   * Only change the azimuth.  If computing over a range of azimuth and
   * elevation values, first set the elevation (outer loop).  Then call
   * this method in the inner loop (for the best efficiency).
   *
   * @param   radius    New radius
   * @param   azimuth   New azimuth value.
   */
  public void setRadiusAzimuth(double radius, double azimuth) {
    r = radius;
    az = azimuth;

    update();
  }

  /*
   * Update this Tuple3D with the internal radius, azimuth, and elevation
   * values.
   */
  private void update() {
    put(Basis3D.I, r*cel*Math.cos(az));
    put(Basis3D.J, r*cel*Math.sin(az));
    put(Basis3D.K, r*sel);
  }

  /**
   * Since this is a sphere, the radius value will be constant, unless changed
   * by the setR() accessor method.
   * 
   * @param   elevation   Elevation.  Any number will return the stored radius.
   * @param   azimuth     Azumuth.    Any number will return the stored radius
   *
   * @return              Radius.
   */
  @Override
  public double getR(double elevation, double azimuth) {
    return r;
  }

}
