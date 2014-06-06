/*
 c  WGS84EGM96Ref.java
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

package com.motekew.vse.envrm;

import com.motekew.vse.math.Matrix;

/**
 * Earth ellipsoid and geoid defining parameters.  WGS 84 parameters
 * define the shape of the reference ellipsoid while EGM96 values
 * define the geoid.  Units are in meters, seconds, and radians, or
 * ER, minutes, and radians, depending on get/setUnits.
 *
 * @author   Kurt Motekew
 * @since    20121118
 */
public class WGS84EGM96Ref implements ICentralBodyReference {
  
    // Degree/Order of available gravitational coefficients
  private static final int DEGORDER = 4;
  private Matrix cl;
  private Matrix sl;

  private static final double SEC_PER_MIN = 60.0;
    // Meters and seconds
  private static final double GM_MSEC   = 3986004.415E8;
  private static final double A_M       = 6378136.3;
  private static final double ER_M      = 6378137.0;
  private static final double OMEGA_SEC = 7.292115E-5;
    // ER and minutes
  private static final double GM_ERMN   = (GM_MSEC/(ER_M*ER_M*ER_M))*60.0*60.0;
  private static final double A_ER      = A_M/ER_M;
  private static final double ER_ER     = 1.0;
  private static final double OMEGA_MN  = OMEGA_SEC*SEC_PER_MIN;

  /**
   * Initializes gravitational coefficients
   */
  public WGS84EGM96Ref() {
      // Size of matrix is 1 greater than degree/order
    cl = new Matrix(DEGORDER+1, DEGORDER+1);
    sl = new Matrix(DEGORDER+1, DEGORDER+1);

      // Set J2: l = 2, m = 0
    cl.put(2+1, 0+1, -1.08262668355e-3);     // deg = 2, order = 0, -J2
    cl.put(2+1, 1+1, -2.41400000000e-10);    // deg = 2, order = 1
    cl.put(2+1, 2+1,  1.57446037456e-6);     // deg = 2, order = 2
      //
    cl.put(3+1, 0+1,  2.53265648533e-6);     // deg = 3, order = 0, -J3
    cl.put(3+1, 1+1,  2.19263852917e-6);     // deg = 3, order = 1
    cl.put(3+1, 2+1,  3.08989206881e-7);     // deg = 3, order = 2
    cl.put(3+1, 3+1,  1.00548778064e-7);     // deg = 3, order = 3
      //
    cl.put(4+1, 0+1,  1.61962159137e-6);     // deg = 4, order = 0, -J4
    cl.put(4+1, 1+1, -5.08799360404e-7);     // deg = 4, order = 1
    cl.put(4+1, 2+1,  7.84175859844e-8);     // deg = 4, order = 2
    cl.put(4+1, 3+1,  5.92099402629e-8);     // deg = 4, order = 3
    cl.put(4+1, 4+1, -3.98407411766e-9);     // deg = 4, order = 4
      //
    // Set J2: l = 2, m = 0
    sl.put(2+1, 1+1,  1.54310000000e-9);     // deg = 2, order = 1
    sl.put(2+1, 2+1, -9.03803806639e-7);     // deg = 2, order = 2
      //
    sl.put(3+1, 1+1,  2.68424890397e-7);     // deg = 3, order = 1
    sl.put(3+1, 2+1, -2.11437612437e-7);     // deg = 3, order = 2
    sl.put(3+1, 3+1,  1.97222559006e-7);     // deg = 3, order = 3
      //
    sl.put(4+1, 1+1, -4.49144872839e-7);     // deg = 4, order = 1
    sl.put(4+1, 2+1,  1.48177868296e-7);     // deg = 4, order = 2
    sl.put(4+1, 3+1, -1.20077667634e-8);     // deg = 4, order = 3
    sl.put(4+1, 4+1,  6.52571425370e-9);     // deg = 4, order = 4
  }

  /**
   * @return   Meters per internal distance units (ER)
   */
  @Override
  public double metersPerDU() { return 6378137; }

  /**
   * @return   Seconds per internal time units (minutes)
   */
  @Override
  public double secondsPerTU() { return SEC_PER_MIN; }

  /**
   * @return EGM96 Gravitational Parameter, ER^3/min^2
   */
  @Override
  public double gravitationalParameter() {
    return GM_ERMN;
  }

  /**
   * This is the "Earth Radius" value that should be used when
   * computing gravitational potential or acceleration.
   *
   * @return EGM96 gravitational scaling factor, ER
   */
  @Override
  public double gravitationalReferenceRadius() {
    return A_ER;
  }

  /**
   * Negative of the unnormalized gravitational spherical harmonic
   * coefficient of degree zero and order 2.
   */
  @Override
  public double j2() {
    return 1.08262668355e-3;
  }

  /**
   * This is the Earth Radius to be used when converting between latitude,
   * longitude, altitude and ITRF Cartesian coordinates.  It is also used to
   * convert values to units of ER.
   *
   * @return WGS 84 defining parameter, ellipsoid semi-major axis, ER
   */
  @Override
  public double ellipsoidSemiMajor() {
      return ER_ER;
  }

  /**
   * @return WGS 84 defining parameter, ellipsoid flattening
   */
  @Override
  public double ellipsoidFlattening() {
    return 1.0/298.257223563;
  }

  /**
   * @return WGS 84 defining parameter, Earth's angular velocity, rad/sec
   *         or rad/min
   */
  @Override
  public double angularVelocity() {
    return OMEGA_MN;
  }

  /**
   * @return   Maximum degree of gravitational model
   */
  @Override
  public int getDegree() { return DEGORDER; }

  /**
   * @return   Maximum order of gravitational model
   */
  @Override
  public int getOrder() { return DEGORDER; }

  /**
   * Creates a matrix of unnormalized gravitational cosine coefficients.  If
   * the requested number of coefficients is greater than the available number,
   * then the returned matrix will still be of the requested size.  However,
   * the remaining elements will be zero.
   *
   * @param   m   Requested degree of gravity model
   * @param   n   Requested order of gravity model
   *
   * @return      [m+1]X[n+1] matrix filled with available coefficients
   */
  @Override
  public Matrix unnormalizedGravityCosCoeff(int m, int n) {
    int ii, jj;
    m++;
    n++;
      // Allocate
    Matrix mtx = new Matrix(m, n);
      // Determine fill limits
    if (m > cl.numRows()) {
      m = cl.numRows();
    }
    if (n > cl.numCols()) {
      n = cl.numCols();
    }
      // fill
    for (ii=1; ii<=m; ii++) {
      for (jj=1; jj<=n; jj++) {
        mtx.put(ii, jj, cl.get(ii, jj));
      }
    }
    return mtx;
  }

  /**
   * Creates a matrix of unnormalized gravitational sine coefficients.  If
   * the requested number of coefficients is greater than the available number,
   * then the returned matrix will still be of the requested size.  However,
   * the remaining elements will be zero.
   *
   * @param   m   Requested degree of gravity model
   * @param   n   Requested order of gravity model
   *
   * @return      [m+1]X[n+1] matrix filled with available coefficients
   */
  @Override
  public Matrix unnormalizedGravitySinCoeff(int m, int n) {
    int ii, jj;
    m++;
    n++;
    Matrix mtx = new Matrix(m, n);

    if (m > sl.numRows()) {
      m = sl.numRows();
    }
    if (n > sl.numCols()) {
      n = sl.numCols();
    }
    for (ii=1; ii<=m; ii++) {
      for (jj=1; jj<=n; jj++) {
        mtx.put(ii, jj, sl.get(ii, jj));
      }
    }
    return mtx;
  }
}
