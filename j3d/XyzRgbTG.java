/*
 c  XyzRgbTG.java
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

import com.sun.j3d.utils.geometry.*;

import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * This TransformGroup builds a model consisting of Cylinders and
 * Spheres that represents a xyz reference frame.  RGB correspond
 * to XYZ.
 * <P>
 * The model is built with the assumption that the silly standard
 * Java3D coordinate system is being used:
 * <PRE>
 *   X is to the right
 *   Y is up
 *   Z sticking out of the monitor
 * </PRE>
 * 
 * @author Kurt Motekew
 * @since  20081217
 */
public class XyzRgbTG extends TransformGroup {

  /**
   * <code>Vector3f</code> is used for translations while <code>Matirx3f</code>
   * is used for rotations.  A <code>Transform3D</code> is the object that uses
   * the Vector3f or Matrix3f - it can be created with either, or both at once.
   * <P>
   * Each object (Sphere or Cylinder) needs to be added to a <code>TransformGroup</code>.
   * TransformGroups can not be re-used -make one per Sphere, Cylinder, etc....
   * Each TransformGroup must be added as a child to this class (another TransformGroup).
   */
  public XyzRgbTG(float alen) {
      // allow the state of the model to be changed after compilation
    setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    float axis_len = alen;                // length of each axis
    float cyl_rad = 0.01f*axis_len;       // radius of each cylinder
    float arw_rad = 2.0f*cyl_rad;         // radius of each arrow head
    float arw_len = 3.0f*arw_rad;         // length of each arrow head

    Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    Color3f red   = new Color3f(1.0f, 0.0f, 0.0f);        // X-axis
    Color3f green = new Color3f(0.0f, 1.0f, 0.0f);        // Y-axis
    Color3f blue  = new Color3f(0.0f, 0.0f, 1.0f);        // Z-axis
       // specular near white (normal object)
    Color3f specular = new Color3f(0.9f, 0.9f, 0.9f);

    /*
     * Create White Appearance (composed of a color, a Material, with
     * various options enabled.
     * 
     * Material(ambient, emissive, diffuse, specular, shininess)
     */

      // Red....
    Material redMat = new Material(red, black, red, specular, 25.0f);
    redMat.setLightingEnable(true);
    Appearance redApp = new Appearance();
    redApp.setMaterial(redMat);        
      // Green....
    Material greenMat = new Material(green, black, green, specular, 25.0f);
    greenMat.setLightingEnable(true);
    Appearance greenApp = new Appearance();
    greenApp.setMaterial(greenMat);       
      // Blue....
    Material blueMat = new Material(blue, black, blue, specular, 25.0f);
    blueMat.setLightingEnable(true);
    Appearance blueApp = new Appearance();
    blueApp.setMaterial(blueMat);

    /*
     * Add reference frame objects
     */

      // Used for translations and rotations
    float[]      zerotup = new float[] { 0.0f, 0.0f, 0.0f };
    Vector3f       trans = new Vector3f();
    Matrix3f         rot = new Matrix3f();
    Transform3D      t3d = new Transform3D();

    /*
     * X axis
     */
      // radius, length, Appearance
    Cylinder xAxis = new Cylinder(cyl_rad, axis_len, redApp);
      // Zero out translation vector.
    trans.set(zerotup);
      // Move the axis half its length in the x direction (to the right)
    trans.x = axis_len/2.0f;
      // Rotate 90 deg around the Z axis so it is on its side pointing +X
    rot.rotZ((float)(Math.PI/2.0));
      // Set the TransformGroup to have the above rotation and translation.
      // Don't scale it (1.0f).
    t3d.set(rot, trans, 1.0f);
      //  Create a TransformGroup for the X-axis
    TransformGroup xAxisTG = new TransformGroup();
      // Set the orientation and position of the TransformGroup
    xAxisTG.setTransform(t3d);
      // Add the X-axis
    xAxisTG.addChild(xAxis);
      // Finally, add the TransformGroup to this BranchGroup
    addChild(xAxisTG);

    Cone xCone = new Cone(arw_rad, arw_len, redApp);
    trans.set(zerotup);
    trans.x = axis_len + arw_len/2.0f;
    rot.rotZ((float)(-Math.PI/2.0));
    t3d.set(rot, trans, 1.0f);
    TransformGroup xConeTG = new TransformGroup();
    xConeTG.setTransform(t3d);
    xConeTG.addChild(xCone);
    addChild(xConeTG);    

    /*
     * Y axis
     */
    Cylinder yAxis = new Cylinder(cyl_rad, axis_len, greenApp);
    trans.set(zerotup);
    trans.y = axis_len/2.0f;
      // Notice there is no need to rotate the Y-axis - already
      // pointed in the correct direction by default
    t3d.set(trans);
    TransformGroup yAxisTG = new TransformGroup();
    yAxisTG.setTransform(t3d);
    yAxisTG.addChild(yAxis);
    addChild(yAxisTG);

    Cone yCone = new Cone(arw_rad, arw_len, greenApp);
    trans.set(zerotup);
    trans.y = axis_len + arw_len/2.0f;
    rot.rotY((float)(0.0));
    t3d.set(rot, trans, 1.0f);
    TransformGroup yConeTG = new TransformGroup();
    yConeTG.setTransform(t3d);
    yConeTG.addChild(yCone);
    addChild(yConeTG);    

    /*
     * Z axis
     */
    Cylinder zAxis = new Cylinder(cyl_rad, axis_len, blueApp);
    trans.set(zerotup);
    trans.z = axis_len/2.0f;
    rot.rotX((float)(Math.PI/2.0));
    t3d.set(rot, trans, 1.0f);
    TransformGroup zAxisTG = new TransformGroup();
    zAxisTG.setTransform(t3d);
    zAxisTG.addChild(zAxis);
    addChild(zAxisTG);

    Cone zCone = new Cone(arw_rad, arw_len, blueApp);
    trans.set(zerotup);
    trans.z = axis_len + arw_len/2.0f;
    rot.rotX((float)(Math.PI/2.0));
    t3d.set(rot, trans, 1.0f);
    TransformGroup zConeTG = new TransformGroup();
    zConeTG.setTransform(t3d);
    zConeTG.addChild(zCone);
    addChild(zConeTG);    
  }
}
