/*
 c  SimV3DJPanel.java
 c
 c  Copyright (C) 2000, 2007 Kurt Motekew
 c
 c  This program is free software; you can redistribute it and/or modify
 c  it under the terms of the GNU General Public License as published by
 c  the Free Software Foundation; either version 2 of the License, or
 c  (at your option) any later version.
 c
 c  This program is distributed in the hope that it will be useful,
 c  but WITHOUT ANY WARRANTY; without even the implied warranty of
 c  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 c  GNU General Public License for more details.
 c
 c  You should have received a copy of the GNU General Public License along
 c  with this program; if not, write to the Free Software Foundation, Inc.,
 c  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.motekew.vse.j3d;

import javax.swing.*;
import java.awt.*;

import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.vp.*;

/**
 * This is the JPanel that contains the Java3D environment.  It is
 * initialized with a configuration object, and a BranchGroup object.
 * 
 * @author  Kurt Motekew
 */
public class SimV3DJPanel extends JPanel {

  /*
   * panel dimensions
   */
  private static final int PWIDTH = 1024;
  private static final int PHEIGHT = 768; 

  /*
   * Object with Initialization information
   */
  SimV3Dcfg cfg = null;

    // initial user position
  private static final Point3d USERPOSN = new Point3d(0,5,20);

    // Use the simple universe convenience utility.
  private SimpleUniverse su;
    // The only object that is the child of a Locale.
  private BranchGroup sceneBG;

  /**
   * Use a JPanel to hold a 3D canvas.
   *
   * @param cfg_in  A SimV3Dcfg that contains initialization parameters.
   * @param models  A BranchGroup array containing the representations of
   *                the objects being modeled.  It is assumed the models use
   *                real world Z up coordinates.  A -90 deg rotation about
   *                the Java3D X-axis will be made to convert it to the
   *                silly internal Y up Java3D coordinates.
   */
  public SimV3DJPanel(SimV3Dcfg cfg_in, BranchGroup[] models) {
    cfg = cfg_in;
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

    createSceneGraph(models);
    initUserPosition();        // set user's viewpoint
    orbitControls(canvas3D);   // controls for moving the viewpoint
    
    su.addBranchGraph(sceneBG);
  }


  /*
   * initializes the scene
   */
  private void createSceneGraph(BranchGroup[] models) {
    sceneBG = new BranchGroup();

    /*
     * Create tg to make the transition from real X, Y, Z(up) axis
     * to stupid Java3D X, Y(up), Z axis (silly computer scientists).
     *
     * This is where scaling options should be added later on too.
     */
    Transform3D t3d = new Transform3D();
    t3d.rotX(-Math.PI/2.0);
    TransformGroup tg = new TransformGroup(t3d);
    for (int ii=0; ii<models.length; ii++) {
      tg.addChild(models[ii]);
    }

    /*
     * create environment
     */
    lightScene();
    addBackground();
    sceneBG.addChild(new CartesianGrid(cfg).getBG() );
      // model of interest
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
    ambientLightNode.setInfluencingBounds(cfg.getBoundingSphere());
    sceneBG.addChild(ambientLightNode);

    /*
     * Set up the directional lights and add to the BranchGroup
     */
      // right, down, forwards
    Vector3f lightDirection  = new Vector3f(-1.0f, -1.0f, 1.0f);
    DirectionalLight light = new DirectionalLight(white, lightDirection);
    light.setInfluencingBounds(cfg.getBoundingSphere());
    sceneBG.addChild(light);
  }


  /*
   * create a black backgroun 
   */
  private void addBackground() {
    Background back = new Background();
    back.setApplicationBounds(cfg.getBoundingSphere());
    back.setColor(0.0f, 0.0f, 0.0f);
    sceneBG.addChild(back);
  }


  /*
   * OrbitBehaviour allows the user to rotate around the scene, pan,
   * and to zoom in and out.
   */
  private void orbitControls(Canvas3D c) {
    OrbitBehavior orbit = new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
    orbit.setSchedulingBounds(cfg.getBoundingSphere());

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
    t3d.lookAt(USERPOSN, new Point3d(0,0,0), new Vector3d(0,1,0));
    t3d.invert();

    steerTG.setTransform(t3d);
  }
}
