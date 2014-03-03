/*
 c  BezierQuadratic.java
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

package com.motekew.vse.math;

/**
 * This class models a quadratic Bezier parametric equation.  It is
 * instantiated with the start and stop anchor points and fully defined
 * afterwards by specifying either the control point or a 3rd point on
 * the curve.  This extension of the <code>Tuple2D</code> class assumes
 * a point on the locus with calls to setIParam (set the independent
 * parameter value).
 * <P>
 * Given an independent variable t, such that (0 <= t <= 1), anchor points
 * A and B, and a control point C, the equation for the quadratic Bezier is:
 *   P = (1 - t)^2 * A  +  2*t*(1 - t) * C  +  t*t*B
 *
 * @author   Kurt Motekew
 * @since    20131229
 */
public class BezierQuadratic extends Tuple2D {
  private static final int NP = 3;          // Number of solve for
  private static final int NY = 2;          // Number of measurements per set
  private static final int MAXITR = 50;

  private double tol = .000001;

  private double tval;
  private Tuple2D pt_t0 = new Tuple2D();  // Anchor point at t=0
  private Tuple2D pt_t1 = new Tuple2D();  // Anchor point at t=1
  private Tuple2D cpt   = new Tuple2D();  // Control point

  /**
   * Initializes with the anchor points defining the bounds of this quadratic
   * Bezier function.  The internal control point is set to bisect the line
   * formed between the two anchor points.  The independent parameter is
   * set to 0.5 and the state of this Tuple2D is updated to represent the
   * point on the locus at t=0.5.  In other words, once initialized, this
   * Bezier quadratic is a straight line with its position initialized to
   * the midpoint between the two anchor points.
   *
   * @param   a   Anchor point associated with independent parameter t=0
   *              to be copied.
   * @param   b   Anchor point associated with independent parameter t=1
   *              to be copied.
   */
  public BezierQuadratic(Tuple2D a, Tuple2D b) {
    pt_t0.set(a);
    pt_t1.set(b);
    cpt.plus(a, b);
    cpt.mult(0.5);       // Set control point to midpoint between anchors
    set(cpt);
    tval = 0.5;
  }

  /**
   * @return   The value of the independent parameter associated with this
   *           point on the quadratic Bezier curve.
   */
  public double getIParam() {
    return tval;
  }

  /**
   * Updates this point on the quadratic Bezier curve given a new independent
   * parameter value.  No checking is performed to ensure 0 <= t <=1, instead
   * getIParam() can be used to check the validity of this object's state.
   *
   * @param   t   The independent parameter used to compute the location of
   *              this point on the quadratic Bezier curve:  0 <= t <= 1
   */
  public void setIParam(double t) {
    Tuple2D tmp = new Tuple2D();
    double omt = 1.0 - t;

    tval = t;

      // (1-t)^2 * A
    this.set(pt_t0);
    this.mult(omt*omt);

      // 2t(1-t) * C
    tmp.set(cpt);
    tmp.mult(2.0*t*omt);
    this.plus(tmp);

      // t^2 * B
    tmp.set(pt_t1);
    tmp.mult(t*t);
    this.plus(tmp);
  }

  /**
   * Sets a new control point to be used with this quadratic Bezier curve
   * and updates the state of this point given the current independent
   * parameter setting (the independent parameter value does not change).
   *
   * @param  c  New control point value to copy
   */
  public void setControlPoint(Tuple2D c) {
    cpt.set(c);
    setIParam(tval);
  }

  /**
   * @return   A new copy of the control point used to define the locus.
   */
  public Tuple2D getControlPoint() {
    Tuple2D c = new Tuple2D(cpt);
    return c;
  }

