/*
 c  ValueViewJP.java
 c
 c  Copyright (C) 2000, 2007 Kurt Motekew
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

import java.awt.BorderLayout;
import java.text.DecimalFormat;

import javax.swing.*;

/**
 * This JPanel holds a single value, and its label,
 * for display.  Something like:  "X1:  10.2".
 *
 * @author Kurt Motekew
 * @since  20070509
 */
public class ValueViewJP extends JPanel {

  private static final String dfmt = "0.00000000E00";
  private DecimalFormat df;

  private static final int FLEN = 11;
  private JTextField dataField;

  /**
   * Initialize with the Label for the value displayed, and the
   * actual initial value.
   *
   * @param  label     A String holding the text used for the data label.
   * @param  dval      The double used to initialize the data value.
   * @param  flen      Length of text field
   * @param  dFormat   DecimalFormat string
   */
  public ValueViewJP(String label, double dval, int flen, String dFormat) {
    setLayout(new BorderLayout());

    JLabel lbl = new JLabel(label);
    df = new DecimalFormat(dFormat);
    dataField = new JTextField(flen);
    dataField.setText(df.format(dval));
    dataField.setHorizontalAlignment(JTextField.RIGHT);
    dataField.setEditable(false);

    add(lbl, "West");
    add(dataField, "East");
  }

  /**
   * Initializes with default size and format.
   */
  public ValueViewJP(String label, double dval) {
    this(label, dval, FLEN, dfmt);
  }

  /**
   * Updates the displayed value within the JPanel.
   *
   * @param  newValue  A double that updates the data value
   */
  public void refresh(double newValue) {
    dataField.setText(df.format(newValue));
  }
}
