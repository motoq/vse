/*
 c  Gravity.java
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

package com.motekew.vse.envrm;

import com.motekew.vse.enums.Basis3D;
import com.motekew.vse.math.*;
import com.motekew.vse.trmtm.Acceleration;

/**
 * This class uses unnormalized zonal, sectorial, and tesseral coefficients
 * to model the gravitational potential and acceleration due to a
 * non-spherical central body.  This allows one to compute the acceleration
 * acting on a body for dynamics related modeling, or to simply compute the
 * shape of an equipotential surface for visualization.
 * <P>
 * Acceleration computations external to this package should make use of
 * <code>GravitationalAcceleration</code>.
 * <P>
 * One obvious use is the modeling of the Geoid.
 * <P>
 * The gravity model algorithm is pretty much a straightforward
 * implementation of textbook equations - not much optimization
 * has been done.
 * <P>
 * Initial validation has been performed.
 * <P>
 * This class should be kept IMMUTABLE for thread safety given it is
 * most efficient to pass this object around for others to access
 * as needed for computations.  In addition, no caching should be
 * performed.
 * 
 * @author   Kurt Motekew
 * @since    20090330
 * @since    20120201    Updated to use general associated Legendre function
 * @since    20120212    Made the degree an optional argument in potential
 *                       and acceleration calls.  Also switched to using the
 *                       <code>SphericalHarmonicCoeff</code> class to pass
 *                       coefficients instead of double[][].
 * @since    20120304    Fixed significant errors in gravity model.  Gravt
 *                       was using an old class that assumed canonical units
 *                       were in use when determining the acceleration vector.
 *                       Both the potential and acceleration vector methods had
 *                       an error when evaluating the summations - the (Re/r)^ll
 *                       factor was being applied incorrectly.
 * @since    20120306    Consolidated gravt methods to call private version
 *                       with arguments that used to be computed internally
 *                       by gravt.
 * @since    20100816    Noticed the two body portion of the acceleration was
 *                       missing.  The summations were not supposed to be
 *                       deviations from spherical gravity, but the total
 *                       acceleration (when implemented correctly).
 * @since    20131120    Moved public access to gravt to GravitationalAcceleration.
 */
public class Gravity implements ISpherical, IGravity {
  private final int degreeOrder;
  
  private double gm, re;
  private double[][] clm = null;          // Unnormalized coefficient
  private double[][] slm = null;          // storage
  
  private LegendrePlmSinX aP = null;      // Associated Legendre Functions

  /**
   * Initialize this gravity model with the gravitational parameter, reference
   * radius, and coefficients.
   * 
   * @param   gm_in   Gravitational parameter = Gm where G is the gravitational
   *                  constant, and m is the total mass of the body
   * @param   re_in   Reference radius used for potential and acceleration
   *                  computations.  Note, that for the Geoid, this is not
   *                  necessarily the earth's semi-major axis.  It is the
   *                  radius used in the definition of the spherical harmonic
   *                  coefficients.
   * @param   shc     The unnormalized form of these spherical harmonic
   *                  coefficients will be used.
   */
  public Gravity(double gm_in, double re_in, SphericalHarmonicCoeff shc) {
    gm = gm_in;
    re = re_in;

    shc.setNormalizedOutput(false);
    clm = shc.cValues();
    slm = shc.sValues();
      // Get degree/order size from size of returned arrays
      // Degree numbers also start at zero
    degreeOrder = clm.length - 1;
    aP = new LegendrePlmSinX(degreeOrder);
  }

  /**
   * @return    Reference (scaling) radius for potential computations.
   */
  @Override
  public double getRefRadius() {
    return re;
  }

  /**
   * @return    Gravitational Parameter (distance_units^3/time_units^2)
   */
  @Override
  public double getGravParam() {
    return gm;
  }

  /**
   * @return    The degree and order of this gravity model.
   */
  @Override
  public int getDegreeOrder() {
    return degreeOrder;
  }

  /**
   * this.getPotential() with the degree and order set to the maximum
   * allowable, as configured during instantation.
   */
  @Override
  public double getPotential(double r, double lat, double lon) {
    return getPotential(degreeOrder, r, lat, lon);
  }

  /**
   * Returns the gravitational potential given a position relative to the
   * centroid of the body.
   *
   * @param   degree   The degree and order to be used in evaluating this
   *                   model.  If greater than the maximum set during
   *                   instantiation, then the max is used.
   * @param   r        Distance from the centroid
   * @param   lat      Latitude:  -pi/2 <= ele <= pi/2  (rad)
   * @param   lon      Longitude: -pi   <= az  <= pi    (rad)
   *
   * @return           Gravitational potential at the input position.
   */
  @Override
  public double getPotential(int degree, double r, double lat, double lon) {
    int ll, mm;
    double reOverR = re/r;
    double gmOverR = gm/r;
    double reOverR_ll = reOverR;
    double mlam = 0.0;

    double pot = 0.0;
    double pot_ll;
    
    if (degree > degreeOrder) {
      degree = degreeOrder;
    }

      // Set Associated Legendre Functions for this latitude
    aP.set(lat);

    for (ll=2; ll<=degree; ll++) {
      pot_ll = 0.0;
      for (mm=0; mm<=ll; mm++) {
        mlam = lon * (double) mm;
        pot_ll += aP.get(ll, mm) * (clm[ll][mm]*Math.cos(mlam) +
                                    slm[ll][mm]*Math.sin(mlam));
      }
      reOverR_ll *= reOverR;    // Math.pow(reOverR, ll)
      pot_ll *= reOverR_ll;
      pot += pot_ll;
    }
    pot += 1.0;
    pot *= gmOverR;
    
    return pot;
  }

