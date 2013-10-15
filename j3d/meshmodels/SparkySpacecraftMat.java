/*
 c  SparkySpacecraftMat.java
 c
 c  Copyright (C) 2000, 2008 Kurt Motekew
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

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.loaders.*;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import javax.media.j3d.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3d;

/**
 * Creates a Java3D TransformGroup model for the SparkySpacecraftMat.
 * This should be added to a BranchGroup.  This model differs from
 * <code>SparkySpacecraft</code> in that it loads a simple materials
 * definition file.
 *
 * This class builds a physical model and sets its initial orientation
 * so the body reference frame aligns with that used in the com.motekew.vse.j3d
 * package (X is along the longitudinal axis, and Z is up).  It also offsets
 * the model along the longitudinal axis so the origin is where the centroid
 * of the vehicle would most likely be.
 * <P>
 * The mesh model is defined in the file:  sparkymatmesh.obj
 * 
 * @author   Kurt Motekew
 * @since    20081013
 */
public class SparkySpacecraftMat extends TransformGroup {
  /*
   * model offset along the x-axis, relative to a model with a scale
   * factor of 1.0;
   */
  private static double XOFF = 0.25;

  /**
   * Initialize with model scale set to 1.0
   */
  public SparkySpacecraftMat() {
    this(1.0);
  }

  /**
   * Initialize with input scale factor.
   * 
   * @param    scale    Scale factor for the model.  A scale of
   *                    1.0 means the distance from the model origin, to the
   *                    furthest point on the model, will be one unit long.
   */
  public SparkySpacecraftMat(double scale) {
      // allow the state of the model to be changed after compilation
    setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
      // add the visual representation to the TransformGroup
    try {
        // first try the sparkymatmesh.obj model
      Scene modelScene = null;
      ObjectFile of = new ObjectFile();
      of.setFlags(ObjectFile.RESIZE     |
                 ObjectFile.TRIANGULATE |
                 ObjectFile.STRIPIFY);   
        // load the mesh defined below
      URL sparkyURL = SparkySpacecraftMat.class.getResource("sparkymatmesh.obj");
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
        //
      tg.addChild(modelBG);
      addChild(tg);
    } catch(java.io.FileNotFoundException ex) {
        // if the above didn't work, load a sphere
      addChild(new Sphere(1.0f));
    }
  }
}
