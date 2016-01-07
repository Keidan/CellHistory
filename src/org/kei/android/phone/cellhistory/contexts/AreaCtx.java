package org.kei.android.phone.cellhistory.contexts;

/**
 *******************************************************************************
 * @file AreaCtx.java
 * @author Keidan
 * @date 07/01/2016
 * @par Project CellHistory
 *
 * @par Copyright 2015-2016 Keidan, all right reserved
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
public class AreaCtx {
  private int    id        = 0;
  private String name      = null;
  private double latitude  = 0.0;
  private double longitude = 0.0;
  
  /**
   * @return the id
   */
  public int getId() {
    return id;
  }
  
  /**
   * @param id
   *          the id to set
   */
  public void setId(final int id) {
    this.id = id;
  }
  
  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return the latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public void setLatitude(final double latitude) {
    this.latitude = latitude;
  }

  /**
   * @return the longitude
   */
  public double getLongitude() {
    return longitude;
  }

  /**
   * @param longitude
   *          the longitude to set
   */
  public void setLongitude(final double longitude) {
    this.longitude = longitude;
  }

}
