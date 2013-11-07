package com.motekew.vse.test;

import com.motekew.vse.math.*;

public class TestQuaternion {

  /**
   * Test Quaternion frame rotation and extraction from DCM
   * 
   * z-axis rotation of 90 deg followed by x-axis rotation of 90 deg
   * 
   * r1 = [1 0 1]
   * r2 = [0 1 1]
   */
  public static void main(String[] args) {
    Tuple3D r1 = new Tuple3D(1, 0, 1);
    System.out.println("r1:  " + r1);
    
    Matrix3X3 dcm = new Matrix3X3();
    Matrix3X3 rx  = new Matrix3X3();
    Matrix3X3 rz  = new Matrix3X3();
    
    double alpha = Math.toRadians(90);
    dcm.mult(rx.rotX(alpha), rz.rotZ(alpha));
    System.out.println("Rx[90]*Rz[90]");
    System.out.println(""+ dcm);
    
    Tuple3D r2 = new Tuple3D();
    r2.mult(dcm, r1);
    System.out.println("r2 with DCM rot:  " + r2);
    
    Quaternion quat = new Quaternion();
    quat.set(dcm);
    System.out.println("Quaternion:  " + quat);
    r2.fRot(quat, r1);
    System.out.println("r2 with quat rot:  " + r2);
    
    Tuple3D xaxis = new Tuple3D(1.0, 0.0, 0.0);
    Tuple3D zaxis = new Tuple3D(0.0, 0.0, 1.0);
    Quaternion qx = new Quaternion();
    Quaternion qz = new Quaternion();
    qx.set(alpha, xaxis);
    qz.set(alpha, zaxis);
    quat.mult(qz, qx);
    System.out.println("Quaternion:  " + quat);
    r2.fRot(quat, r1);
    System.out.println("r2 with quat quat rot:  " + r2);

  }

}
