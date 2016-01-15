package org.kei.android.phone.cellhistory.towers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.location.Location;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.SignalStrength;



/**
 *******************************************************************************
 * @file TowerInfo.java
 * @author Keidan
 * @date 25/11/2015
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
public class TowerInfo {
  public static final String[]  LEVEL_NAMES           = { "none", "poor",
      "moderate", "good", "great"                    };
  public static final int       UNKNOWN_ASU           = 99;
  public static final String    UNKNOWN               = "Unknown";
  public static final String    STR_CDMA              = "CDMA";
  public static final String    STR_WCDMA             = "WCDMA";
  public static final String    STR_GSM               = "GSM";
  public static final String    STR_LTE               = "LTE";
  public static final String    DEFAULT_TOSTRING_SEP  = ";";
  private String                provider              = UNKNOWN;
  private String                operator              = UNKNOWN;
  private int                   mcc                   = -1;
  private int                   mnc                   = -1;
  private int                   cellId                = -1;
  private int                   lac                   = -1;
  private int                   psc                   = -1;
  private int                   signalStrength        = UNKNOWN_ASU;
  private int                   signalStrengthPercent = 0;
  private String                type                  = UNKNOWN;
  private long                  records               = 0;
  private int                   neighboringNb         = 0;
  private List<NeighboringInfo> neighboring           = null;
  private int                   asu                   = UNKNOWN_ASU;
  private int                   lvl                   = 0;
  private int                   network               = -1;
  private String                networkName           = UNKNOWN;
  private double                longitude             = Double.NaN;
  private double                latitude              = Double.NaN;
  private Lock                  lock                  = new ReentrantLock();
  private long                  timestamp             = new Date().getTime();
  private double                speed                 = 0.0;
  private double                distance              = 0.0;
  private int                   satellites            = 0;
  private MobileNetworkInfo     mobileNetworkInfo     = null;
  private Location              currentLocation       = null;
  private AreaInfo              currentArea           = null;
  private boolean               allowOperator         = true;
  private boolean               allowMCC              = true;
  private boolean               allowMNC              = true;
  private boolean               allowCellId           = true;
  private boolean               allowLAC              = true;
  private boolean               allowGeolocation      = true;
  private boolean               allowPSC              = true;
  private boolean               allowType             = true;
  private boolean               allowNetwork          = true;
  private boolean               allowASU              = true;
  private boolean               allowLVL              = true;
  private boolean               allowSS               = true;
  private boolean               allowNeighboring      = true;
  private boolean               allowProvider         = true;
  private boolean               allowDistance         = true;
  private boolean               allowSatellites       = true;
  private boolean               allowSpeed            = true;
  private boolean               allowDataSpeedRx      = true;
  private boolean               allowDataSpeedTx      = true;
  private boolean               allowDataDirection    = true;
  private boolean               allowIPv4             = true;
  private boolean               allowIPv6             = true;
  private boolean               allowAreas             = true;
  
  public TowerInfo() {
    mobileNetworkInfo = new MobileNetworkInfo();
    neighboring = new ArrayList<NeighboringInfo>();
  }
  
  public TowerInfo(final TowerInfo ti) {
    this();
    provider = ti.provider;
    operator = ti.operator;
    mcc = ti.mcc;
    mnc = ti.mnc;
    cellId = ti.cellId;
    lac = ti.lac;
    psc = ti.psc;
    signalStrength = ti.signalStrength;
    signalStrengthPercent = ti.signalStrengthPercent;
    type = ti.type;
    records = ti.records;
    neighboringNb = ti.neighboringNb;
    asu = ti.asu;
    lvl = ti.lvl;
    network = ti.network;
    networkName = ti.networkName;
    longitude = ti.longitude;
    latitude = ti.latitude;
    speed = ti.speed;
    distance = ti.distance;
    satellites = ti.satellites;
    mobileNetworkInfo = new MobileNetworkInfo(ti.getMobileNetworkInfo());
    neighboring.clear();
    neighboring.addAll(ti.getNeighboring());
    if(ti.currentLocation != null) {
      currentLocation = new Location(ti.currentLocation);
    } else currentLocation = null;
    if(ti.currentArea != null) {
      currentArea = new AreaInfo(ti.currentArea);
    } else currentArea = null;
  }
  
  public void lock() {
    this.lock.lock();
  }
  
  public void unlock() {
    this.lock.unlock();
  }

  @Override
  public String toString() {
    return toJSON(true);
  }
  
  public String toJSON(final boolean indentation) {
    StringBuilder sb = new StringBuilder();
    
    sb.append(indentation ? "    " : "").append("{").append(indentation ? "\n" : "");
    sb.append(indentation ? "      " : "").append("\"timestamp\":").append(getTimestamp()).append(",").append(indentation ? "\n" : "");
    if(allowOperator) sb.append(indentation ? "      " : "").append("\"ope\":\"").append(getOperator()).append("\",").append(indentation ? "\n" : "");
    if(allowProvider) sb.append(indentation ? "      " : "").append("\"provider\":\"").append(getProvider()).append("\",").append(indentation ? "\n" : "");
    if(allowMCC) sb.append(indentation ? "      " : "").append("\"mcc\":").append(getMCC()).append(",").append(indentation ? "\n" : "");
    if(allowMNC) sb.append(indentation ? "      " : "").append("\"mnc\":").append(getMNC()).append(",").append(indentation ? "\n" : "");
    if(allowCellId) sb.append(indentation ? "      " : "").append("\"cid\":").append(getCellId()).append(",").append(indentation ? "\n" : "");
    if(allowLAC) sb.append(indentation ? "      " : "").append("\"lac\":").append(getLac()).append(",").append(indentation ? "\n" : "");
    if(allowGeolocation) {
      sb.append(indentation ? "      " : "").append("\"lat\":\"").append(getLatitude()).append("\",").append(indentation ? "\n" : "");
      sb.append(indentation ? "      " : "").append("\"lon\":\"").append(getLongitude()).append("\",").append(indentation ? "\n" : "");
    }
    if(allowSatellites) sb.append(indentation ? "      " : "").append("\"satellites\":").append(getSatellites()).append(",").append(indentation ? "\n" : "");
    if(allowSpeed) sb.append(indentation ? "      " : "").append("\"spd\":").append(getSpeed()).append(",").append(indentation ? "\n" : "");
    if(allowDistance) sb.append(indentation ? "      " : "").append("\"dist\":").append(getDistance()).append(",").append(indentation ? "\n" : "");
    if(allowPSC) sb.append(indentation ? "      " : "").append("\"psc\":").append(getPsc()).append(",").append(indentation ? "\n" : "");
    if(allowType) sb.append(indentation ? "      " : "").append("\"type\":\"").append(getType()).append("\",").append(indentation ? "\n" : "");
    if(allowNetwork) sb.append(indentation ? "      " : "").append("\"net\":\"").append(getNetworkName()).append("\",").append(indentation ? "\n" : "");
    if(allowLVL) sb.append(indentation ? "      " : "").append("\"lvl\":").append(getLvl()).append(",").append(indentation ? "\n" : "");
    if(allowASU) sb.append(indentation ? "      " : "").append("\"asu\":").append(getAsu()).append(",").append(indentation ? "\n" : "");
    if(allowSS) {
      sb.append(indentation ? "      " : "").append("\"ss\":").append(getSignalStrength()).append(",").append(indentation ? "\n" : "");
      sb.append(indentation ? "      " : "").append("\"ssp\":").append(getSignalStrengthPercent()).append(",").append(indentation ? "\n" : "");
    }
    if(allowDataSpeedRx) sb.append(indentation ? "      " : "").append("\"rx\":").append(getMobileNetworkInfo().getRxSpeed()).append(",").append(indentation ? "\n" : "");
    if(allowDataSpeedTx) sb.append(indentation ? "      " : "").append("\"tx\":").append(getMobileNetworkInfo().getTxSpeed()).append(",").append(indentation ? "\n" : "");
    if(allowDataDirection) sb.append(indentation ? "      " : "").append("\"dir\":\"").append(MobileNetworkInfo.getDataActivityMin(getMobileNetworkInfo().getDataActivity())).append("\",").append(indentation ? "\n" : "");
    if(allowIPv4) sb.append(indentation ? "      " : "").append("\"ipv4\":\"").append(getMobileNetworkInfo().getIp4Address()).append("\",").append(indentation ? "\n" : "");
    if(allowIPv6) sb.append(indentation ? "      " : "").append("\"ipv6\":\"").append(getMobileNetworkInfo().getIp6Address()).append("\",").append(indentation ? "\n" : "");
    sb.append(indentation ? "      " : "").append("\"areas\": [").append(indentation ? "\n" : "");
    if(allowAreas && getCurrentArea() != null) {
      sb.append(getCurrentArea().toJSON(indentation));
    }
    sb.append(indentation ? "      " : "").append("],").append(indentation ? "\n" : "");
    sb.append(indentation ? "      " : "").append("\"neighborings\": [").append(indentation ? "\n" : "");
    if(allowNeighboring) {
      int size = getNeighboring().size();
      for(int i = 0; i < size; ++i) {
        NeighboringInfo ni = getNeighboring().get(i);
        sb.append(ni.toJSON(indentation));
        if(i < size - 1) sb.append(indentation ? "      " : "").append(",").append(indentation ? "\n" : "");
      }
    }
    sb.append(indentation ? "      " : "").append("]").append(indentation ? "\n" : "");
    sb.append(indentation ? "    " : "").append("}").append(indentation ? "\n" : "");
    return sb.toString();
  }
  
  public static String lineJSON(final String spaces, final String tag, final Object value, final boolean string, final boolean end) {
    StringBuilder sb = new StringBuilder();
    if(spaces != null)
      sb.append(spaces);
    sb.append("\"").append(tag).append("\":");
    if(string) sb.append("\"");
    sb.append(value);
    if(string) sb.append("\"");
    if(spaces != null)
      sb.append(!end ? ",\n" : "\n");
    else
      sb.append(!end ? "," : "");
    return sb.toString();
  }
  
  public String toXML(final boolean indentation) {
    StringBuilder sb = new StringBuilder();
    if(indentation) sb.append("  ");
    sb.append("<tower>");
    if(indentation) sb.append("\n");
    String spaces = indentation ? "    " : null;
    sb.append(lineXML(spaces, "timestamp", getTimestamp()));
    if(allowOperator) sb.append(lineXML(spaces, "ope", getOperator()));
    if(allowProvider) sb.append(lineXML(spaces, "provider", getProvider()));
    if(allowMCC) sb.append(lineXML(spaces, "mcc", getMCC()));
    if(allowMNC) sb.append(lineXML(spaces, "mnc", String.format("%02d", getMNC())));
    if(allowCellId) sb.append(lineXML(spaces, "cid", getCellId()));
    if(allowLAC) sb.append(lineXML(spaces, "lac", getLac()));
    if(allowGeolocation) {
      sb.append(lineXML(spaces, "lat", getLatitude()));
      sb.append(lineXML(spaces, "lon", getLongitude()));
    }
    if(allowSatellites) sb.append(lineXML(spaces, "satellites", getSatellites()));
    if(allowSpeed) sb.append(lineXML(spaces, "spd", getSpeed()));
    if(allowDistance) sb.append(lineXML(spaces, "dist", getDistance()));
    if(allowPSC) sb.append(lineXML(spaces, "psc", getPsc()));
    if(allowType) sb.append(lineXML(spaces, "type", getType()));
    if(allowNetwork) sb.append(lineXML(spaces, "net", getNetworkName()));
    if(allowLVL) sb.append(lineXML(spaces, "lvl", getLvl()));
    if(allowASU) sb.append(lineXML(spaces, "asu", getAsu()));
    if(allowSS) {
      sb.append(lineXML(spaces, "ss", getSignalStrength()));
      sb.append(lineXML(spaces, "ssp", getSignalStrengthPercent()));
    }

    if(allowDataSpeedRx) sb.append(lineXML(spaces, "rx", getMobileNetworkInfo().getRxSpeed()));
    if(allowDataSpeedTx) sb.append(lineXML(spaces, "tx", getMobileNetworkInfo().getTxSpeed()));
    if(allowDataDirection) sb.append(lineXML(spaces, "dir", MobileNetworkInfo.getDataActivityMin(getMobileNetworkInfo().getDataActivity())));
    if(allowIPv4) sb.append(lineXML(spaces, "ipv4", getMobileNetworkInfo().getIp4Address()));
    if(allowIPv6) sb.append(lineXML(spaces, "ipv6", getMobileNetworkInfo().getIp6Address()));
    if(indentation) sb.append("    ");
    sb.append("<areas>");
    if(indentation) sb.append("\n");
    if(allowAreas && getCurrentArea() != null) 
      sb.append(getCurrentArea().toXML(indentation));
    if(indentation) sb.append("    ");
    sb.append("</areas>");
    if(indentation) sb.append("\n");
    if(indentation) sb.append("    ");
    sb.append("<neighborings>");
    if(indentation) sb.append("\n");
    if(allowNeighboring) 
      for(NeighboringInfo ni : getNeighboring()) {
        sb.append(ni.toXML(indentation));
      }
    if(indentation) sb.append("    ");
    sb.append("</neighborings>");
    if(indentation) sb.append("\n  ");
    sb.append("</tower>");
    if(indentation) sb.append("\n");
    return sb.toString();
  }
  
  public static String lineXML(final String spaces, final String tag, final Object value) {
    StringBuilder sb = new StringBuilder();
    if(spaces != null)
      sb.append(spaces);
    sb.append("<").append(tag).append(">").append(value).append("</").append(tag).append(">");
    if(spaces != null)
      sb.append("\n");
    return sb.toString();
  }
  
  public String toString(final String sep, final String neighboringSep) {
    StringBuilder sb = new StringBuilder();
    sb.append(getTimestamp()).append(sep);
    if(allowOperator) sb.append(getOperator()).append(sep);
    else sb.append(sep);
    if(allowProvider) sb.append(getProvider()).append(sep);
    else sb.append(sep);
    if(allowMCC) sb.append(getMCC()).append(sep);
    else sb.append(sep);
    if(allowMNC) sb.append(String.format("%02d", getMNC())).append(sep);
    else sb.append(sep);
    if(allowCellId) sb.append(getCellId()).append(sep);
    else sb.append(sep);
    if(allowLAC) sb.append(getLac()).append(sep);
    else sb.append(sep);
    if(allowGeolocation) {
      sb.append(getLatitude()).append(sep);
      sb.append(getLongitude()).append(sep);
    } else sb.append(sep).append(sep);
    if(allowSatellites) sb.append(getSatellites()).append(sep);
    else sb.append(sep);
    if(allowSpeed) sb.append(getSpeed()).append(sep);
    else sb.append(sep);
    if(allowDistance) sb.append(getDistance()).append(sep);
    else sb.append(sep);
    if(allowPSC) sb.append(getPsc()).append(sep);
    else sb.append(sep);
    if(allowType) sb.append(getType()).append(sep);
    else sb.append(sep);
    if(allowNetwork) sb.append(getNetworkName()).append(sep);
    else sb.append(sep);
    if(allowLVL) sb.append(getLvl()).append(sep);
    else sb.append(sep);
    if(allowASU) sb.append(getAsu()).append(sep);
    else sb.append(sep);
    if(allowSS) {
      sb.append(getSignalStrength()).append(sep);
      sb.append(getSignalStrengthPercent()).append(sep);
    } else sb.append(sep).append(sep);

    if(allowDataSpeedRx) sb.append(getMobileNetworkInfo().getRxSpeed()).append(sep);
    else sb.append(sep);
    if(allowDataSpeedTx) sb.append(getMobileNetworkInfo().getTxSpeed()).append(sep);
    else sb.append(sep);
    if(allowDataDirection) sb.append(MobileNetworkInfo.getDataActivityMin(getMobileNetworkInfo().getDataActivity())).append(sep);
    else sb.append(sep);
    if(allowIPv4) sb.append(getMobileNetworkInfo().getIp4Address()).append(sep);
    else sb.append(sep);
    if(allowIPv6) sb.append(getMobileNetworkInfo().getIp6Address()).append(sep);
    else sb.append(sep);

    if(allowAreas && getCurrentArea() != null) {
      sb.append(getCurrentArea().toString(neighboringSep));
    }
    else sb.append(sep);
    
    if(allowNeighboring) {
      int size = getNeighboring().size();
      for(int i = 0; i < size; ++i) {
        NeighboringInfo ni = getNeighboring().get(i);
        sb.append(ni.toString(neighboringSep));
        if(i < size - 1) sb.append(neighboringSep);
      }
    }
    return sb.toString();
  }
  
  public void allow(
      final boolean allowOperator, final boolean allowMCC, final boolean allowMNC,
      final boolean allowCellId, final boolean allowLAC, final boolean allowGeolocation, final boolean allowPSC,
      final boolean allowType, final boolean allowNetwork, final boolean allowASU,
      final boolean allowLVL, final boolean allowSS, final boolean allowNeighboring,
      final boolean allowProvider, final boolean allowDistance, final boolean allowSatellites, final boolean allowSpeed, 
      final boolean allowDataSpeedRx, final boolean allowDataSpeedTx, final boolean allowDataDirection, 
      final boolean allowIPv4, final boolean allowIPv6, final boolean allowAreas) {
    this.allowOperator = allowOperator;
    this.allowMCC = allowMCC;
    this.allowMNC = allowMNC;
    this.allowCellId = allowCellId;
    this.allowLAC = allowLAC;
    this.allowGeolocation = allowGeolocation;
    this.allowPSC = allowPSC;
    this.allowType = allowType;
    this.allowNetwork = allowNetwork;
    this.allowASU = allowASU;
    this.allowLVL = allowLVL;
    this.allowSS = allowSS;
    this.allowNeighboring = allowNeighboring;
    this.allowProvider = allowProvider;
    this.allowDistance = allowDistance;
    this.allowSatellites = allowSatellites;
    this.allowSpeed = allowSpeed;
    this.allowDataSpeedRx = allowDataSpeedRx;
    this.allowDataSpeedTx = allowDataSpeedTx;
    this.allowDataDirection = allowDataDirection;
    this.allowIPv4 = allowIPv4;
    this.allowIPv6 = allowIPv6;
    this.allowAreas = allowAreas;
  }
  
  public static TowerInfo decodeInformations(final TowerInfo owner, final SignalStrength ss) throws Exception {
    TowerInfo ti = owner;
    if(ti == null) ti = new TowerInfo();
    SignalStrengthReflect ssr = new SignalStrengthReflect(ss);
    int n = ssr.getAsuLevel();
    ti.setAsu(n);
    if(n != UNKNOWN_ASU) ti.setSignalStrengthPercent((int)(((double)n) / ssr.getAsuLimit() * 100.0));
    else ti.setSignalStrengthPercent(0);
    ti.setSignalStrength(ssr.getDbm());
    ti.setLvl(ssr.getLevel());
    if(ti.getSignalStrengthPercent() < 0) ti.setSignalStrengthPercent(0);
    else if(ti.getSignalStrengthPercent() > 100) ti.setSignalStrengthPercent(100);
    return ti;
  }
  
  public static  String getTelephonyType(final CellInfo allCellInfo) {
    if(allCellInfo != null) {
      if(CellInfoCdma.class.isInstance(allCellInfo)) {
        return STR_CDMA;
      } else if(CellInfoWcdma.class.isInstance(allCellInfo)) {
        return STR_WCDMA;
      }  else if(CellInfoGsm.class.isInstance(allCellInfo)) {
        return STR_GSM;
      } else if(CellInfoLte.class.isInstance(allCellInfo)) {
        return STR_LTE;
      }
    }
    return allCellInfo != null ? allCellInfo.getClass().getSimpleName() : UNKNOWN;
  }

  /**
   * @return the operator
   */
  public String getOperator() {
    return operator;
  }

  /**
   * @param operator
   *          the operator to set
   */
  public void setOperator(final String operator) {
    this.operator = operator;
  }

  /**
   * @return the mcc
   */
  public int getMCC() {
    return mcc;
  }

  /**
   * @param mcc
   *          the mcc to set
   */
  public void setMCC(final int mcc) {
    this.mcc = mcc;
  }

  /**
   * @return the mnc
   */
  public int getMNC() {
    return mnc;
  }

  /**
   * @param mnc
   *          the mnc to set
   */
  public void setMNC(final int mnc) {
    this.mnc = mnc;
  }

  /**
   * @return the cellId
   */
  public int getCellId() {
    return cellId;
  }

  /**
   * @param cellId
   *          the cellId to set
   */
  public void setCellId(final int cellId) {
    this.cellId = cellId;
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
   * @return the psc
   */
  public int getPsc() {
    return psc;
  }

  /**
   * @param psc
   *          the psc to set
   */
  public void setPsc(final int psc) {
    this.psc = psc;
  }

  /**
   * @return the signalStrength
   */
  public int getSignalStrength() {
    return signalStrength;
  }

  /**
   * @param signalStrength
   *          the signalStrength to set
   */
  public void setSignalStrength(final int signalStrength) {
    this.signalStrength = signalStrength;
  }

  /**
   * @return the signalStrengthPercent
   */
  public int getSignalStrengthPercent() {
    return signalStrengthPercent;
  }

  /**
   * @param signalStrengthPercent
   *          the signalStrengthPercent to set
   */
  public void setSignalStrengthPercent(final int signalStrengthPercent) {
    this.signalStrengthPercent = signalStrengthPercent;
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
   * @return the records
   */
  public long getRecords() {
    return records;
  }

  /**
   * @param records
   *          the records to set
   */
  public void setRecords(final long records) {
    this.records = records;
  }

  /**
   * @return the neighboringNb
   */
  public int getNeighboringNb() {
    return neighboringNb;
  }

  /**
   * @param neighboringNb
   *          the neighboringNb to set
   */
  public void setNeighboringNb(final int neighboringNb) {
    this.neighboringNb = neighboringNb;
  }
  
  /**
   * @return the neighboring list
   */
  public List<NeighboringInfo> getNeighboring() {
    return neighboring;
  }

  /**
   * @param neighboring
   *          the neighboring to add
   */
  public void addNeighboring(final NeighboringInfo neighboring) {
    this.neighboring.add(neighboring);
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
   * @return the lvl
   */
  public int getLvl() {
    return lvl;
  }

  /**
   * @param lvl
   *          the lvl to set
   */
  public void setLvl(final int lvl) {
    this.lvl = lvl;
  }

  /**
   * @return the network
   */
  public int getNetwork() {
    return network;
  }

  /**
   * @param network
   *          the network to set
   */
  public void setNetwork(final int network) {
    this.network = network;
  }

  /**
   * @return the networkName
   */
  public String getNetworkName() {
    return networkName;
  }

  /**
   * @param networkName
   *          the networkName to set
   */
  public void setNetworkName(final String networkName) {
    this.networkName = networkName;
  }

  /**
   * @return the longitude
   */
  public double getLongitude() {
    return longitude;
  }

  /**
   * @param longitude the longitude to set
   */
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  /**
   * @return the latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * @param latitude the latitude to set
   */
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  /**
   * @return the provider
   */
  public String getProvider() {
    return provider;
  }

  /**
   * @param provider the provider to set
   */
  public void setProvider(String provider) {
    this.provider = provider;
  }

  /**
   * @return the timestamp
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * @param timestamp the timestamp to set
   */
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * @return the speed
   */
  public double getSpeed() {
    return speed;
  }

  /**
   * @param speed the speed to set
   */
  public void setSpeed(double speed) {
    this.speed = speed;
  }

  /**
   * @return the distance
   */
  public double getDistance() {
    return distance;
  }

  /**
   * @param distance the distance to set
   */
  public void setDistance(double distance) {
    this.distance = distance;
  }

  /**
   * @return the satellites
   */
  public int getSatellites() {
    return satellites;
  }

  /**
   * @param satellites the satellites to set
   */
  public void setSatellites(int satellites) {
    this.satellites = satellites;
  }

  /**
   * @return the mobileNetworkInfo
   */
  public MobileNetworkInfo getMobileNetworkInfo() {
    return mobileNetworkInfo;
  }

  /**
   * @return the currentLocation
   */
  public Location getCurrentLocation() {
    if(currentLocation == null)
      currentLocation = new Location((String)null);
    return currentLocation;
  }

  /**
   * @param currentLocation the currentLocation to set
   */
  public void setCurrentLocation(Location currentLocation) {
    this.currentLocation = currentLocation;
  }

  /**
   * @return the currentArea
   */
  public AreaInfo getCurrentArea() {
    return currentArea;
  }

  /**
   * @param currentArea the currentArea to set
   */
  public void setCurrentArea(AreaInfo currentArea) {
    this.currentArea = currentArea;
  }
  
}
