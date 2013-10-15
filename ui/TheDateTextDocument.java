/*
 c  TheDateTextDocument.java
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

import javax.swing.text.*;

/**
 * <code>TheDateTextDocument</code> is an extension of
 * <code>PlainTextDocument</code>.  It is used for preliminary error
 * checking of a date field.  It currently insures that only positive
 * integers are added to the date field.
 *
 * @author Kurt A. Motekew
 * @since  20020201
 */
public class TheDateTextDocument extends PlainDocument {
  /*
   * Derived from the InTextDocument class presented in
   * "Core Java 2 Vol I - Fundamentals" by Horstmann and Cornell.
   */

  /**
   * Called whenever a character is inserted into an object derived
   * from a JTextField where the <code>createDefaultModel()</code>
   * method has been overwriteen to return a
   * <code>theDateTextDocument</code> object instead of a
   * <code>PlainDocument</code> object.
   */
  public void insertString(int offs, String str, AttributeSet a)
                                       throws BadLocationException {
    if (str == null) {
      return;
    }

    try {
         // If this fails then we are done.
      Integer.parseInt(str);

      /*
       * Handle getting new text (str) and inserting into
       * previous text (oldString), creating newString.
       */
      String oldString = getText(0, getLength());
      String newString = oldString.substring(0, offs) +
                         str + oldString.substring(offs);
         // Now check to see if new string parses.  If so, call super.
      Integer.parseInt(newString);
      super.insertString(offs, str, a);
    } catch(NumberFormatException e) {
      // it didn't parse into an int, then don't add to the string.
      // do nothing.
    }
  }
}
