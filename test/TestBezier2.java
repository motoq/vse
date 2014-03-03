package com.motekew.vse.test;

import com.motekew.vse.math.*;

public class TestBezier2 {

  /**
   * Test the BezierQuadratic.setReferencePoint() method.  This method
   * solves for the appropriate control point given a reference point
   * on the curve.
   */
  public static void main(String[] args) {
    Tuple2D a = new Tuple2D(12.0, -10.0);
    Tuple2D p = new Tuple2D(17.5, -5.5);
    Tuple2D b = new Tuple2D(20.0, 0.0);
    Tuple2D eCpt = new Tuple2D(18.682035,-6.405179);

    BezierQuadratic b2 = new BezierQuadratic(a, b);
    int nitr = b2.setReferencePoint(p);
    double tr = b2.getIParam();
    System.out.println("Anchor Points, t=0:  " + a);
    System.out.println("Anchor Points, t=1:  " + b);
    System.out.println("Expected Reference point:  " + p);
    System.out.println("Computed Reference point, t=" + tr + ":  " + b2);
    System.out.println("Expected Control point:  " + eCpt);
    System.out.println("Computed Control point:  " + b2.getControlPoint() +
                       " after " + nitr + " iterations");
    
  }

}
