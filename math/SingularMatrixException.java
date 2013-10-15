/*
 c  SingularMatrixException.java
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

package com.motekew.vse.math;

/**
 * Thrown to indicate that an operation to a <code>Matrix</code>
 * can not be completed because a singular matrix needs to be
 * inverted.
 * <P>
 * This exception extends <code>RuntimeException</code> and does not need
 * to be caught by try statements.  Errors that throw this exception are due
 * to programmer errors and are generally not going to be recoverable (the
 * math is bad).
 *
 * @author  Kurt Motekew
 * @version 20080819
 */
public class SingularMatrixException 
                                         extends RuntimeException {
  /**
   * Constructs a <code>SingularMatrixException</code> with no 
   * detail message. 
   */
  public SingularMatrixException() {
    super();
  }

  /**
   * Constructs a <code>SingularMatrixException</code> class 
   * with the specified detail message. 
   *
   * @param   s   the detail message.
   */
  public SingularMatrixException(String s) {
    super(s);
  }
}
