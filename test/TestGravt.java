package com.motekew.vse.test;

import com.motekew.vse.envrm.*;
import com.motekew.vse.trmtm.Acceleration;
import com.motekew.vse.math.*;

/**
 * This program does a quick sanity check for the gravity model using
 * outputs from a trusted application's 4x4 gravity model.
 * <PRE>
 * The expected outputs for 
 * lat = 50 & lon = -100 are:
 * ax:   0.000273974088
 * ay:   0.001553775992
 * az:  -0.001882985691
 * and for lat = -50 & lon = 100:
 * ax:   0.000273975775
 * ay:  -0.001553773172
 * az:   0.001882993210
 */
public class TestGravt {
    // deg = order = 4
  private static final int NGC = 5;
  
  public static void main(String[] args) {

    Matrix clM = new Matrix(NGC, NGC);
    Matrix slM = new Matrix(NGC, NGC);
      // Set J2: l = 2, m = 0
    clM.put(2+1, 0+1, -1.08262668355e-3);     // deg = 2, order = 0, -J2
    clM.put(2+1, 1+1, -2.41400000000e-10);    // deg = 2, order = 1
    clM.put(2+1, 2+1,  1.57446037456e-6);     // deg = 2, order = 2
      //
    clM.put(3+1, 0+1,  2.53265648533e-6);     // deg = 3, order = 0, -J3
    clM.put(3+1, 1+1,  2.19263852917e-6);     // deg = 3, order = 1
    clM.put(3+1, 2+1,  3.08989206881e-7);     // deg = 3, order = 2
    clM.put(3+1, 3+1,  1.00548778064e-7);     // deg = 3, order = 3
      //
    clM.put(4+1, 0+1,  1.61962159137e-6);     // deg = 4, order = 0, -J4
    clM.put(4+1, 1+1, -5.08799360404e-7);     // deg = 4, order = 1
    clM.put(4+1, 2+1,  7.84175859844e-8);     // deg = 4, order = 2
    clM.put(4+1, 3+1,  5.92099402629e-8);     // deg = 4, order = 3
    clM.put(4+1, 4+1, -3.98407411766e-9);     // deg = 4, order = 4
      //
    // Set J2: l = 2, m = 0
    slM.put(2+1, 1+1,  1.54310000000e-9);     // deg = 2, order = 1
    slM.put(2+1, 2+1, -9.03803806639e-7);     // deg = 2, order = 2
      //
    slM.put(3+1, 1+1,  2.68424890397e-7);     // deg = 3, order = 1
    slM.put(3+1, 2+1, -2.11437612437e-7);     // deg = 3, order = 2
    slM.put(3+1, 3+1,  1.97222559006e-7);     // deg = 3, order = 3
      //
    slM.put(4+1, 1+1, -4.49144872839e-7);     // deg = 4, order = 1
    slM.put(4+1, 2+1,  1.48177868296e-7);     // deg = 4, order = 2
    slM.put(4+1, 3+1, -1.20077667634e-8);     // deg = 4, order = 3
    slM.put(4+1, 4+1,  6.52571425370e-9);     // deg = 4, order = 4

    /*
     * Earth coefficients
     */
 
      // Units of ER and ER^3/min^2
    double gm = (3600.*398600.4418/(6378.137*6378.137*6378.137));
    SphericalHarmonicCoeff shc = new SphericalHarmonicCoeff(false, clM.values(), slM.values());
    Gravity geo = new Gravity(gm, 1.0, shc);
    
    SphereCart pos = new SphereCart();
    double r  = 1.5;
    Acceleration accel = new Acceleration();
    double lat;
    double lon;
    
    System.out.println("GM:  " + gm);
    for (int ii=0; ii<5; ii++) {
      System.out.println("--- Deg/Order = " + ii + " ---");
      lat = Math.toRadians(50.0);
      lon = Math.toRadians(-100.0);
      geo.gravt(ii, r, lat, lon);
      accel.set(geo);
      System.out.println("Given r lat lon, then pos");
      System.out.println("Lat = "   + Math.toDegrees(lat) + 
                       "\tLon = "   + Math.toDegrees(lon) +
                       "\tAccel:  " + accel);
      pos.setRElAz(r, lat, lon);
      geo.gravt(ii, pos);
      accel.set(geo);
      System.out.println("Lat = "   + Math.toDegrees(lat) + 
                       "\tLon = "   + Math.toDegrees(lon) +
                       "\tAccel:  " + accel);      
        //
      lat = Math.toRadians(-50.0);
      lon = Math.toRadians(100.0);
      geo.gravt(ii, r, lat, lon);
      accel.set(geo);
      System.out.println("Given r lat lon, then pos");
      System.out.println("Lat = "   + Math.toDegrees(lat) + 
                       "\tLon = "   + Math.toDegrees(lon) +
                       "\tAccel:  " + accel);
      pos.setRElAz(r, lat, lon);
      geo.gravt(ii, pos);
      accel.set(geo);
      System.out.println("Lat = "   + Math.toDegrees(lat) + 
                       "\tLon = "   + Math.toDegrees(lon) +
                       "\tAccel:  " + accel);
    }

  }
}

