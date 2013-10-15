package com.motekew.vse.test;

import com.motekew.vse.math.Matrix;

public class TestMatrixInv {

  /**
   * Basic testing of Matrix inverse - reference answers generated with
   * Octave.
   */
  public static void main(String[] args) {
    /*
     * Octave/Matlab inputs:
     * 
       mtx = [ [ 0.698324,   0.974878,   0.059827,   0.751897 ]
               [ 0.572738,   0.255445,   0.164917,   0.779979 ]
               [0.904493,   0.624874,   0.940801,   0.770569  ]
               [0.459168,   0.686534,   0.286250,   0.358937  ] ]
       mtx^-1
       ans =

                58.9373  -45.3810   33.9299  -97.6882
               -13.0925    9.6688   -8.1148   23.8363
               -19.3899   13.8574   -9.6005   31.1159
               -34.8900   28.5088  -20.2272   57.3468
               
       det(mtx) 
       ans = -0.0048203
       
       
        m1 = [ 0.19528,   0.40406,   0.43793 ;
               0.62381,   0.22026,   0.52130 ;
               0.19898,   0.45789,   0.32259   ]
        det(m1)
        ans =  0.033759
        m1^-1
        ans =
              -4.96587   2.07878   3.38212
              -2.88829  -0.71518   5.07669
               7.16273  -0.26709  -6.19220
               
               
         m2 = [ 0.105692,   0.120879,   0.700778 ;
                0.975246,   0.216512,   0.421010 ;
                0.089626,   0.605712,   0.960162  ]
         det(m2)
         ans =  0.28675
         m2^-1
         ans =
         -0.16434   1.07551  -0.35165
         -3.13391   0.13487   2.22816
          1.99235  -0.18547  -0.33131
          
           m3 = [ 1 2 ;
                  3 4  ]
           m3^-1
           ans =
           -2.00000   1.00000
            1.50000  -0.50000

     *
     */
    double[][] mtx = { { 0.698324,   0.974878,   0.059827,   0.751897 },
                       { 0.572738,   0.255445,   0.164917,   0.779979 },
                       { 0.904493,   0.624874,   0.940801,   0.770569 },
                       { 0.459168,   0.686534,   0.286250,   0.358937 }};
    
    Matrix matGauss = new Matrix(mtx);
    System.out.println("Original Matrix");
    System.out.println("" + matGauss);
    double det = matGauss.det();
    System.out.println("Determinant:  " + det);
    System.out.println("After Determinant");
    System.out.println("" + matGauss);

    Matrix matLU = new Matrix(mtx);
    System.out.println("\nOriginal Matrix");
    System.out.println("" + matLU);
    matLU.croutLU();
    System.out.println("Crout LU");
    System.out.println("" + matLU);
    /*
    Tuple ytup = new Tuple(new double[] {0., 1., 0., 0.});
    Tuple xtup = new Tuple(ytup.length());
    matLU.solve(ytup, xtup);
    System.out.println("Measurements");
    System.out.println("" + ytup);
    System.out.println("Solve For");
    System.out.println("" + xtup);
    */
    
    Matrix mat = new Matrix(mtx);
    Matrix matinv = new Matrix(mtx);
    Matrix matprod = new Matrix(mat.numRows(), mat.numCols());
    System.out.println("\nOriginal Matrix");
    System.out.println("" + mat);
    matinv.invert();
    System.out.println("Matrix Inverse");
    System.out.println("" + matinv);
    matprod.mult(mat, matinv);
    System.out.println("Product of Original and Inverse");
    System.out.println("" + matprod);
    
    double[][] mtx2 = { { 2.0 } };
    mat = new Matrix(mtx2);
    System.out.println("\nOriginal Matrix");
    System.out.println("" + mat);
    mat.invert();
    System.out.println("Matrix Inverse");
    System.out.println("" + mat);
    
    mat = new Matrix(1,1);
    mat.put(1,1,2.0);
    System.out.println("\nOriginal Matrix");
    System.out.println("" + mat);
    mat.invert();
    System.out.println("Matrix Inverse");
    System.out.println("" + mat);
       
      // Overall functionality tested Start testing other matrices
    double [][] m1 = { { 0.19528,   0.40406,   0.43793, },
                       { 0.62381,   0.22026,   0.52130, },
                       { 0.19898,   0.45789,   0.32259  } };
    mat = new Matrix(m1);
    System.out.println("\nOriginal Matrix");
    System.out.println("" + mat);
    System.out.println("Matrix Determinant:  " + mat.det());
    mat.invert();
    System.out.println("Matrix Inverse");
    System.out.println("" + mat);
    
      //
    double [][] m2 = { { 0.105692,   0.120879,   0.700778, },
                       { 0.975246,   0.216512,   0.421010, },
                       { 0.089626,   0.605712,   0.960162, } };
    mat.set(m2);
    System.out.println("\nOriginal Matrix");
    System.out.println("" + mat);
    System.out.println("Matrix Determinant:  " + mat.det());
    mat.invert();
    System.out.println("Matrix Inverse");
    System.out.println("" + mat);
    
    double[][] m3 = { { 1., 2. },
                      { 3., 4. } };
    mat = new Matrix(m3);
    System.out.println("\nOriginal Matrix");
    System.out.println("" + mat);
    System.out.println("Matrix Determinant:  " + mat.det());
    mat.invert();
    System.out.println("Matrix Inverse");
    System.out.println("" + mat);
  }

}
