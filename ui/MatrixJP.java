/*
 c  MatrixJP.java
 c
 c  Copyright (C) 2011 Kurt Motekew
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

package com.motekew.vse.ui;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.text.DecimalFormat;

import com.motekew.vse.math.Matrix;
import com.motekew.vse.math.Tuple;
import com.motekew.vse.servm.Errorable;
import com.motekew.vse.servm.IEditError;
import com.motekew.vse.servm.IORanges;

/**
 * A JPanel displaying a matrix of <code>DecimalMinMaxJTF</code> fields.
 * Currently, simple error checking is implemented (it must be a double
 * half the size of +/-IORanges.MAX_VALUE).
 *
 * @author Kurt A. Motekew
 * @since  20111023
 */
public class MatrixJP extends JPanel implements Errorable, IEditError {

  private static final double MAX_VALUE = IORanges.MAX_VALUE;
  private final int M;        // Rows in matrix
  private final int N;        // Columns in matrix
  private Matrix mtx;         // Values held by matrix.  Not really necessary
                              // now, but will be handy when more matrix type
                              // options (error checking for symmetric, pos
                              // definite, etc) are implemented.

  private DecimalMinMaxJTF[][] elements;

    // Error checking
  private boolean errorFlag = false;
  private String errorLabel = "MatrixJP";
  private String baseErrorLabel = errorLabel;

  public MatrixJP(int rows, int columns, int fieldWidth) {
    M = rows;
    N = columns;
    mtx = new Matrix(M, N);
    elements = new DecimalMinMaxJTF[M][N];
    setLayout(new GridLayout(M,N));
    for (int ii=0; ii<M; ii++) {
      for (int jj=0; jj<N; jj++) {
        elements[ii][jj] = new DecimalMinMaxJTF(fieldWidth,
                                                -MAX_VALUE, MAX_VALUE);
        //elements[ii][jj].set(ii*N+1 + jj);
          // elements added row-wise
        add(elements[ii][jj]);
      }
    }
  }

  /**
   * Set the displayed values with the input matrix.  Dimensions
   * must match.
   *
   * @param   mIn    Input matrix of values.
   */
  public void set(Matrix mIn) {
    mtx.set(mIn);
    updateElementFields();
  }

  /**
   * Set the displayed values with the input tuple.  Dimensions
   * must match.  In other words, if the Tuple is of dimension 3,
   * then this JPanel must represent a matrix that has 3 rows and
   * 1 column.
   *
   * @param   tIn    Input tuple of values.
   */
  public void set(Tuple tIn) {
    mtx.set(tIn);
    updateElementFields();
  }

  /**
   * Copies the contents of the displayed values to mOut.  Dimensions
   * must match.
   *
   * @param   mOut    Output:  Matrix into which displayed values are copied.
   * @return          A pointer to mOut.
   */
  public Matrix get(Matrix mOut) {
    updateMatrixValues();
    mOut.set(mtx);
    return mOut;
  }

  /**
   * Copies the contents of the displayed values to tOut.  Dimensions must
   * match - see comments regarding set(Tuple tIn).
   *
   * @param   tOut    Output:  Tuple into which displayed values are copied.
   * @return          A pointer to tOut.
   */
  public Tuple get(Tuple tOut) {
    updateMatrixValues();
    tOut.set(mtx);
    return tOut;
  }

  /**
   * Sets the display format for each text field.  The same format is used
   * for each cell.
   *
   * @param   formatStr    String used by <code>DecimalFormat</code>.
   */
   public void setFormat(String formatStr) {
     double tmp;
     for (int ii=0; ii<M; ii++) {   
       for (int jj=0; jj<N; jj++) {
         elements[ii][jj].setFormat(new DecimalFormat(formatStr));
         tmp = elements[ii][jj].get();
         elements[ii][jj].set(tmp);
       }
     }
   }

  /**
   * Set the label to be used when an error is encountered.
   *
   * @param str <code>String<code> Label to be used for this object.  
   *            It is best to use the actual label used for this text field,
   *            if one exits.  This label will be used to convey
   *            information to the user as to which field needs to
   *            be fixed.
   */
  public void setErrorLabel(String str) {
    errorLabel = str;
    baseErrorLabel = errorLabel;
  }

  /**
   * Set the label to be used when an error is encountered.
   *
   * @param lbl <code>JLabel</code> to be used for this object.
   *            The text from the label will be used to identify
   *            this object to the user in case it contains an error.
   */
  public void setErrorLabel(JLabel lbl) {
    setErrorLabel(lbl.getText());
  }

  /**
   * Returns the error state of the object.
   *
   * @return <code>true</code> if in the error state (meaning something
   *         needs to be fixed by the user) or <code>false</code> if OK.
   */
  @Override
  public boolean getErrorFlag() {
    updateStatus();
    return errorFlag;
  }

  /**
   * Returns the text label identifying the error source.
   *
   * @return error label.
   */
  @Override
  public String getErrorLabel() {
    return errorLabel;
  }

  /**
   * Called when a signal is given that the value of the fields
   * in this matrix need to be checked for validity.
   */
  @Override
  public void updateStatus() {
    errorFlag = false;
    for (int ii=0; ii<M; ii++) {
      for (int jj=0; jj<N; jj++) {
        if (elements[ii][jj].getErrorFlag()) {
          errorFlag = true;
          errorLabel = baseErrorLabel + "(" + (ii+1) + "," + (jj+1) + ")";
          return;
        }
      }
    }
  }

  /**
   * Empty routine to satisfy Errorable interface.  Might have some use later.
   */
  @Override
  public void prepareForNewStatus() {
  }

  /**
   * @param   edit    If true, sets each field in matrix as editable.  If
   *                  false, then sets each field as not editable.
   */
  @Override
  public void setEditable(boolean edit) {
    for (int ii=0; ii<M; ii++) {
      for (int jj=0; jj<N; jj++) {
        elements[ii][jj].setEditable(edit);
      }
    }
  }

  /**
   * Individually sets fields as editable.
   * 
   * @param   enabled     If true, set editable, false, not editable
   * @param   row         Row to set - index starts at 1.  Ignored if too big
   * @param   col         Colum to set - index starts at 1.  "     "   "   "
   */
  public void setEnabled(boolean enabled, int row, int col) {
    row--;
    col--;
    if (row <= M  &&  row >= 0  &&  col <= N  &&  col >= 0) {
      elements[row][col].setEnabled(enabled);
    }
  }

  /*
   * Sets internally stored <code>Matrix</code> values to those displayed.
   */
  private void updateMatrixValues() {
    for (int ii=0; ii<M; ii++) {
      for (int jj=0; jj<N; jj++) {
        mtx.put(ii+1, jj+1, elements[ii][jj].get());
      }
    }
  }

  /*
   * Sets values displayed to be those of internally stored <code>Matrix</code>.
   */
  private void updateElementFields() {
    for (int ii=0; ii<M; ii++) {
      for (int jj=0; jj<N; jj++) {
        elements[ii][jj].set(mtx.get(ii+1, jj+1));
      }
    }
  }
}
