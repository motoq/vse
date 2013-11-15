/*
 c  TestSpacecraft.java
 c
 c  Copyright (C) 2000, 2013 Kurt Motekew
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
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.loaders.*;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import javax.media.j3d.*;
import javax.vecmath.Vector3d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3f;

import com.motekew.vse.enums.Basis3D;
import com.motekew.vse.strtm.MassDyadic;

/**
 * Creates a Java3D TransformGroup model for the TestSpacecraft.
 * This should be added to a BranchGroup.  This model differs from
 * <code>SparkySpacecraft</code> in that it loads a simple materials
 * definition file.
 *
 * This class builds a physical model and sets its initial orientation
 * so the body reference frame aligns with that used in the com.motekew.vse.j3d
 * package (X is along the longitudinal axis, and Z is up).  The model
 * wireframe is defined in a file in this package.
 * 
 * @author   Kurt Motekew
 * @since    20081013
 */
public class TestSpacecraft extends TransformGroup {
  /*
   * model offset along the x-axis, relative to a model with a scale
   * factor of 1.0;
   */
  private static double XOFF = 0.25;

  private BranchGroup modelBG;
  private float sf = 1.0f;

  /**
   * Initialize with model scale set to 1.0.
   */
  public TestSpacecraft() {
    this(1.0);
  }

  /**
   * Initialize with input scale factor.  The model BranchGroup is retained
   * allowing for the getMassDyadic function to retrieve vertex info.
   * 
   * @param    scale    Scale factor for the model.  A scale of
   */
  public TestSpacecraft(double scale) {
    sf = (float) scale;
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
      URL sparkyURL = TestSpacecraft.class.getResource("sparkymatmesh.obj");
      if (sparkyURL == null) {
        System.out.println("Can't find sphere.obj");
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
      modelBG = modelScene.getSceneGroup();
      
      tg.addChild(modelBG);
      addChild(tg);
    } catch(java.io.FileNotFoundException ex) {
        // if the above didn't work, load a sphere
      addChild(new Sphere(1.0f));
    }
  }

  /**
   * Not yet tested - pulls and scales model vertex values to compute
   * moments of inertia.  NOTE:  Check orientation of model axes vs.
   * body axes....
   *
   * @param    mass     Total mass of the solid model.  Moments
   *                    assume all mass is evenly distributed at
   *                    each *vertex* point (not throughout solid).                    
   * @return            Inertia tensor based on input mass units and
   *                    the distance units from class instatniation.
  */
  public MassDyadic getMassDyadic(double mass) {
    Shape3D s3d = (Shape3D) modelBG.getChild(0);
    GeometryArray ga = (GeometryArray)s3d.getGeometry();
    GeometryInfo geometryInfo = new GeometryInfo(ga);
    Point3f[] verts = geometryInfo.getCoordinates();
    double ixx = 0.0;
    double iyy = 0.0;
    double izz = 0.0;
    double ixy = 0.0;
    double ixz = 0.0;
    double iyz = 0.0;
    double dm = mass/verts.length;
    for (int ii=0; ii<verts.length; ii++) {
      verts[ii].x *= sf;
      verts[ii].y *= sf;
      verts[ii].z *= sf;
    }
    for (int ii=0; ii<verts.length; ii++) {
      ixx += dm * (double) (verts[ii].y*verts[ii].y + verts[ii].z*verts[ii].z);
      iyy += dm * (double) (verts[ii].x*verts[ii].x + verts[ii].z*verts[ii].z);
      izz += dm * (double) (verts[ii].x*verts[ii].x + verts[ii].y*verts[ii].y);
      ixy += dm * (double) (verts[ii].x*verts[ii].y);
      ixz += dm * (double) (verts[ii].x*verts[ii].z);
      iyz += dm * (double) (verts[ii].y*verts[ii].z);
    }
    MassDyadic mI = new MassDyadic();
    mI.put(Basis3D.I, Basis3D.I, ixx);
    mI.put(Basis3D.J, Basis3D.J, iyy);
    mI.put(Basis3D.K, Basis3D.K, izz);
    mI.put(Basis3D.I, Basis3D.J, ixy);
    mI.put(Basis3D.I, Basis3D.K, ixz);
    mI.put(Basis3D.J, Basis3D.K, iyz);
    return mI;
  }
}
