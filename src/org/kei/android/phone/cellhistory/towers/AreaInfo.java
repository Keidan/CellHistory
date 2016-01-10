package org.kei.android.phone.cellhistory.towers;

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
  public static final double DEFAULT_RADIUS = 30.0;
  private int                id             = 0;
  private String             name           = null;
  private double             latitude       = 0.0;
  private double             longitude      = 0.0;
  private double             radius         = 0.0;
  
  public String toString() {
    return name;
  }
  
  public String toString(final String sep) {
    return name + sep + latitude + sep + longitude + sep + radius;
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
