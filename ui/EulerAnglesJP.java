/*
 c  EulerAnglesJP.java
 c
 c  Copyright (C) 2000, 2011 Kurt Motekew
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
import javax.swing.*;

import com.motekew.vse.enums.EulerA;
import com.motekew.vse.servm.ErrorReportable;

/**
 * Creates a JPanel with Euler Angle text fields for entry.
 */
public class EulerAnglesJP extends JPanel implements ErrorReportable {
    // To ease checking on ErrorReportable
  private static final int NFIELDS = 3;    // Number of entry fields
  private static final int HD = 0;         // heading
  private static final int EL = 1;         // elevation
  private static final int BK = 2;         // bank

  private DecimalMinMaxJTF[] eulerFields = new DecimalMinMaxJTF[NFIELDS];

  public EulerAnglesJP() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    eulerFields[HD]  = new DecimalMinMaxJTF(8, -180.0, 180.0);
    eulerFields[HD].setErrorLabel("Heading");
    JPanel headingJPanel = new JPanel();
    headingJPanel.setLayout(new BorderLayout());
    headingJPanel.add(new JLabel("Heading (deg):  "), BorderLayout.WEST);
    headingJPanel.add(eulerFields[HD], BorderLayout.EAST);
  
    eulerFields[EL] = new DecimalMinMaxJTF(8, -90.0, 90.0);
    eulerFields[EL].setErrorLabel("Elevation");
    JPanel elevationJPanel = new JPanel();
    elevationJPanel.setLayout(new BorderLayout());
    elevationJPanel.add(new JLabel("Elevation (deg):  "), BorderLayout.WEST);
    elevationJPanel.add(eulerFields[EL], BorderLayout.EAST);

    eulerFields[BK] = new DecimalMinMaxJTF(8, -90.0, 90.0);
    eulerFields[BK].setErrorLabel("Bank");
    JPanel bankJPanel = new JPanel();
    bankJPanel.setLayout(new BorderLayout());
    bankJPanel.add(new JLabel("Bank (deg):  "), BorderLayout.WEST);
    bankJPanel.add(eulerFields[BK], BorderLayout.EAST);

    add(headingJPanel);
    add(elevationJPanel);
    add(bankJPanel);
  }

  /**
   * Gets the value contained by the selected field.
   *
   * @param   ndx   Indicates BANK, ELEVATION, or HEADING
   *
   * @return  Value of desired parameter.
   */
  public double get(EulerA ndx) {
    double returnVal = 0.0;

    switch(ndx) {
      case HEAD:
        returnVal = eulerFields[HD].get();
        break;
      case ELEV:
        returnVal = eulerFields[EL].get();
        break;
      case BANK:
        returnVal = eulerFields[BK].get();
        break;
    }
    return returnVal;
  }

  /**
   * Sets the field value.  Will trigger error checking.
   *
   * @param   ndx   Indicates BANK, ELEVATION, or HEADING
   * @param   val   New value for this field
   */
  public void put(EulerA ndx, double val) {
    switch(ndx) {
      case HEAD:
        eulerFields[HD].set(val);
        break;
      case ELEV:
        eulerFields[EL].set(val);
        break;
      case BANK:
        eulerFields[BK].set(val);
        break;
    }
  }

  /**
   * @return    True if one of the input fields in this object has
   *            and error.  False otherwise.
   */
  @Override
  public boolean getErrorFlag() {
    for (int ii=0; ii<NFIELDS; ii++) {
      if (eulerFields[ii].getErrorFlag()) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return    The Error code of the first data field indicating
   *            an entry error has been made.
   */
  @Override
  public String getErrorLabel() {
    for (int ii=0; ii<NFIELDS; ii++) {
      if (eulerFields[ii].getErrorFlag()) {
        return eulerFields[ii].getErrorLabel();
      }
    }
    return "";
  }

}
