/*
 c  AttitudeDetTRIAD.java
 c
 c  Copyright (C) 2012, 2013 Kurt Motekew
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

package com.motekew.vse.c0ntm;

import com.motekew.vse.math.*;
import com.motekew.vse.sensm.IPointingObsModeled;

/**
 * This Quaternion makes use of the TRIAD algorithm to determine the
 * transformation from a known reference frame to another given
 * two sets of unit pointing vectors in the known frame and two
 * sets in the frame to be determined.  See AIAA 81-4003, "Three-Axis
 * Attitude Determination from Vector Observations", M.D. Shuster and
 * S.D. Oh.
 *
 * @author   Kurt Motekew
 * @since    20120924
 * @since    20131109  modified to extend the Quaternion class
 */
public class AttitudeDetTRIAD extends Quaternion
                              implements IAttitudeUVecSolver {

  /**
   * Determine the Quaternion transforming known pointing vectors in reference
   * frame "A" to pointing vectors provided by sensors in reference frame "B".
   * The two sensors with the smallest measurement uncertainty error
   * budgets are used (or the first two, if none of the others are smaller).
   * No consideration is given to the geometry of the sensors.
   *
   * @param    sensors    Measurements and known/modeled reference point
   *                      supplier
   *
   * @return              Zero
   */
  @Override
  public int solve(IPointingObsModeled[] sensors) {
    Matrix3X3 mat = new Matrix3X3();
    int nitr =  estimateAtt(sensors, mat);
    set(mat);
    return nitr;
  }

  /*
   * Determine the DCM transforming known pointing vectors in reference
   * frame "A" to pointing vectors provided by sensors in reference frame "B".
   *
   * @param    sensors    Measurements and known/modeled reference point
   *                      supplier.  At least two are needed.
   * @param    amat       Output DCM representing a frame rotation
   *                      from the sensor to known reference frame.
   *
   * @return              Zero
   */
  private int estimateAtt(IPointingObsModeled[] sensors, Matrix3X3 amat) {
    int numSensors = sensors.length;

    if (numSensors < 2) {
      return -1;
    }
    int validSensors = 0;
    for (int ii=0; ii<numSensors; ii++) {
      if (sensors[ii].getNumMeasurements() > 0) {
        validSensors++;
      }
    }
    if (validSensors < 2) {
      return -1;
    }

      // Find 2 valid sensors with smallest measurement error.
      // Keep it simple with two passes for now instead of
      // one complicated pass.
    int itkr1 = 0;
    while (sensors[itkr1].getNumMeasurements() < 1) {
      itkr1++;
    }
    Tuple2D sigmaUV = new Tuple2D();
    sensors[itkr1].getRandomError(sigmaUV);
    double minSigUV = sigmaUV.dot(sigmaUV);
    double sigUV;
    int start = itkr1 + 1;
    for (int ii=start; ii<numSensors; ii++) {
      if (sensors[ii].getNumMeasurements() > 0) {
        sensors[ii].getRandomError(sigmaUV);
        sigUV = sigmaUV.dot(sigmaUV);
        if (sigUV < minSigUV) {
          itkr1 = ii;
          minSigUV = sigUV;
        }
      }
    }
    int itkr2 = (itkr1 == (start-1)) ? start : start-1;
    while (sensors[itkr2].getNumMeasurements() < 1  ||  itkr2 == itkr1) {
      itkr2++;
    }
    start = itkr2 + 1;
    sensors[itkr2].getRandomError(sigmaUV);
    minSigUV = sigmaUV.dot(sigmaUV);
    for (int ii=start; ii<numSensors; ii++) {
      if (ii != itkr1  &&  sensors[ii].getNumMeasurements() > 0) {
        sensors[ii].getRandomError(sigmaUV);
        sigUV = sigmaUV.dot(sigmaUV);
        if (sigUV < minSigUV) {
          itkr2 = ii;
          minSigUV = sigUV;
        }
      }
    }

      // Reference vectors
    Tuple3D v1 = new Tuple3D();
    Tuple3D v2 = new Tuple3D();
    sensors[itkr1].getReferencePointing(0, v1);
    sensors[itkr2].getReferencePointing(0, v2);
      // Form reference basis
    Tuple3D r1 = new Tuple3D();
    Tuple3D r2 = new Tuple3D();
    Tuple3D r3 = new Tuple3D();
    double mag;
    Matrix3X3 mRef = new Matrix3X3();
    r1.set(v1);
    r2.cross(v1, v2);
    r3.cross(v1, r2);
    mag = r2.mag();
    r2.div(mag);
    r3.div(mag);
    mRef.setRows(r1, r2, r3);
      // Observed vectors
    sensors[itkr1].getDirection(0, v1);
    sensors[itkr2].getDirection(0, v2);
      // Form observation basis
    Tuple3D s1 = new Tuple3D();
    Tuple3D s2 = new Tuple3D();
    Tuple3D s3 = new Tuple3D();
    Matrix3X3 mObs = new Matrix3X3();
    s1.set(v1);
    s2.cross(v1,v2);
    s3.cross(v1, s2);
    mag = s2.mag();
    s2.div(mag);
    s3.div(mag);
    mObs.setColumns(s1, s2, s3);

    amat.mult(mObs, mRef);

    return 0;    
  }

}
