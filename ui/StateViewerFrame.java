/*
 c  StateViewerFrame.java
 c
 c  Copyright (C) 2000, 2010 Kurt Motekew
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
import javax.swing.*;

import com.motekew.vse.math.Tuple;

/**
 * This JFrame is the main window for a State vector
 * status display.
 * 
 * @author  Kurt Motekew
 * @since   20101106
 */
public class StateViewerFrame extends JFrame {

  private StatusJP sjp;
  private StateJP statejp;

  /**
   * Intiates the state and control status display.
   *
   * @param title  A string for the window title bar
   * @param t0     The initial time value for the state vector
   * @param xin    A Tuple with the state values.
   */
  public StateViewerFrame(String title, double t0, Tuple xx) {
    super(title);

      // BorderLayout
    Container cp = getContentPane();

    sjp  = new StatusJP(t0);
    sjp.setBorder(BorderFactory.createEtchedBorder());
    statejp = new StateJP(xx);

    cp.add(sjp, BorderLayout.SOUTH);
    cp.add(statejp, BorderLayout.SOUTH);

    /*
     * Odds and ends
     */
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    pack();
    setResizable(true);
    setVisible(true);
  }

  /**
   * Refreshes the displayed time, and the state vector
   *
   * @param  newTime  A double - the current time to display.
   * @param  xx       A Tuple containing the new state vector.
   */
  public void refresh(double newTime, Tuple xx) {
    sjp.refreshTime(newTime);
    statejp.refresh(xx);
  }
}
