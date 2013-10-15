/*
 c  ExitApplicationAction.java
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

package com.motekew.vse.ui;

import java.awt.event.*;
import javax.swing.*;

/**
 * An extension to the <code>AbstractAction</code> class that reacts to an
 * actionPerformed event by calling <code>System.exit(0)</code>
 *
 * @author Kurt A. Motekew
 * @since  20020202
 */
public class ExitApplicationAction extends AbstractAction {

  /**
   * called when an actionPerformed event is sent out from and object
   * that has registered this object as an actionListener.
   */
  public void actionPerformed(ActionEvent evt) {
    System.exit(0);
  }
}
