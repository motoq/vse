/*
 c  SimpleConeTrackerCfg.java
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

package com.motekew.vse.sensm;

import com.motekew.vse.math.Quaternion;

/**
 * A lightweight class to ease the transfer of SimpleConeTracker settings.
 *
 * @author   Kurt Motekew
 * @since    20131207
 */
public class SimpleConeTrackerCfg {
  private int nm;
  private double cw, sigma;
  private Quaternion b2s = new Quaternion();

  /**
   * @param   maxMeas       Maximum number of measurement sets supported by
   *                        this sensor at a time.
   * @param   coneWidth     Full conewidth of the sensor, radians.
   * @param   oneSigma      One sigma measurement error, radians.
   * @param   body2Sensor   Quaternion transformation from body to sensor
   *                        attitude.
   */
  public SimpleConeTrackerCfg(int maxMeas, double coneWidth, double oneSigma,
                                                      Quaternion body2Sensor) {
    nm = maxMeas;
    cw = coneWidth;
    sigma = oneSigma;
    b2s.set(body2Sensor);
  }

  /** @return Maximum number of measurement sets supported at a time */
  public int maxMeasurementSets() { return nm; }

  /** @return Full conewidth of the sensor, radians */
  public double fullConeWidth() { return cw; }

  /** @return One sigma measurement error, radians */
  public double oneSigmaRandom() { return sigma; }

  /** @return Body to sensor quaternion, new */
  public Quaternion bodyToSensorAtt() { return new Quaternion(b2s); }
}
