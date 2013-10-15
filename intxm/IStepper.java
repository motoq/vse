/*
 c  IStepper.java
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

package com.motekew.vse.intxm;

/**
 *  Interface used to define a class that will represent some state
 *  at a given time, and allow the state to be updated to a new time
 *  defined by a time increment (step size).
 *
 *  @author  Kurt Motekew
 *  @since   20101116
 *
 */
public interface IStepper {
  
  /**
   * Propagates the system by the internal step size.  This default step
   * size should always be valid.  It is up to the designer to decide if
   * it should be the optimum step size (based on some criteria), the
   * maximum stepsize allowable to maintain accuracy, etc....
   */
  public void step();

  /**
   * Propagates the system by the step size delta.  It is up to the
   * system designer to decide if this will be used as the integration
   * method's step size, or if some optimal step size will be used for
   * the integrator, and then interpolation will be used to arrive at a
   * state at this input step size.
   *
   * @param delta    A double representing the step size to propagate
   *                 by.
   */
  public void step(double delta);

}
