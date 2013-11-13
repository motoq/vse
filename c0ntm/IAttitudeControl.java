/*
 c  IAttitudeControl.java
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

package com.motekew.vse.c0ntm;

import com.motekew.vse.enums.IGetBasis3D;
import com.motekew.vse.math.Tuple3D;
import com.motekew.vse.math.Matrix3X3;
import com.motekew.vse.math.Quaternion;

/**
 * Defines an interface that computes and returns torques about axes
 * given a current and desired attitude criteria.
 *
 * @author   Kurt Motekew
 * @since    20131112
 */
public interface IAttitudeControl extends IGetBasis3D {

  /** Zero attitude rates method */
  public void set(Tuple3D wvec);

  /** Current and desired attitudes expressed as Quaternions */
  public void set(Quaternion currentAtt, Quaternion desiredAtt,
                                                  Tuple3D wvec);

  /** Current attitude as a quaternion and desired as a matrix */
  public void set(Quaternion currentAtt, Matrix3X3 desiredAtt,
                                                 Tuple3D wvec);

  /** Current and desired attitude expressed as matrices */
  public void set(Matrix3X3 currentAtt, Matrix3X3 desiredAtt,
                                                     Tuple3D wvec);

}
