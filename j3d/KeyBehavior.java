/*
 c  KeyBehavior.java
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

package com.motekew.vse.j3d;

import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import javax.media.j3d.*;

/**
 * KeyBehavior controls updating a visual model's internal status
 * through user keyboard input.  Generally, only the models control
 * will be modified.
 *
 * Note that only the initialize() and processStimulus() methods
 * are allowed to call wakeupOn().
 *
 * @author Kurt Motekew
 * @since  20070428
 */
public class KeyBehavior extends Behavior {

  private WakeupCondition keyPress;
  private IVisualModel    visMod;

  public KeyBehavior(IVisualModel vm) {
    keyPress = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
    visMod = vm;
  }

  /**
   * Initialize wakeup criteria.
   */
  public void initialize() {
    wakeupOn(keyPress); 
  }

  public void processStimulus(Enumeration criteria) {
    WakeupCriterion wakeup;
    AWTEvent[] event;

      // process all keypresses
    while(criteria.hasMoreElements()) {
        // get the current wakup criteria
      wakeup = (WakeupCriterion) criteria.nextElement();
        // only process AWT events - ignore others
      if(wakeup instanceof WakeupOnAWTEvent) {
          // get AWT events and look at them all
        event = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
        for( int i = 0; i < event.length; i++ ) {
            // Only process events related to key presses
          if(event[i].getID() == KeyEvent.KEY_PRESSED )
            visMod.processKeyEvent((KeyEvent)event[i]);
        }
      }
    }
      // reset wakeup
    wakeupOn(keyPress);
  }
}
