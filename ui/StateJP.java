/*
 c  StateJP.java
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

import javax.swing.*;

import com.motekew.vse.math.Tuple;

/**
 * A JPanel that displays State vector values.
 *
 * @author  Kurt Motekew
 * @since   20101106
 */
public class StateJP extends JPanel {
  VectorViewJP xView;

  /**
   * Initialize.
   *
   * @param  x0  A Tuple with initial state values
   * 
   * @author  Kurt Motekew
   * @since   20101106
   */
  public StateJP(Tuple x0) {
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    xView = new VectorViewJP("State", x0);
    xView.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    add(xView);
  }

  /**
   * Updates the State display
   *
   * @param  xx  A Tuple with initial state values
   */
  public void refresh(Tuple xx) {
    xView.refresh(xx);
  }
}
