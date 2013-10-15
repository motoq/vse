/*
 c  HandleObserverNotificationUtil.java
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

import java.util.ArrayList;

/**
 * This class is a utility simplifying the use of <code>IHandleObserver</code>
 * and <code>IHandleObservable</code> classes.
 *
 * @author Kurt Motekew
 * @since  20111013
 */
public class HandleObserverNotificationUtil {
  ArrayList<ObserverObserved> ooList = new ArrayList<ObserverObserved>();

    // Local class to hold Observer/handle pairs for ArrayList.
  private class ObserverObserved {
    IHandleObserver observer;
    int observedHandle;
    ObserverObserved(IHandleObserver o, int h) {
      observer = o;
      observedHandle = h;
    }
  }

  /**
   * Add an observing class and the handle of the object it is
   * observing to the list.
   *
   * @param   observer        Object to be notified upon request.
   * @param   observedHandle  Handle (identfier) of object notifying
   *                          observer.
   */
  public void addObserverObserved(IHandleObserver observer,
                                              int observedHandle) {
    ooList.add(new ObserverObserved(observer, observedHandle));
  }

  /**
   * Calls the touch() method of each observer, passing the
   * handle of the object being observed.
   */
  public void notifyObservers() {
    ObserverObserved oo = null;
    int n = ooList.size();
    for (int ii=0;  ii<n; ii++) {
      oo = ooList.get(ii);
      oo.observer.touch(oo.observedHandle);
    }
  }

}
