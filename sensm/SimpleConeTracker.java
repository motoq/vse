/*
 c  SimpleConeTracker.java
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

package com.motekew.vse.sensm;

import java.util.Random;

import com.motekew.vse.enums.Basis3D;
import com.motekew.vse.math.*;

/**
 * A simple model of a sensor with a cone shaped field of view
 * that provides both the modeled or "known" pointing vector to a
 * reference point within the cone's FOV and the measured pointing
 * vector.
 * <P>
 * Upon being told to take a measurement, this sensor makes use of a
 * uniform random number generator and sensor FOV limits (a cone) to
 * create simulated reference point.  A Gaussian random number
 * generator is then used to perturb the "simulated" measurement. 
 * <P>
 * An example would be to simulate a star tracker when it is not
 * desired to make use of a star map and star recognition algorithms.
 *
 * @author   Kurt Motekew
 * @since    20121002
 */
public class SimpleConeTracker implements IPointingObsModeled {
  private int nMax;
  private int nMeas = 0;
    // Sensor orientation relative to body on which it is mounted
  private Quaternion body2Sensor = new Quaternion();
    // Computed reference vector in computational reference frame
  private Tuple3D[] r_t_b_i;
    // Measured pointing in sensor reference frame
  private Tuple2D[] uv;
    // Measured pointing in body reference frame
  private Tuple3D[] doa;

    // Sensor characteristics
  private Random myRand = new Random();
  private double sigmaUV = 0.0; 
  private double coneWidth = Angles.TPI;
  private boolean valid = true;

  /**
   * @param    n   Maximum number of measurements that can be taken by
   *               this sensor.  A random number generator will
   *               determine if 1->n measurements are to be returned.
   *               See measure() for more details.  If zero, then
   *               no measurements will be returned.
   */
  public SimpleConeTracker(int n) {
    nMax = n;
    r_t_b_i = new Tuple3D[nMax];
    uv = new Tuple2D[nMax];
    doa = new Tuple3D[nMax];
    for (int ii=0; ii<nMax; ii++) {
      r_t_b_i[ii] = new Tuple3D();
      uv[ii] = new Tuple2D();
      doa[ii] = new Tuple3D();
    }
  }

  /**
   * @return    Number of measurements taken (number of available pointing
   *            vectors) and corresponding reference vectors
   */
  @Override
  public int getNumMeasurements() {
    return nMeas;
  }

  /**
   * Sets the full conewidth, defining the sensor's FOV.
   *
   * @param    fullcone   Full conewidth, rad
   */
  public void setConeWidth(double fullcone) {
    coneWidth = fullcone;
  }

  /**
   * Given the current body position and attitude, compute true and
   * simulated measurements based on sensor characteristics (orientation,
   * FOV, measurement noise).
   *
   * @param    rBody    Body position in the computational reference frame
   * @param    i2Body   Body attitude relative to the computational reference
   *                    frame 
   */
  public void measure(Tuple3D rBody, Quaternion i2Body) {
    Quaternion qAtt = new Quaternion();  // Computational to Sensor
    qAtt.mult(i2Body, body2Sensor);
    Tuple3D r_t_b_s = new Tuple3D();     // Target relative to body in Sensor

      // Nothing to do if turned off
    if (nMax < 1) {
      nMeas = 0;
      return;
    }

      // Find the number of measurements taken - add 1 since end is exclusive
      // Ensure at least one measurement is taken (already exited above if
      // nMax = zero (sensor turned off).
    nMeas = myRand.nextInt(nMax+1);
    nMeas = (nMeas > 0) ? nMeas : 1;

    for (int ii=0; ii<nMeas; ii++) {
        // Fake reference point somewhere within conewidth
      double sineHalfCone = Math.sin(myRand.nextDouble()*coneWidth/2.0);
      double clock = myRand.nextDouble()*Angles.TPI;
      double x = Math.cos(clock)*sineHalfCone;
      double y = Math.sin(clock)*sineHalfCone;
      r_t_b_s.put(Basis3D.I, x);
      r_t_b_s.put(Basis3D.J, y);
        // Always positive Z
      r_t_b_s.put(Basis3D.K, Math.sqrt(1 - x*x - y*y));
      r_t_b_i[ii].vRot(qAtt, r_t_b_s);
        // Done with reference vector - unitize just in case...
      r_t_b_i[ii].unitize();

        // Compute simulated measurement from pointing vector in sensor
        // reference frame by adding random error to x & y elements. If
        // these two components now RSS > 1, it is necessary to re-unitize();
      if (sigmaUV > 0.0) {
        r_t_b_s.put(Basis3D.I, r_t_b_s.get(Basis3D.I) +
                               sigmaUV*myRand.nextGaussian());
        r_t_b_s.put(Basis3D.J, r_t_b_s.get(Basis3D.J) +
                               sigmaUV*myRand.nextGaussian());
        if (r_t_b_s.mag() > 1.0) {
          r_t_b_s.unitize();
        }
      }
      doa[ii].vRot(body2Sensor, r_t_b_s);
      uv[ii].set(r_t_b_s);
    }
  } //ESCA

