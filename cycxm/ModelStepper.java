/*
 c  ModelStepper.java
 c
 c  Copyright (C) 2000, 2010 Kurt Motekew
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

package com.motekew.vse.cycxm;

import com.motekew.vse.intxm.IStepper;

/**
 * This class manages timing related bookkeeping for a model relying
 * on some form of numeric integration (or propagation by sequential
 * discrete step sizes).  It determines how many integration steps are
 * needed to meet a larger output step size.
 * <P>
 * A smaller output step size than integration step size is not
 * supported.  For such situations, it would be best to generate
 * state as a function of time, and interpolate.  If the output step
 * size is smaller than the integration step size, then the first call
 * to stepper() will result in the integration step size being set to
 * the output step size.
 * <P>
 * This class can be used to update multiple different models, provided
 * they all use the same step sizes (either that, or the step sizes need
 * to be modified between calls to stepper()).
 * <P>
 * Note, units are not assigned to any of the timing variables.  They
 * should be dependent on the model.  The only requirement is that
 * units be consistent within this class.
 *
 * @author  Kurt Motekew
 * @since   20101104
 */
public class ModelStepper {

    // Factor used in determining if another full integration step size
    // can be taken when dt < odt.
  private static final double F = 1.5;

  /*
   * Timing related parameters, see accessor methods for details
   */
  private double dt    = 1.0;   // model integration step size
  private double odt   = 1.0;    // output refresh rate

  /**
   * Set the integration step size.
   * 
   * @param    dtin    The integration step size to be used when propagating
   *                   the model to the next output time.  It is best for
   *                   the integration step size to evenly divide into the
   *                   output step size.  Otherwise, the last integration step
   *                   may be 50% larger or smaller than dtin to ensure the state
   *                   of the model is updated to match the output time.
   */
  public void setDt(double dtin) {
    dt = dtin;
  }

  /**
   * Get the integration step size for the dynamic model
   *
   * @return        Integration step size
   */
  public double getDt() {
    return dt;
  } 

  /**
   * Set the output rate.
   * 
   * @param    odtin    The output rate.  See comments regarding dtin.
   */
  public void setODt(double odtin) {
    odt = odtin;
  }

  /**
   * Get the output refresh rate.
   *
   * @return        refresh rate
   */
  public double getODt() {
    return odt;
  }

  /**
   * Increment state of system by odt in steps of dt.  dt will be set to
   * odt if odt < dt.
   * <P>              
   * NOTE          It is always best if the integration and output step
   *               sizes are multiples of each other.  Otherwise, the final
   *               integration step used to approach odt could be 0.5*dt to
   *               1.5*dt. 
   * <P>               
   * NOTE          Probably best for integration step size and output rate
   *               to be multiples of each other.
   */
  public void stepper(IStepper eqns) {
    boolean done = false;    // Keep stepping
    double dtroom = F*dt;    // Time allowing for 2 steps with last > || < 50% of dt.
    double dt_sofar = 0.0;   // Approaches odt
    double t2go = 0.0;       // Time left to reach odt.
    
    if (odt < dt) {
      dt = odt;
    }

    while (!done) {
      t2go = odt - dt_sofar;
      if (t2go > dtroom) {
        eqns.step(dt);
        dt_sofar += dt;
      } else {
        eqns.step(t2go);
        done = true;
      }
    }  
  }
}
