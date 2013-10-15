/*
 c  RotatingBodyVisModel.java
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

import java.awt.event.*;
import javax.media.j3d.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Quat4d;

import com.motekew.vse.enums.XQ;
import com.motekew.vse.trmtm.*;
import com.motekew.vse.ui.*;

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
 * @since   20101109
 */
public class RotatingBodyVisModel implements IVisualModel {
    // Java3D config settings
  private SimV3Dcfg cfg;
  private double duScale = 1.0;

    // Dynamic model
  private RotatingBodySys sys;

    // Transform group passed in to be adjusted.  This is where position
    // and attitude updates are made.  This transform group contains the
    // solid model being simulated.
  private TransformGroup tg;

    // Cache - Java3D objects used to update the TransformGroup.  These
    //         objects are filled making use of the state vector.
  private class TransformCache {
    Transform3D t3d = new Transform3D();  // position and orientation
    Vector3d trans  = new Vector3d();     // Java3D position vector
    Quat4d attitudeQ = new Quat4d();      // Java3D inertial to body
  }
  private TransformCache tfC = new TransformCache();

  /*
   * Used to store state, output, and control values locally for display in
   * a graphic output window.
   */
  private StateXQ     state;
    // Used to display state vector.
  private StateViewerFrame svf;
  private int scovf_skips = 0;

  /**
   * Initialize the link with the <code>TransformGroup</code>
   * used to manipulate the model and the <code>ISysEqns</code>
   * representing the model.
   *
   * @param  cfgin  Java3D environment configuration object.
   * @param  tgIn   The TransformGroup to be controlled by
   *                this object.
   * @param  sysIn  The system of equations being visualized.
   */
  public RotatingBodyVisModel(SimV3Dcfg cfgin, TransformGroup tgIn,
                                               RotatingBodySys sysIn) {
    cfg = cfgin;
    duScale = cfg.getDU();
    tg = tgIn;
    sys = sysIn;

      // Initialize Visual model state
    tfC.trans.set(sys.getX(XQ.X), sys.getX(XQ.Y), sys.getX(XQ.Z));
    tfC.trans.scale(duScale);
    tfC.attitudeQ.set(sys.getX(XQ.QI),
        sys.getX(XQ.QJ), sys.getX(XQ.QK), sys.getX(XQ.Q0));
      // And update visualization environment settings
    tfC.t3d.set(tfC.attitudeQ, tfC.trans, 1.0);
    tg.setTransform(tfC.t3d);

      // Create display outputs
    state   = new StateXQ();
    updateOutputs();
    svf = new StateViewerFrame("RotatingBody", 0.0, state);       
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
  
      // Only update outputs window if it is visible
    if (svf.isVisible()) {
      if (scovf_skips == cfg.getTskip()) {
        scovf_skips = 0;
        updateOutputs();
        svf.refresh(sys.getT(), state);
      } else {
        scovf_skips++;
      }
    }

      // update position
    tfC.trans.set(sys.getX(XQ.X), sys.getX(XQ.Y), sys.getX(XQ.Z));
    tfC.trans.scale(duScale);

      // Now attitude.
    tfC.attitudeQ.set(sys.getX(XQ.QI),
                      sys.getX(XQ.QJ), sys.getX(XQ.QK), sys.getX(XQ.Q0));

      // update rotation and translation
    tfC.t3d.set(tfC.attitudeQ, tfC.trans, 1.0);
    tg.setTransform(tfC.t3d);
  }

  /**
   * Updates the model based on user keyboard input.  The
   * only recognized event is pressing the spacebar to toggle
   * the status window on/off.
   *
   * @param  eventKey  A KeyEvent containing the key codes
   *                   representing the key that was pressed.
   */
  @Override
  public void processKeyEvent(KeyEvent eventKey) {
    int    keyCode = eventKey.getKeyCode();

    switch(keyCode) {
      /* *******  Display Info  ******* */
    case KeyEvent.VK_SPACE:
      if (svf.isVisible()) {
        svf.setVisible(false);
      } else {
        svf.pack();
        svf.setVisible(true);
      }
      break;
    default:
      break;
    }
  }

  /*
   * Updates the local arrays that store state, control, and
   * output vector values.  Used for text output window.
   */
  private void updateOutputs() {
    for (XQ x : XQ.values()) {
      state.put(x, sys.getX(x));
    }
  }

  /*
   * Called whenever the state is abruptly changed external to
   * propagation.  For example, manually setting velocity values
   * to zero.
   */
  //private void discontinuity() {
  //}
}
