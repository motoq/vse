package com.motekew.vse.test;

import com.motekew.vse.enums.Decomposition;
import com.motekew.vse.math.*;

public class TestCholesky {

  /**
   * 
   * Spot checks Cholesky Decomposition and solving systems of linear
   * equations using:  Cholesky, Crout, QR
   *

    A =
     6    15    55
    15    55   225
    55   225   979
    
    chol(A)'
    ans =
    2.449489742783178    0.000000000000000    0.000000000000000
    6.123724356957946    4.183300132670377    0.000000000000000
   22.453655975512469   20.916500663351886    6.110100926607781

    A =
    1.000000000000000    0.000000000000000    1.000000000000000
    1.000000000000000    1.000000000000000    2.718281828459045
    1.000000000000000    2.000000000000000    7.389056098930650
    1.000000000000000    3.000000000000000   20.085536923187664
    1.000000000000000    4.000000000000000   54.598150033144229

    y =
    1
    3
    7
   10
   20
   
   p =
   0.841028017946541
   1.863954808051839
   0.211622507766470

   * 
   * Basic testing of solving a Least Squares problem with
   * Cholesky decomposition
   */
  public static void main(String[] args) {
    System.out.println("\t--- TESTING CHOLESKY ---");
    
    double[][] a = { {  6., 15., 55.  },
                     { 15., 55., 225. },
                     {55., 225., 979. } };
    Matrix ac = new Matrix(a);
    System.out.println("A");
    System.out.println("" + ac);
    ac.cholesky();
    System.out.println("Acholesky");
    System.out.println("" + ac);
    ac.zeroUpper();
    System.out.println("Acholesky zeroUpper()");
    System.out.println("" + ac);
    
    double[] yvals = { 1., 3., 7., 10., 20. };
    double[][] apvals = { { 1., 0., Math.pow(Math.E, 0) },
                          { 1., 1., Math.pow(Math.E, 1) },
                          { 1., 2., Math.pow(Math.E, 2) },
                          { 1., 3., Math.pow(Math.E, 3) },
                          { 1., 4., Math.pow(Math.E, 4) }  };
    
    Tuple yTup = new Tuple(yvals);
    Matrix aMat = new Matrix(apvals);
    
    System.out.println("Y values");
    System.out.println("" + yTup);
    System.out.println("A");
    System.out.println("" + aMat);
    
    System.out.println("\t--- TESTING LLS ---");
    
    yTup = new Tuple(yvals);
    aMat = new Matrix(apvals);
    SysSolver ss = new SysSolver(Decomposition.CHOLESKY, aMat);
    ss.solve(yTup);
    System.out.println("\nUsing the SysSolver with Cholesky");
    System.out.println("" + ss);
    
    ss = new SysSolver(Decomposition.CROUT, aMat);
    ss.solve(yTup);
    System.out.println("\nUsing the SysSolver with Crout");
    System.out.println("" + ss);

    ss = new SysSolver(Decomposition.QR, aMat);
    ss.solve(yTup);
    System.out.println("\nUsing the SysSolver with QR");
    System.out.println("" + ss);
    
    System.out.println("\t--- TESTING WLS ---");
    
    Matrix wm = new Matrix(yTup.length());
    //wm.identity();
    for (int ii=1; ii<=yTup.length(); ii++) {
      double sig = ii*.01;
      wm.put(ii, ii, 1.0/(sig*sig));
    }
    
    SysSolverWeighted wss = new SysSolverWeighted(Decomposition.CHOLESKY, aMat, wm);
    wss.solve(yTup);
    System.out.println("\nUsing the Weighted SysSolver with Cholesky");
    System.out.println("" + wss);
    
    wss = new SysSolverWeighted(Decomposition.CROUT, aMat, wm);
    wss.solve(yTup);
    System.out.println("\nUsing the Weighted SysSolver with Crout");
    System.out.println("" + wss);
    
    wss = new SysSolverWeighted(Decomposition.QR, aMat, wm);
    wss.solve(yTup);
    System.out.println("\nUsing the Weighted SysSolver with QR");
    System.out.println("" + wss);    
  }

}
