package com.motekew.vse.j3d;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.ArrayList;

/**
 *
 * ColouredTiles creates a coloured quad array of tiles.
 * No lighting since no normals or Material used
 *
 * Other than making the ArrayLists typesafe, this class is pretty much
 * verbatim from Andrew Davison's "Killer Game Programming in Java" book,
 * Copyright 2005 O'Reilly Media, Inc., * ISBN:  0-596-00730-2:
 *
 * <http://fivedots.coe.psu.ac.th/~ad/jg/index.html>
 * <ad@fivedots.coe.psu.ac.th>
 *
 * This code falls under the fair use clause explained in the book's preface.
 * Therefore, I will not LGPL this file.
 *
 * Although this book focuses on game development, I highly recommend it
 * as a first book for learning Java3D.  Having no prior Java3D and no
 * OpenGL experience of any kind, I started with understanding Chapter 1
 * of Sun's old 1999 "Getting Started with the Java 3D API" tutorial.  From 
 * there I read the first two chapters of Davidson's book (the first half of
 * the book is pertains primarily to standard Java 2D stuff) before moving
 * right on to the second half of the book which covers Java3D.  The Java3D
 * section (all 500+ pages of it) was very readable, and got me up to speed
 * quickly with real world useful examples.  While the chapters build on
 * each other to some extent, they can also be used separately as reference
 * material.  Throughout the book, many  tips, tricks, and pitfalls are 
 * brought up helping the coder write  more efficient and safer code.  Many
 * other texts and research papers are also listed by the author.
 *
 * @author Andrew Davison
 * @since  200504
 * @author Kurt Motekew
 * @since  200704
 */
public class ColouredTiles extends Shape3D {
  private QuadArray plane;

  public ColouredTiles(ArrayList<Point3f> coords, Color3f col) {
    plane = new QuadArray(coords.size(), 
			GeometryArray.COORDINATES | GeometryArray.COLOR_3 );
    createGeometry(coords, col);
    createAppearance();
  }    

  private void createGeometry(ArrayList<Point3f> coords, Color3f col) {
    int numPoints = coords.size();

    Point3f[] points = new Point3f[numPoints];
    coords.toArray( points );
    plane.setCoordinates(0, points);

    Color3f cols[] = new Color3f[numPoints];
    for(int i=0; i < numPoints; i++)
      cols[i] = col;
    plane.setColors(0, cols);

    setGeometry(plane);
  }

  private void createAppearance() {
    Appearance app = new Appearance();

    PolygonAttributes pa = new PolygonAttributes();
    pa.setCullFace(PolygonAttributes.CULL_NONE);   
      // so can see the ColouredTiles from both sides
    app.setPolygonAttributes(pa);

    setAppearance(app);
  }

}
