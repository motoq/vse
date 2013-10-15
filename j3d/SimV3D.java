/*
 c  SimV3D.java
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

import javax.swing.*;
import java.awt.*;

import javax.media.j3d.BranchGroup;

/**
 * Create a JPanel to hold a Java3D scene used to display
 * simulations in a 3D environment.  The 3D stuff starts
 * with SimV3DJPanel.
 *
 * @author Kurt Motekew
 * @since  200704
 */
public class SimV3D extends JFrame {
  /**
   * Initializes the JFrame with a single model.  See init.
   *
   * @param  cfg      A SimV3Dcfg containing initialization parameters
   *                  for the Viewer (axis length, grid options, etc...)
   * @param  models   A BranchGroup containing the model to be inserted
   *                  into the Viewer.
   */
  public SimV3D(SimV3Dcfg cfg, BranchGroup model) {
    super("Simulation Viewer 3D");
    BranchGroup[] models = new BranchGroup[1];
    models[0] = model;
    init(cfg, models);
  }

  /**
   * Intializes the JFrame with an array of models.  See init.
   *
   * @param  cfg      A SimV3Dcfg containing initialization parameters
   *                  for the Viewer (axis length, grid options, etc...)
   * @param  models   A BranchGroup[] containing the model(s) to be inserted
   *                  into the Viewer.  It is assumed these models use
   *                  a Z up right handed coordinate system instead of the
   *                  silly Y up coordinate system.  A -90 deg rotation about
   *                  the Java3D X-axis will be made to compensate.
   */
  public SimV3D(SimV3Dcfg cfg, BranchGroup[] models) {
    super("Simulation Viewer 3D");
    init(cfg, models);
  }

  /**
   * Intializes the JFrame, and sends modeling information
   * to the <code>SimV3DJPanel</code> object.
   *
   * @param  cfg      A SimV3Dcfg containing initialization parameters
   *                  for the Viewer (axis length, grid options, etc...)
   * @param  models   A BranchGroup[] containing the model(s) to be inserted
   *                  into the Viewer.  It is assumed these models use
   *                  a Z up right handed coordinate system instead of the
   *                  silly Y up coordinate system.  A -90 deg rotation about
   *                  the Java3D X-axis will be made to compensate.
   */
  private void init(SimV3Dcfg cfg, BranchGroup[] models) {
    Container c = getContentPane();
    c.setLayout(new BorderLayout());
    SimV3DJPanel sim = new SimV3DJPanel(cfg, models);
    c.add(sim, BorderLayout.CENTER);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setResizable(true);
    setVisible(true);
  }

}