  /**
   * Compute the modeled unit pointing vector to the object being tracked
   * relative to the computational (inertial) reference frame.
   *
   * @param    ith      Indicates which pointing vector to return, 0 based.
   *                    Use getNumMeasurements to find out how many are valid.
   * @param    r_t_b    Output position of tracked point relative to the
   *                    body origin in the computational reference frame
   */
  @Override
  public void getReferencePointing(int ith, Tuple3D r_t_b) {
    r_t_b.set(r_t_b_i[ith]);
  }

  /**
   * Computes the unit pointing vector to the object being tracked
   * in sensor coordinates.
   *
   * @param    ith         Indicates which pointing vector to return, 0 based.
   *                       Use getNumMeasurements to find out how many are valid.
   * @param     obsUV      Output:  The observed unit pointing vector
   *                       relative to the sensor reference frame (not the
   *                       body).
   */
  @Override
  public void getPointing(int ith, Tuple2D obsUV) {
    obsUV.set(uv[ith]);
  }

  /**
   * Computes the unit pointing vector to the object being tracked in
   * body coordinates (as defined by getOrientation).
   *
   * @param    ith         Indicates which pointing vector to return, 0 based.
   *                       Use getNumMeasurements to find out how many are valid.
   * @param     obsUV      Output:  The observed unit pointing vector
   *                       relative to the body reference frame.
   */
  @Override
  public void getDirection(int ith, Tuple3D obsDOA) {
    obsDOA.set(doa[ith]);
  }

  /** 
   * Returns the orientation of the sensor relative to the body
   * on which it is mounted.
   *
   * @param    q_b2s   Output:  body to sensor orientation
   */
  @Override
  public void getOrientation(Quaternion q_b2s) {
    q_b2s.set(body2Sensor);
  }

  /**
   * Sets the orientation of the sensor relative to the body
   * on which it is mounted.
   *
   * @param   q_b2s    New body to sensor orientation
   */
  public void setOrientation(Quaternion q_b2s) {
    body2Sensor.set(q_b2s);
  }

  /** @return    true */
  @Override
  public boolean isValid() {
    return valid;
  }

  /**
   * Gets the 1-sigma standard deviation used when applying Gaussian
   * noise to the computed pointing angle measurements.
   * Noise is applied individually to 'u' and 'v'
   *
   * @param    sigOut    Output 1-sigma per-axis random measurement
   *                     uncertainty.  They are equal for this sensor type.
   */
  @Override
  public void getRandomError(Tuple2D sigOut) {
    sigOut.set(sigmaUV, sigmaUV);
  }

  /**
   * Sets the 1-sigma standard deviation to be used when applying
   * Gaussian noise to the returned pointing angle measurements.
   * Noise is applied independently to 'u' and 'v'
   *
   * @param    sigIn    1-sigma per-axis random measurement uncertainty
   */
  public void setRandomError(double sigIn) {
    if (sigIn > 0.0) {
      sigmaUV = sigIn;
    }
  }

  /**
   * @param    biasOut    Output:  zeros as bias modeling is not currently
   *                      supported by this sensor model.
   */
  @Override
  public void getMeasurementBias(Tuple2D biasOut) {
    biasOut.set(0.0, 0.0);
  }

}
