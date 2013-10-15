/*
 c  StatusCheckerEnter.java
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

import java.awt.event.*;

import com.motekew.vse.servm.Errorable;

/**
 * Extends <code>KeyAdapter</code>, used to check an Errorable
 * object.  Simply calls the Errorable object's updteStatus() method
 * when a KeyPressed == VK_ENTER.
 *
 * @author Kurt Motekew
 * @since  20020201
 */
public class StatusCheckerEnter extends KeyAdapter {

  // The object to monitor that will receive the VK_ENTER.
  private Errorable monitor = null;

  /**
   * Initialize the class with the object to be monitored.
   *
   * @param toMonitor The object being monitored that may receive
   *                  a VK_ENTER.
   */
  public StatusCheckerEnter(Errorable toMonitor) {
    monitor = toMonitor;
  }

  /**
   * This method is called when the object being monitored receives
   * a VK_ENTER keypress.
   *
   * @param evt The event representign a keypress.  Only interested
   *            in the VK_ENTER ones.
   */
  public void keyPressed(KeyEvent evt) {
    if (evt.getSource() == monitor  &&  evt.getKeyCode() == KeyEvent.VK_ENTER) {
      monitor.updateStatus();
    }
  }
}
