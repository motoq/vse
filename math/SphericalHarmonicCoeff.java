/*
 c  SphericalHarmonicCoeff.java
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
 * This class manages a set of spherical harmonic coefficients.
 * The current implementation supports square models (degree = order).
 * Unnormalized or Normalized values are supported.  The intent of this
 * class is to manage, transmit, and format the coefficients as required
 * by a given model.  Once in the desired form, the coefficients should
 * be output and used in array form.
 *
 * @author   Kurt Motekew
 * @since    20120211
 */
public class SphericalHarmonicCoeff {
  public static final int MAX_NORMALIZATION = KaulaNorm.MAX_NORMALIZATION;
  
  private static final int SIN = 0;
  private static final int COS = 1;

  private boolean normalized = false;     // Form of locally stored coeffs
  private boolean normalizedOut = false;  // Form to output coeffs
  private boolean converting = false;     // Inidcates if output is converted
  private double[][] clm = null;          // pointer to cos coeff
  private double[][] slm = null;          // pointer to sin coeff
  private int degreeOrder;

  private KaulaNorm nlmK;

  /**
   * Initialize with spherical harmonic coefficients.  See
   * <code>KaulaNorm</code> for limitations on degree and order if
   * conversions between normalized and unnormalized coefficients are
   * necessary.
   * <P>
   * The default output format is the one used here to initialize the
   * coefficients.
   * 
   * @param   areNormalized  True indicates input coefficients are Kaula
   *                         normalized.
   * @param   clm_in         COS coefficients.  Doesn't copy elements - points
   *                         to this array.
   * @param   slm_in         SIN coefficients.  Doesn't copy.
   */
  public SphericalHarmonicCoeff(boolean areNormalized,
                                double[][] clm_in, double[][] slm_in) {
    normalized = areNormalized;
    normalizedOut = normalized;
    clm = clm_in;
    slm = slm_in;
    degreeOrder = clm.length - 1;    // Degree numbers also start at zero
    nlmK = new KaulaNorm(2*degreeOrder);
  }

  /**
   * @return    If the spherical coefficients were originally set and stored
   *            in normalized form, return true.  If they are stored in
   *            unnormalized format, false.  Lets the user know what format
   *            coefficients were originally loaded in.
   */
  public boolean normalizedStorage() {
    return normalized;
  }

  /**
   * @return    true, if converting from interanlly stored format for
   *            output option.  Will be false if output option is the same
   *            as the format used for internal storage.
   */
  public boolean needToConvert() {
    return converting;
  }

  /** 
   * @param   newNormOutOpt   If true, set to normalized output.  If false,
   *                          set to unnormalized output.
   */
  public void setNormalizedOutput(boolean newNormOutOpt) {
    normalizedOut = newNormOutOpt;

    converting = !(normalized == normalizedOut);
  }

  /**
   * @return    2D square array of Cosine coefficients.  If converting,
   *            the max size is dictated by MAX_NORMALIZATION;
   */
  public double[][] cValues() {
    return cs(COS);
  }

  /**
   * @return    2D square array of Sine coefficients.  If converting,
   *            the max size is dictated by MAX_NORMALIZATION;
   */
  public double[][] sValues() {
    return cs(SIN);
  }

  /*
   * @param     sinOrCos   Indicates which coefficients to return,
   *                       SphericalHarmonicCoeff.SIN or .COS
   *
   * @return               2D square array of Sine or Cosine coefficients.
   */
  private double[][] cs(int sinOrCos) {
    double[][] vals;

      // Point to the set to be returned
    if (sinOrCos == SIN) {
      vals = slm;
    } else {
      vals = clm;
    }

    int rsize = degreeOrder;
      // Limit returned values if normalization factor can't be computed
    if (converting  &&  degreeOrder > MAX_NORMALIZATION) {
      rsize = MAX_NORMALIZATION;
    }

      // Allocate and fill with coefficients
    double[][] rvals = new double[rsize+1][rsize+1];
    for (int ll=0; ll<=rsize; ll++) {
      for (int mm=0; mm<=ll; mm++) {
        if (converting) {
          if (normalized) {
              // unnormalize
            rvals[ll][mm] = nlmK.get(ll,mm)*vals[ll][mm];
          } else {
              // normalize
            rvals[ll][mm] = vals[ll][mm] / nlmK.get(ll,mm);
          }
        } else {
          rvals[ll][mm] = vals[ll][mm];
        }
      }
    }
    return rvals;
  }

}
