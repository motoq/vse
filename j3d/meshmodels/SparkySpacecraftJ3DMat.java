/*
 c  SparkySpacecraftJ3DMat.java
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

package com.motekew.vse.j3d.meshmodels;

import java.net.URL;
import java.util.Enumeration;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.loaders.*;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * Creates a Java3D TransformGroup model for the SparkySpacecraftJ3DMat.
 * This should be added to a BranchGroup.
 *
 * This class builds a physical model and sets its initial orientation
 * so the body reference frame aligns with that used in the com.motekew.vse.j3d
 * package (X is along the longitudinal axis, and Z is up).  It also offsets
 * the model along the longitudinal axis so the origin is where the centroid
 * of the vehicle would most likely be.
 * <P>
 * The mesh model is defined in the file:  sparkymesh_noball.obj
 * <P>
 * This model is more or less a com.motekew.vse.test.  It uses a version of the SparkySpacecraft
 * that doesn't contain a sphere in the middle of the meshmodel, and instead
 * creates the sphere using a J3D Sphere that is created in this file.  Separate
 * Appearances are applied to the spacecraft and the Java3D sphere, creating
 * the model as a whole.
 * 
 * @author  Kurt Motekew
 */
public class SparkySpacecraftJ3DMat extends TransformGroup {
  /*
   * model offset along the x-axis, relative to a model with a scale
   * factor of 1.0;
   */
  private static double XOFF = 0.30;

  /**
   * Initialize with model scale set to 1.0
   */
  public SparkySpacecraftJ3DMat() {
    this(1.0);
  }
  
  public SparkySpacecraftJ3DMat(double scale) {
      // zero emissive (doesn't glow) - use black
    Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
      // ambient and diffuse blue (normal object)
    Color3f blue = new Color3f(0.3f, 0.3f, 0.8f);
    Color3f red  = new Color3f(0.8f, 0.3f, 0.3f);
      // specular near white (normal object)
    Color3f specular = new Color3f(0.9f, 0.9f, 0.9f);

      // Material(ambient, emissive, diffuse, specular, shininess)
    Material blueMat= new Material(blue, black, blue, specular, 25.0f);
    blueMat.setLightingEnable(true);
    Material redMat= new Material(red, black, red, specular, 25.0f);
    redMat.setLightingEnable(true);

    Appearance blueApp = new Appearance();
    blueApp.setMaterial(blueMat);
    Appearance redApp = new Appearance();
    redApp.setMaterial(redMat);

      // allow the state of the model to be changed after compilation
    setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      // add the visual representation to the TransformGroup
    try {
        // first try the sparkymesh.obj model
      Scene modelScene = null;
      ObjectFile of = new ObjectFile();
      of.setFlags(ObjectFile.RESIZE     |
                 ObjectFile.TRIANGULATE |
                 ObjectFile.STRIPIFY);   
        // load the mesh defined below
      URL sparkyURL =
          SparkySpacecraftJ3DMat.class.getResource("sparkymesh_noball.obj");
      if (sparkyURL == null) {
        throw new java.io.FileNotFoundException("Can't find sparkymesh.obj" +
                                                                         this);
      }
      modelScene = of.load(sparkyURL);
        // rotate model axis to align with simulation axis.  Also, offset
        // it along the x-axis so the cg and origin coincide.
      Vector3d trans = new Vector3d();  // offset along x-axis
      trans.x = XOFF*scale;
      Matrix3d m1 = new Matrix3d();
      m1.rotX(Math.PI/2.0);          // nose down into plane
      Matrix3d m2 = new Matrix3d();
      m2.rotZ(Math.PI/2.0);          // nose yaw into X
      m2.mul(m1);                    // combine
      Transform3D t3d = new Transform3D(m2, trans, scale);
      TransformGroup tg = new TransformGroup(t3d);
      BranchGroup modelBG = modelScene.getSceneGroup();
        // modify appearance here
      Enumeration modelE = modelBG.getAllChildren();
      while(modelE.hasMoreElements()) {
        Object modelO = modelE.nextElement();
        if (modelO instanceof Shape3D) {
         Shape3D modelS3D = (Shape3D) modelO;
         modelS3D.setAppearance(blueApp);
        }
      }
      tg.addChild(modelBG);
      addChild(tg);
      addChild(new Sphere(0.25f*((float) scale), redApp));
    } catch(java.io.FileNotFoundException ex) {
        // if the above didn't work, load a sphere
      addChild(new Sphere(1.0f, blueApp));
    }
  }
}
