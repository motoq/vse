/*
 c  KeplerianOE.java
 c
 c  Copyright (C) 2000, 2011 Kurt Motekew
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
import com.motekew.vse.enums.Keplerian;
import com.motekew.vse.math.Quaternion;
import com.motekew.vse.math.Tuple;
import com.motekew.vse.math.Tuple3D;

/**
 * This class represents a set of the classic orbital elements:
 * Semi-major axis, eccentricity, inclination right ascension of
 * the ascending node, argument of perigee, and true anomaly.
 *
 * @author  Kurt Motekew
 * @since   20110222
 */
public class KeplerianOE extends Tuple {

  private static final Tuple3D ihat = new Tuple3D(1.0, 0.0, 0.0);
  private static final Tuple3D khat = new Tuple3D(0.0, 0.0, 1.0);
  private static final double TWOPI = com.motekew.vse.math.Angles.TPI;
    // Tolerance for magnitude of vectors ending up in denominators
  private static final double TOL = 0.000001;

  private double gm = 1.0;

    // Used to see if labels for this extension of the Tuple have
    // been initialized.  Don't bother wasting the memory setting
    // unless the names have been asked for via getLabels();
  private boolean needToInitLabels = true;

    // Eases indexing.
  private static final Keplerian A = Keplerian.A;  // Semi-major axis
  private static final Keplerian E = Keplerian.E;  // Eccentricity
  private static final Keplerian I = Keplerian.I;  // Inclination
  private static final Keplerian O = Keplerian.O;  // RAAN
  private static final Keplerian W = Keplerian.W;  // Argument of perigee
  private static final Keplerian V = Keplerian.V;  // True Anomaly

  /**
   * Create orbit state in Keplerian element set format using defaults.
   */
  public KeplerianOE() {
    super(Keplerian.values().length);
    put(A, 1.0);
  }

  /**
   * @param   gmin    Gravitational Parameter, equal to the gravity constant
   *                  times the central body mass.  Units are distance^3/time^2.
   * @param   a       Semi-major axis, units consistent with gmin & rgimin
   * @param   e       Eccentricity
   * @param   i       Inclination, radians
   * @param   node    Right ascension of the ascending node, radians
   * @param   w       Argument of Periapsis, radians
   * @param   nu      True anomaly, radians
   */
  public KeplerianOE(double gmin, double a, double e,
                     double i, double node, double w, double nu) {
    this();
    gm = gmin;
    put(A, a);
    put(E, e);
    put(I, i);
    put(O, node);
    put(W, w);
    put(V, nu);
  }

  /**
   * @return    Gravitational Parameter value relating position and velocity
   *            to the amount of energy in the orbit.  distance^3/time^2
   */
  public double getGM() {
    return gm;                                                         
  }

  /**
   * Sets the internal gravitational parameter value.
   * 
   * @param    gmin    New value for GM.  distance^3/time^2
   */
  public void setGM(double gmin) {
    gm = gmin;
  }

  /**
   * Sets the value of the KeplerianOE given a <code>Keplerian</code>
   * enum index.
   *
   * @param  ndx   A Keplerian index for the row
   * @param  val   A double value to be set for the KeplerianOE.  All
   *               values are in radians, other than eccentricity.
   */
  public void put(Keplerian ndx, double val) {
    put(ndx.ordinal()+1, val);
  }

  /**
   * Gets the value of the KeplerianOE given a <code>Keplerian</code>
   * enum index.
   *
   * @param  ndx   A Keplerian index for the row
   *
   * @return       A double value for the requested element (radians,
   *               unless eccentricity).
   */
  public double get(Keplerian ndx) {
    return get(ndx.ordinal()+1);
  }

  /**
   * @return        Mean Anomaly, radians
   */
  public double getMeanAnomaly() {
    double ea = getEccentricAnomaly();
 
    return  ea - get(E)*Math.sin(ea);
  }

  /**
   * @return        Eccentric Anomaly, radians
   */
  public double getEccentricAnomaly() {
    double e  = get(E);
    double nu = get(V);

    double one_plus_ecosv = 1.0/(1.0 + e*Math.cos(nu));
    double sinE = Math.sin(nu)*Math.sqrt(1.0 - e*e)*one_plus_ecosv;
    double cosE = (e + Math.cos(nu))*one_plus_ecosv;
    return Math.atan2(sinE, cosE);
  }

