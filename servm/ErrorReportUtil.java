/*
 c  ErrorReportUtil.java
 c
 c  Copyright (C) 2011 Kurt Motekew
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

public class ErrorReportUtil {

  /**
   * Returns the index to the first ErrorReportable entry in the ArrayList that
   * returns an error condition.  An index less than zero is returned if there
   * are no errors (hopefully the normal condition)
   * 
   * @param   errorList    The list to search through
   * 
   * @return               The index of the first entry with an error.  If
   *                       all entries are good, then a -1 is returned.
   */
  public static int getErrorLabelIndex(ArrayList<ErrorReportable> errorList) {
    ErrorReportable er = null;
    int num = errorList.size();
    int erNdx = -1;
    for (int ii=0; ii<num; ii++) {
      er = errorList.get(ii);
      if (er.getErrorFlag()) {
        erNdx = ii;
      }
    }
    return erNdx;
  }

}
