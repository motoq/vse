/*
 c  AttitudeDetQuatC.java
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

package com.motekew.vse.c0ntm;

import com.motekew.vse.enums.Q;
import com.motekew.vse.math.*;
import com.motekew.vse.sensm.*;

/**
 * This Quaternion estimates attitude given unit pointing vectors
 * to known reference points and pointing vectors measured from sensors.
 * It requires at least two measurements to work.
 * <P>
 * A very simple WLS solution is implemented.  The measurement errors
 * between each set are considered independent from each other.  Similarly,
 * the measurement errors in the pointing vector information is also
 * independent.  This results in a diagonal weighting matrix and allows
 * for measurement accumulation (no matter now many measurements are
 * supplied, the largest matrix to "invert" is a 2x2 - no inversion
 * actually takes place as Cholesky decomposition is used).
 * <P>
 * This class differs from AttitudeDetQuat in that only the complex
 * components of the quaternion are solved for, using the quaternion
 * norm condition to resolve the scalar term.
 *
 * @author   Kurt Motekew
 * @since    20130921
 * @since    20131109  modified to extend the Quaternion class
 */
public class AttitudeDetQuatC extends Quaternion
                              implements IAttitudeUVecSolver {
  public static final int NY = 2;        // Number of measurements per set    
  public static final int NP = 3;        // Number of solve for parameters

  private int maxitr = 50;
  private double tol = .000005;          // Note tolerance can be smaller than
                                         // AttitudeDetQuat

  private AttitudeDetTRIAD attInit = new AttitudeDetTRIAD();
  private SysSolverBD dp = new SysSolverBD(NY, NP);
  private Covariance qCov = new Covariance(NP);

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
   *
   * @return              Number of iterations to reach convergence.
   *                      If less than zero, convergence, as determined by
   *                      tol, was not reached.  A negative number is also
   *                      returned if less than two measurements were
   *                      passed in.
   */ 
  @Override
  public int solve(IPointingObsModeled[] sensors) {
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

    Tuple2D uv   = new Tuple2D();         // Sensor pointing vec

    Tuple phat = new Tuple(NP);           // Vector of solve for params
    double dpnew;
    double dpold = tol*100.0;

      // Jacobian, A = dO/dq
    DuvDqC a = new DuvDqC();
    Quaternion qb2s = new Quaternion();   // Body to sensor

    Matrix  w   = new Matrix(NY);         // Weigthing matrix
    Tuple2D r   = new Tuple2D();          // Residual
    Tuple3D xyz = new Tuple3D();          // Modeled pointing vec to reference
    Tuple3D ycb = new Tuple3D();          // Computed pointing vec in body
    Tuple3D ycs = new Tuple3D();          // Computed pointing vec in sensor
    Tuple2D yc  = new Tuple2D();          // Computed Measurement

    Tuple2D sigmaUV = new Tuple2D();
    double sii, sjj, wii, wjj;
    double qs, qi, qj, qk;

      // Use a deterministic method for the first guess
    attInit.solve(sensors);
    set(attInit);
    standardize();
    phat.put(1, get(Q.QI));
    phat.put(2, get(Q.QJ));
    phat.put(3, get(Q.QK));
    int nMeas;
    for (nitr=1; nitr<maxitr; nitr++) {
      dp.reset();
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
            // Actual Measurement
          sensors[jj].getPointing(kk, uv);
            // Computed measurement, y
          ycb.fRot(this, xyz);
          ycs.fRot(qb2s, ycb);
          yc.set(ycs, 1);
            // Residual 
          r.minus(uv, yc);
            // Normal equations
          a.partials(xyz, qb2s, this);
          dp.accumulate(a, w, r);   
        }
      }
      try {
          // Solve for complex components of quaternion
          // and associated covariance.
        dp.solve();
        qCov.set(dp);
      } catch(SingularMatrixException sme) {
        System.out.println("Can't decompose information matrix");
        return -1;
      }
        // Don't let the update get too big, but scale to
        // maintain direction.
      while ((dpnew = dp.mag()) > dpold) {
        dp.mult(0.5);
      }
      phat.plus(dp);
        // Update full quaternion for output or next residual computation
      qi = phat.get(1);
      qj = phat.get(2);
      qk = phat.get(3);
      qs = Math.sqrt(1.0 - qi*qi - qj*qj - qk*qk);
      set(qs, qi, qj, qk);
      if (dpnew < tol  &&  dpnew <= dpold) {     
        break;
      }
      dpold = dpnew;
    }

      // Outputs
    if (nitr >= maxitr) {
      nitr = -1;
    }
    return nitr;    
  }

  /**
   * @return   A copy of the most recently generated quaternion
   *           covariance.  The scalar is the first element.
   *           Only returning diagonal elements for now.  The scalar
   *           variance is the sum of the complex component covariances.
   */
  public Matrix covariance() {
    Matrix fullQCov = new Matrix(4);
    fullQCov.put(1, 1, qCov.get(1, 1) + qCov.get(2, 2) + qCov.get(3, 3));
    fullQCov.put(2, 2, qCov.get(1, 1));
    fullQCov.put(3, 3, qCov.get(2, 2));
    fullQCov.put(4, 4, qCov.get(3, 3));
    
    return fullQCov;
  }

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

}