  /**
   * Sets the (osculating) orbital element values given a gravitational
   * parameter value.  Position and velocity units should be consistent
   * with gm.  Values are converted to canonical ones internally to mitigate
   * potential numerical issues.
   *
   * @param  gmin    Gravitational Parameter, equal to the gravity constant
   *                 times the central body mass.  Units are distance^2/time^2.
   * @param  rgm     Scaling parameter associated with gmin.
   * @param  rin     Position, distance units
   * @param  vin     Velocity, distance/time units
   */
  public void set(double gmin, double rgm, Tuple3D rin, Tuple3D vin) {
    gm = gmin;

    c2kC.r.set(rin);
    c2kC.v.set(vin);
    if (gm != 1.0  ||  rgm != 1.0) {
      double tmp = Math.sqrt(rgm/gm);
      c2kC.r.mult(1.0/rgm);             // Divide to get distance units
      c2kC.v.mult(tmp);                 // Cancellation leads to simple
    }                                   // velocity (DU/TU) conversion

    double rmag = c2kC.r.mag();
    double vmag = c2kC.v.mag();

    double energy = vmag*vmag/2 - 1.0/rmag;

      // Eccentricity
    double tmp = vmag*vmag - 1.0/rmag;
    c2kC.h.set(c2kC.r);    // Use h & n as temporary space until set for real use;
    c2kC.h.mult(tmp);
    double rdotv = c2kC.r.dot(c2kC.v);
    c2kC.n.set(c2kC.v);
    c2kC.n.mult(rdotv);
    c2kC.e.minus(c2kC.h, c2kC.n);
    double emag = c2kC.e.mag();
    put(E, emag);

      // Maybe set a flag to use semilatus rectum in place of semi-major?
      // Convert from DU to units compatible with stored gm & rgm
    if (Math.abs(emag - 1.0) > TOL) {
      put(A, -0.5*rgm/energy);
    } else {
      put(A, rgm*c2kC.h.dot(c2kC.h));
    }
    
    // Angular momentum
    c2kC.h.cross(c2kC.r,c2kC.v);
    double hmag = c2kC.h.mag();
      // node line
    c2kC.n.cross(khat, c2kC.h);
    double nmag = c2kC.n.mag(); 

      // Inclination.  No further checking needed
      // i = acos( khat*h / (kmag*hmag) );
    put(I, Math.acos(c2kC.h.get(Basis3D.K)/hmag));

      // RAAN
      // o = acos(  ihat*n / (imag*nmag) );
    if (nmag < TOL) {
      put(O, 0.0);
    } else {
      put(O, Math.acos(c2kC.n.get(Basis3D.I)/nmag));
      if (c2kC.n.get(Basis3D.J) < 0.0) {
        put(O, TWOPI-get(O));
      }
    }

      // Argument of Perigee - set to zero if perfectly circular or
      // equatorial.
    if (emag < TOL  ||  nmag < TOL) {
      if (emag < TOL) {
        put(W, 0.0);
      } else {
        put(W, Math.acos(c2kC.e.get(Basis3D.I)/emag));
        if (c2kC.e.get(Basis3D.J) < 0.0) {
          put(W, TWOPI-get(W));
        }
      }
    } else {
      put(W, Math.acos(c2kC.n.dot(c2kC.e)/(nmag*emag)));
      if (c2kC.e.get(Basis3D.K) < 0.0) {
        put(W, TWOPI-get(W));
      }
    }

      // True Anomaly
    if (emag < TOL) {
      put(V, get(W));
    } else {
      put(V, Math.acos(c2kC.e.dot(c2kC.r)/(emag*rmag)));
      if (rdotv < 0.0) {
        put(V, TWOPI-get(V));
      }
    }
  
  }

    // set() Cache - Cartesian to OE
  private class Cart2KepCache {
    Tuple3D r    = new Tuple3D();
    Tuple3D v    = new Tuple3D();
    Tuple3D h    = new Tuple3D();
    Tuple3D n    = new Tuple3D();
    Tuple3D e    = new Tuple3D();
  }
  private Cart2KepCache c2kC = new Cart2KepCache();

