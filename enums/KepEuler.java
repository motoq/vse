/*
 c  KepEuler.java
 c
 c  Copyright (C) 2000, 2011 Kurt Motekew
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
 * This enum represents the classical orbital elements for
 * orbital position/velocity, along with Euler angles for
 * attitude.
 * 
 * @author Kurt Motekew
 * @since 20110507
 */
public enum KepEuler {

  A,    // Semi-major axis
  E,    // Eccentricity
  I,    // Inclination
  O,    // Right Ascension of the Ascending Node
  W,    // Argument of Perigee
  V,    // True Anomaly
  BANK,
  ELEV,
  HEAD
}
