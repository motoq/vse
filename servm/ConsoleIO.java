/*
 c  ConsoleIO.java
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

package com.motekew.vse.servm;

import java.io.*;

/**
 * This class handles simple prompted Console input.
 *
 * @author Kurt Motekew
 * @since  20070411
 */
public class ConsoleIO {
  BufferedReader stdin = null;

  /**
   * Initializes the console input stream.  This stream can
   * be used for any input values.
   */
  public ConsoleIO() {
    stdin = new BufferedReader(new InputStreamReader(System.in));
  }

  /**
   * Reads in a double.  If an error is encountered (can't parse
   * the double), the prompt will be printed again, and again, and
   * again until a valid double is entered.  It will also catch
   * an <code>IOException</code>, send a warning message to the
   * console, and return a bad value.
   *
   * @param promptMsg   A String with the user request for console input.
   *
   * @return            The value input by the user.  If an
   *                    <code>IOException</code> is encountered, the
   *                    value 666.0 will be returned.
   */
  public double readDouble(String promptMsg) {
    while (true) {
      System.out.print(promptMsg);
      try {
        return(Double.parseDouble(stdin.readLine()));
      } catch (NumberFormatException nfe) {
        System.out.println("Bad input, try again!");
      } catch (IOException ioe) {
        System.out.println("IO error!!!  Corrupt Input Value!!!");
        return(666.);
      }
    }
  }
}
