/*
 c  Quaternion.java
 c
 c  Copyright (C) 2000, 2013 Kurt Motekew
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

package com.motekew.vse.math;

import com.motekew.vse.enums.*;

/**
 * This class represents a Quaternion
 * <P>
 * See "Quaternions and Rotation Sequences" by Jack B. Kuipers for
 * more information.
 *
 * @author Kurt Motekew
 * @since  20080830
 */
public class Quaternion implements IGetQ {
  private static final Tuple3D IHAT = new Tuple3D(1.0, 0.0, 0.0);
  private static final Tuple3D JHAT = new Tuple3D(0.0, 1.0, 0.0);
  private static final Tuple3D KHAT = new Tuple3D(0.0, 0.0, 1.0);  

    // tolerance value used for numeric considerations
  private static final double TOL = 0.000001;

    /** 
     * DCM to quaternion alg selection factor.  0.25 requires
     * the first solved for quaternion element to be at least
     * 1/4 in magnitude or greater.  1.0 would result in the
     * first needing to be 1/2 or larger (see notes in code).
     */
  public static final double KAPPA = 0.25;

    // used for Tuple3D indexing
  private static final Basis3D I = Basis3D.I;
  private static final Basis3D J = Basis3D.J;
  private static final Basis3D K = Basis3D.K;

    // and for external Quaternion indexing
  private static final Q Q0 = Q.Q0;
  private static final Q QI = Q.QI;
  private static final Q QJ = Q.QJ;
  private static final Q QK = Q.QK;

    // quaternion components - set to unit quaternion with
    // zero rotation angle
  private double q0 = 1;
  private double qi = 0;
  private double qj = 0;
  private double qk = 0;

  /**
   * Default contstructor
   */
  public Quaternion() {
  }

  /**
   * Constructor to initialize with scalars
   * 
   * @param   scalar    Scalar component
   * @param   v1        First vector component
   * @param   v2        Second vector component
   * @param   v3        Third vector component
   */
  public Quaternion(double scalar, double v1, double v2, double v3) {
    q0 = scalar;
    qi = v1;
    qj = v2;
    qk = v3;
  }

  /**
   * @param   q2copy   Instantiate with a Quaternion to copy.
   */
  public Quaternion(Quaternion q2copy) {
    set(q2copy);
  }

  /**
   * Sets the component values of this quaternion.
   *
   * @param  ndx   A <code>Q<code> indicating which component to
   *               modify.
   * @param  val   The double value to be inserted.
   */
  public void put(Q ndx, double val) {
    switch(ndx) {
      case Q0:
        q0 = val;
        break;
      case QI:
        qi = val;
        break;
      case QJ:
        qj = val;
        break;
      case QK:
        qk = val;
        break;
    }
  }

  /**
   * Gets the component values of this quaternion.
   *
   * @param  ndx   A <code>Q<code> indicating which component to
   *               retrieve.
   *
   * @return      The double value representing the requested component
   */
  @Override
  public double get(Q ndx) {
    switch(ndx) {
      case Q0:
        return(q0);
      case QI:
        return(qi);
      case QJ:
        return(qj);
      case QK:
        return(qk);
      default:
        return(0.0);
    }
  }

  /**
   * Set individual components of this quaternion all at once with
   * scalars.
   *
   * @param  s     Scalar value of quaternion
   * @param  i     X component of vector portion
   * @param  j     Y component of vector portion
   * @param  k     Z component of vector portion
   */
  public void set(double s, double i, double j, double k) {
    q0 = s;
    qi = i;
    qj = j;
    qk = k;
  }

  /**
   * Copy components one for one from the object implementing the IGetQ
   * interface into this Quaternion.
   *
   * @param  igq    An object implementing the IGetQ interface.
   */
  public void set(IGetQ igq) {
    q0 = igq.get(Q0);
    qi = igq.get(QI);
    qj = igq.get(QJ);
    qk = igq.get(QK);
  }

  /**
   * Sets the quaternion given a <bold>unit</bold> pointing vector and angle
   * of rotation.
   * 
   * @param    alpha    Angle, in radians
   * @param    axis     Unit vector aligned with axis of rotation.
   */
  public void set(double alpha, Tuple3D t3d) {
    alpha /= 2.0;
    q0 = Math.cos(alpha);
    alpha = Math.sin(alpha);
    qi = t3d.get(I)*alpha;
    qj = t3d.get(J)*alpha;
    qk = t3d.get(K)*alpha;
  }