  /**
   * Sets the semi-major axis and eccentricity given the periapsis
   * and apoapsis.
   * 
   * @param    periapsis    Periapsis (perigee for earth based) length
   * @param    apoapsis     Apoapsis (apogee for earth based) length
   */
  public void aeGivenPeriApo(double periapsis, double apoapsis) {
    put(A, (apoapsis+periapsis)/2.0);
    put(E, (apoapsis-periapsis)/(apoapsis+periapsis));
  }

  /**
   * Generates inertial position and velocity state vectors vectors derived
   * from orbital elements.  Units are consistent with the time and
   * distance units of the Gravitational Parameter returned by getGM().
   * <P>
   * Note:  Position and velocity will be in the same inertial reference
   *        frame as the internal Keplerian elements.
   * 
   *
   * @param  r       Output Cartesian Position, distance units
   * @param  v       Output Velocity, distance/time units
   *
   */
  public void getRV(Tuple3D r, Tuple3D v) {
      // Intialize temporary storage
    if (k2cC == null) {
      k2cC = new Kep2CartCache();
        k2cC.r_pqw = new Tuple3D();
        k2cC.v_pqw = new Tuple3D();
        k2cC.q   = new Quaternion();
        k2cC.q2  = new Quaternion();
        k2cC.q12 = new Quaternion();
    }

    double e = get(E);
    double p = get(A)*(1.0 - e*e);
    double nu = get(V);
    double cosv = Math.cos(nu);
    double sinv = Math.sin(nu);
    double ecosv_p1 = 1.0 + e*cosv;

      // Compute position in PQW
    k2cC.r_pqw.put(Basis3D.I, p*cosv/ecosv_p1); 
    k2cC.r_pqw.put(Basis3D.J, p*sinv/ecosv_p1); 
    k2cC.r_pqw.put(Basis3D.K, 0.0); 
      // Compute velocity in PQW
    double coef = Math.sqrt(gm/p);
    k2cC.v_pqw.put(Basis3D.I, -coef*sinv); 
    k2cC.v_pqw.put(Basis3D.J,  coef*(get(E) + cosv)); 
    k2cC.v_pqw.put(Basis3D.K, 0.0); 

      // Rotate to ECI.  First, back out argument of perigee, about
      // z-axis moving down to X-Y plane.  Next, level out along
      // X-Y plane by backing out inclination along the x-axis.
      // Finally, back out the RAAN about the z-axis  
    k2cC.q.set(-get(W), khat);
    k2cC.q2.set(-get(I), ihat);
    k2cC.q12.mult(k2cC.q, k2cC.q2);
    k2cC.q2.set(-get(O), khat);
    k2cC.q.mult(k2cC.q12, k2cC.q2);
    r.fRot(k2cC.q, k2cC.r_pqw);
    v.fRot(k2cC.q, k2cC.v_pqw);
  }

    //  getRV() Cache - Keplerian to pos & vel vectors
  private class Kep2CartCache {
    Tuple3D r_pqw = null;
    Tuple3D v_pqw = null;
    Quaternion q, q2, q12;
  }
  private Kep2CartCache k2cC = null;

  /**
   * Returns an array of String labels for each element of this KeplerianOE
   * Tuple3D.
   *
   * @return         A new arrray of String labels - not the pointer to the
   *                 internally stored labels.                             
   */
  @Override
  public String[] getLabels() {
    if (needToInitLabels) {
      needToInitLabels = false;
      String[] names = new String[Keplerian.values().length];
      for (Keplerian ii : Keplerian.values()) {
        names[ii.ordinal()] = ii.toString();
      }
      super.setLabels(names);
      return names;
    } else {
      return super.getLabels();
    }
  }

  /**
   * Sets an array of labels that are to be associated with this KeplerianOE
   * Tuple3D.
   *                     
   * @param  lbl[]   An array of labels that are to be copied into to this
   *                 KeplerianOE object.
   */
  @Override
  public void setLabels(String[] lbls) {
    needToInitLabels = false;
    super.setLabels(lbls);
  }

}
