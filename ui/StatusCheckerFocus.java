/*
 c  StatusCheckerFocus.java
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
 * Implements <code>FocusListener</code>, used to check an Errorable
 * object.  Simply calls the Errorable object's updteStatus() method
 * when focus is lost, and it's prepareForNewStatus() method when
 * focus is gained.
 *
 * @author Kurt A. Motekew
 * @since  20020201
 */
public class StatusCheckerFocus implements FocusListener {

  // The object to monitor that will be gaining and losing focus.
  private Errorable monitor = null;

  /**
   * Initialize the class with the object to be monitored.
   *
   * @param toMonitor The being monitored that may gain or lose
   *                  focus.
   */
  public StatusCheckerFocus(Errorable toMonitor) {
    monitor = toMonitor;
  }

  /**
   * This method is called when the object being monitored loses
   * focus, and this los of focus is not temporary.
   *
   * @param evt The event representign a loss of focus for the
   *            object being monitored.
   */
  public void focusLost(FocusEvent evt) {
    if (evt.getSource() == monitor  &&  !evt.isTemporary()) {
      monitor.updateStatus();
    }
  }

  /**
   * This method is called when the object being monitored gains
   * focus, and this gain is not temporary.
   *
   * @param evt The event prepresenting a gain in focus for the
   *            object being monitored.
   */
  public void focusGained(FocusEvent evt) {
    if (evt.getSource() == monitor  &&  !evt.isTemporary()) {
      monitor.prepareForNewStatus();
    }
  }

}
