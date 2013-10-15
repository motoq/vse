/*
 c  KaulaNorm.java
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
 * This class computes Kaula spherical harmonic normalization factors
 * and stores them for retrieval.  The values stored will unnormalize
 * spherical harmonic coefficients and normalize associated Legendre
 * polynomials through multiplication.
 * <P>
 * Clm = Nlm * Clm_normalized
 * Slm = Nlm * Slm_normalized
 * Plm_normalized = Nlm * Plm
 * <P>
 * Nlm = sqrt( ((l - m)!(2l + 1)k) / (l + m)! ), k = (m==0) ? 1 : 2
 * <P>
 * This fits in with typical use - spherical harmonics are typically
 * published in normalized format.  Multiplication of these terms will
 * result in the unnormalized form.  Associated Legendre functions are computed
 * as unnormalized, and may require normalization if they are used with
 * normalized coefficients.
 * <P>
 * A direct implementation is currently employed - a recursive one allowing
 * for higher degree/order may be employed in the future (limits on double
 * representation of the factorial fuction).
 *
 * @author   Kurt Motekew
 * @since    20120211
 */
public class KaulaNorm {
  /** Maximum allowable degree & order */
  public static final int MAX_NORMALIZATION = FactorialD.MAXN/2;

  private FactorialD fact = null;
  private final int degOrder;
  private double[][] nvals;

  /**
   * @param   nmax   Maximum degree and order for which the normalization
   *                 factors will be computed.  MAX_NORMALIZATION is the
   *                 largest acceptable value.  For values less than 0 and
   *                 greater than MAX_NORMALIZATION, MAX_NORMALIZATION will
   *                 be used.
   */
  public KaulaNorm(int nmax) {
    if (nmax < 0  ||  nmax > MAX_NORMALIZATION) {
      degOrder = MAX_NORMALIZATION;
    } else {
      degOrder = nmax;
    }
    fact = new FactorialD(2*degOrder);

    double k, fnum, fden;
    nvals = new double[degOrder+1][degOrder+1];
    for (int ll=0; ll<=degOrder; ll++) {
      for (int mm=0; mm<=ll; mm++) {
        k = (mm == 0) ? 1 : 2;
        fnum = fact.get(ll - mm);
        fden = fact.get(ll + mm);
        nvals[ll][mm] = Math.sqrt((fnum*(2*ll + 1.)*k)/fden);
      }
    }
  }

  /**
   * @return    The degree and order supported by this instantiation of
   *            this class.
   */
  public int getDegreeOrder() {
    return degOrder;
  }

  /**
   * @param   ll    Requested order.  If greater than indicated by
   *                getDegreeOrder, or less than 0, returns 0.
   * @param   mm    Requested degree.  If greater than indicated by
   *                getDegreeOrder, greater than the degree,  or
                    less than 0, returns 0.
   *
   * @return        Kaula normalization factor for requested degree and order.
   */
  public double get(int ll, int mm) {
    if (ll < 0  ||  ll > MAX_NORMALIZATION  ||  mm < 0  ||  mm > ll) {
      return 0.;
    } else {
      return nvals[ll][mm];
    }
  }

}
