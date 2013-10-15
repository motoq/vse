/*
 c  VectorSpaceIndexOutOfBoundsException.java
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

package com.motekew.vse.math;

/**
 * Thrown to indicate that an index to a <code>VectorSpace</code>
 * has been exceeded.
 *
 * Note that this exception ultimately extends a RuntimeException
 * and does not need to be caught by try statements.  Errors that
 * throw this exception are due to programmer errors and are generally
 * not going to be recoverable (the math is bad).  Exceptions to this
 * would be in a real time simulation where it would be undesireable for
 * the application to just die, or some form of user interactive math
 * tool where errors would ultimately be due to bad user input, and
 * a message could be sent indicating the error to the user.
 *
 * @author  Kurt Motekew
 * @version 20070530
 */
public class VectorSpaceIndexOutOfBoundsException 
                                         extends IndexOutOfBoundsException {
  /**
   * Constructs a <code>VectorSpaceIndexOutOfBoundsException</code> with no 
   * detail message. 
   */
  public VectorSpaceIndexOutOfBoundsException() {
    super();
  }

  /**
   * Constructs a <code>VectorSpaceIndexOutOfBoundsException</code> class 
   * with the specified detail message. 
   *
   * @param   s   the detail message.
   */
  public VectorSpaceIndexOutOfBoundsException(String s) {
    super(s);
  }
}
