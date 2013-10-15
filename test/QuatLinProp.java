package com.motekew.vse.test;

import com.motekew.vse.math.*;
import com.motekew.vse.trmtm.QuatStateTrans;

public class QuatLinProp {

  /**
   * Test Quaternion state transition matrix vs. axis and constant
   * angular velocity method.
   * 
   */
  public static void main(String[] args) {
      // Axis propagation
    Quaternion dq  = new Quaternion();
    Quaternion qae = new Quaternion();      // Axis eigen vec prop
    Quaternion qsd = new Quaternion();      // Strapdown prop
    Quaternion q0  = new Quaternion(5.33997051E-01, -4.53878236E-01,
                                    5.04031852E-01, 5.04770827E-01);
    Tuple3D omegaV = new Tuple3D(3.59441576E-01, -1.84505251E-01,
                                                 -1.41607749E-01);
    Tuple3D eigAxis = new Tuple3D();
    double omega = omegaV.mag();
    double t0 = 0.0;
    double te = 1.0;
    double t = t0;
    double dt = 0.1;
    double ang;
      // State Transition Matrix
    QuatStateTrans phi = new QuatStateTrans();
    //phi.useQuickEvaluation(true);
    
    q0.normalize();
    eigAxis.set(omegaV);
    eigAxis.unitize();
    System.out.println("t:  " + t + "\tQ:  " + q0 + "\n");
    while (t <= te) {
        // First compute and print eigenaxis propagation
      ang = omega*(t-t0);
      dq.set(ang, eigAxis);
      qae.mult(q0, dq);
      System.out.println("t:  " + t + "\tQ:  " + qae);
        // Now compute using the state transition matrix
      phi.set(t-t0, omegaV);
      //Strapdown.stateTransition(t-t0, omegaV, phi);
      qsd.set(q0);
      qsd.mult(phi);
      qsd.normalize();
      System.out.println("t:  " + t + "\tQ:  " + qsd);
      qsd.conj();
      dq.mult(qae, qsd);
      System.out.println("dang:  " + Math.toDegrees(dq.angle()));

      t += dt;
    }
    

  }

}
