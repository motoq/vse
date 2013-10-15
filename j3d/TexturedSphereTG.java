/*
 c  Orbiter.java
 c
 c  Copyright (C) 2000, 2011 Kurt Motekew
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

import java.net.URL;
import java.awt.Container;

import javax.media.j3d.*;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.image.TextureLoader;

public class TexturedSphereTG extends TransformGroup {
  public TexturedSphereTG(float rad, int div, Color3f color, URL matURL) {
      // allow the state of the model to be changed after compilation
    setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    Color3f emissive = new Color3f(0.0f, 0.0f, 0.0f);
    Color3f specular = new Color3f(0.9f, 0.9f, 0.9f);
    Appearance app = new Appearance();
    Material mat = new Material(color, emissive, color, specular,  128.0f);
    mat.setLightingEnable(true);
    if (matURL != null) {
      TextureLoader loader = new TextureLoader(matURL, "LUMINANCE",  
                                                       new Container());
      Texture texture = loader.getTexture();
      texture.setBoundaryModeS(Texture.WRAP);
      texture.setBoundaryModeT(Texture.WRAP);
      TextureAttributes texAttr = new TextureAttributes();
      texAttr.setTextureMode(TextureAttributes.MODULATE);
      app.setTexture(texture);
      app.setTextureAttributes(texAttr);
      app.setMaterial(mat);
        // Conversion from Java3D to VehSIM ref frame - probably won't
        // matter for most textures.
      Matrix3d m1 = new Matrix3d();
      m1.rotX(Math.PI/2.0);
      Matrix3d m2 = new Matrix3d();
      m2.rotZ(Math.PI/2.0);
      m2.mul(m1);
      Transform3D t3d = new Transform3D(m2, new Vector3d(0.0, 0.0, 0.0), 1.0);
      TransformGroup tg = new TransformGroup(t3d);
        // Now create Sphere
      int primflags = Primitive.GENERATE_NORMALS +          
                      Primitive.GENERATE_TEXTURE_COORDS;
      tg.addChild(new Sphere(rad, primflags, div, app));
      addChild(tg);
    } else {
        // Create a sphere without the texture using input color and
      app.setMaterial(mat);
      TransformGroup tg = new TransformGroup();
      tg.addChild(new Sphere(rad, app));
      addChild(tg);
    }
  }
  
}