  /**
   * Sets the quaternion given an axis about which to rotate and an angle. 
   * 
   * @param    alpha    Angle, in radians
   * @param    axis     Cartesian axis about which to rotate
   */
  public void set(double alpha, Basis3D axis) {
    switch (axis) {
      case I:
        set(alpha, IHAT);
        break;
      case J:
        set(alpha, JHAT);
        break;
      case K:
        set(alpha, KHAT);
        break;
      default:
        identity();
    }
  }

  /**
   * Sets the values of this Quaternion given a <code>Tuple</code> and a start
   * index in that Tuple from which to copy 4 consecutive values.  If the input
   * Tuple isn't long enough to supply all four values, then a
   * VectorSpaceIndexOutOfBoundsException will be thrown.
   * 
   * @param    <code>Tuple</code> from which to copy elements.
   * @param    Start index from where to begin copying elements.
   * 
   * @throws   Thrown if the input <code>Tuple</code> can't be accessed at the
   *           supplied index and three subsequent values (input Tuple not long
   *           enough).
   */
  public void set(Tuple tin, int ndx) {
    int ndxend = ndx + 3;

    if (tin.N < ndxend) {
      throw new VectorSpaceIndexOutOfBoundsException(
          "Setting Tuple: index out of bounds:  (" + ndxend + ")");
    } else {
      q0 = tin.get(ndx);
      qi = tin.get(ndx+1);
      qj = tin.get(ndx+2);
      qk = tin.get(ndxend);
    }
  }

  /**
   * Sets this quaternion based on the input direction cosine matrix.
   * <P>
   * Method based discussion in "Quaternion Computation from a Geometric Point
   * of View" by Malcolm Shuster and Gregory Natanson.
   * <P>
   * Many methods check for division by a number "near" zero.  Others begin
   * by determining the quaternion element with the largest magnitude.  This
   * one resembles the 2nd method, except instead of searching for the
   * largest quaternion element (extra computations) it finds the first
   * element that is greater than or equal to 1/2 in magnitude and bases
   * the rest of the quaternion components on that element.
   * <P>
   * Updated to use KAPPA instead of '1' in algorithm selection logic, so the
   * required magnitude of the first solved for quaternion will be dependent
   * on this setting:  KAPPA = 1 -> q_first > 1/2; KAPPA = 0.25 -> q_first > 1/4
   * ("DCM to Quaternion and Back Again", by Kurt Motekew).
   *
   * @param   dcm    Rotation matrix
   */
  public void set(Matrix3X3 dcm) {
    double tmp, d4;
    double[][] mvals = dcm.valuesPtr();

      // epsilon < KAPPA < 1, where epsilon is large enough to not
      // cause numerical issues.  If KAPPA is set to 1, change the
      // '>' to '>='.
    if ((tmp = 1 + mvals[0][0] + mvals[1][1] + mvals[2][2]) > KAPPA) {
      tmp = Math.sqrt(tmp);
      q0 = 0.5*tmp;
      d4 = 0.5/tmp;
      qi = (mvals[1][2] - mvals[2][1])*d4;
      qj = (mvals[2][0] - mvals[0][2])*d4;
      qk = (mvals[0][1] - mvals[1][0])*d4;
    } else if ((tmp = 1 + mvals[0][0] - mvals[1][1] - mvals[2][2]) > KAPPA) {
      tmp = Math.sqrt(tmp);
      qi = 0.5*tmp;
      d4 = 0.5/tmp;
      q0 = (mvals[1][2] - mvals[2][1])*d4;
      qj = (mvals[0][1] + mvals[1][0])*d4;
      qk = (mvals[0][2] + mvals[2][0])*d4;
    } else if ((tmp = 1 - mvals[0][0] + mvals[1][1] - mvals[2][2]) > KAPPA) {
      tmp = Math.sqrt(tmp);
      qj = 0.5*tmp;
      d4 = 0.5/tmp;
      q0 = (mvals[2][0] - mvals[0][2])*d4;
      qi = (mvals[0][1] + mvals[1][0])*d4;
      qk = (mvals[1][2] + mvals[2][1])*d4;
    } else if ((tmp = 1 - mvals[0][0] - mvals[1][1] + mvals[2][2]) > KAPPA) {
      tmp = Math.sqrt(tmp);
      qk = 0.5*tmp;
      d4 = 0.5/tmp;
      q0 = (mvals[0][1] - mvals[1][0])*d4;
      qi = (mvals[0][2] + mvals[2][0])*d4;
      qj = (mvals[1][2] + mvals[2][1])*d4;
    } else {
      throw new SingularQuaternionException("Can't extract Quaternion" +
                                            " from DCM");
    }
  } 

