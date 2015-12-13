package org.kei.android.phone.cellhistory.sensors;
/**
 *******************************************************************************
 * @file IAccelSensor.java
 * @author Keidan
 * @date 11/12/2015
 * @par Project CellHistory
 *
 * @par Copyright 2015 Keidan, all right reserved
 *
 *      This software is distributed in the hope that it will be useful, but
 *      WITHOUT ANY WARRANTY.
 *
 *      License summary : You can modify and redistribute the sources code and
 *      binaries. You can send me the bug-fix
 *
 *      Term of the license in in the file license.txt.
 *
 *******************************************************************************
 */
public interface IAccelSensor {

  /**
   * Speed in m/s
   */
  public void accelUpdate(float timestamp, double velocity);
  
}
