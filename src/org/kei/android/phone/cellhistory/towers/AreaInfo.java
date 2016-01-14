package org.kei.android.phone.cellhistory.towers;

import android.location.Location;

/**
 *******************************************************************************
 * @file AreaInfo.java
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
public class AreaInfo {
  public static final String UNKNOWN        = "Unknown";
  public static final double DEFAULT_RADIUS = 30.0;
  private int                id             = 0;
  private String             name           = UNKNOWN;
  private double             radius         = DEFAULT_RADIUS;
  private Location           location       = new Location("");
  
  public String toString() {
    return name;
  }
  
  public String toString(final String sep) {
    return name + sep + location.getLatitude() + sep + location.getLongitude() + sep + radius;
  }
  
  public Location getLocation() {
    return location;
  }

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
    return location.getLatitude();
  }

  /**
   * @param latitude
   *          the latitude to set
   */
  public void setLatitude(final double latitude) {
    location.setLatitude(latitude);
  }

  /**
   * @return the longitude
   */
  public double getLongitude() {
    return location.getLongitude();
  }

  /**
   * @param longitude
   *          the longitude to set
   */
  public void setLongitude(final double longitude) {
    location.setLongitude(longitude);
  }

  /**
   * @return the radius
   */
  public double getRadius() {
    return radius;
  }

  /**
   * @param radius the radius to set
   */
  public void setRadius(double radius) {
    this.radius = radius;
  }

}
