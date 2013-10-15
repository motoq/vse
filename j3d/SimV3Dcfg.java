/*
 c  SimV3Dcfg.java
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

import javax.media.j3d.BoundingSphere;
import javax.vecmath.Point3d;

/**
 * This class contains parameters used to define the SimV3D
 * environment.  These values are used only during initiation
 * of the virtual environment.  All defaults work, creating a 
 * 20X20 unit length grid with a BoundingSphere of 100 units.
 * The accessor methods are used to enforce some rules.  For
 * instance, the gridLen must be even.  If it is not even, it will
 * be increased by one unit.  Setting the gridLen value cause the
 * boundSize to be adjusted to a sane value.  The user can then
 * manually adjust the boundSize if he knows what he really wants.
 *
 * Even if the grid isn't being plotted, the gridLen value needs to
 * be set in order to create the Cartesian axes.
 *
 * @author Kurt Motekew
 * @since  20070424
 */
public class SimV3Dcfg {
  
  private double scale2DU = 1.0;

  /*
   * Timing related parameters, see accessor methods for details
   */
  private double dt    = 0.05;   // seconds - integration step size
  private int    jdt   = 100;    // milliseconds - Java3D refresh rate
  private int    dts   = 2;      // dt steps per jdt
  private int    tskip = 2;      // how many J3D updates before
                                 // updating the text output

  /*
   * Flag indicating if the grid in the XY (Java 3D XZ)
   * plane should be created.
   */
  private boolean plotGrid = true;

  /* Size of the grid */
  private int  gridLen  = 20;

  /* BoundingSphere size */
  private int boundSize;

  /* Bounding Sphere */
  private BoundingSphere bounds = null;

  /**
   * Initialize <code>SimV3Dcfg</code> setting <code>boundSize</code>
   * to be 5 times <code>gridLen</code>.
   */
  public SimV3Dcfg() {
    boundSize = 5*gridLen;
  }
  
  /**
   * @return            The scale factor to convert from real world distances
   *                    to those used in the Cartesian grid.  Default is 1.0;
   */
  public double getDU() {
    return scale2DU;
  }
  
  /**
   * Sets the scaling factor from real world units to those that fit on the
   * Cartesian grid making use of "nice" integers.
   * 
   * @param    dScale    Scale factor for converting distance units for display
   *                     on the Cartesian Grid.
   */
  public void setDU(double dScale) {
    scale2DU = dScale;
  }

  /**
   * Compute the Java3D update rate from the integration step size and the integration
   * steps per Java3D refresh (there need to be 1 or more integration steps per Java3D
   * refresh).  Set text window update rate equal to or less than Java3D update rate.
   *
   * @param  dtin      Integration step size, in milliseconds
   * @param  dtsin     Integration steps per Java3D refresh.
   * @param  tskipin   The number of J3D updates that need to occur before a
   *                   update to the text windows
   */
  public void setDT(int dtin, int dtsin, int tskipin) {
    
    if (dtsin < 1) {
      dts = 1;
    } else {
      dts = dtsin;
    }

      // Convert dtin to seconds
    dt = ((double) dtin)/1000.0;
      // dtin already in ms - just multiply by relative refresh rate
    jdt = dts*dtin;
    
    if (tskipin < 0) {
      tskip = 0;
    } else {
      tskip = tskipin;
    } 
  }

  /**
   * Get the integration step size for the dynamic model
   *
   * @return        seconds
   */
  public double getDT() {
    return dt;
  }

  /**
   * Get the number of integration steps per Java3D model
   * refreshes.
   *
   * @return        The number of steps forward the integrator
   *                should take for each refresh of the Java3D
   *                environment.
   */
  public int getDTs() {
    return dts;
  }

  /**
   * Get the Java3D model refresh step size.
   *
   * @return        millisections
   */
  public int getJDT() {
    return jdt;
  }

  /**
   * Get number of times to skip updating the text window
   * per Java3D refresh
   *
   * @return        number of skipped updates
   */
  public int getTskip() {
    return tskip;
  }

  /**
   * Turns the grid plotting either on or off.
   *
   * @param   flag      A boolean value - true to plot the grid
   *                    and false to not plot it.
   */
  public void setPlotGrid(boolean flag) {
    plotGrid = flag;
  }

  /**
   * Outputs the plotGrid setting.
   *
   * @return            A boolean value - true to plot the grid
   *                    and false to not plot it.
   */
  public boolean getPlotGrid() {
    return plotGrid;
  }

  /**
   * Sets the size of the grid (total length of each side).  Also sets 
   * the boundSize value to a reasonable default (5 times the size of the 
   * grid).
   *
   * @param   size      An int value representing the length of
   *                    each side of the grid.  It must be an
   *                    even value.  If it isn't even, it will
   *                    automatically be incremented by 1.
   */
  public void setGridLen(int len) {
    gridLen = len;
    boundSize = 5*len;
  }

  /**
   * Sets the size of the grid (total length of each side) and the
   * the boundSize value based on user inputs.
   *
   * @param   size      An int value representing the length of
   *                    each side of the grid.  It must be an
   *                    even value.  If it isn't even, it will
   *                    automatically be incremented by 1.
   *
   * @param   bSize     An int value representing the BoundingSphere
   *                    size (the BoundingSphere is centered at the
   *                    origin of the grid).
   */
  public void setGridLen(int len, int bSize) {
    gridLen = len;
    boundSize = bSize;
      // reset to null so a new BoundingSphere is created
    bounds = null;
  }

  /**
   * Returns the length of the grid (length of a side)
   *
   * @return            An int length of the grid
   */
  public int getGridLen() {
    return gridLen;
  }

  /**
   * Returns the BoundingSphere size (centerd at the origin of the
   * coordinate system).
   *
   * @return            An int size of the BoundingSphere
   */
  public int getBoundSize() {
    return boundSize;
  }

  /**
   * Returns the BoundingSphere.
   *
   * @return            A BoundingSphere for the model.
   */
  public BoundingSphere getBoundingSphere() {
    if (bounds == null) {
      bounds = new BoundingSphere(new Point3d(0,0,0), boundSize);
    }
    return bounds;
  }
}
