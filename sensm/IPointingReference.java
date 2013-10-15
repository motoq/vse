/*
 c  IPointingReference.java
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

import com.motekew.vse.math.Tuple3D;

/**
 * Defines an interface for a the return of unit pointing vectors
 * towards known reference points.  See the specific implementation
 * for a definition of the reference frame and point of origin.
 *
 * @author   Kurt Motekew
 * @since    20120913
 */
public interface IPointingReference {

  /** Get the ith reference point - three components */
  public void getReferencePointing(int ith, Tuple3D xyz);

}
