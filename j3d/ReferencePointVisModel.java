/*
 c  ReferencePointVisModel.java
 c
 c  Copyright (C) 2011 Kurt Motekew
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
import javax.media.j3d.*;
import javax.vecmath.Vector3d;

import com.motekew.vse.enums.X3D;
import com.motekew.vse.j3d.SimV3Dcfg;
import com.motekew.vse.trmtm.ReferencePointSys;

/**
 * This class holds both a <code>ISysEqns</code> object and a
 * <code>TransformGroup</code> object.  Since it implements the
 * <code>IVisualModel</code> interface, it guarantees the existence
 * of an update method.
 * <P>
 * This class forms the link between the graphic model display and the
 * algorithms describing the motion of the system being modeled.
 * It makes the jump between dynamics and graphics.
 *
 * @author  Kurt Motekew
 * @since   20111111
 */
public class ReferencePointVisModel implements IVisualModel {
    // Java3D config settings
  private SimV3Dcfg cfg;
  private double duScale = 1.0;

    // Dynamic model
  private ReferencePointSys sys;

    // Transform group passed in to be adjusted.  This is where position
    // updates are made.  This transform group contains the solid model
    //  being simulated.
  private TransformGroup tg;
  private Transform3D t3d = new Transform3D();  // position object
  private Vector3d trans  = new Vector3d();     // position vector

  /**
   * Initialize the link with the <code>TransformGroup</code>
   * used to manipulate the model and the <code>ISysEqns</code>
   * representing the model.
   *
   * @param  tgIn   The TransformGroup to be controlled by
   *                this object.
   * @param  sysIn  The system of equations being visualized.
   */
  public ReferencePointVisModel(SimV3Dcfg cfgin, TransformGroup tgIn,
                                                 ReferencePointSys sysIn) {
    cfg = cfgin;
    duScale = cfg.getDU();
    tg = tgIn;
    sys = sysIn;

      // Initialize Visual model state
    trans.set(sys.getX(X3D.X), sys.getX(X3D.Y), sys.getX(X3D.Z));
    trans.scale(duScale);
      // And update visualization environment settings
    t3d.set(trans);
    tg.setTransform(t3d);
  }

  /**
   * Returns the time corresponding to the model's current state.
   * 
   * @return    Model simulation time.
   */
  @Override
  public double getModelTime() {
    return sys.getT();
  }

  /**
   * Requests an update be made.  This request is made externally
   * by the graphic environment.
   *
   * @param delta  A double representing the elapsed time in seconds
   *               since the last update of the Java3D environment.
   */
  @Override
  public void update(double delta) {
      // Update system state to new time and get state.  New time will
      // be sys.getT() + delta.
    sys.step(delta);
  
      // update position
    trans.set(sys.getX(X3D.X), sys.getX(X3D.Y), sys.getX(X3D.Z));
    trans.scale(duScale);

      // update rotation and translation
    t3d.set(trans);
    tg.setTransform(t3d);
  }

  /**
   * Doesn't do anything right now.
   *
   * @param  eventKey  A KeyEvent containing the key codes
   *                   representing the key that was pressed.
   */
  @Override
  public void processKeyEvent(KeyEvent eventKey) {
  }
}