  /**
   * Returns the potential using the reference radius and the maximum
   * degree/order stored.
   * 
   * @param   lat      Latitude:  -pi/2 <= ele <= pi/2  (rad)
   * @param   lon      Longitude: -pi   <= az  <= pi    (rad)
   *
   * @return           Potential at this point.
   */
  @Override
  public double getR(double lat, double lon) {
    return this.getPotential(degreeOrder, re, lat, lon);
  }

  /**
   * Creates a new gravitational acceleration model using this gravity model.
   * Thread safety should be OK since this class is to remain immutable.
   *
   * @return   New gravitational acceleration model using a pointer to this
   *           gravity model.
   */
  @Override
  public GravitationalAcceleration getGravityModel() {
    return new GravitationalAcceleration(this);
  }

  /**
   * Computes the gravitational acceleration given inputs formatted from public
   * gravt methods.  Results stored in accel variable.
   *
   * @param   degree   The degree and order of this model.
   * @param   r        Distance from the centroid
   * @param   slat     Sine latitude
   * @param   clat     Cosine latitude
   * @param   lon      Longitude: -pi   <= az  <= pi    (rad)
   * @param   ri       X component of position vector
   * @param   rj       Y component of position vector
   * @param   rk       Z component of position vector
   *
   * @param   accel    Output/computed acceleration vector.
   */
   void gravt(int degree, double r, double slat, double clat, double lon,
                                         double ri, double rj, double rk,
                                                      Acceleration accel) {
    int ll, mm;               // Coefficient indices
    double reOverR = re/r;    // Earth radius of gravity model divided by r
    double gmOverR = gm/r;    // GM / r

      // Constants over all summations (ll & mm):
    double reOverR_ll = reOverR;
    double tanLat = slat/clat;
      // Constants for inner summation (just mm):
    double mlam, cmlam, smlam, alfSinX_ll_mm;

      // Partials of potential function.
    double dudr   = 0.0;
    double dudlat = 0.0;
    double dudlon = 0.0;
      // Summations per degree
    double dudr_ll;
    double dudlat_ll;
    double dudlon_ll;

      // Temp variables used in the computation of Cartesian acceleration.
    double dudrOverR, r2, ri2rj2, rirj, dlatTerm, dlonTerm;

    if (degree > degreeOrder) {
      degree = degreeOrder;
    }   

      // Set associated Legendre Functions for this latitude
    aP.set(slat, clat);

      // Calculate partials of potential w.r.t. r, lat, lon.
    for (ll=2; ll<=degree; ll++) {
        // Initialize summation over order indices
      dudr_ll   = 0.0;
      dudlat_ll = 0.0;
      dudlon_ll = 0.0;
      for (mm=0; mm<=ll; mm++) {
        mlam = lon * (double) mm;
        cmlam = Math.cos(mlam);
        smlam = Math.sin(mlam);
        alfSinX_ll_mm = aP.get(ll, mm);
        dudr_ll += alfSinX_ll_mm * (clm[ll][mm]*cmlam + slm[ll][mm]*smlam);
        dudlat_ll += (aP.get(ll,mm+1) - mm*tanLat*alfSinX_ll_mm) *
                                       (clm[ll][mm]*cmlam + slm[ll][mm]*smlam);
        dudlon_ll += mm*alfSinX_ll_mm * (slm[ll][mm]*cmlam - clm[ll][mm]*smlam);
      }
      reOverR_ll *= reOverR;    // Math.pow(reOverR, ll)
      dudr_ll   *= reOverR_ll * (double) (ll+1);
      dudlat_ll *= reOverR_ll;
      dudlon_ll *= reOverR_ll;
      dudr   += dudr_ll;
      dudlat += dudlat_ll;
      dudlon += dudlon_ll;
    }
    dudr    = (-gmOverR/r)*(1.0 + dudr);    // spherical gravity + deviations
    dudlat *=  gmOverR;
    dudlon *=  gmOverR;

      // Temp variables to save computations and make the final equations
      // a bit more manageable.
    r2        = r*r;
    dudrOverR = dudr/r;
    ri2rj2    = ri*ri + rj*rj;
    rirj      = Math.sqrt(ri2rj2);
    dlatTerm  = dudrOverR - rk*dudlat/(r2*rirj);
    dlonTerm  = dudlon/ri2rj2;

      // Compute acceleration combining both sets of partials
    double ai = dlatTerm*ri  - dlonTerm*rj;
    double aj = dlatTerm*rj  + dlonTerm*ri;
    double ak = dudrOverR*rk + dudlat*rirj/r2;

    // Set final accelerations for output.
   accel.put(Basis3D.I, ai);
   accel.put(Basis3D.J, aj);
   accel.put(Basis3D.K, ak);
  }
}
