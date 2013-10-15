/*
 c  TimeVisualModel.java
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
package com.motekew.vse.j3d;

import java.awt.event.KeyEvent;

/**
 * System time Singleton.  Time is updated to be the sum of the internally
 * stored time, and a delta time passed into the update method.  Singleton
 * used to allow this static class to be passed to methods expecting a
 * IVisualModel and associated methods.
 * 
 * Bill Pugh's threadsafe Singleton pattern implementation.
 * 
 *  @author  Kurt Motekew
 *  @since   20101104
 */
public class TimeVisualModel implements IVisualModel {

  private static double simulationTime = 0.0;

  /*
   * Private constructor prevents instantiation from other classes.
   */
  private TimeVisualModel() {
  }

  /**
   * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
   * or the first access to SingletonHolder.INSTANCE, not before.
   */
  private static class TimeVisualModelHolder { 
    public static final TimeVisualModel INSTANCE = new TimeVisualModel();
  }

  /**
   * Returns an instance of the TimeVisualModel.  This is useful for passing
   * this static class as a IVisualModel.
   */
  public static TimeVisualModel getInstance() {
    return TimeVisualModelHolder.INSTANCE;
  }

  /**
   * Updates the current time with a delta time.
   *
   * @param delta  A double representing the elapsed time in seconds
   *               from the current time.  The current time is to be
   *               updated based on this time.
   */
  @Override
  public void update(double delta) {
    simulationTime += delta;
  }
  
  /**
   * Returns the simulation time.
   * 
   * @return    Simulation time.
   */
  @Override
  public double getModelTime() {
    return simulationTime;
  }

  /**
   * Responds to keyboard events.  Useful for keyboard functions
   * not related to individual IVisualModels.
   *
   * @param  eventKey   A KeyEvent containing keyboard intputs.
   */
  @Override
  public void processKeyEvent(KeyEvent eventKey) {
  }
}
