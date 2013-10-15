/*
 c  TimeOfDayTextDocument.java
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
 * <code>TimeOfDayTextDocument</code> is an extension of
 * <code>PlainTextDocument</code>.  It is used for preliminary error
 * checking of a time field.  It currently insures that only positive
 * integers are added to the date field.  ":"'s, "."'s, and spaces are
 * also allowed.
 *
 * @author Kurt A. Motekew
 * @since  20021023
 */
public class TimeOfDayTextDocument extends PlainDocument {
  /*
   * Derived from the InTextDocument class presented in
   * "Core Java 2 Vol I - Fundamentals" by Horstmann and Cornell.
   */

  /**
   * Called whenever a character is inserted into an object derived
   * from a JTextField where the <code>createDefaultModel()</code>
   * method has been overwriteen to return a
   * <code>theTimeOfDayTextDocument</code> object instead of a
   * <code>PlainDocument</code> object.
   */
  public void insertString(int offs, String str, AttributeSet a)
                                       throws BadLocationException {
    if (str == null) {
      return;
    }

    boolean fail = true;
    char chBuf[] = str.toCharArray();
    for (int i=0; i<chBuf.length; i++) {
      if (Character.isDigit(chBuf[i])) {
        fail = false;
      } else if (Character.isWhitespace(chBuf[i])) {
        fail = false;
      } else if (chBuf[i] == ':'  ||  chBuf[i] == '.') {
        fail = false;
      }
    }

    if (fail) {
      return;
    } else {
      /*
       * Put new character into string.
       */
      super.insertString(offs, str, a);
    }
  }
}
