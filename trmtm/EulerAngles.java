/*
 c  EulerAngles.java
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

package com.motekew.vse.trmtm;

import com.motekew.vse.enums.Basis3D;
import com.motekew.vse.enums.EulerA;
import com.motekew.vse.enums.Q;
import com.motekew.vse.math.Angles;
import com.motekew.vse.math.Matrix3X3;
import com.motekew.vse.math.Quaternion;
import com.motekew.vse.math.Tuple3D;

/**
 * This class represents a set of Euler angles - Bank, Elevation,
 * and Heading.  It also contains classes that handle rotation
 * sequences and other conversions.  Rotation sequences, and other
 * conversions, will follow the Aerospace sequence for Intertial to
 * Body transformations:  Rotate about the Z-axis by the heading,
 * rotate about the new Y-axis by the elevation, and finally, rotate
 * about the newest X-axis by the Bank.
 *
 * @author  Kurt Motekew
 * @since   20080911
 */
public class EulerAngles extends Tuple3D {

    // Used to see if labels for this extension of the Tuple have
    // been initialized.  Don't bother wasting the memory setting
    // unless the names have been asked for via getLabels();
  private boolean needToInitLabels = true;

    // Eases indexing.
  private static final EulerA BANK = EulerA.BANK;    // +/- pi
  private static final EulerA ELEV = EulerA.ELEV;    // +/- pi/2
  private static final EulerA HEAD = EulerA.HEAD;    // +/- pi

    // Cach for local computations.
  private static Quaternion qtmp = new Quaternion();

  /**
   * Returns an array of String labels for each element of this EulerAngles
   * Tuple3D.
   *
   * @return         A new arrray of String labels - not the pointer to the
   *                 internally stored labels.                             
   */
  @Override
  public String[] getLabels() {
    if (needToInitLabels) {
      needToInitLabels = false;
      String[] names = new String[EulerA.values().length];
      for (EulerA ii : EulerA.values()) {
        names[ii.ordinal()] = ii.toString();
      }
      super.setLabels(names);
      return names;
    } else {
      return super.getLabels();
    }
  }

  /**
   * Sets an array of labels that are to be associated with this EulerAngles
   * Tuple3D.  It is probably best to use the default labels.
   *                     
   * @param  lbl[]   An array of labels that are to be copied into to this
   *                 EulerAngles object.
   */
  @Override
  public void setLabels(String[] lbls) {
    needToInitLabels = false;
    super.setLabels(lbls);
  }

  /**
   * Sets the value of the EulerAngles given a <code>EulerA</code>
   * enum index.
   *
   * @param  ndx   A EulerA index for the row
   * @param  val   A double value in radians to be set for the EulerAngles
   */
  public void put(EulerA ndx, double val) {
    put(ndx.ordinal()+1, val);
  }

  /**
   * Gets the value of the EulerAngles given a <code>EulerA</code>
   * enum index.
   *
   * @param  ndx   A EulerA index for the row
   *
   * @return       A double value in radians for the requested element
   */
  public double get(EulerA ndx) {
    return get(ndx.ordinal()+1);
  }

  /**
   * Sets the value of the EulerAngles given a <code>EulerA</code>
   * enum index.
   *
   * @param  ndx   A EulerA index for the row
   * @param  val   A double value in degrees to be set for the EulerAngles
   */
  public void putDeg(EulerA ndx, double val) {
    put(ndx.ordinal()+1, Angles.RAD_PER_DEG*val);
  }

  /**
   * Gets the value of the EulerAngles given a <code>EulerA</code>
   * enum index.
   *
   * @param  ndx   A EulerA index for the row
   *
   * @return       A double value in degrees for the requested element
   */
  public double getDeg(EulerA ndx) {
    return Angles.DEG_PER_RAD*get(ndx.ordinal()+1);
  }

