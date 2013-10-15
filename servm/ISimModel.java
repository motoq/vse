/*
 c  ISimModel.java
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

package com.motekew.vse.servm;

/**
 *  Interface defining a class that represents a model that can be plugged
 *  into an application.  The term model is used very generally here, and
 *  is defined only by the method provided in this interface.
 *
 *  @author  Kurt Motekew
 *  @since   20081014
 *  @since   20111011  Changed method from "run()" to "launch()" just in case
 *                     a conflict with a Thread comes into play for an
 *                     ISimModel.
 *
 */
public interface ISimModel {

  /**
   *  Start this model.
   */
  public void launch();

}
