/*
 c  IHandleObservable.java
 c
 c  Copyright (C) 2000, 2011 Kurt Motekew
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
 *  Interface defining an observable class that has an integer
 *  handle (identifier) attached to it.  The utility
 *  HandleObserverNotificationUtil comes in handy when implementing.
 *
 *  @author  Kurt Motekew
 *  @since   20111013
 *
 */
public interface IHandleObservable {

  /**
   * @param   observer   An object observing this object.
   * @param   handle     Handle by which this object is to be known
   *                     by the <code>IHandleObserver</code> object.
   */
  public void registerObserver(IHandleObserver observer, int handle);
  
  /**
   * Called to trigger notification of observers.
   */
  public void notifyObservers();

}