  /**
   * Determines if the two quaternions are equal.
   *
   * @param  quat      A Quaternion to compare to this one.
   *
   * @return boolean   true if the two quaternions are equal
   */
  public boolean equals(Quaternion quat) {
    if (q0 == quat.get(Q0)  &&  qi == quat.get(QI)  &&
        qj == quat.get(QJ)  &&  qk == quat.get(QK)      ) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Sets the value of this quaternion to the sum of the
   * two input quaternions
   *
   * @param  q1      first quaternion to add
   * @param  q2      second quaternion to add
   */
  public void plus(Quaternion q1, Quaternion q2) {
    q0 = q1.get(Q0) + q2.get(Q0);
    qi = q1.get(QI) + q2.get(QI);
    qj = q1.get(QJ) + q2.get(QJ);
    qk = q1.get(QK) + q2.get(QK);
  }

  /**
   * Sets the value of this quaternion to the sum of this
   * and the input quaternion
   *
   * @param  q2      quaternion to add to this quaternion
   */
  public void plus(Quaternion q2) {
    q0 += q2.get(Q0);
    qi += q2.get(QI);
    qj += q2.get(QJ);
    qk += q2.get(QK);
  }

  /**
   * Sets the value of this quaternion to the difference
   * of the two input quaternions:  this = q1 - q2
   *
   * @param  q1      first quaternion
   * @param  q2      the quaternion to subtract from the first
   */
  public void minus(Quaternion q1, Quaternion q2) {
    q0 = q1.get(Q0) - q2.get(Q0);
    qi = q1.get(QI) - q2.get(QI);
    qj = q1.get(QJ) - q2.get(QJ);
    qk = q1.get(QK) - q2.get(QK);
  }

  /**
   * multiplies this quaternion by the input scalar
   *
   * @param  sc      scalar to multiply this quaternion by
   */
  public void mult(double sc) {
    q0 *= sc;
    qi *= sc;
    qj *= sc;
    qk *= sc;
  }

  /**
   * Sets the value of this quaternion to the product
   * of the two input quaternions:  this = q1*q2
   *
   * @param  p      first quaternion
   * @param  q      second quaternion
   */
  public void mult(Quaternion p, Quaternion q) {
    q0 =   p.get(Q0)*q.get(Q0) - p.get(QI)*q.get(QI)
         - p.get(QJ)*q.get(QJ) - p.get(QK)*q.get(QK);
    qi =   p.get(Q0)*q.get(QI) + p.get(QI)*q.get(Q0)
         + p.get(QJ)*q.get(QK) - p.get(QK)*q.get(QJ);
    qj =   p.get(Q0)*q.get(QJ) - p.get(QI)*q.get(QK)
         + p.get(QJ)*q.get(Q0) + p.get(QK)*q.get(QI);
    qk =   p.get(Q0)*q.get(QK) + p.get(QI)*q.get(QJ)
         - p.get(QJ)*q.get(QI) + p.get(QK)*q.get(Q0);
  }

  /**
   * Sets the value of this quaternion to the product of the
   * input matrix and this quaternion in vector form (remember,
   * the scalar is the first element).  this = PHI*this
   *
   * @param   phi   4x4 matrix
   */
  public void mult(Matrix phi) {
    double p0 = q0;
    double pi = qi;
    double pj = qj;
    double pk = qk;

    q0 = p0*phi.get(1,1) + pi*phi.get(1,2) + pj*phi.get(1,3) + pk*phi.get(1,4);
    qi = p0*phi.get(2,1) + pi*phi.get(2,2) + pj*phi.get(2,3) + pk*phi.get(2,4);
    qj = p0*phi.get(3,1) + pi*phi.get(3,2) + pj*phi.get(3,3) + pk*phi.get(3,4);
    qk = p0*phi.get(4,1) + pi*phi.get(4,2) + pj*phi.get(4,3) + pk*phi.get(4,4);
  }

  /**
   * Sets the value of this quaternion to the complex
   * conjugate of itself.
   */
  public void conj() {
    qi = -qi;
    qj = -qj;
    qk = -qk;
  }

  /**
   * Sets the value of this quaternion to the complex
   * conjugate of the input quaternion.
   *
   * @param  qua     quaternion from which to form complex conj
   */
  public void conj(Quaternion quat) {
    q0 =  quat.get(Q0);
    qi = -quat.get(QI);
    qj = -quat.get(QJ);
    qk = -quat.get(QK);
  }

  /**
   * @return The square of the norm of this quaternion
   */
  public double normSQ() {
    return  q0*q0 + qi*qi + qj*qj + qk*qk;
  }

  /**
   * Sets to a zero rotation, equivalent to an identity DCM.
   */
  public void identity() {
    q0 = 1.0;
    qi = 0.0;
    qj = 0.0;
    qk = 0.0;
  }

  /**
   * Multiplies this quaternion by -1 if the scalar element is less
   * than zero.  Calling this method will ensure the scalar element
   * is always positive.
   */
  public void standardize() {
    if (q0 < 0.0) {
      this.mult(-1.0);
    }
  }

  /**
   * Normalizes this quaternion.  This process divides each
   * component of this quaternion by its magnitude.
   */
  public void normalize() {
    double norm_inv = 1.0/Math.sqrt(this.normSQ());
    q0 *= norm_inv;
    qi *= norm_inv;
    qj *= norm_inv;
    qk *= norm_inv;
  }

  /**
   * Normalizes this quaternion if it differs from unity by more than
   * TOL.  This process divides each component of this quaternion by
   * its magnitude.
   */
  public void normalizeTOL() {
    double mag2 = this.normSQ();
    
    if (Math.abs(mag2 - 1.0) > TOL) {
      double norm_inv = 1.0/Math.sqrt(mag2);
      q0 *= norm_inv;
      qi *= norm_inv;
      qj *= norm_inv;
      qk *= norm_inv;
    }
  }

  /**
   * Sets the value of this quaternion to the inverse
   * of the input quaternion.
   *
   * @param  quat     quaternion from which to form inversion
   */
  public void invert(Quaternion quat) {
    double n2 = quat.normSQ();

    if (n2 != 0.0) {
      this.conj(quat);
      this.mult(1.0/n2);
    } else {
      throw(new SingularQuaternionException(
                           "Can't invert Quaternion, norm^2 = " + n2));
    }
  }

  /**
   * @return            Rotation angle about axis, radians.
   */
  public double angle() {
    return 2.0*Math.acos(q0);
  }

  /**
   * Computes the axis of rotation and rotation angle about
   * that axis corresponding to this quaternion.
   *
   * @param   eigaxis   Output axis of rotation
   *
   * @return            Rotation angle about axis, radians.
   */
  public double axisAngle(Tuple3D eigaxis) {
    double alpha = 2.0*Math.acos(q0);
    double sao2 = Math.sin(alpha/2.0);
    eigaxis.put(I, qi/sao2);
    eigaxis.put(J, qj/sao2);
    eigaxis.put(K, qk/sao2);
    return alpha;
  }

  /**
   * Prints the components of this <code>Quaternion</code>
   */
  public String toString() {
    return("q0:  " + q0 + "  q:  " + qi + " " + qj + " " + qk); 
  }
}


