/*
 c  StateControlJP.java
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

import com.motekew.vse.math.Tuple;

/**
 * A JPanel that displays state vector and control
 * vector values.
 * 
 * @author  Kurt Motekew
 * @since   2007
 */
public class StateControlJP extends JPanel {
  VectorViewJP xView, uView;

  /**
   * Initialize.
   *
   * @param  x0  A Tuple with initial state values
   * @param  u0  A Tuple with initial control values
   */
  public StateControlJP(Tuple x0, Tuple u0) {
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    xView = new VectorViewJP("State", x0);
    xView.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    uView = new VectorViewJP("Control", u0);
    uView.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    add(xView);
    add(uView);
  }

  /**
   * Updates the State and Control displays
   *
   * @param  xx  A Tuple with initial state values
   * @param  uu  A Tuple with initial control values
   */
  public void refresh(Tuple xx, Tuple uu) {
    xView.refresh(xx);
    uView.refresh(uu);
  }
}
