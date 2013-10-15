/*
 c  Matrix3X3.java
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

package com.motekew.vse.math;

import com.motekew.vse.enums.Basis3D;

/**
 * <code>Matrix3X3</code> is a <code>Matrix</code> representing
 * a 3X3 square matrix.  Any attempt to access an element of this matrix
 * that is out of range will result in a VectorSpaceIndexOutOfBoundsException
 * being thrown.  Note that indexing starts with 1, not zero.  Indexing
 * can also be made via the <code>Basis3D</code> enum.
 * <P>
 * This is a specialization of the more generic <code>Matrix</code> class.
 * It uses the <Code>Basis3D</code> enum to access members.
 * <P>
 * Many operations are explicitly written out to eliminate loops.  This used
 * to give an order of magnitude increase in speed, but the compiler and
 * run time environment have become smart enough to make this type of
 * optimization unnecessary.  These methods are still here only because
 * there is no reason to remove them....
 *
 * @author Kurt Motekew
 * @since  20071027
 * @since  20130507   Made use of valuesPtr().
 */

public class Matrix3X3 extends Matrix {
  private final double vals[][];
  
  /**
   * Default constructor - just initializes the size of this Matrix3X3
   */
  public Matrix3X3() {
    super(3);                 // 3X3 matrix
    vals = valuesPtr();
  }

  /**
   * Initialize the Matrix3X3 using a 2D array of doubles.
   * The 
   *
   * @param   mtrx    A double[3][3] used to initialize this Matrix3X3
   *                  It should be a block double array, not jagged.
   *                  A <code>VectorSpaceArgumentException</code> will
   *                  be thrown if the dimensions of the array are not
   *                  3X3.
   */
  public Matrix3X3(double[][] mtrx) {
    this();
    this.set(mtrx);
  }

  /**
   * Set to the identity matrix.
   */
  public void identity() {
    zero();
    vals[0][0] = 1.0;
    vals[1][1] = 1.0;
    vals[2][2] = 1.0;
  }

  /**
   * Sets the value of the Matrix3X3 given <code>Basis3D</code>
   * enum indices.
   *
   * @param  row   A Basis3D index for the row
   * @param  col   A Basis3D index for the column
   * @param  val   A double value to be inserted into the (row, col)
   *               element
   */
  public void put(Basis3D row, Basis3D col, double val) {
    vals[row.ordinal()][col.ordinal()] = val;
  }

  /**
   * Sets the value of the Matrix3X3 given <code>Basis3D</code>
   * enum indices, and the symetric element.  This is used to
   * set elements of a symmetric matrix, automatically keeping
   * the matrix symmetric.
   *
   * @param  row   A Basis3D index for the row
   * @param  col   A Basis3D index for the column
   * @param  val   A double value to be inserted into the (row, col)
   *               element and the (col, row) element.
   */
  public void putSym(Basis3D row, Basis3D col, double val) {
    vals[row.ordinal()][col.ordinal()] = val;
    if (row != col) {
      vals[col.ordinal()][row.ordinal()] = val;
    }
  }

  /**
   * Copies the values of the input Matrix3X3 to this one.
   */
  public void set(Matrix3X3 in) {
    double inptr[][] = in.valuesPtr();

    vals[0][0] = inptr[0][0];
    vals[0][1] = inptr[0][1];
    vals[0][2] = inptr[0][2];
      //
    vals[1][0] = inptr[1][0];
    vals[1][1] = inptr[1][1];
    vals[1][2] = inptr[1][2];
      //
    vals[2][0] = inptr[2][0];
    vals[2][1] = inptr[2][1];
    vals[2][2] = inptr[2][2];
  }

  /**
   * Sets each column to the entered Tuple3D vectors.
   *
   * @param   col1   New 1st column
   * @param   col2   2nd column
   * @param   col3   3rd column
   */
  public void setColumns(Tuple3D col1, Tuple3D col2, Tuple3D col3) {
    vals[0][0] = col1.get(1);
    vals[1][0] = col1.get(2);
    vals[2][0] = col1.get(3);
    vals[0][1] = col2.get(1);
    vals[1][1] = col2.get(2);
    vals[2][1] = col2.get(3);
    vals[0][2] = col3.get(1);
    vals[1][2] = col3.get(2);
    vals[2][2] = col3.get(3);
  }

  /**
   * Sets each row to transpose of entered Tuple3D vectors.
   *
   * @param   row1   New 1st row
   * @param   row2   2nd row
   * @param   row3   3rd row
   */
  public void setRows(Tuple3D row1, Tuple3D row2, Tuple3D row3) {
    vals[0][0] = row1.get(1);
    vals[0][1] = row1.get(2);
    vals[0][2] = row1.get(3);
    vals[1][0] = row2.get(1);
    vals[1][1] = row2.get(2);
    vals[1][2] = row2.get(3);
    vals[2][0] = row3.get(1);
    vals[2][1] = row3.get(2);
    vals[2][2] = row3.get(3);
  }