  /*
   * Calculates the matrix Q, which is multiplied by a vector
   * giving the equivalent of the quaternion rotation operation
   * qvq*, where v is the vector rotated within a reference frame,
   * q is this quaternion, and q* is the complex conjugate of the
   * quaternion.  The rotated vector, v2 = Qv1.  A frame rotation,
   * instead of a vector rotation, can be accomplished by rotating
   * the vector by the transpose of the Q matrix.
   */
  /*
  private void updateQ() {
    double q0q0 = q0*q0;
    double q0qi = q0*qi;
    double q0qj = q0*qj;
    double q0qk = q0*qk;
    double qiqj = qi*qj;
    double qiqk = qi*qk;
    double qjqk = qj*qk;

    q11 = 2.0*(q0q0  + qi*qi) - 1.0;
    q21 = 2.0*(qiqj + q0qk);
    q31 = 2.0*(qiqk - q0qj);
    q12 = 2.0*(qiqj - q0qk);
    q22 = 2.0*(q0q0  + qj*qj) - 1.0;
    q32 = 2.0*(qjqk + q0qi);
    q13 = 2.0*(qiqk + q0qj);
    q23 = 2.0*(qjqk - q0qi);
    q33 = 2.0*(q0q0  + qk*qk) - 1.0;
  }
  */
