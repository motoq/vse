/*
 c  SphericalTG.java
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

import com.motekew.vse.enums.Basis3D;
import com.motekew.vse.math.*;

/**
 * Creates a Transform Group solid model based off a
 * <code>SphericalFunction</code>, where a radius is computed as a function
 * of azimuth and elevation.  Radius values are generated for elevations
 * from 90 deg to -90 deg, and azimuth values from -180 deg to 180.  Each of
 * The Cartesian representation of these azimuth, elevation, and radius
 * values are used to form Java3D QuadArrays which are turned into Shape3D
 * faces.  For a sphere, these faces are the same shape as would be found on
 * a globe through the combination of parallels and meridians.
 * <P>
 * The radius generating fuction should return valid values for the above
 * azimuth and elevation ranges.  No logic has been added to turn this into
 * a generalized shape creation class (A donut would fail).
 *
 * @author  Kurt Motekew
 * @since   20101106
 */
public class SphericalTG extends TransformGroup {

  /**
   * @param   nElBands     Number of elevation bands - there will be
   *                       twice as many azimuth bands.
   * @param   sFunct       Function returning a radius value given an
   *                       azimuth and elevation.  This class will work
   *                       correctly if the function returns one valid
   *                       value per az/el combination, where the azimuth
   *                       is between -PI and +PI, and the elevation
   *                       is between -PI/2 and PI/2.                 
   * @param   app          Appearance to be used with the shape in this
   *                       TransformGroup.
   * @param   scale        Scale factor to apply to distances produced by
   *                       sFunct.
   */
  public SphericalTG(int nElBands, ISpherical sFunct, Appearance app,
                                                          double scale) {
    /*
     * Used for creating faces - NormalGenerator is a reusable utility,
     * all others will have to be created for each face.
     */
    NormalGenerator ng = new NormalGenerator();
    QuadArray quad = null;
    TriangleArray tri = null;
    GeometryInfo gi = null;
    Shape3D faceShape = null;

    /*
     * Set up variables needed to create faces.
     */
    int ii, jj;
      // Twice as many azimuth bands as elevation bands.
    int nAzBands = 2*nElBands;
      // Radius of sphere
    double radius = 0.0;
      // Lat/Lon increment value.  The size of each face.
    double delta = Math.PI/((double) nElBands);
      // Iterate on Lat/Lon.  Radius constant for now, will be
      // a function of Lat/Lon in the future.
    double el, az;

      // allow the state of the model to be changed after compilation
    setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    Tuple3D xyz = new Tuple3D();
    SphericalUtil spu = new SphericalUtil();  // Take advantage of el cache

      // Only need one point at these locations.  Handle first and last el
      // bands with TriangleArrays, not QuadArrays.  Scale coordinates.
    el = Math.PI/2.0;
    az = 0.0;
    sFunct.getXYZ(el, az, xyz);
    Point3d northPole = new Point3d(0.0, 0.0, scale*xyz.get(Basis3D.K));
    el = -Math.PI/2.0;
    sFunct.getXYZ(el, az, xyz);
    Point3d southPole = new Point3d(0.0, 0.0, scale*xyz.get(Basis3D.K));

    /*
     * These arrays hold points for the parallels.  Each parallel consists
     * of a number of
     */
      // Used for swapping parallel memory space.
    Point3d[] tmpPts    = new Point3d[nAzBands+1];
      // Holds meridian values to two sets of parallels
    Point3d[][] parallels = new Point3d[2][];
    parallels[0] = new Point3d[nAzBands+1];    // upper parallel
    parallels[1] = new Point3d[nAzBands+1];    // next parallel down
    for (ii=0; ii<(nAzBands+1); ii++) {
      parallels[0][ii] = new Point3d();
      parallels[1][ii] = new Point3d();
    }
      // Used to generate individual shapes
    Point3d[] quadCoords = new Point3d[4];
    Point3d[] triCoords = new Point3d[3];

    /*
     * Now create shapes
     */

      // Prime with North Pole
    for (ii=0; ii<=nAzBands; ii++) {
      parallels[0][ii].x = northPole.x;
      parallels[0][ii].y = northPole.y;
      parallels[0][ii].z = northPole.z;
    }

    el = Math.PI/2.0;
    az = -Math.PI;
    for (ii=0; ii<nElBands; ii++) {            // -1 here
        // make next parallel.  Started at north pole, work down....
      el -= delta;
      spu.setElevation(el);
      for (jj=0; jj<nAzBands; jj++) {
        radius = scale*sFunct.getR(el, az);
        spu.rLatLon2xyz(radius, az, xyz);
        parallels[1][jj].x = xyz.get(Basis3D.I);
        parallels[1][jj].y = xyz.get(Basis3D.J);
        parallels[1][jj].z = xyz.get(Basis3D.K);
        az += delta;
      }
      parallels[1][nAzBands].x = parallels[1][0].x;
      parallels[1][nAzBands].y = parallels[1][0].y;
      parallels[1][nAzBands].z = parallels[1][0].z;
        // now make faces
      for (jj=0; jj<nAzBands; jj++) {             // -1 here
        if (ii == 0) {
            // Work clockwise - start with lower left, then lower right,
            // and finally the north pole.
          triCoords[0] = parallels[1][jj];
          triCoords[1] = parallels[1][jj+1];
          triCoords[2] = northPole;
          tri = new TriangleArray(triCoords.length,
                                                  GeometryArray.COORDINATES |
                                                  GeometryArray.NORMALS     |
                                                  GeometryArray.COLOR_3);
          tri.setCoordinates(0, triCoords);
          gi = new GeometryInfo(tri);
          ng.generateNormals(gi);
          tri.setNormals(0, gi.getNormals());
          faceShape = new Shape3D(tri, app);
            // add the visual representation to the TransformGroup
          this.addChild(faceShape); 
        } else if (ii == (nElBands-1)) {
            // Work clockwise - start with the south pole, then upper right, 
            // and finally upper left.
          triCoords[0] = southPole;
          triCoords[1] = parallels[0][jj+1];
          triCoords[2] = parallels[0][jj];
          tri = new TriangleArray(triCoords.length,
                                                  GeometryArray.COORDINATES |
                                                  GeometryArray.NORMALS     |
                                                  GeometryArray.COLOR_3);
          tri.setCoordinates(0, triCoords);
          gi = new GeometryInfo(tri);
          ng.generateNormals(gi);
          tri.setNormals(0, gi.getNormals());
          faceShape = new Shape3D(tri, app);
            // add the visual representation to the TransformGroup
          this.addChild(faceShape);     
        } else {
            // Work clockwise, so start with lower left corner, and work
            // to lower right, then upper right, and finally upper left.
          quadCoords[0] = parallels[1][jj];
          quadCoords[1] = parallels[1][jj+1];
          quadCoords[2] = parallels[0][jj+1];
          quadCoords[3] = parallels[0][jj];                 
          quad = new QuadArray(quadCoords.length, GeometryArray.COORDINATES |
                                                  GeometryArray.NORMALS     |
                                                  GeometryArray.COLOR_3);
          quad.setCoordinates(0, quadCoords);
          gi = new GeometryInfo(quad);
          ng.generateNormals(gi);
          quad.setNormals(0, gi.getNormals());
          faceShape = new Shape3D(quad, app);
            // add the visual representation to the TransformGroup
          this.addChild(faceShape);       
        }
      }
      tmpPts = parallels[0];
      parallels[0] = parallels[1];
        // Don't need these points anymore, but do need the memory....
      parallels[1] = tmpPts;
    }
  }

}
