/*
 c  Velocity.java
 c
 c  Copyright (C) 2013 Kurt Motekew
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

import com.motekew.vse.enums.IGetDX3D;
import com.motekew.vse.enums.DX3D;
import com.motekew.vse.enums.Basis3D;
import com.motekew.vse.math.Tuple3D;

/**
 * Specialization of the Tuple3D class, to be recognized as a vector
 * with components of velocity.
 *
 * @author Kurt Motekew
 * @since  20131116
 */
public class Velocity extends Tuple3D {

  /**
   * Gets the value of the Velocity given a <code>DX3D</code>
   * enum index.
   *
   * @param  vel   An object with velocity elements to copy
   */
  public void set(IGetDX3D vel) {
    put(Basis3D.I, vel.get(DX3D.DX));
    put(Basis3D.J, vel.get(DX3D.DY));
    put(Basis3D.K, vel.get(DX3D.DZ));
  }

  /**
   * @return   The requested velocity component.
   */
  public double get(DX3D ndx) {
    return get(ndx.ordinal() + 1);
  }
}
