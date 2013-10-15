/*
 c  StatusJP.java
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

import javax.swing.*;

import java.text.DecimalFormat;

/**
 * This JPanel is used to display overall status.  Currently, supported
 * status indicators are:
 * <p>
 * Simulation Time.
 * <p>
 * Methods are provided to update the stored status, and to refresh the
 * display (it was decided to keep these two separated for added flexibility).
 *
 * @author Kurt Motekew
 * @since  20070509
 */
public class StatusJP extends JPanel {

  private static final DecimalFormat df = new DecimalFormat("0.00");

  private JTextField timeField = new JTextField(8);
  

  /**
   * Initialize with start time and a label.
   *
   * @param  t0     A double representing the start time of what is being
   *                monitored.
   * @param  label
   */
  public StatusJP(double t0, String label) {
    timeField.setText(df.format(t0));
    timeField.setHorizontalAlignment(JTextField.RIGHT);
    timeField.setEditable(false);

    add(new JLabel(label));
    add(timeField);
  }

  /**
   * Initialize with start time.
   *
   * @param  t0  A double representing the start time of what is being
   *             monitored.  Default label is used.
   */
  public StatusJP(double t0) {
    this(t0, "Simulation Time:  ");
  }

  /**
   * Refreshes the JPanel's time display.
   *
   * @param  newTime  A double - the new time to display
   */
  public void refreshTime(double newTime) {
    timeField.setText(df.format(newTime));
  }
}
