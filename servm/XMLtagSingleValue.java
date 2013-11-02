/*
 c  XMLtagSingleValue.java
 c
 c  Copyright (C) 2013 Kurt Motekew
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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class is used to parse the string value of an XML tag
 * given an Element and the assumption that the Element should
 * contain only a single tag of a given name.  If more than one
 * tag exists, the class can be configured to send an Exception
 * vs. return the first or last tag value.
 *
 * @author  Kurt Motekew
 * @since   20131101
 */
public class XMLtagSingleValue {

  /**
   * Used to distinguish the three options available for handling
   * tags that are defined multiple times.
   */
  public enum XMLtagSingle {
    SINGLE,
    FIRST,
    LAST
  }

    // Default to error unless a single instance of the desired
    // tag exists.
  private XMLtagSingle tagOpt = XMLtagSingle.SINGLE; 

  /**
   * @param   opt   Single will throw an exception if more than
   *                one tag is defined.  FIRST will return the
   *                first instance, while LAST will return the
   *                last instance.
   */
  public void setDuplicateBehavior(XMLtagSingle opt) {
    tagOpt = opt;
  }

  /**
   * @param   elem   Element to parse for the desired tag name
   * @param   tag    Tag name to look for.
   *
   * @return         String value associated with the requested tag
   *
   * @throws         Thrown if the tag name isn't found or if SINGLE
   *                 is chosen when multiple tags with the same name
   *                 are defined.
   */
  public String getValue(Element elem, String tag) throws XMLtagException {
    int nTags;
    int iTag = 0;

      // Get all the tags from this Element - then decide how to
      // handle duplicates
    NodeList tags = elem.getElementsByTagName(tag);
    if (tags == null  ||  (nTags = tags.getLength()) == 0) {
      throw new XMLtagException("Missing tag " + tag);
    }
    switch (tagOpt) {
      case SINGLE:
        if (nTags != 1) {
          throw new XMLtagException("Multiple tag definitions " + tag);
        }
        break;
      case FIRST:
        iTag = 0;
        break;
      case LAST:
        iTag = nTags - 1;
        break;
    }
      // Now get the actual value
    Element valElem = (Element) tags.item(iTag);
    NodeList valsList = valElem.getChildNodes();
    if (valsList == null  ||  valsList.getLength() != 1) {
      throw new XMLtagException("Multiple tag values " + tag);
    }

    return valsList.item(0).getNodeValue();
  }
}
