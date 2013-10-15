/*
 c  XdX6DOF.java
 c
 c  Copyright (C) 2000, 2008 Kurt Motekew
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

package com.motekew.vse.enums;

/**
 * This enum represents position/orientation and velocity/angular velocity
 * for a 6DOF simulation.  Note that bank, elevation, and heading are
 * typically considered absolute values relative to an earth fixed reference
 * frame, while roll, pitch, and yaw are perturbations in body coordinates.
 *
 * @author Kurt Motekew
 * @since 20080824
 */
public enum XdX6DOF {

  X,       // position
  Y,
  Z,
  DX,      // velocity
  DY,
  DZ,
  PHI,     // bank      (roll)
  THETA,   // elevation (pitch)
  PSI,     // heading   (yaw)
  P,       // roll rate
  Q,       // pitch rate
  R        // yaw rate

}
