/*
 c  TimeOfDayUI.java
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
 * UI object representing the TimeOfDay object.  This is an extension of the
 * JTextField object.  Upon losing focus or recieving a VK_ENTER, the
 * object will check the format of the entered Time.  If it is acceptable,
 * only the error status of the object will be updated (to no error).
 * If fails, the object will change to an error state, requesting focus
 * and alerting the user by highlighting the backgroud of the date object
 * to red.
 *
 * @author Kurt A. Motekew
 * @since  20021024
 */
public class TimeOfDayUI extends JTextField implements Errorable,
                                                       ErrorReportable {
  /**
   * The field width.  Big enough to hold a date.
   */
  public static final int FIELDWIDTH = 8;

  private static final Color errorColor = Color.red;

  private TimeOfDay  timeChecker = new TimeOfDay();
  private Color      backgroundColor = null;
  
  private boolean    errorFlag       = false;
  private String     errorLabel      = "TimeOfDayUI";

  /**
   * Default time is 00:00:00
   */
  public TimeOfDayUI() {
    super(FIELDWIDTH);
    stdInit(0.0, 0.0, 0.0);
  }

  /**
   * Initialize with hours, minutes, and seconds
   *
   * @param hh value for hours
   * @param mm value for minutes
   * @param ss value for seconds
   */
  public TimeOfDayUI(double hh, double mm, double ss) {
    super(FIELDWIDTH);
    stdInit(hh, mm, ss);
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


  /*
   * Satisfy ErrorReportable interface.
   */

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
   * Returns he text label identifying the error source.  This should idealy match
   * the text label used in conjunction with the the TimeOfDayUI text field so the
   * error can be reported back to the user, and the user can easily identify which
   * compenent in the GUI needs attention.
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
   * Attempts to update the time of day.  It takes the current text string,
   * in the <code>TimeOfDayUI</code>, and attempts to set the time to a
   * <code>TimeOfDay</code> object (the stored value timeChecker), and if that
   * is successful toString() timeChecker resetting the time value making sure
   * it is all pretty and whatnot.  If any part of the oeration is not successful,
   * the error flag will be set to false.  Otherwise it will e set to true.
   */
  @Override
  public void updateStatus() {
    try {
      timeChecker.setTime(getText());
      setText(timeChecker.toString());
      setErrorFlag(false);
    } catch(BadTimeException bte) {
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
    return new TimeOfDayTextDocument();
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

  /*
   * Standard initialization to be performed by each constructor.
   *
   * @param hh hour value
   * @param mm minute value
   * @param ss second value
   */
  private void stdInit(double hh, double mm, double ss) {
       // Create format popup menu.
    final JPopupMenu formatPopup = new JPopupMenu("Format");
    formatPopup.add(new JMenuItem("D HH:MM:SS"));
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
    timeChecker = new TimeOfDay(hh, mm, ss);
    setText(timeChecker.toString());
    setHorizontalAlignment(JTextField.RIGHT);
    addFocusListener(new StatusCheckerFocus(this));
    addKeyListener(new StatusCheckerEnter(this));
  }
}
