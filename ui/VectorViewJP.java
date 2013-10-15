/*
 c  VectorViewJP.java
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
import javax.swing.*;

import com.motekew.vse.math.Tuple;

/**
 * Creates a JPanel that lists the values contained in <code>Tuple</code>
 * using a BoxLayout.
 *
 * @author Kurt Motekew
 * @since  20070509
 */
public class VectorViewJP extends JPanel {
  private ValueViewJP[] valueJPs;    // array of JPanels with values

  /**
   * Initialize with a column name, the name label to be used for each
   * value, and the list of <code>Tuple<code> values.
   * <P>
   * If the length of the Tuple's getLabels() array is equal to the
   * Dimension of the Tuple, then these String values will be used as
   * the element labels.  Otherwise, the Tuple's single getLabel()
   * will be used, augmented with its element number (X -> X1, X2, ... XM).
   *
   * @param colName  A String with the name that should be assigned to
   *                 this column of values.
   * @param tpl      A Tuple containing the values to view.
   */
  public VectorViewJP(String colName, Tuple tpl) {
    int ii;
    
    String rowLabel = tpl.getLabel();
    String[] rowLabels = tpl.getLabels();

    setLayout(new BorderLayout());

    JPanel headerJP = new JPanel();
    JPanel dataJP   = new JPanel();
    dataJP.setLayout(new BoxLayout(dataJP, BoxLayout.Y_AXIS));

    headerJP.add(new JLabel(colName));

    valueJPs = new ValueViewJP[tpl.length()];
    if (rowLabels.length == tpl.length()) {
      for (ii=0; ii<valueJPs.length; ii++) {
        valueJPs[ii] = new ValueViewJP(rowLabels[ii] + ":  ", tpl.get(ii+1));
        dataJP.add(valueJPs[ii]);
      }
    } else {
      for (ii=0; ii<valueJPs.length; ii++) {
        valueJPs[ii] = new ValueViewJP(rowLabel+(ii+1)+":  ", tpl.get(ii+1));
        dataJP.add(valueJPs[ii]);
      }
    }

    add(headerJP, "North");
    add(dataJP,   "South");
  }

  /**
   * Pass on the message that all <code>ValueViewJP</code> JPanels should
   * update their displays.
   *
   * @param  tpl      A Tuple containing values to be updated.
   *                  This Tuple should be the same length as the original
   *                  used to update this object.  If it is longer, then
   *                  only the first values that will fit will be used.
   *                  if it is shorter, then all values will be used, but
   *                  only to fill the first values.length ValueViewJP
   *                  values.
   */
  public void refresh(Tuple tpl) {
    int n = (valueJPs.length<=tpl.length())?valueJPs.length:tpl.length();
    for (int ii=0; ii<n; ii++) {
      valueJPs[ii].refresh(tpl.get(ii+1));
    }
  }
}