  /**
   * Given a 3rd point on the Bezier curve, solves for the independent
   * parameter value and the control point needed for the locus to pass
   * through the desired point.  The independent parameter is updated
   * to match the input reference point.
   * <P>
   * A direct linear least squares solution can be used to solve for the
   * control point when the independent parameter associated with the
   * reference point is know 'a priori'.  Most methods require this to
   * be known, make assumptions, or attempt to solve for the value separately.
   * I simply makes use of iterative least squares to solve for all unknowns -
   * the independent parameter has a nonlinear relationship, necessitating the
   * differential correction approach.  Note:  For this implementation, the
   * partials for each "observation" are computed.  However, the residuals
   * driving those partials are always between the desired reference point and
   * the computed.
   *
   * @param  rpt  Reference point from which to determine a new control point
   *              This object will be within getTolerance() of rpt.
   *
   * @return      Number of iterations required for convergence.  If less than
   *              zero, then convergence was not achieved to the desired
   *              tolerance or the solution diverged.  See get/setTolerance.
   */
  public int setReferencePoint(Tuple2D rpt) {
    SysSolverBD bdSolver = new SysSolverBD(NY, NP);
    B2Partials dbdx = new B2Partials();                        // Jacobian

      // - Initialize the solve for vector X
      // - tr is the value of the independent parameter associated with the
      //   reference point.  It is a solve for parameter - an initial guess
      //   of 1/2 is used.
      // - The initial guess to the control point is the reference point value.
    double tr = 0.5;
    cpt.set(rpt);
    setIParam(tr);

    Tuple2D drpt = new Tuple2D();

      // Refine estimate
    int nitr;
    for (nitr=0; nitr<MAXITR; nitr++) {
        // Compute exit criteria, desired vs. computed reference point
      drpt.minus(rpt, this);
      if (drpt.mag() <= tol) {
        break;
      }

        // Block diagonal normal equations allow for "accumulation"
        // Note use of the same drpt in each accumulation step
      bdSolver.reset();
        // Accumulate "measurements" - Anchor point at t = 0
      dbdx.partials(0.0, pt_t0, cpt, pt_t1);
      bdSolver.accumulate(dbdx, drpt);
        //                           - Reference point at t = tr
      dbdx.partials(tr, pt_t0, cpt, pt_t1);
      bdSolver.accumulate(dbdx, drpt);
        //                           - Anchor point at t = 1
      dbdx.partials(1.0, pt_t0, cpt, pt_t1);
      bdSolver.accumulate(dbdx, drpt);

        // Update 
      bdSolver.solve();
      tr += bdSolver.get(1);
      cpt.put(1, cpt.get(1) + bdSolver.get(2));
      cpt.put(2, cpt.get(2) + bdSolver.get(3));
      setIParam(tr);
    }
    if (nitr == MAXITR) {
      nitr = -1;
    }

    return nitr;
  }

  /**
   * This is used with the setReferencePoint() method.
   *
   * @return   The current convergence criteria tolerance - see setTolerance.
   */
  public double getTolerance() { return tol; }

  /**
   * This is used with the setReferencePoint() method.
   *
   * @param   tol   The absolute value of the difference between the desired
   *                reference point and computed (based on the latest control
   *                point estimate) is compared to this setting to determine
   *                when convergence has been achieved.
   */
  public void setTolerance(double tolIn) { tol = tolIn; }

  /*
   * Partials of Cartesian coordinates on a quadratic Bezier curve
   * w.r.t. the independent parameter and the control point location.
   */
  private class B2Partials extends Matrix {
    B2Partials() {
      super(2,3);
    }

    /*
     * Computes partials given:
     *
     * @param   t   Independent parameter, 0 <= t <= 1
     * @param   a   Anchor point at t = 0
     * @param   c   Control point
     * @param   b   Anchor point at t = 1
     */
    void partials(double t, Tuple2D a, Tuple2D c, Tuple2D b) {
      double dxdt = (2.0*t - 2.0)*a.get(1) +
                    (2.0 - 4.0*t)*c.get(1) +
                            2.0*t*b.get(1);
      double dydt = (2.0*t - 2.0)*a.get(2) +
                    (2.0 - 4.0*t)*c.get(2) +
                            2.0*t*b.get(2);
      double ddc = 2.0*t*(1.0 - t);

      this.put(1,1, dxdt);
      this.put(2,1, dydt);
      this.put(1,2, ddc);
      this.put(2,2, 0.0);
      this.put(1,3, 0.0);
      this.put(2,3, ddc);
    }
  }
}