  /**
   * Creates a Direction Cosine Matrix given these EulerAngles.
   * See <code>toQuatFrameRot</code> for further info on order
   * of rotations.
   *
   * @param   dcm   Output:  <code>Matrix3X3</code> DCM (a frame rotation).
   * @return        A pointer to dcm
   */
  public Matrix3X3 toDCM(Matrix3X3 dcm) {
    double psi   = get(HEAD);
    double theta = get(ELEV);
    double phi   = get(BANK);

    double cpsi = Math.cos(psi);
    double spsi = Math.sin(psi);
    double ctheta = Math.cos(theta);
    double stheta = Math.sin(theta);
    double cphi = Math.cos(phi);
    double sphi = Math.sin(phi);

      // 1st Row
    dcm.put(Basis3D.I,Basis3D.I, cpsi*ctheta);
    dcm.put(Basis3D.I,Basis3D.J, spsi*ctheta);
    dcm.put(Basis3D.I,Basis3D.K, -stheta);
      // 2nd Row
    dcm.put(Basis3D.J,Basis3D.I, cpsi*stheta*sphi - spsi*cphi);
    dcm.put(Basis3D.J,Basis3D.J, spsi*stheta*sphi + cpsi*cphi);
    dcm.put(Basis3D.J,Basis3D.K, ctheta*sphi);
      // 3rd Row
    dcm.put(Basis3D.K,Basis3D.I, cpsi*stheta*cphi + spsi*sphi);
    dcm.put(Basis3D.K,Basis3D.J, spsi*stheta*cphi - cpsi*sphi);
    dcm.put(Basis3D.K,Basis3D.K, ctheta*cphi);

    return dcm;
  }

  /**
   * Given a direction cosine matrix, convert to Euler angles
   * following the aerospace sequence used by this class.
   * 
   * @param   dcm    DCM to convert to Euler Angles
   */
  public void fromDCM(Matrix3X3 dcm) {
    qtmp.set(dcm);
    toQuatFrameRot(qtmp);
  }

  /**
   * Creates a <code>Quaternion</code> given these EulerAngles.
   * The Aerospace sequence is used to create an Inertial to Body
   * rotation frame:  Rotate by the heading about the z-axis,
   * then by the elevation about the y-axis, and then by the bank    
   * about the x-axis.  The end result of three quaternions multiplied
   * together representing these rotations is below.
   *                                                       
   * @param   quat   <code>Quaternion</code> to hold componets computed
   *                 from euler angles (output).
   * @return         Pointer to quat.
   */
  public Quaternion toQuatFrameRot(Quaternion quat) {
    double y = get(HEAD) / 2.0;       // yaw
    double p = get(ELEV) / 2.0;       // pitch
    double r = get(BANK) / 2.0;       // roll

    double sinp = Math.sin(p);
    double siny = Math.sin(y);
    double sinr = Math.sin(r);
    double cosp = Math.cos(p);
    double cosy = Math.cos(y);
    double cosr = Math.cos(r);

    quat.put(Q.Q0, cosr * cosp * cosy + sinr * sinp * siny);
    quat.put(Q.QI, sinr * cosp * cosy - cosr * sinp * siny);
    quat.put(Q.QJ, cosr * sinp * cosy + sinr * cosp * siny);
    quat.put(Q.QK, cosr * cosp * siny - sinr * sinp * cosy);

    // should not need to be normalized at this point
    return quat;
  }

  /**
   *  Extracts Bank, Elevation, and Heading angles from the
   *  entered <code>Quaternion</code>.
   *  
   *  @param   quat     <code>Quaternion</code> from which to extract
   *                    Euler Angles.
   */
  public void fromQuatFrameRot(Quaternion quat) {
    
    this.fromQuatFrameRot(quat.get(Q.Q0), quat.get(Q.QI),
                          quat.get(Q.QJ), quat.get(Q.QK));    
  }

  /**
   *  Extracts Bank, Elevation, and Heading angles from the
   *  entered quaternion components.
   *  
   *  @param   q0in     scalar component of quaternion
   *  @param   qiin     first vector component of quaternion
   *  @param   qjin     second vector component of quaternion
   *  @param   qkin     thrid vector component of quaternion
   */
  public void fromQuatFrameRot(double q0in,
                               double qiin, double qjin, double qkin) {
    double elevation;

    double q0 = q0in;
    double q1 = qiin;
    double q2 = qjin;
    double q3 = qkin;
    
    double m11 = 2.0*(q0*q0 + q1*q1) - 1.0;
    double m12 = 2.0*(q1*q2 + q0*q3);
    double m13 = 2.0*(q1*q3 - q0*q2);
    double m23 = 2.0*(q2*q3 + q0*q1);
    double m33 = 2.0*(q0*q0 + q3*q3) - 1.0;

      // make sure elevation is within limits.  atan2 should
      // have taken care of the others....
    put(BANK, Math.atan2(m23, m33));
    elevation = Math.asin(-m13);
    put(ELEV, Angles.setPIo2(elevation));
    put(HEAD, Math.atan2(m12, m11));
  }

  /**
   * Prints Bank, Elevation, and Heading
   */
  public String toString() {
    return("Bank:  "        + getDeg(BANK) + "  Elevation:  " + getDeg(ELEV) +
                                               "  Heading:  "   + getDeg(HEAD));
  }

}
