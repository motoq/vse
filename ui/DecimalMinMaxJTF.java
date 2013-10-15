/*
 c  DecimalMinMaxJTF.java
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.text.DecimalFormat;

import com.motekew.vse.servm.Errorable;
import com.motekew.vse.servm.IEditError;

/**
 * Text field with a double that employs error checking to make sure
 * the value remains within a range:
 * <P>
 *   minValue <= value <= maxValue
 * <P>
 * There is currently no open bracket option (< or >).
 * The default value is 0 with a default DecimalFormat of "0.00000000E00".
 * <P>
 * Internal note:  Always use set to change text value.
 *
 * @author Kurt A. Motekew
 * @since  20111014
 */
public class DecimalMinMaxJTF extends JTextField implements Errorable,
                                                            IEditError {
    // Should be updated by error checking each time the stored
    // value is changed.
  private double minValue = 0.0;
  private double maxValue = 0.0;
  private double doubleValue = 0.0;

  private DecimalFormat df = new DecimalFormat("0.00000000E00");

    // Error checking stuff
  private Color   backgroundColor = null;
  private static final Color errorColor = Color.red;
  private boolean errorFlag = false;
  private String errorLabel = "DecimalMinMaxJTF";

  /**
   * Initializes with the desired field width and value limits.
   * no checking is done to make sure dmax > dmin.
   *
   * @param   fieldWidth   JTextField fieldwidth.
   * @param   dmin         Field will error when a value
   *                       is less than this entry.
   * @param   dmax         Field will error when a value
   *                       exceeds this entry.
   */
  public DecimalMinMaxJTF(int fieldWidth, double dmin, double dmax) {
    super(fieldWidth);
    minValue = dmin;
    maxValue = dmax;
      // Create format popup menu.
    final JPopupMenu formatPopup = new JPopupMenu("Valid Range");
    formatPopup.add(new JMenuItem(minValue + " to " + maxValue));
      // Add mouselistener stuff to deal with the popup.
    addMouseListener(new MouseAdapter() {
      // Unixy way
    public void mousePressed(MouseEvent evt) {
      if (evt.isPopupTrigger()) {
        formatPopup.show(evt.getComponent(), evt.getX(), evt.getY());
      }
    }
      // M$ Way
    public void mouseReleased(MouseEvent evt) {
      if (evt.isPopupTrigger()) {
        formatPopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
      }
    });

      // Display
    backgroundColor = getBackground();
    setHorizontalAlignment(JTextField.RIGHT);
      // Signals error checking
    addFocusListener(new StatusCheckerFocus(this));
    addKeyListener(new StatusCheckerEnter(this));
    
      // Call to make use of current formatting, etc.
    set(0.0);
  }

  /**
   * Update the value held by the text field.  Checking is called
   * after the update.
   *
   * @param   dval   Double to be converted to text and formatted.
   *                 by the current text formatter.
   */
  public void set(double dval) {
    setText("" + dval);
    updateStatus();
  }

  /**
   * @return    Numeric representation of value stored in this
   *            field.
   */
  public double get() {
    return doubleValue;
  }

  /**
   * @param   newDF   Use this format to display numeric value
   *                  in the text field.
   */
  public void setFormat(String newDF) {
    df = new DecimalFormat(newDF);
    set(doubleValue);
  }

  /**
   * @param   newDF   Use this format to display numeric value
   *                  in the text field.
   */
  public void setFormat(DecimalFormat newDF) {
    df = newDF;
    set(doubleValue);
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
    return errorFlag;
  }

  /**
   * Returns the text label identifying the error source.  This should idealy
   * match the text label used in conjunction with the GUI that this field
   * is implementing, helping the user to locate the problem.
   *
   * @return error label.
   */
  @Override
  public String getErrorLabel() {
    return errorLabel;
  }

  /**
   * Called when a signal is given that the value of this field
   * needs to be checked for validity.
   */
  @Override
  public void updateStatus() {
    try {
      doubleValue = Double.parseDouble(getText());
      if (doubleValue > maxValue  ||  doubleValue < minValue) {
        setErrorFlag(true);
      } else {
        setErrorFlag(false);
        setText(df.format(doubleValue));
      }
    } catch(NumberFormatException e) {
       setErrorFlag(true);
    }
  }

  /**
   * Empty routine to satisfy Errorable interface.  Might have some use later.
   */
  @Override
  public void prepareForNewStatus() {
  }

  /*
   * Sets the error flag of the object and proceeds with any
   * steps to be taken when set.
   *
   * @param newErrorFlag value of the new error states.
   */
  private void setErrorFlag(boolean newErrorFlag) {
    errorFlag = newErrorFlag;
    if (errorFlag == true) {
      setBackground(errorColor);
    } else {
      if (!backgroundColor.equals(getBackground())) {
        setBackground(backgroundColor);
      }
    }
  }
}
