/*
 c  ModelViewerJPanel.java
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

import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.vp.*;

/**
 * This JPanel contains the Java3D environment for viewing a model.  It is
 * initialized with a BranchGroup object.
 *
 * @author Kurt Motekew
 * @since  20071011
 */
public class ModelViewerJPanel extends JPanel {

  private BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0), 20);

  /*
   * panel dimensions
   */
  private static final int PWIDTH = 1024;
  private static final int PHEIGHT = 768; 

    // initial user position
  private static final Point3d USERPOSN = new Point3d(0,0,5);

    // Use the simple universe convenience utility.
  private SimpleUniverse su;
    // The only object that is the child of a Locale.
  private BranchGroup sceneBG;

  /**
   * Use a JPanel to hold a 3D canvas.
   *
   * @param model   A BranchGroup containing the representation of the
   *                object being modeled.  It is assumed this model uses
   *                the standard Java3D coordinate system of Y up.
   */
  public ModelViewerJPanel(BranchGroup model) {
    setLayout(new BorderLayout());
    setOpaque(false);
    setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

    GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
    Canvas3D canvas3D = new Canvas3D(config);
    add("Center", canvas3D);
      // give focus to the canvas so keyboard events can affect behavior
    canvas3D.setFocusable(true);
    canvas3D.requestFocus();

    su = new SimpleUniverse(canvas3D);

    createSceneGraph(model);
    initUserPosition();        // set user's viewpoint
    orbitControls(canvas3D);   // controls for moving the viewpoint

    su.addBranchGraph(sceneBG);
  }

  /*
   * initializes the scene
   */
  private void createSceneGraph(BranchGroup model) {
    sceneBG = new BranchGroup();

    /*
     * This is where scaling and initial orientation/position options
     * should be taken care of.
     */
    Transform3D t3d = new Transform3D();
    // t3d.rotX(-Math.PI/2.0);  this would orient with Z-up
    TransformGroup tg = new TransformGroup(t3d);
    tg.addChild(model);

    /*
     * create environment
     */
    lightScene();
    addBackground();
    sceneBG.addChild(tg);

    sceneBG.compile();
  }

  /*
   * create one ambient light, and two directional lights
   */
  private void lightScene() {
    Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    /*
     * Set up the ambient light and add to the BranchGroup
     */
    AmbientLight ambientLightNode = new AmbientLight(white);
    ambientLightNode.setInfluencingBounds(bounds);
    sceneBG.addChild(ambientLightNode);

    /*
     * Set up the directional light and add to the BranchGroup
     */
      // left, down, backwards
    Vector3f lightDirection  = new Vector3f(-1.0f, -1.0f, -1.0f);
    DirectionalLight light = new DirectionalLight(white, lightDirection);
    light.setInfluencingBounds(bounds);
    sceneBG.addChild(light);

  }

  /*
   * create a black backgroun 
   */
  private void addBackground() {
    Background back = new Background();
    back.setApplicationBounds(bounds);
    back.setColor(0.0f, 0.0f, 0.0f);
    sceneBG.addChild(back);
  }

  /*
   * OrbitBehaviour allows the user to rotate around the scene, pan,
   * and to zoom in and out.
   */
  private void orbitControls(Canvas3D c) {
    OrbitBehavior orbit = new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
    orbit.setSchedulingBounds(bounds);

    ViewingPlatform vp = su.getViewingPlatform();
    vp.setViewPlatformBehavior(orbit);	    
  }

  /*
   * Set initial viewpoint
   */
  private void initUserPosition() {
    ViewingPlatform vp = su.getViewingPlatform();
    TransformGroup steerTG = vp.getViewPlatformTransform();

    Transform3D t3d = new Transform3D();
      // Copies the transform component steerTG into t3d
    steerTG.getTransform(t3d);

    /*
     * Helping function that specifies the position and orientation of a 
     * view matrix. The inverse of this transform can be used to control 
     * the ViewPlatform object within the scene graph.
     */
    t3d.lookAt( USERPOSN, new Point3d(0,0,0), new Vector3d(0,1,0));
    t3d.invert();

    steerTG.setTransform(t3d);
  }
}
