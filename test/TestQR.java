package com.motekew.vse.test;

import com.motekew.vse.math.Matrix;
import com.motekew.vse.math.QR;
import com.motekew.vse.math.Tuple;

public class TestQR {

  /**
   * Tests QR decomposition as implemented by the Matrix class.  See
   * comments in code for expected values.
   */
  public static void main(String[] args) {
    double[][] aArray = { { 1.0, -1.0,  4.0 },
                          { 1.0,  4.0, -2.0 },
                          { 1.0,  4.0,  2.0 },
                          { 1.0, -1.0,  0.0 }};
    /*
     *         Q                R
     *  0.5  -0.5   0.5      2  3  2
     *  0.5   0.5  -0.5      0  5 -2
     *  0.5   0.5   0.5      0  0  4
     *  0.5  -0.5  -0.5
     */
    Matrix aMat = new Matrix(aArray);
    QR qr = new QR(aMat);
    System.out.println("=====================================================================");
    System.out.println("A:\n" + aMat);
    System.out.println("Q:\n" + qr.q);
    System.out.println("R:\n" + qr.r);
    aMat.mult(qr.q, qr.r);
    System.out.println("QR:\n" + aMat);
    checkOrtho(qr.q);
    
    
      // Another test
    double[][] aArray2 = { { 4.0, -6.0,  5.0 },
                           {-2.0,  3.0,  0.0 },
                           { 1.0, -7.0, -9.0 },
                           { 8.0,  0.0,  1.0 }};
    // From Octave:
    //        A =
    //        4  -6   5
    //       -2   3   0
    //        1  -7  -9
    //        8   0   1
    //
    //   Q =
    //  -0.433861   0.482544   0.707421
    //   0.216930  -0.241272  -0.075738
    //  -0.108465   0.743812  -0.638820
    //  -0.867722  -0.394567  -0.292793
    //
    //   R =
    //
    //  -9.21954   4.01321  -2.06084
    //   0.00000  -8.82576  -4.67615
    //   0.00000   0.00000   8.99370

    aMat = new Matrix(aArray2);
    qr.set(aMat);
    System.out.println("=====================================================================");
    System.out.println("A:\n" + aMat);
    System.out.println("Q:\n" + qr.q);
    System.out.println("R:\n" + qr.r);
    aMat.mult(qr.q,qr.r);
    System.out.println("QR:\n" + aMat);
    checkOrtho(qr.q);
  }
  
  private static void checkOrtho(Matrix q) {
    int m = q.numRows();
    int n = q.numCols();
    // Quick orthonormal check
    Tuple[] qCols = new Tuple[n];
    for (int ii=0; ii<n; ii++) {
      qCols[ii] = new Tuple(m);
      q.getColumn(ii+1, qCols[ii]);
    }
    for (int ii=0; ii<n; ii++) {
      for (int jj=0; jj<n; jj++) {
        if (ii < jj) {
          System.out.println("Q Col " + ii + " dot Q col " + jj + ":   " + 
                                                  qCols[ii].dot(qCols[jj]));
        }
      }
    }
  }

}
