/*
 c  IStateSpace.java
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

package com.motekew.vse.intxm;

import com.motekew.vse.math.Tuple;

/**
 *  Interface used to define a class that will represent the State-Space
 *  of a dynamic model, most likely composed of a set of differential
 *  equations.
 *
 *  @author  Kurt Motekew
 *  @since   20101116
 *
 */
public interface IStateSpace extends IGetTime {

  /**
   * Returns the number of differential equations in this system.
   * This is most likely a system of 1st order differential equations.
   *
   * @return       An int representing the number of 1st order diff Qs.
   */
  public int getOrder();

  /**
   * Returns the number of outputs in this system of equations.
   * Outputs are typically related to the states being solved for
   * by the differential equations.  However, outputs are quantities
   * that are often measured, or used in control systems, where as
   * the state parameters being solved for may not be convenient for
   * such purposes.  For example, a system of equations may solve for
   * Quaternion values to represent the attitude of an object.  However,
   * Euler angles may better represent attitude parameters that would
   * actually be measured by the object being modeled. 
   *
   * @return       An int representing the number of outputs related to
   *               this system.
   */
  public int getNumOutputs();

  /**
   * Returns the number of controls in this system of equations.
   * (A control would be something such as a throttle, steering
   * mechanism, etc...).
   *
   * @return       An int representing the number of controls affecting
   *               this system.
   */
  public int getNumControls();

  /**
   * Sets the current system time.
   *
   * @param t0     The current simulation time.
   */
  public void setT(double t0);

  /**
   *  Gets the state of the system.  Copies values into the input Tuple.
   *
   *  @param   out    Current state of the system - an output.  Dimension is
   *                  equal to getOrder().
   *
   *  @return         Time (or independent variable value) associated with the
   *                  current state.
   */
  public double getX(Tuple out);

  /**
   *  Sets the state of the system to the input Tuple.  Values should be copied
   *  into the system instead of setting the pointer to the input Tuple.
   *
   *  @param   in     Tuple from which state vector values are to be set - an
   *                  input.  Dimension is equal to getOrder().
   */
  public void setX(Tuple in);

  /**
   * Returns an array of Strings that are appropriate labels for the
   * state vector values in this system.
   *
   * @return      Array of String labels representing the state vector
   *              element names.  If the length of this array is not
   *              equal to getOrder(), then maybe they repeat, or maybe
   *              the <code>VectorSpace</code> getLabel() method should
   *              be used.
   */
  public String[] getXNames();

  /**
   *  Gets outputs that are a function of the state/EOM, but not directly
   *  solved for. 
   *
   *  @param   out    Tuple into which output values are to be copied.
   *                  Use getNumOutputs() to determine the necessary size
   *                  of the Tuple passed in.
   */
  public void getY(Tuple out);

  /**
   * Returns an array of Strings that are appropriate labels for the
   * outputs to this system.
   *
   * @return      Array of String labels representing the output
   *              element names.  If the length is not equal to
   *              getNumOutputs(), then maybe the getLabel() method
   *              should be used.
   */
  public String[] getYNames();

  /**
   *  Copies values of the control vector of this system into the passed in
   *  Tuple.
   *
   *  @param   out    Tuple into which control vector values are to be copied.
   *                  Use the getNumControls() method to determine how  big the
   *                  passed in Tuple needs to be.
   */
  public void getU(Tuple out);

  /**
   * Copies values from the input Tuple into the control vector of this system.
   * method to not complain.
   *
   * @param   in     Tuple from which control vector values are to be copied.
   *                 Use of the getNumControls() method to determine how big
   *                 the passed in Tuple needs to be is recomended.
   */
  public void setU(Tuple in);

  /**
   * Returns an array of Strings that are appropriate labels for the
   * controls to this system.
   *
   * @return      Array of String labels representing the control
   *              element names.  If the length of the output array is
   *              not equal to the output from getNumControls(), then
   *              the getName() method might be more appropriate.
   */
  public String[] getUNames();

}
