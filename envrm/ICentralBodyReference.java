/*
 c  ICentralBodyReference.java
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

package com.motekew.vse.envrm;

import com.motekew.vse.math.Matrix;

/**
 * Defines functionality used to create central body models for which
 * the shape can be modeled as an oblate spheroid (say, the  WGS84
 * ellipoid model) and the gravitational model by a spherical harmonic
 * expansion (say, the EGM96 geoid).
 * 
 * @author   Kurt Motekew
 * @since    20121118
 */
public interface ICentralBodyReference {

  /**
   * Gravitational Parameter for this gravity model, DU^3/TU^2
   */  
  public double gravitationalParameter();

  /**
   * The reference radius used when computing gravitational potential
   * and/or acceleration.
   */
  public double gravitationalReferenceRadius();

  /**
   * Negative of the unnormalized gravitational spherical harmonic
   * coefficient of degree zero and order 2.
   */
  public double j2();

  /**
   * Reference ellipsoid semi-major axis.
   */
  public double ellipsoidSemiMajor();

  /**
   * Reference ellipsoid semi-major axis.
   */
  public double ellipsoidFlattening();

  /**
   * Rotation Rate - definition depends on implementation
   */
  public double angularVelocity();

  /**
   * Sets unnormalized gravitational coefficients (Cosine)
   */
  public void unnormalizedGravityCosCoeff(Matrix mtx);

  /**
   * Sets unnormalized gravitational coefficients (Sine)
   */
  public void unnormalizedGravitySinCoeff(Matrix mtx);

}
