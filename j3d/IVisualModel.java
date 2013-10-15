/*
 c  IVisualModel.java
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

import java.awt.event.KeyEvent;

/**
 * Interface defining a class that is called to update the state of the
 * visual (3D) model represented by the class implementing this interface.
 * It garantees an update method.
 *
 * This class forms the link between the graphic model display and the
 * algorithms describing the motion of the system being modeled.
 *
 * This class makes the jump between dynamics and graphics.  THIS is as 
 * far up the chain as any knowledge of the system being modeled should 
 * really extend.  The BranchGroup containing this object, and all other
 * graphics and GUI objects above, should be relatively generic and
 * ignorant of the implementation of the system.
 *
 * Note that this is where the transformation from real world coordinate
 * systems should be made to the Java3D one (X primary, Y "up").
 *
 * @author  Kurt Motekew
 * @since   20070421
 */
public interface IVisualModel {

  /**
   * Returns the time corresponding to the model's current state.
   * 
   * @return    Time corresponding to the model's current state.
   */
  public double getModelTime();

  /**
   * Requests an update be made based because a certain amount of time
   * (delta) has passed.
   *
   * @param delta  A double representing the elapsed time in seconds.
   *               The state of the model should be updated from its
   *               current state and time to the new state with an
   *               updated time.
   */
  public void update(double delta);

  /**
   * Updates the model based on keyboard events.  This is most likely
   * a request to update a control system control.
   *
   * @param  eventKey   A KeyEvent containing keyboard intputs.
   */
  public void processKeyEvent(KeyEvent eventKey);
}
