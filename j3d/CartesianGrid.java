package com.motekew.vse.j3d;

import java.awt.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Cone;
import javax.vecmath.*;
import java.util.ArrayList;

/**
 * Creates a Cartesian coordinate system in which a state space simulation
 * can be presented in three dimensions.  The option to create grid in the
 * XY (Java3D XZ) plane, along with specifying the size of this grid, is
 * possible via the <code>SimV3Dcfg</code> object.
 *
 * The original structure for this class comes from Andrew Davison's
 * "Killer Game Programming in Java" book, Copyright 2005 O'Reilly Media, Inc.,
 * ISBN:  0-596-00730-2:
 *
 * <http://fivedots.coe.psu.ac.th/~ad/jg/index.html>
 * <ad@fivedots.coe.psu.ac.th>
 *
 * I added the axes and labels, made the ArrayList typesafe, and added the 
 * option of an adjustable grid length.  Otherwise, the code is pretty much 
 * right out of the book and falls under the fair use clause explained in the
 * book's preface.  Therefore, I will not LGPL this file.
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
public class CartesianGrid {

    // should be even
  private final int FLOOR_LEN;

  private final static Color3f blue = new Color3f(0.0f, 0.1f, 0.4f);
  private final static Color3f green = new Color3f(0.0f, 0.5f, 0.1f);
  private final static Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
  private final static Color3f black = new Color3f(0.0f, 0.0f, 0.0f);

  private BranchGroup cartBG;

  /**
   * Create tiles, origin marker, and axis labels
   */
  public CartesianGrid(SimV3Dcfg cfg) {
    FLOOR_LEN = cfg.getGridLen();

    cartBG = new BranchGroup();

    if (cfg.getPlotGrid()) {
      ArrayList<Point3f> blueCoords = new ArrayList<Point3f>();
      ArrayList<Point3f> greenCoords = new ArrayList<Point3f>();

      boolean isBlue;
      for(int z=-FLOOR_LEN/2; z <= (FLOOR_LEN/2)-1; z++) {
        isBlue = (z%2 == 0)? true : false;    // set colour for new row
        for(int x=-FLOOR_LEN/2; x <= (FLOOR_LEN/2)-1; x++) {
          if (isBlue)
            createCoords(x, z, blueCoords);
          else 
            createCoords(x, z, greenCoords);
          isBlue = !isBlue;
        }
      }
      cartBG.addChild( new ColouredTiles(blueCoords, blue) );
      cartBG.addChild( new ColouredTiles(greenCoords, green) );
    }
    makeAxes(FLOOR_LEN, true);
    makeGraduations();
  }

  /*
   * Coords for a single blue or green square,
   * its left hand corner at (x,0,z)
   */
  private void createCoords(int x, int z, ArrayList<Point3f> coords) {
      // points created in counter-clockwise order
    Point3f p1 = new Point3f(x, 0.0f, z+1.0f);
    Point3f p2 = new Point3f(x+1.0f, 0.0f, z+1.0f);
    Point3f p3 = new Point3f(x+1.0f, 0.0f, z);
    Point3f p4 = new Point3f(x, 0.0f, z);   
    coords.add(p1); coords.add(p2); 
    coords.add(p3); coords.add(p4);
  }

  /*
   * Create x, y, and z axes
   */
  private void makeAxes(float aLength, boolean makeLabels) {
    float aRadius = 0.001f * aLength;
    float cRadius = aRadius*4.0f;
    float cHeight = cRadius*8.0f;
    Vector3f coneOffsetV = 
                   new Vector3f(0.0f, aLength/2.0f+cHeight/2.0f, 0.0f);

      // White axis with no emmisivity
    Material whiteMat = new Material(white, black, white, white, 1.0f);
    whiteMat.setLightingEnable(true);

    Appearance whiteApp = new Appearance();
    whiteApp.setMaterial(whiteMat);

    /*
     * Make x-axis (java3D x-axis)
     */
    Transform3D xConeT3d = new Transform3D();
    xConeT3d.setTranslation(coneOffsetV);

    TransformGroup xConeTg = new TransformGroup(xConeT3d);
    TransformGroup xAxisTg = new TransformGroup();
    xConeTg.addChild(new Cone(cRadius, cHeight, whiteApp));
    xAxisTg.addChild(new Cylinder(aRadius, aLength, whiteApp));

    Transform3D xt3d = new Transform3D();
    xt3d.rotZ(-Math.PI/2.0);
    TransformGroup xtg = new TransformGroup(xt3d);
    xtg.addChild(xConeTg);
    xtg.addChild(xAxisTg);

    cartBG.addChild(xtg);

    /*
     * Make y-axis (java3D z-axis)
     */
    Transform3D yConeT3d = new Transform3D();
    yConeT3d.setTranslation(coneOffsetV);

    TransformGroup yConeTg = new TransformGroup(yConeT3d);
    TransformGroup yAxisTg = new TransformGroup();
    yConeTg.addChild(new Cone(cRadius, cHeight, whiteApp));
    yAxisTg.addChild(new Cylinder(aRadius, aLength, whiteApp));

    Transform3D yt3d = new Transform3D();
    yt3d.rotX(-Math.PI/2.0);
    TransformGroup ytg = new TransformGroup(yt3d);
    ytg.addChild(yConeTg);
    ytg.addChild(yAxisTg);

    cartBG.addChild(ytg);

    /*
     * Make z-axis (java3D y-axis)
     */
    Transform3D zConeT3d = new Transform3D();
    zConeT3d.setTranslation(coneOffsetV);

    TransformGroup zConeTg = new TransformGroup(zConeT3d);
    TransformGroup zAxisTg = new TransformGroup();
    zConeTg.addChild(new Cone(cRadius, cHeight, whiteApp));
    zAxisTg.addChild(new Cylinder(aRadius, aLength, whiteApp));

    TransformGroup ztg = new TransformGroup();
    ztg.addChild(zConeTg);
    ztg.addChild(zAxisTg);

    cartBG.addChild(ztg);

    /*
     * Make axis labels
     */
    if (makeLabels) {
      Vector3d pt = new Vector3d();
      double axisLabelOffset = 0.5 + (double) (FLOOR_LEN/2);

      pt.x = axisLabelOffset;
      cartBG.addChild(makeText(pt,"X-Axis") );

      pt.x = 0.0;
      pt.z = -axisLabelOffset;
      cartBG.addChild(makeText(pt,"Y-Axis") );

      pt.z = 0.0;
      pt.y = axisLabelOffset;
      cartBG.addChild(makeText(pt,"Z-Axis"));
    }

  }

  /*
   * Place numbers along the +X, +Y, and +Z axes at the integer positions
   */
  private void makeGraduations() {
    Vector3d pt = new Vector3d();

      // along x-axis  (Java3D X-Axis)
    for (int i=-FLOOR_LEN/2; i <= FLOOR_LEN/2; i++) {
      pt.x = i;
      cartBG.addChild(makeText(pt,""+i) );
    }

      // along z-axis  (Java3D Y-Axis)
    pt.x = 0;
    for (int i=-FLOOR_LEN/2; i <= FLOOR_LEN/2; i++) {
      pt.y = i;
      cartBG.addChild(makeText(pt,""+i) );
    }

      // along y-axis  (Java3D Z-Axis)
    pt.y = 0;
    for (int i=-FLOOR_LEN/2; i <= FLOOR_LEN/2; i++) {
      pt.z = i;
      cartBG.addChild(makeText(pt,""+-i) );
    }
  }

  /*
   * Create a Text2D object at the specified vertex
   */
  private TransformGroup makeText(Vector3d vertex, String text) {
       // 36 point bold Sans Serif
    Text2D message = new Text2D(text, white, "SansSerif", 36, Font.BOLD );
    Appearance app = message.getAppearance();
    PolygonAttributes pa = app.getPolygonAttributes();
    if (pa == null) {
      pa = new PolygonAttributes();
    }
    pa.setCullFace(PolygonAttributes.CULL_NONE);
    app.setPolygonAttributes(pa);

    TransformGroup tg = new TransformGroup();
    Transform3D t3d = new Transform3D();
    t3d.setTranslation(vertex);
    tg.setTransform(t3d);
    tg.addChild(message);
    return tg;
  }

  public BranchGroup getBG() {
    return cartBG;  
  }

}

