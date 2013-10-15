/*
 c  StateControlOutputViewerFrame.java
 c
 c  Copyright (C) 2000, 2008 Kurt Motekew
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
 * This JFrame is the main window for a State, Control, and Output
 * vector status display.
 * 
 * @author  Kurt Motekew
 * @since   2007
 */
public class StateControlOutputViewerFrame extends JFrame {

  private StatusJP sjp;
  private StateControlOutputJP scyjp;

  /**
   * Intiates the state, control, and output status display.
   *
   * @author Kurt Motekew
   * @since  20080913
   *
   * @param title  A string for the window title bar
   * @param t0     The initial time value for the state vector
   * @param xx     A Tuple with the state values.
   * @param uu     A Tuple similar to xin above, but used for
   *               the control vector.
   * @param yy     A Tuple with the output values
   */
  public StateControlOutputViewerFrame(String title, double t0,
                                  Tuple xx, Tuple uu, Tuple  yy)  {
    super(title);
      // BorderLayout
    Container cp = getContentPane();

    sjp  = new StatusJP(t0);
    sjp.setBorder(BorderFactory.createEtchedBorder());
    scyjp = new StateControlOutputJP(xx, uu, yy);

    cp.add(sjp, BorderLayout.NORTH);
    cp.add(scyjp, BorderLayout.SOUTH);

    /*
     * Odds and ends
     */
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    pack();
    setResizable(true);
    setVisible(true);
  }

  /**
   * Refreshes the displayed time, and the state & control
   * vectors
   *
   * @param  newTime  A double - the current time to display.
   * @param  xx       A Tuple containing the new state vector.
   * @param  uu       A Tuple containing the new control vector
   * @param  yy       A Tuple containing the new output vector
   */
  public void refresh(double newTime, Tuple xx, Tuple uu, Tuple yy) {
    sjp.refreshTime(newTime);
    scyjp.refresh(xx, uu, yy);
  }
}
