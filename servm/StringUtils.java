/*
 c  StringUtils.java
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

package com.motekew.vse.servm;

/**
 * This class contains String related helper functions.
 *
 * @author Kurt Motekew
 * @since  20101108
 */
public class StringUtils {

  /**
   * Private constructor prevents instantiation from other classes.
   */
  private StringUtils() {
  }

  /**
   * Some functions return an array of Strings.  Under some circumstances,
   * that array should be empty, but a non-null array of strings should
   * be returned for safety.  This class provides an array of a single
   * string set to "".  It is done here so if a better default is
   * thought of, it can be changed in a single place.
   *
   * @return            The String[]:
   */
  public static String[] blankStringArray() {
    return new String[] {" "};
  }
}
