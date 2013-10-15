/*
 c  TheDateUI.java
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

import com.motekew.vse.envrm.*;
import com.motekew.vse.servm.ErrorReportable;
import com.motekew.vse.servm.Errorable;

/**
 * UI object representing the Date object.  This is an extension of the
 * JText Field object.  Upon losing focus or recieving a VK_ENTER, the
 * object will check the format of the entered date.  If it is acceptable,
 * only the error status of the object will be updated (to no error).
 * If fails, the object will change to an error state, requesting focus
 * and alerting the user by highlighting the backgroud of the date object
 * to red.
 *
 * @author Kurt A. Motekew
 * @since  20020201
 */
public class TheDateUI extends JTextField implements Errorable,
                                                     ErrorReportable {
  /**
   * The field width.  Big enough to hold a date.
   */
  public static final int FIELDWIDTH = 8;

  private static final Color errorColor = Color.red;

  private Color   backgroundColor = null;
  private TheDate dateChecker = new TheDate();
  private boolean errorFlag  = false;
  private String  errorLabel = "TheDateUI";

  /**
   * Initialize with default date as determined by <code>TheDate</code>.
   */
  public TheDateUI() {
    super(FIELDWIDTH);
    stdInit(new TheDate().getYYYYMMDD());
  }

  /**
   * Initialize with a given date.
   *
   * @param idate <code>int</code> representation of the date in
   *              YYYYMMDD format.
   */
  public TheDateUI(int idate) {
    super(FIELDWIDTH);
    stdInit(idate);
  }

  /**
   * Set the label to be used when an error is encountered.
   *
   * @param str <code>String<code>Label to be used for this object.  
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

  /*
   * Satisfy ErrorReportable interface.
   */

  /**
   * Returns he error state of the object.
   *
   * @return <code>true</code> if in the error state (meaning something
   *         needs to be fixed by the user) or <code>false</code> if OK.
   */
  @Override
  public boolean getErrorFlag() {
    return errorFlag;
  }

  /**
   * Returns he text label identifying the error source.  This should ideally match
   * the text label used in conjunction with the the TheDateUI text field so the
   * error can be reported back to the user, and the user can easily identify which
   * component in the GUI needs attention.
   *
   * @return error label.
   */
  @Override
  public String getErrorLabel() {
    return errorLabel;
  }


  /*
   * Satisfy the Errorable interface.
   */

  /**
   * Check the currently entered date for errors.  If there is an error, set the
   * error flag and make any necessary visual changes to the text field to indicate
   * the error condition.
   */
  @Override
  public void updateStatus() {
    try {
      dateChecker.setYYYYMMDD(getText());
      setText(dateChecker.toString());
      setErrorFlag(false);
    } catch(BadDateException bde) {
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
   * Other methods.
   */

  /*
   * Override this method to enforce custom error checking
   * (preliminary check while typing).
   */
  protected Document createDefaultModel() {
    return new TheDateTextDocument();
  }

  /*
   * Sets the error flag of the object and proceeds with any
   * steps to be taken when set.
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

  /*
   * Standard initialization to be performed by each constructor.
   *
   * @param idate <code>int</code> form of date in YYYYMMDD format.
   */
  private void stdInit(int idate) {
       // Create format popup menu.
    final JPopupMenu formatPopup = new JPopupMenu();
    JMenuItem format1 = new JMenuItem("YYYYMMDD");
    formatPopup.add(format1);
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
       // Data and display
    backgroundColor = getBackground();
    setText(Integer.toString(idate));
    setHorizontalAlignment(JTextField.RIGHT);
    addFocusListener(new StatusCheckerFocus(this));
    addKeyListener(new StatusCheckerEnter(this));
  }
}
