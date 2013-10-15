/*
 c  ModelTimeBehavior.java
 c
 c  Copyright (C) 2007 Kurt Motekew
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

import java.util.Enumeration;
import javax.media.j3d.*;

import com.motekew.vse.servm.IStartStop;

/**
 * ModelTimeBehavior controls updating of a visual model (what you
 * see on the screen) controlled by some dynamic model (such as a
 * system of differential equations that is integrated, or an analytic
 * model, that is solved for as a function of time).
 * 
 * For the Java3D based simulations, this is where TIME is ultimately
 * controlled.  Only one of these should be used per simulator to guarantee
 * time stays consistent across models.  See processStimulus for info on
 * simulation time.
 *
 * Note that only the initialize() and processStimulus() methods
 * are allowed to call wakeupOn().
 *
 * @author   Kurt Motekew
 * @since    20070421
 */
public class ModelTimeBehavior extends Behavior implements IStartStop {
    // Flag to indicate if simulation time should be progressed.  If fault,
    // the simulation is essentially paused.  Default is on, so turn off if
    // you don't want the simulation to hit the ground running
  private boolean running = true;
    // Time delay between visualization environement update, milliseconds
  private int timeDelay = 100;
    // Time passed to models - new time for models to update to for display
  private double timeDelaySec = ((double) timeDelay)/1000.0;
    // Initialize to time = 0
  private double simulationTime = 0.0;  
    // Time delay passed to wakeupOn()
  private WakeupCondition timeOut;
  private IVisualModel visModels[] = null;
  private int numModels = 0;

  /**
   * Initializes <code>ModelTimeBehavior</code> with the time delay to
   * be used between display refresh and the <code>IVisualModel</code>
   * who's state is to be updated.
   *
   * @param td         A int time delay value in miliseconds
   * @param vm         A IVisualModel representing what the thing
   *                   being modeled looks like.  It's update method
   *                   stimulates the underlying system to propagate in
   *                   time and then update the model in the virtual world.
   */
  public ModelTimeBehavior(int td, IVisualModel vm) {
    timeDelay = td;
    numModels = 1;
    visModels = new IVisualModel[numModels];
    visModels[0] = vm;
    timeOut = new WakeupOnElapsedTime(timeDelay);
      // Convert the time delay from ms to s, no scale factor on init.
    timeDelaySec = ((double) timeDelay)/1000.0;
  }

  /**
   * Initializes <code>ModelTimeBehavior</code> with the time delay to
   * be used between display updates.
   *
   * @param td          A int time delay value in milliseconds
   */  
  public ModelTimeBehavior(int td) {
    timeDelay = td;
    timeOut = new WakeupOnElapsedTime(timeDelay);
      // Convert the time delay from ms to s
    timeDelaySec = ((double) timeDelay)/1000.0;
  }
  
  /**
   * Initializes <code>ModelTimeBehavior</code> with a default refresh rate.
   */  
  public ModelTimeBehavior() {
    timeOut = new WakeupOnElapsedTime(timeDelay);
      // Convert the time delay from ms to s
    timeDelaySec = ((double) timeDelay)/1000.0;
  }

  /**
   * Initializes <code>ModelTimeBehavior</code> with the 
   * <code>IVisualModel</code> objects who's states are to be updated.
   *
   * @param vms        IVisualModel array representing what the things
   *                   being modeled look like.  Their update methods
   *                   stimulate the underlying system to propagate in
   *                   time and then update the model in the virtual world.
   */  
  public void setModels(IVisualModel[] vms) {
    numModels = vms.length;
    visModels = vms;
  }

  /**
   * When called, sets the objects state such that simulation time will
   * progress when processStimulus() below is called.  This is the default
   * behavior.
   */
  @Override
  public void start() {
    running = true;
  }

  /**
   * When called, sets the objects state such that simulation time
   * no longer progresses - essentially stopping/pausing the simulation.
   */
  @Override
  public void stop() {
    running = false;
  }

  /**
   * Returns the amount of simulation time elapsed.  It is the
   * accumulation of timeDelaySec.
   */
  public double getSimulationTime() {
    return simulationTime;
  }

  /**
   * Set simulation time scale factor.  The higher the number, the faster
   * simulation time will pass compared to system (computer) time.
   * Specifically, for each call to update, a scaled amount of the
   * system time that has passed will used.
   * <P>
   * Don't set so low as to make simulation time delta less than
   * integration step sizes....
   *
   * @param    tScale    Time scale factor
   */
  public void setTimeScaleFactor(double tScale) {
    timeDelaySec = ((double) timeDelay)/1000.0 * tScale;
  }

  /**
   * Sets initial wakeup time delta;
   */
  public void initialize() { 
    wakeupOn(timeOut);
  }

  /**
   * Called automatically once the timer goes off.  Note that
   * the wakup condition is reset via the wakeupOn() method,
   * and that the input parameter (an enum) is ignored because
   * this event is triggered by time only (there is no decision
   * to make as to what should be done.
   * 
   * This method ultimately controls simulation time.  Each
   * model should store it own time since epoch, and update
   * this time with the timeDelaySec value.
   * 
   * Note that each model's update routine is called before
   * the simulation is put back to sleep.  This means, no matter
   * how long it takes to compute the update to the model, the
   * update refresh rate will not be able to outpace the time
   * required to make the update - it will simply slow the
   * progression of the visual display, as one would expect.
   * 
   * This would be a great place for parallel processing.
   * Different models could be updated at the same time.
   * 
   * HOWEVER - keep in mind it is probably best to compute
   * update the state of all environmental influences FIRST,
   * and then update models that depend on these environments.
   */
  public void processStimulus(Enumeration criteria) {
    double deltaSec;
      // Update simulation time, then determine how far each
      // model must be propagated.
    if (running) {
      simulationTime += timeDelaySec;
      for (int ii=0; ii<numModels; ii++) {
        deltaSec = simulationTime - visModels[ii].getModelTime();
        visModels[ii].update(deltaSec);
      }
    }
    wakeupOn(timeOut);
  }
}
