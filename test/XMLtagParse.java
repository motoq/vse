/*
 c  XMLtagParse.java
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

package com.motekew.vse.test;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import java.io.UnsupportedEncodingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.motekew.vse.servm.*;

/**
 * Tests XMLtagSingleValue.  Reads 5 points from internal XML
 * stream, with two "bad" point definitions.
 */
public class XMLtagParse {
  public static void main(String[] args) {
      // Create local XML "file" here
    String testXML = "<?xml version = \"1.0\"?>" +
                     "<Points>"                  +
                       "<Point>"                 +
                         "<X>0.0</X>"            +
                         "<Y>0.0</Y>"            +
                         "<Z>0.0</Z>"            +
                       "</Point>"                +
                       "<Point>"                 +
                         "<X>1.0</X>"            +
                         "<Y>2.0</Y>"            +
                         "<Z>3.0</Z>"            +
                       "</Point>"                +
                       "<Point>"                 +      // Bad Point
                         "<X>3.0</X>"            +
                         "<Y>4.0</Y>"            +
                         "<Y>5.0</Y>"            +
                         "<Z>6.0</Z>"            +
                       "</Point>"                +
                       "<Point>"                 +      // Bad Point
                         "<X>7.0</X>"            +
                         "<Z>9.0</Z>"            +
                       "</Point>"                +
                       "<Point>"                 +
                         "<X>15.0</X>"           +
                         "<Y>20.0</Y>"           +
                         "<Z>33.0</Z>"           +
                       "</Point>"                +
                     "</Points>";

    try {
        // Build intput stream
      byte[] bytes = testXML.getBytes("UTF-8");
      ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
        // Set up XML for parsing
      DocumentBuilder db = 
          DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = db.parse(inStream); 
        // Get a list of all "Point" structures
      NodeList points = doc.getElementsByTagName("Point");
        // Parse Point values, error on duplicates
      XMLtagSingleValue xmler = new XMLtagSingleValue();
      xmler.setDuplicateBehavior(XMLtagSingleValue.XMLtagSingle.SINGLE);
        // Now get components
      int nPoints = (points != null) ? points.getLength() : 0;
      for (int ii=0; ii<nPoints; ii++) {
        Element elem = (Element) points.item(ii);
        try {
          double x = Double.valueOf(xmler.getValue(elem, "X"));
          double y = Double.valueOf(xmler.getValue(elem, "Y"));
          double z = Double.valueOf(xmler.getValue(elem, "Z"));
          System.out.printf("\nPoint %d:\t\t%f\t%f\t%f", (ii+1), x, y, z);
        } catch (XMLtagException xte) {
          System.out.printf("\nPoint %d is bad", (ii+1));
        }
      }
      System.out.println("");
    } catch (UnsupportedEncodingException uee) {
      System.out.println("Couldn't convert String to bytes " + uee);
    } catch (ParserConfigurationException pce) {
      System.out.println("Couldn't create DocumentBuilder " + pce);
    } catch (SAXException se) {
      System.out.println("Error with XML parser " + se);
    } catch (IOException ioe) {
      System.out.println("IO Error parsing XML " + ioe);
    }
  }
}
