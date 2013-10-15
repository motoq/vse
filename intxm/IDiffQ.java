/*
 c  IDiffQ.java
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
 *  Interface used to define functions representing a system of
 *  1st order differential equations.  It is intended for use inside
 *  a class that implements the <code>ISysEqns</code> (or similar)
 *  interface.  The idea is to use the class that implements this
 *  interface with a <code>IIntegrator</code>.
 *
 *  @author  Kurt Motekew
 *  @since   20070411
 *
 */
public interface IDiffQ {

  /**
   * Returns the number of differential equations represented by
   * the this object.  This should be a system of 1st order differential
   * equations.
   *
   * @return       An int indicating the number of 1st order diff Qs.
   */
  public int getOrder();

  /**
   * This method computes the derivative values based on the model
   * of the system of equations.  This is where the real work is done.
   * 
   * @param t     A double time for which the functions (derivatives)
   *              are to be evaluated.
   * @param x     A Tuple state vector at time t.
   * @param xd    A Tuple state vector to insert the computed derivative 
   *              values.  This is an Output.
   */
  public void getXDot(double t, Tuple x, Tuple xd);

}
