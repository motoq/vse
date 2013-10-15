/*
 c  RK4.java
 c
 c  Copyright (C) 2000, 2007 Kurt Motekew
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

package com.motekew.vse.intxm;

import com.motekew.vse.math.*;

/**
 * <code>RK4</code> implements the Runge-Kutta 4th Order integrator
 * to integrate a system of first order differential equations.
 * Note that the time value of the system is not updated, but is returned
 * by the integrator.
 *
 * @author  Kurt Motekew
 * @since   20070406
 *
 */

public class RK4 implements IIntegrator {

  private int     order = 0;       // Order of diffQs
  private Tuple   xd, x, xa;       // Used for propagation.  Stored
                                   // here just for efficiency so they
                                   // will only be created during object
                                   // initialization.
  /**
   * Initializes the <code>RK4</code> object with a system
   * of first order differential equations.
   *
   * @param se      <code>ISysEqns</code> containing a system of first
   *                order differential equations to be integrated.
   */
  public RK4(int orderIn) {
    order = orderIn;
    xd = new Tuple(order);
    x  = new Tuple(order);
    xa = new Tuple(order);
  }

  /**
   * Implements RK4 to update the state of the state of the system of
   * first order differential equations <code>IDiffQ</code> from xx0
   * at time t0 to xx at time t+ t0.  The new system time is returned.
   *
   *  @param t0        A double epoch time
   *  @param dt        A double increment to propagate xx from t0 to t+dt.
   *  @param xx0       A Tuple representing the state of the system
   *                   at t0.
   *  @param dq        A IDiffQ that supplies the derivatives of the
   *                   system.
   *  @param xx        A Tuple for the updated value of xx0.
   *                   This is an Output.
   *
   *  @return          A double for the updated system state time.  Note
   *                   that the system designer can avoid truncation
   *                   errors resulting from the addition of many numbers
   *                   by instead updating the system time with the
   *                   number of steps taken since the simulation beginning
   *                   times the step size:  t = t_epoch_simulation + i*delta
   */
  @Override
  public double step(double t0, double dt, Tuple xx0, IDiffQ diffQs,
                                                         Tuple xx)  {
    double tt, t;
    double q;
    double val;
    int ii;

      /* first */
    tt = t0;
    diffQs.getXDot(tt, xx0, xd);
    for (ii=1; ii<=order; ii++) {
      val = xd.get(ii)*dt;
      xa.put(ii, val);
      val = xx0.get(ii) + 0.5*xa.get(ii);
      x.put(ii, val);
    }

      /* second */
    t = tt + 0.5*dt;
    diffQs.getXDot(t, x, xd);
    for (ii=1; ii<=order; ii++) {
      q   = xd.get(ii)*dt;
      val = xx0.get(ii) + 0.5*q;
      x.put(ii, val);
      val = xa.get(ii) + q + q;
      xa.put(ii, val);
    }

      /* third */
    diffQs.getXDot(t, x, xd);
    for (ii=1; ii<=order; ii++) {
      q      = xd.get(ii)*dt;
      val    = xx0.get(ii) + q;
      x.put(ii, val);
      val    = xa.get(ii) + q + q;
      xa.put(ii, val);
    }

      /* forth */
    tt += dt;
    diffQs.getXDot(tt, x, xd);
    for (ii=1; ii<=order; ii++) {
      val = xx0.get(ii) + (xa.get(ii) + xd.get(ii)*dt)/6.0;
      xx.put(ii, val);
    }
    return(tt);
  } 
}
