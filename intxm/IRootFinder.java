/*
 c  IRootFinder.java
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

/**
 *  Interface used to define a class that will find roots of an
 *  equation
 *
 *  @author  Kurt Motekew
 *  @since   20101201
 *
 */
public interface IRootFinder {

  /**
   * Compute the root of the function given a guess.
   *
   * @param guess      <code>double</code> starting value for the root-
   *                   finder.  Initial guess.
   * @return           <code>double</code> If an exception is not thrown,
   *                   the computed root:  x such that f(x) = 0
   * @throws           <code>NonConvergenceException</code>
   */
  public double solve(double guess) throws NonConvergenceException;

}
