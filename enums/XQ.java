/*
 c  XQ.java
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

package com.motekew.vse.enums;

/**
 * This enum represents the position and orientation of an object in
 * three dimesions.  Quaternions are used for attitude.
 *
 * @author Kurt Motekew
 * @since 20101108
 */
public enum XQ {
  X,       // position
  Y,
  Z,
  Q0,      //    Quaternion scalar component
  QI,      // \
  QJ,      //  > Quaternion vector component
  QK,      // /
}
