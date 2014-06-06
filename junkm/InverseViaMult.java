/*
 c  InverseViaMult.java
 c
 c  Copyright (C) 2014 Kurt Motekew
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

package com.motekew.vse.junkm;

import com.motekew.vse.servm.ConsoleIO;

/**
 * This class inverts a floating point number without division.  Instead
 * it employs Newton's method.
 */
public class InverseViaMult {
  private static final int MAXITR = 100;
  private static final double TOL = 1.0e10*Double.MIN_VALUE;

  private int nitr = 0;
  private double a = 0.0;

  /**
   * @iparam   den   Value to be inverted.
   */
  public InverseViaMult(double den) {
    int ii;
    double a0 = 0.01;

      // Exit and set to invalid if the denominator is zero
    if (den == 0.0) {
      nitr = MAXITR;
      return;
    }
 
      // If the denominator is negative, start with a negative guess
    if (den < 0.0) {
      a0 *= -1.0;
    }

    for (ii=0; ii<MAXITR; ii++) {
      a = a0*(2.0 - den*a0);
      if (Math.abs(a - a0) < TOL) {
        break;
      }
      a0 = a;
    }
    nitr = ii;
  }

  /** @return   Inverted value */
  public double inverse() { return a; };

  /** @return   Number of iterations require to invert */
  public int iterations() { return nitr; }

  /** @return   If valid, the inversion was sucessful */
  public boolean valid() { return !(nitr == MAXITR); }

  /** @return   Tolerance used for convergence */
  public double tolerance() { return TOL; }

  /**
   * Prompts the user for values to invert.  Enter a zero to quit.
   */
  public static void main(String[] args) {
    ConsoleIO cio = new ConsoleIO();
    double den;;
    
    while ((den = cio.readDouble("Enter a number to invert (0 to exit):  ")) != 0.0) {
      InverseViaMult inverter = new InverseViaMult(den);
      System.out.printf("\nInverse of %f is %f", den, 1.0/den);
      System.out.printf("\nInverse computed by multiplication is %f",
                       inverter.inverse());
      System.out.println(" in " + inverter.iterations() + " iterations with" +
                         " a success flag of " + inverter.valid() + "\n");
    }
    System.out.println("\nDone\n");
  }
}
