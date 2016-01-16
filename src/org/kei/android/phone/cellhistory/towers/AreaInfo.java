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
  public static final String DEFAULT_TOSTRING_SEP = ",";
  public static final String UNKNOWN              = "Unknown";
  public static final double DEFAULT_RADIUS       = 30.0;
  private int                id                   = 0;
  private String             name                 = UNKNOWN;
  private double             radius               = DEFAULT_RADIUS;
  private Location           location             = new Location("");
  private double             distance             = -1;
  private boolean            used                 = false;
  private boolean            title                = false;
  
  public AreaInfo() {
  }  

  public AreaInfo(final boolean title) {
    this.title = title;
  }
  
  public AreaInfo(final AreaInfo ai) {
    id = ai.id;
    name = ai.name;
    radius = ai.radius;
    distance = ai.distance;
    location = new Location(ai.location);
  }
  
  public String toString() {
    return name;
  }
  
  public String toString(final String sep) {
    final StringBuilder sb = new StringBuilder();
    sb.append(name).append(sep);
    sb.append(location.getLatitude()).append(sep);
    sb.append(location.getLongitude()).append(sep);
    sb.append(radius).append(sep);
    sb.append(distance).append(sep);
    sb.append(used);
    return sb.toString();
  }
  
  public String toJSON(final boolean indentation) {
    final StringBuilder sb = new StringBuilder();
    sb.append(indentation ? "        " : "").append("{").append(indentation ? "\n" : "");
    sb.append(indentation ? "          " : "").append("\"name\":\"").append(name).append("\",").append(indentation ? "\n" : "");
    sb.append(indentation ? "          " : "").append("\"latitude\":").append(location.getLatitude()).append(",").append(indentation ? "\n" : "");
    sb.append(indentation ? "          " : "").append("\"longitude\":").append(location.getLongitude()).append(",").append(indentation ? "\n" : "");
    sb.append(indentation ? "          " : "").append("\"radius\":").append(radius).append(",").append(indentation ? "\n" : "");
    sb.append(indentation ? "          " : "").append("\"distance\":").append(distance).append(indentation ? "\n" : "");
    sb.append(indentation ? "          " : "").append("\"used\":").append(used).append(indentation ? "\n" : "");
    sb.append(indentation ? "        " : "").append("}").append(indentation ? "\n" : "");
    return sb.toString();
  }
  
  public String toXML(final boolean indentation) {
    final StringBuilder sb = new StringBuilder();
    if(indentation) sb.append("      ");
    sb.append("<area>");
    if(indentation) sb.append("\n");
    String spaces = indentation ? "        " : null;
    sb.append(TowerInfo.lineXML(spaces, "name", name));
    sb.append(TowerInfo.lineXML(spaces, "latitude", location.getLatitude()));
    sb.append(TowerInfo.lineXML(spaces, "longitude", location.getLongitude()));
    sb.append(TowerInfo.lineXML(spaces, "radius", radius));
    sb.append(TowerInfo.lineXML(spaces, "distance", distance));
    sb.append(TowerInfo.lineXML(spaces, "used", used));
    if(indentation) sb.append("      ");
    sb.append("</area>");
    if(indentation) sb.append("\n");
    return sb.toString();
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

  /**
   * @return the distance
   */
  public double getDistance() {
    return distance;
  }
  
  public void reset() {
    distance = -1;
    used = false;
  }

  /**
   * @param distance the distance to set
   */
  public void setDistance(double distance) {
    this.distance = distance;
    if(distance <= radius && getLatitude() != 0 && getLongitude() != 0)
      used = true;
  }

  /**
   * @return the used
   */
  public boolean isUsed() {
    return used;
  }
  
  /**
   * @return the title
   */
  public boolean isTitle() {
    return title;
  }
  
  /**
   * @param title
   *          the title to set
   */
  public void setTitle(final boolean title) {
    this.title = title;
  }

}