  /**
   * Gets the value of the Matrix3X3 given <code>Basis3D</code>
   * enum indices.
   *
   * @param  row   A Basis3D index for the row
   * @param  col   A Basis3D index for the column
   *
   * @return       A double value for the requested element
   */
  public double get(Basis3D row, Basis3D col) {
    return vals[row.ordinal()][col.ordinal()];
  }

  /**
   * Multiplies this matrix by num
   *
   * @param  num    number to multiply this matrix by
   */
  public void mult(double num) {
    vals[0][0] *= num;
    vals[0][1] *= num;
    vals[0][2] *= num;
      //
    vals[1][0] *= num;
    vals[1][1] *= num;
    vals[1][2] *= num;
      //
    vals[2][0] *= num;
    vals[2][1] *= num;
    vals[2][2] *= num;
  }

  /**
   * Divides this matrix by num
   *
   * @param  num    number to divide this matrix by
   */
  public void div(double num) {
    this.mult(1.0/num);
  }

  /**
   * Sets the value of this matrix to the inverse if m.  The input matrix
   * is inverted by dividing the adjoint of it by the determinant.
   *
   * @param   m  Matrix3X3 to be inverted
   *
   * @return     Pointer to this matrix
   */
  public Matrix3X3 invert(Matrix3X3 m) {
    double det = m.det();
    if (Math.abs(det) > Matrix.EPS) {
      vals[0][0] =       m.get(2,2)*m.get(3,3) - m.get(3,2)*m.get(2,3);
      vals[1][0] = -1.0*(m.get(2,1)*m.get(3,3) - m.get(3,1)*m.get(2,3));
      vals[2][0] =       m.get(2,1)*m.get(3,2) - m.get(3,1)*m.get(2,2) ;
      vals[0][1] = -1.0*(m.get(1,2)*m.get(3,3) - m.get(3,2)*m.get(1,3));
      vals[1][1] =       m.get(1,1)*m.get(3,3) - m.get(3,1)*m.get(1,3) ;
      vals[2][1] = -1.0*(m.get(1,1)*m.get(3,2) - m.get(3,1)*m.get(1,2));
      vals[0][2] =       m.get(1,2)*m.get(2,3) - m.get(2,2)*m.get(1,3) ;
      vals[1][2] = -1.0*(m.get(1,1)*m.get(2,3) - m.get(2,1)*m.get(1,3));
      vals[2][2] =       m.get(1,1)*m.get(2,2) - m.get(2,1)*m.get(1,2) ;
      mult(1.0/det);
    } else {
      throw(new SingularMatrixException("Can't invert matrix, determinant = "
                                                                      + det));
    }
    return this;
  }

  /**
   * Sets this Matrix to be a reference frame transformation representing
   * a rotation about the X-axis by the input angle.
   *
   * @param    alpha    Rotation angle about X-axis, radians
   *
   * @return            A pointer to this matrix
   *
   */
  public Matrix3X3 rotX(double alpha) {
    double calpha = Math.cos(alpha);
    double salpha = Math.sin(alpha);

    vals[0][0] = 1.0;  vals[0][1] =     0.0;  vals[0][2] =    0.0;
    vals[1][0] = 0.0;  vals[1][1] =  calpha;  vals[1][2] = salpha;
    vals[2][0] = 0.0;  vals[2][1] = -salpha;  vals[2][2] = calpha;

    return this;
  }

  /**
   * Sets this Matrix to be a reference frame transformation representing
   * a rotation about the Y-axis by the input angle.
   *
   * @param    alpha    Rotation angle about Y-axis, radians
   *
   * @return            A pointer to this matrix
   *
   */
  public Matrix3X3 rotY(double alpha) {
    double calpha = Math.cos(alpha);
    double salpha = Math.sin(alpha);

    vals[0][0] = calpha;  vals[0][1] = 0.0;  vals[0][2] = -salpha;
    vals[1][0] =    0.0;  vals[1][1] = 1.0;  vals[1][2] =     0.0;
    vals[2][0] = salpha;  vals[2][1] = 0.0;  vals[2][2] =  calpha;

    return this;
  }

  /**
   * Sets this Matrix to be a reference frame transformation representing
   * a rotation about the Z-axis by the input angle.
   *
   * @param    alpha    Rotation angle about Z-axis, radians
   *
   * @return            A pointer to this matrix
   *
   */
  public Matrix3X3 rotZ(double alpha) {
    double calpha = Math.cos(alpha);
    double salpha = Math.sin(alpha);

    vals[0][0] =  calpha;  vals[0][1] = salpha;  vals[0][2] = 0.0;
    vals[1][0] = -salpha;  vals[1][1] = calpha;  vals[1][2] = 0.0;
    vals[2][0] =     0.0;  vals[2][1] =    0.0;  vals[2][2] = 1.0;

    return this;
  }

}
