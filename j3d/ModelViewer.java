/*
 c  ModelViewer.java
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
 * Create a JPanel to hold a Java3D scene used to display a 3D model
 * 
 * @author Kurt Motekew
 * @since 20071110
 */
public class ModelViewer extends JFrame {
  /**
   * @param  model    A BranchGroup containing the model(s) to be inserted
   *                  into the Viewer.  The standard Java3D coordinate system
   *                  is used.
   */
  public ModelViewer(BranchGroup model) {
    super("ModelViewer");
      // BorderLayout, default for ContentPane
    Container c = getContentPane();
    ModelViewerJPanel sim = new ModelViewerJPanel(model);
    c.add(sim, BorderLayout.CENTER);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setResizable(true);
    setVisible(true);
  }
}
