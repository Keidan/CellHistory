package org.kei.android.phone.cellhistory.towers;

/**
 *******************************************************************************
 * @file NeighboringInfo.java
 * @author Keidan
 * @date 10/12/2015
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
public class NeighboringInfo {
  public static final String DEFAULT_TOSTRING_SEP = ",";
  private int                lac                  = -1;
  private int                cid                  = -1;
  private int                asu                  = 99;
  private int                strength             = 0;
  private String             type                 = "Unknown";
  private boolean            oldMethod            = false;
  private boolean            title                = false;

  public NeighboringInfo(final boolean title) {
    this.title = title;
  }
  
  public NeighboringInfo(final boolean oldMethod, final int lac, final int cid,
      final int asu, final String type, final int strength) {
    this.oldMethod = oldMethod;
    if (lac == Integer.MAX_VALUE)
      this.lac = -1;
    else
      this.lac = lac;
    if (cid == Integer.MAX_VALUE)
      this.cid = -1;
    else
      this.cid = cid;
    this.asu = asu;
    this.type = type;
    this.strength = strength;
  }
  
  @Override
  public String toString() {
    return toJSON();
  }

  public String toString(final String sep) {
    final StringBuilder sb = new StringBuilder();
    sb.append(oldMethod).append(sep);
    sb.append(lac).append(sep);
    sb.append(cid).append(sep);
    sb.append(asu).append(sep);
    sb.append(type).append(sep);
    sb.append(strength);
    return sb.toString();
  }

  public String toJSON() {
    final StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append("\"old\":").append(oldMethod ? 1 : 0).append(",");
    sb.append("\"lac\":").append(lac).append(",");
    sb.append("\"cid\":").append(cid).append(",");
    sb.append("\"asu\":").append(asu).append(",");
    sb.append("\"nt\":\"").append(type).append("\",");
    sb.append("\"str\":").append(strength);
    sb.append("}");
    return sb.toString();
  }
  
  public String toXML() {
    final StringBuilder sb = new StringBuilder();
    sb.append("      <neighboring>\n");
    sb.append("        <old>").append(oldMethod ? 1 : 0).append("</old>\n");
    sb.append("        <lac>").append(lac).append("</lac>\n");
    sb.append("        <cid>").append(cid).append("</cid>\n");
    sb.append("        <asu>").append(asu).append("</asu>\n");
    sb.append("        <nt>").append(type).append("</nt>\n");
    sb.append("        <str>").append(strength).append("</str>\n");
    sb.append("      </neighboring>\n");
    return sb.toString();
  }
  
  /**
   * @return the lac
   */
  public int getLac() {
    return lac;
  }
  
  /**
   * @param lac
   *          the lac to set
   */
  public void setLac(final int lac) {
    this.lac = lac;
  }
  
  /**
   * @return the cid
   */
  public int getCid() {
    return cid;
  }
  
  /**
   * @param cid
   *          the cid to set
   */
  public void setCid(final int cid) {
    this.cid = cid;
  }
  
  /**
   * @return the asu
   */
  public int getAsu() {
    return asu;
  }
  
  /**
   * @param asu
   *          the asu to set
   */
  public void setAsu(final int asu) {
    this.asu = asu;
  }
  
  /**
   * @return the strength
   */
  public int getStrength() {
    return strength;
  }
  
  /**
   * @param strength
   *          the strength to set
   */
  public void setStrength(final int strength) {
    this.strength = strength;
  }
  
  /**
   * @return the type
   */
  public String getType() {
    return type;
  }
  
  /**
   * @param type
   *          the type to set
   */
  public void setType(final String type) {
    this.type = type;
  }
  
  /**
   * @return the oldMethod
   */
  public boolean isOldMethod() {
    return oldMethod;
  }
  
  /**
   * @param oldMethod
   *          the oldMethod to set
   */
  public void setOldMethod(final boolean oldMethod) {
    this.oldMethod = oldMethod;
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
