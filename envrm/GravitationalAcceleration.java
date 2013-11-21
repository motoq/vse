/*
 c  GravitationalAcceleration.java
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

import com.motekew.vse.enums.Basis3D;
import com.motekew.vse.math.Tuple3D;
import com.motekew.vse.trmtm.Acceleration;

/**
 * This extension of Tuple3D/Acceleration makes use of the
 * <code>Gravity</code> class to compute gravitational acceleration via
 * the <code>IGravitationalAcceleration</code> interface.  It is initialized
 * with (and points to, not copies) an initialized Gravity object.  Pointing
 * to the object shouldn't be an issue since Gravity should be immutable.
 *
 * @author   Kurt Motekew
 * @since    20131120   Separated from the Gravity class
 */
public class GravitationalAcceleration extends Acceleration
                                       implements IGravitationalAcceleration {
  private final int degreeOrder;
  private Gravity gravityModel;

  /**
   * @param   gvt   Gravity object to point to when computing acceleration.
   */
  public GravitationalAcceleration(Gravity gvt) {
    degreeOrder = gvt.getDegreeOrder();
    gravityModel = gvt;
  }

  /**
   * this.gravt() with the degree and order set to the maximum allowable,
   * as configured during instantation.  Access resulting acceleration via
   * the IGetDDX3D interface
   *
   * @param   r        Distance from the centroid
   * @param   lat      Latitude:  -pi/2 <= ele <= pi/2  (rad)
   * @param   lon      Longitude: -pi   <= az  <= pi    (rad)
   */
  @Override
  public void gravt(double r, double lat, double lon) {
    gravt(degreeOrder, r, lat, lon);
  }

  /**
   * Computes the gravitational acceleration given a position relative to the
   * centroid of the body.  Input position is in spherical (lat, lon, radius),
   * and output is in Cartesian (x, y, z).  Access resulting acceleration via
   * the IGetDDX3D interface.
   *
   * @param   degree   The degree and order of this model.
   * @param   r        Distance from the centroid
   * @param   lat      Latitude:  -pi/2 <= ele <= pi/2  (rad)
   * @param   lon      Longitude: -pi   <= az  <= pi    (rad)
   */
  @Override
  public void gravt(int degree, double r, double lat, double lon) {
    double slat = Math.sin(lat);
    double clat = Math.cos(lat);

    gravityModel.gravt(degree, r, slat, clat, lon,
          r*clat*Math.cos(lon), r*clat*Math.sin(lon), r*slat, this);
  }

  /**
   * this.gravt with the degree and order set to the maximum allowable,
   * as configured during instantation.  Access resulting acceleration via
   * the IGetDDX3D interface.
   *
   * param   pos      Position, body fixed, relative to the centroid.
   */
  @Override
  public void gravt(Tuple3D pos) {
    gravt(degreeOrder, pos);
  }

  /**
   * This version of gravt accepts body relative Cartesian position as an
   * input.  Otherwise, it is the same as gravt(degree, r, lat, lon).
   * Access resulting acceleration via the IGetDDX3D interface.
   *
   * @param   degree   The degree and order of this model.
   * @param   pos      Position, body fixed, relative to the centroid.
   */
  @Override
  public void gravt(int degree, Tuple3D pos) {
    double r, rp2, slat, clat, lon;
    double rx = pos.get(Basis3D.I);
    double ry = pos.get(Basis3D.J);
    double rz = pos.get(Basis3D.K);

    rp2 = rx*rx + ry*ry;
    r = Math.sqrt(rp2 + rz*rz);

    slat = rz/r;
    clat = Math.sqrt(rp2)/r;
    lon = Math.atan2(ry, rx);

    gravityModel.gravt(degree, r, slat, clat, lon, rx, ry, rz, this);
  }
}
