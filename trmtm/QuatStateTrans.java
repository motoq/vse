/*
 c  QuatStateTrans.java
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

import com.motekew.vse.enums.Basis3D;
import com.motekew.vse.math.Matrix;
import com.motekew.vse.math.Tuple3D;

/**
 * This is the quaternion state transition matrix derived by
 * integrating the strapdown equation, a matrix differential equation:
 *
 *   q_2 = Phi * q_1
 *
 * where this matrix is Phi.  See the set() method for notes regarding
 * the solution method.
 *   q_dot = 0.5*W*q
 *   A = 0.5*W*t ; t = t2 - t1
 *   q_2 = exp(A)*q_k = sum_0^\infty(A^!/i!)
 *
 * @author  Kurt Motekew
 * @since   20131010
 */
public class QuatStateTrans extends Matrix {
  private boolean quick = false;

  /**
   * Default behavior is to not modify the original state - initialize
   * to the (4x4) identity matrix.
   */
  public QuatStateTrans() {
    super(4);
    this.identity();
  }

  /**
   * The default method for evaluating the matrix differential
   * equation is to sum the first 6 terms (I + ... +A^5/5!),
   * and involves creating two additional 4x4 matrices each time
   * set() is called.  When enabled, only the first three terms
   * are used (I + A + A^2/2!) - less computations are required
   * and matrices are not instantiated at each call.
   *
   * @param   useQuick   If true, enables faster yet less accurate
   *                     3 term evaluation of matrix differential
   *                     equation.  Default is set to false.
   */
  public void useQuickEvaluation(boolean useQuick) {
    quick = useQuick;
  }

  /**
   * Sets this matrix to the (linear) state transition matrix given
   * a duration and angular velocity vector, which is assumed to
   * be fixed in time.  This is a matrix that should be multiplied
   * by a quaternion, treated as a 4 element vector, where the 1st
   * element is the scalar.  The new quaternion should be normalized
   * for best accuracy.  See <code>Quaternion.stateTransition()</code>
   * for a better way to propagate.  This is intended for error 
   * propagation.  The series solution is carried out up to A^5/5! by
   * default.  If
   *
   * @param   dt      Propagation time
   * @param   omega   Angular velocity vector, units consistent with dt
   */
  public void set(double dt, Tuple3D omega) {
    double aI = 0.5*omega.get(Basis3D.I)*dt;
    double aJ = 0.5*omega.get(Basis3D.J)*dt;
    double aK = 0.5*omega.get(Basis3D.K)*dt;

      // Which method
    if (quick) {
        // A^0 / 0!
      this.identity();

        // + A^1 / 1!, only affects off diagonal
      this.put(1,2, -aI);
      this.put(1,3, -aJ);
      this.put(1,4, -aK);
      this.put(2,1,  aI);
      this.put(3,1,  aJ);
      this.put(4,1,  aK);
      this.put(2,3,  aK);
      this.put(3,2, -aK);
      this.put(2,4, -aJ);
      this.put(4,2,  aJ);
      this.put(3,4,  aI);
      this.put(4,3, -aI);

        // + A^2 / 2!
      double aSquared = -0.5*(aI*aI + aJ*aJ + aK*aK);
      this.put(1,1, this.get(1,1) + aSquared);
      this.put(2,2, this.get(2,2) + aSquared);
      this.put(3,3, this.get(3,3) + aSquared);
      this.put(4,4, this.get(4,4) + aSquared);
    } else {
      Matrix idt = new Matrix(4);
      Matrix aMat = new Matrix(4);

        // A^0 / 0!
      this.identity();

        // + A^1 / 1!, only affects off diagonal
      aMat.put(1,2, -aI);
      aMat.put(1,3, -aJ);
      aMat.put(1,4, -aK);
      aMat.put(2,1,  aI);
      aMat.put(3,1,  aJ);
      aMat.put(4,1,  aK);
      aMat.put(2,3,  aK);
      aMat.put(3,2, -aK);
      aMat.put(2,4, -aJ);
      aMat.put(4,2,  aJ);
      aMat.put(3,4,  aI);
      aMat.put(4,3, -aI);
      this.plus(aMat);

        // + A^2 / 2!
      double a2 = -(aI*aI + aJ*aJ + aK*aK);
      idt.identity();
      idt.mult(0.5*a2);
      this.plus(idt);

        // + A^3/3!
      aMat.mult(a2/6.0);
      this.plus(aMat);

        // + A^4/4!
      idt.mult(a2/12.0);
      this.plus(idt);

        // + A^5/5!
      aMat.mult(a2/20.0);
      this.plus(aMat);
    }
  }

  /**
   * The series solution is carried out up to A^5/5!
   *
   * @param   dt      Propagation time
   * @param   omega   Angular velocity vector, units consistent with dt
   */ 
  public void set2(double dt, Tuple3D omega) {

  }
}
