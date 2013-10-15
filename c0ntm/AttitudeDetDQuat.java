/*
 c  AttitudeDetDQuat.java
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

package com.motekew.vse.c0ntm;

import com.motekew.vse.enums.*;
import com.motekew.vse.math.*;
import com.motekew.vse.sensm.*;

/**
 * This class handles attitude determination given unit pointing vectors
 * to known reference points and pointing vectors measured from sensors.
 * It requires at least two measurements to work.
 * <P>
 * It is based of a method presented in "A Gyro-Free Quaternion-Based
 * Attitude Determination System Suitable for Implementation Using Low
 * Cost Senosrs" by Gebre-Egziabher, D., et al.  Instead of solving for
 * the quaternion that will rotate the observed vector to the known
 * vector, quaternion correction (via quaternion multiplication) is
 * solved for, making use of a small angle approximation where
 * q_correction = [1 qi qj qk].  This allows for only the vector part
 * of a quaternion to be solved for.
 * <P>
 * The method from the paper has been modified by transforming the reference
 * vector to the measurement frame vs. transforming the measurement to the
 * reference vector frame.  Also, the residual used in computing the correction
 * is based on only the x & y components of the measurement/reference vectors
 * as the third component of the measurement is dependent on the first two
 * and shouldn't add any new information (in addition to making it more difficult
 * to factor in error statistics).
 *
 * @author   Kurt Motekew
 * @since    20121011
 */
public class AttitudeDetDQuat {
  public static final int NY = 2;        // Number of measurements per set    
  public static final int NP = 3;        // Number of solve for parameters

  private int maxitr = 50;
  private double tol = .0005;

  private AttitudeDetTRIAD attInit = new AttitudeDetTRIAD();

  /**
   * Estimates attitude of a body given pointing vectors supplied by
   * sensors and the modeled/known pointing vectors in the inertial/
   * computational reference frame.  The pointing vector is in the
   * sensor reference frame.  The partials handle the transformation
   * from sensor to body (the attitude determination is for the body
   * reference frame).
   *
   * @param    sensors    Measurements and known/modeled reference point
   *                      supplier
   * @param    qAtt       Output quaternion representing a frame rotation
   *                      from the sensor to known reference frame.
   *
   * @return              Number of iterations to reach convergence.
   *                      If less than zero, convergence, as determined by
   *                      tol, was not reached.  A negative number is also
   *                      returned if less than two measurements were
   *                      passed in.
   */ 
  public int estimateAtt(IPointingObsModeled[] sensors, Quaternion qAtt) {
    int numSensors = sensors.length;

      // First check to see if enough measurements are present
    int validSensors = 0;
    for (int ii=0; ii<numSensors; ii++) {
      if (sensors[ii].getNumMeasurements() > 0) {
        validSensors++;
      }
    }
    if (validSensors < 2) {
      return -1;
    }
    int nitr;

    Tuple2D uv    = new Tuple2D();        // Sensor pointing vec
    Tuple3D xyz   = new Tuple3D();        // Modeled/known pointing vec in i
    Tuple3D xyz_b = new Tuple3D();        // Modeled/known pointing vec in b
    Tuple3D xyz_s = new Tuple3D();        // Modeled/known pointing vec in s
    Tuple2D uvc   = new Tuple2D();        // Computed measurement

    Tuple3D qe_vec = new Tuple3D();       // Update to attitude estimate (vector)
    Quaternion qe  = new Quaternion();    // Update to attitude, quat
    Quaternion qtmp = new Quaternion();   // Temporary quaternion

      // Jacobian, A = dO/dq
    Matrix a = new Matrix(NY,NP);
    Quaternion qb2s = new Quaternion();   // Body to sensor

    Matrix  w = new Matrix(NY);           // Weigthing matrix
    Tuple2D r = new Tuple2D();            // Residual
    SysSolverBD ss = new SysSolverBD(NY, NP);

    Tuple2D sigmaUV = new Tuple2D();
    double sii, sjj, wii, wjj;

    attInit.estimateAtt(sensors, qAtt);
    int nMeas;
    for (nitr=1; nitr<maxitr; nitr++) {
      ss.reset();
      for (int jj=0; jj<numSensors; jj++) {
        nMeas = sensors[jj].getNumMeasurements();
        if (nMeas < 1) {
          continue;
        }
          // Configure aspects that remain constant for a given sensor.
          // Attitude and weighting matrix
        sensors[jj].getOrientation(qb2s);
        sensors[jj].getRandomError(sigmaUV);
        sii = sigmaUV.get(1);
        sjj = sigmaUV.get(2);
          // Only set if both sigmas are greater than zero
        wii = (sii > 0.0  &&  sjj > 0.0) ? (1.0/(sii*sii)) : 1.0;
        wjj = (sii > 0.0  &&  sjj > 0.0) ? (1.0/(sjj*sjj)) : 1.0;
          // Assume measurement errors are uncorrelated
        w.put(1,1, wii);
        w.put(2,2, wjj);
          // Accumulate measurements for this sensor
        for (int kk=0; kk<nMeas; kk++) {
          // Known/modeled location
          sensors[jj].getReferencePointing(kk, xyz);
          qAtt.frameRot(xyz, xyz_b);
          qb2s.frameRot(xyz_b, xyz_s);
          sensors[jj].getPointing(kk, uv);
            // True vs. computed measurement:  residual
          uvc.set(xyz_s, 1);
          r.minus(uv, uvc);
            // Normal equations
          partials(xyz, a);
          ss.accumulate(a, w, r);
        }
      }
      try {
        ss.solve(qe_vec);
        ss.reset();
      } catch(SingularMatrixException sme) {
        System.out.println("Can't decompose information matrix");
        return -1;
      } 
        // Scalar = 1 from instantiation
      qe.put(Q.QI, qe_vec.get(Basis3D.I));
      qe.put(Q.QJ, qe_vec.get(Basis3D.J));
      qe.put(Q.QK, qe_vec.get(Basis3D.K));
      qtmp.set(qAtt);
      qAtt.mult(qtmp, qe);
      qAtt.normalize();
      if (qe_vec.mag() < tol) {     
        break;
      }
    }

      // Outputs
    if (nitr >= maxitr) {
      nitr = -1;
    }
    return nitr;    
  } // ESCA

    /**
     * @return maximum iterations used for WLS solution
     */
  public int getMaxIterations() { return maxitr; }

    /**
     * @param    newMaxItr     Set maximum number of iterations to be
     *                         used for WLS solution.
     */
  public void setMaxIterations(int newMaxItr) { maxitr = newMaxItr; }

    /**
     * @return tolerance compared to norm of quat estimate update
     */ 
  public double getTol() { return tol; }

    /**
     * @param   newTol    New tolerance to be used to check for convergence
     *                    of estimation process.  When the norm of the
     *                    quaternion update vector falls below this value,
     *                    convergence is triggered.
     */
  public void setTol(double newTol) { tol = newTol; }

  /* 
   * Computes partial relating the error (true reference vector minus
   * computed reference vector) w.r.t. the quaternion that would correct
   * for the error
   */
  private void partials(Tuple3D r, Matrix amat) {
    amat.zero();
    amat.put(1, 2,  2.0*r.get(3));
    amat.put(2, 1, -2.0*r.get(3));
    amat.put(1, 3, -2.0*r.get(2));
    //amat.put(3, 1,  2.0*r.get(2));
    amat.put(2, 3,  2.0*r.get(1));
    //amat.put(3, 2, -2.0*r.get(1));
    amat.mult(-1.0);
  }

}
