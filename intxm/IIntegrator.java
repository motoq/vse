/*
 c  IIntegrator.java
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

import com.motekew.vse.math.Tuple;

/**
 *  Interface defining a class that performs numeric integration.
 *
 *  @author  Kurt Motekew
 *  @since   20070406
 *
 */
public interface IIntegrator {

  /**
   *  Implements a method of numeric integration to update the state
   *  of the system of first order differential equations <code>IDiffQ</code>
   *  from x0 at time t0 to x at time t+t0.  The new system time is returned.
   *
   *  @param t0        A double epoch time
   *  @param delta     A double delta over which propagation occurs.
   *  @param x0        A Tuple representing the state of the system
   *                   at t0.
   *  @param dq        A IDiffQ that supplies the derivatives of the
   *                   system.
   *  @param x         A Tuple for the updated value of x.
   *                   This is an Output.
   *
   *  @return          A double for the updated system state time.
   *                   The system designer can use this value, although
   *                   it is more reliable to compute current system time
   *                   by updating the original simulation epoch with i*delta
   *                   where delta is the fixed step size, and i is the
   *                   number of iterations (this can help eliminate truncation
   *                   errors associated with adding many values together.
   *                   Note that this can only be done with a fixed time
   *                   step.  The returned time must be used with a variable
   *                   step integrator.
   */
  public double step(double t0, double delta, Tuple x0, IDiffQ dq,
                                                           Tuple x);

}
