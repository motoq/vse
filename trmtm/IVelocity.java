/*
 c  IVelocity.java
 c
 c  Copyright (C) 2000, 2012 Kurt Motekew
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

package com.motekew.vse.trmtm;

import com.motekew.vse.math.Tuple3D;

/**
 * Defines an interface for accessing the velocity of an object in
 * 3 dimensional space.
 * 
 * @author   Kurt Motekew
 * @since    20110221
 * @since    20121128   Consolidated time and non-time dependent versions
 */
public interface IVelocity {

  /**
   * @param   t     Input:  The time for which the velocity should
   *                        be retrieved.
   *                       
   * @param   vel   Output:  The velocity is copied into this variable.
   *
   * @return        Actual time associated with output velocity
   */
  public double getVelocity(double t, Tuple3D vel);

}
