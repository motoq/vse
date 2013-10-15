/*
 c  LegendrePlmSinX.java
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

package com.motekew.vse.math;

/**
 * This class computes The associated Legendre functions of sin(x),
 * P[l][m](sin(x)).  When working in a spherical coordinate system, x is
 * the elevation (latitude) as measured from the x-y plane.  It is equivalent
 * to P_l_m[cos(V)], where V is the colatitude (angle measured from the
 * z-axis.  sin(x) is more commonly used when working with a gravity model
 * where as cos(V) shows up is other physics problems.
 * <P>
 * The recursive algorithm presented in David Vallado's "Fundamentals of
 * Astrodynamics and Applications" (all editions) is used here.  The object is
 * instantiated with the order and degree (assumed equal) for which the
 * functions are to be evaluated.
 *
 * @author   Kurt Motekew
 * @since    20120131
 */
public class LegendrePlmSinX {
  private final int N;

  private double[][] alf;

  /**
   * @param   degree_order   Degree and Order to which the associated
   *                         Legendre functions are to be evaluated.
   */
  public LegendrePlmSinX(int degree_order) {
    N = (degree_order > 1) ? degree_order : 1;
    alf = new double[N+1][N+1];
  }

  /**
   * @return    The max degree of the associated Legendre function computed
   *            by this instance of this class.
   */
  public int getMaxDegree() {
    return N;
  }

  /**
   * @return    The max order of the associated Legendre function computed
   *            by this instance of this class.
   */
  public int getMaxOrder() {
    return N;
  }

  /**
   * Computes the associated Legendre function of sin(x).  Just for
   * clarity, when x is input, P[l,m](x) is not computed.  Instead
   * P[l,m](sin(x)) is computed.
   * <P>
   *
   * @param   x     The associated Legendre function of the sin of this
   *                parameter will be computed.  The angle x is in radians. 
   *                It should be a latitude or elevation measurement, not
   *                the colatitude.  Do not pass in the sin of x.
   */
  public void set(double x) {
    set(Math.sin(x), Math.cos(x));
  }

  /**
   * Computes the associated Legendre function of sin(x).  
   *
   * @param   sx    The sine of the angle for which the associated Legendre
   *                function should be computed.
   * @param   cx    The cosine of the angle for which the associated Legendre
   *                function should be computed.
   */
  public void set(double sx, double cx) {
      // P[ll][mm] = P[degree][order]
    int ll, mm;

      // Prime the recursion pumps
    alf[0][0] = 1.0;
    alf[1][0] = sx;
    alf[1][1] = cx;
      //
    for (ll=2; ll<=N; ll++) {
      for (mm=0; mm<=N; mm++) {
        if (ll == mm) {
          alf[ll][ll] = (2.*ll - 1.)*cx*alf[ll-1][ll-1];
        } else if (mm < ll  &&  mm != 0) {
          alf[ll][mm] = alf[ll-2][mm] + (2.*ll - 1.)*cx*alf[ll-1][mm-1];
        } else {
          alf[ll][mm] = ( (2.*ll - 1.)*sx*alf[ll-1][0] -
                          (   ll - 1.)   *alf[ll-2][0]   )/((double) ll);
        }
      }
    }
  }

    /**
     * Returns the associated Legendre functions of Sin(X).
     * 
     * @param   degree   Zero or greater, but less maximum set at instantiation.
     * @param   order    Less than or equal to degree
     */
  public double get(int degree, int order) {
    if (order < 0) {
      return 0.0;
    }  else if (order > degree) {
      return 0.0;
    } else if (degree > N) {
      return 0.0;
    } else {
      return alf[degree][order];
    }
  }

  /**
   * NOTE:
   * This method is retained from an earlier class only to help with
   * validation of the recursive method that should be used.
   * 
   * Computes the Associated Legendre Function of sin(x).  Just for
   * clarity, when x is input, P[l,m](x) is not computed.  Instead
   * P[l,m](sin(x)) is computed.  A better implementation will be made
   * in the future - this is done as is to com.motekew.vse.test some things.
   * <P>
   * Up to degree and order 4 is supported.  Order should be less than or
   * equal to the degree.  Indices outside of this range will result in a
   * zero being returned.
   *
   * @param   x     The associated Legendre function value of the sin of
   *                this parameter will be computed.  X is an angle and
   *                should be in radians.  Do not pass in the sin of X.
   * @param   ll    The degree - as implemented, it should be between 0 and 4.
   * @param   mm    The order - as implemented, it should be between 0 and ll.
   */
  public static double alfSinX(double x, int ll, int mm) {
    double retVal = 0.0;
    double cx = 0.0;
    double sx = 0.0;

    cx = Math.cos(x);
    sx = Math.sin(x);

    switch(ll) {
      case 0:
        if (mm == 0) {
          retVal = 1.0;
        }
        break;
      case 1:
        if (mm == 0) {
          retVal = sx;
        } else if (mm == 1) {
          retVal = cx;
        }
        break;
      case 2:
        if (mm == 0) {
          retVal = 0.5*(3.0*sx*sx - 1.0);
        } else if (mm == 1) {
          retVal = 3.0*sx*cx;
        } else if (mm == 2) {
          retVal = 3.0*cx*cx;
        }
        break;
      case 3:
        if (mm == 0) {
          retVal = 0.5*sx*(5.0*sx*sx-3.0);
        } else if (mm == 1) {
          retVal = 0.5*cx*(15.0*sx*sx - 3.0);
        } else if (mm == 2) {
          retVal = 15.0*cx*cx*sx;
        } else if (mm == 3) {
          retVal = 15.0*cx*cx*cx;
        }
        break;
      case 4:
        if (mm == 0) {
          retVal = 1.0/8.0*(35.0*sx*sx*sx*sx - 30.0*sx*sx + 3.0);
        } else if (mm == 1) {
          retVal = 2.5*cx*sx*(7.0*sx*sx - 3.0);
        } else if (mm == 2) {
          retVal = 7.5*cx*cx*(7.0*sx*sx - 1.0);
        } else if (mm == 3) {
          retVal = 105.0*cx*cx*cx*sx;
        } else if (mm == 4) {
          retVal = 105.0*cx*cx*cx*cx;
        }
        break;
    }
    return retVal;
  }

}
