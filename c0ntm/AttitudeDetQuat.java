/*
 c  AttitudeDetQuat.java
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
 * A "poor man's" constrained solution is implemented by normalizing the
 * the quaternion after each estimate.  In addition, restrictions are placed
 * on how large the correction to prior estimates can be.  Corrections
 * are *added* to previous estimates in the traditional nonlinear manner,
 * which has not real meaning in terms of quaternion math (typically,
 * a rotation correction is solved for).  Another downside, compared
 * to other methods, is four unknowns must be solved for vs. only
 * three.  The upside is no kludges are needed for the quaternion
 * covariance - it naturally comes about as a direct result of the
 * estimation process.
 *
 * @author   Kurt Motekew
 * @since    20120913
 * @since    20131109  modified to extend the Quaternion class
 */
public class AttitudeDetQuat extends Quaternion
                             implements IAttitudeUVecSolver {
  public static final int NY = 2;        // Number of measurements per set    
  public static final int NP = 4;        // Number of solve for parameters

  private int maxitr = 50;
  private double tol = .00005;

  private AttitudeDetTRIAD attInit = new AttitudeDetTRIAD();
  private SysSolverBD ss = new SysSolverBD(NY, NP);
  private Matrix qCov;

  /**
   * Initializes the class - estimateAtt() must be called before
   * the stored covariance is valid.
   */
  public AttitudeDetQuat() {
    qCov = ss.emptyCovariance();
  }

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
    Tuple dp   = new Tuple(NP);           // Update to estimate
    double dpnew;
    double dpold = tol*100.0;

      // Jacobian, A = dO/dq
    DuvDq a = new DuvDq();
    Quaternion qb2s = new Quaternion();   // Body to sensor

    Matrix  w   = new Matrix(NY);         // Weigthing matrix
    Tuple2D r   = new Tuple2D();          // Residual
    Tuple3D xyz = new Tuple3D();          // Modeled pointing vec to reference
    Tuple3D ycb = new Tuple3D();          // Computed pointing vec in body
    Tuple3D ycs = new Tuple3D();          // Computed pointing vec in sensor
    Tuple2D yc  = new Tuple2D();          // Computed Measurement

    Tuple2D sigmaUV = new Tuple2D();
    double sii, sjj, wii, wjj;

      // Use a deterministic method for the first guess, set a vector
      // with quaternion values for estimation operations.  This method
      // typically converges well starting with the identity quaternion
      // as long as there are three input vectors.  A good guess makes it
      // work better with 2 measurements, or when the measurements are
      // extra noisy.
    attInit.solve(sensors);
    set(attInit);
    standardize();
    phat.set(1, this);
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
          ss.accumulate(a, w, r);   
        }
      }
      try {
        ss.solve(dp);
        ss.covariance(qCov);
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
        // Poor man's constrained WLS...
      phat.unitize();
        // Update quaternion for output or next residual computation
      set(phat,1);
      standardize();
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
   */
  public Matrix covariance() {
    return new Matrix(qCov);
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
