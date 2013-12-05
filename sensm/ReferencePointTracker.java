/*
 c  ReferencePointTracker.java
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

import com.motekew.vse.intxm.IGetTime;
import com.motekew.vse.math.*;
import com.motekew.vse.trmtm.*;

/**
 * Creates a model of a system that tracks a point.  A unit pointing
 * vector subject to noise is the measurement.  This is a bare bones
 * sensor model that assumes the sensor reference frame is aligned with
 * the platform frame (no translation or rotation).  Only random
 * measurement error is implemented for measurement generation.
 * Note that while the interface supports the return of multiple
 * vectors, this class will always indicate that only a single point
 * is available.
 *
 * @author   Kurt Motekew
 * @since    20120830
 */
public class ReferencePointTracker implements IPointingObsModeled {

    // Model data providers
  private IGetTime  sysTime = null;
  private IPosAtt   tracker = null;
  private IPosition tracked = null;
    // Measurement noise
  private Random  myRand = null;
  private double sigmaUV = 0.0; 

  /**
   * Initialize this tracker with the parent platform and the point
   * to be tracked.
   *
   * @param   sysT  Provides access to the current time.
   * @param   tkr   The object the tracker is mounted on, sync'ed
   *                with the current time.
   * @param   pt    The point to be tracked.
   */
  public ReferencePointTracker(IGetTime sysT, IPosAtt tkr, IPosition pt) {
    sysTime = sysT;
    tracker = tkr;
    tracked = pt;
  }

  /**
   * Given the number of measurements returns is always deterministic (1),
   * this method does nothing as getDirection() computes the pointing
   * on the fly.
   */
  @Override
  public void measure(double t) {
  }

  /**
   * @return    Indicates a single measurement will be returned
   */
  @Override
  public int getNumMeasurements() {
    return 1;
  }

  /**
   * Compute the unit pointing vector to the object being tracked relative
   * to the computational (inertial) reference frame, based on knowledge
   * and/or modeling of the known reference point.
   *
   * @param             Ignored - only a single vector is available
   * @param    r_t_b    Output position of tracked point relative to the
   *                    body origin in the computational reference frame
   */
  @Override
  public void getReferencePointing(int ith, Tuple3D r_t_b) {
    double time = sysTime.getT();

    Tuple3D r_t_o = new Tuple3D();      // Tracked relative to computational
    Tuple3D r_b_o = new Tuple3D();      // Body relative to computational
 
    tracker.getPosition(time, r_b_o);
    tracked.getPosition(time, r_t_o);
    
    r_t_b.minus(r_t_o, r_b_o);
    r_t_b.unitize();
  }

  /**
   * Computes the unit pointing vector to the object being trackedl
   *
   * @param                Ignored - only a single vector is available
   * @param     uv         Output:  The unit pointing vector.
   */
  @Override
  public void getPointing(int ith, Tuple2D uv) {
    Tuple3D doa = new Tuple3D();
    getDirection(0, doa);
    uv.set(doa);
  }

  /**
   * Computes the unit pointing vector to the object being tracked.
   *
   * @param             Ignored - only a single vector is available
   * @param     uv         Output:  The unit pointing vector.
   */
  @Override
  public void getDirection(int ith, Tuple3D r_t_b_b) {
    double time = sysTime.getT();

    Tuple3D r_t_o_i = new Tuple3D();        // Tracked relative to origin
    Tuple3D r_b_o_i = new Tuple3D();        // Body relative to origin
    Tuple3D r_t_b_i = new Tuple3D();        // Tracked relative to body
                                            // In the computational ref frame
    Quaternion qi2b = new Quaternion();
 
    tracker.getPosition(time, r_b_o_i);
    tracker.getAttitude(time, qi2b);
    tracked.getPosition(time, r_t_o_i);
 
    r_t_b_i.minus(r_t_o_i, r_b_o_i);
    r_t_b_b.fRot(qi2b, r_t_b_i);
    r_t_b_b.unitize();
      // Add random error to output pointing vector components.
      // If these two components now have a magnitude > 1, it
      // is necessary to re-unitize();
    if (sigmaUV > 0.0) {
      r_t_b_b.put(1, r_t_b_b.get(1) + sigmaUV*myRand.nextGaussian());
      r_t_b_b.put(2, r_t_b_b.get(2) + sigmaUV*myRand.nextGaussian());
      if (r_t_b_b.mag() > 1.0) {
        r_t_b_b.unitize();
      }
    }
  }

  /** 
   * Returns the identity Quaternion since the attitude of this
   * sensor is assumed to be aligned with that of the body for
   * this sensor.
   *
   * @param    q_b2s   Output:  Identity quaternion (1 0 0 0)
   */
  @Override
  public void getOrientation(Quaternion q_b2s) {
    q_b2s.identity();
  }

  /**
   * There are currently no constraints on this model.
   *
   * @return    true
   */
  @Override
  public boolean isValid() { return true; }

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
   * @param    biasOut    Output:  zeros as bias modeling is not currently
   *                      supported by this sensor model.
   */
  @Override
  public void getMeasurementBias(Tuple2D biasOut) {
    biasOut.set(0.0, 0.0);
  }

  /**
   * Sets the 1-sigma standard deviation to be used when applying
   * Gaussian noise to the returned pointing angle measurements.
   * Noise is applied individually to 'u' and 'v'
   *
   * @param    sigIn    1-sigma per-axis random measurement uncertainty
   */
  public void setRandomError(double sigIn) {
    if (sigIn > 0.0) {
      sigmaUV = sigIn;
      myRand = new Random();
    }
  }

}
