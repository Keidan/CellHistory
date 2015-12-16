package org.kei.android.phone.cellhistory.towers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;



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
  
  public TowerInfo() {
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
    neighboring.clear();
    neighboring.addAll(ti.getNeighboring());
  }
  
  public boolean equals(final TowerInfo ti) {
    boolean b = provider.equals(ti.provider) && operator.equals(ti.operator) 
        && mcc == ti.mcc && mnc == ti.mnc && cellId == ti.cellId 
        && lac == ti.lac && psc == ti.psc && signalStrength == ti.signalStrength 
        && signalStrengthPercent == ti.signalStrengthPercent && type == ti.type 
        && asu == ti.asu && lvl == ti.lvl && network == ti.network && networkName.equals(ti.networkName)
        && speed == ti.speed && distance == ti.distance;
    if(b) {
      int size1 = neighboring.size();
      int size2 = ti.neighboring.size();
      if(size1 != size2) return false;
      boolean found = false;
      for(int i = 0; i < size1; ++i) {
        NeighboringInfo ni1 = neighboring.get(i);
        for(int j = 0; j < size2; ++j) {
          NeighboringInfo ni2 = ti.neighboring.get(j);
          if(ni1.toString().equals(ni2.toString())) {
            found = true;
            break;
          }
        }
        if(!found) return false;
      }
    }
    return b;
  }
  
  public void lock() {
    this.lock.lock();
  }
  
  public void unlock() {
    this.lock.unlock();
  }

  @Override
  public String toString() {
    return toJSON();
  }
  
  public String toJSON() {
    StringBuilder sb = new StringBuilder();

    sb.append("{");
    sb.append("\"timestamp\":").append(getTimestamp()).append(",");
    sb.append("\"ope\":\"").append(getOperator()).append("\",");
    sb.append("\"mcc\":").append(getMCC()).append(",");
    sb.append("\"mnc\":").append(getMNC()).append(",");
    sb.append("\"cid\":").append(getCellId()).append(",");
    sb.append("\"lac\":").append(getLac()).append(",");
    sb.append("\"lat\":\"").append(getLatitude()).append("\",");
    sb.append("\"lon\":\"").append(getLongitude()).append("\",");
    sb.append("\"spd\":").append(getSpeed()).append(",");
    sb.append("\"dist\":").append(getDistance()).append(",");
    sb.append("\"psc\":").append(getPsc()).append(",");
    sb.append("\"type\":\"").append(getType()).append("\",");
    sb.append("\"net\":\"").append(getNetworkName()).append("\",");
    sb.append("\"lvl\":").append(getLvl()).append(",");
    sb.append("\"asu\":").append(getAsu()).append(",");
    sb.append("\"ss\":").append(getSignalStrength()).append(",");
    sb.append("\"ssp\":").append(getSignalStrengthPercent()).append(",");
    int size = getNeighboring().size();
    sb.append("\"neighborings\": [");
    for(int i = 0; i < size; ++i) {
      NeighboringInfo ni = getNeighboring().get(i);
      sb.append(ni.toJSON());
      if(i < size - 1) sb.append(",");
    }
    sb.append("]");
    sb.append("}");
    return sb.toString();
  }
  
  public String toXML() {
    StringBuilder sb = new StringBuilder();
    sb.append("  <tower>\n");
    sb.append("    <timestamp>").append(getTimestamp()).append("</timestamp>\n");
    sb.append("    <ope>").append(getOperator()).append("</ope>\n");
    sb.append("    <mcc>").append(getMCC()).append("</mcc>\n");
    sb.append("    <mnc>").append(String.format("%02d", getMNC())).append("</mnc>\n");
    sb.append("    <cid>").append(getCellId()).append("<cid>\n");
    sb.append("    <lac>").append(getLac()).append("</lac>\n");
    sb.append("    <lat>").append(getLatitude()).append("</lat>\n");
    sb.append("    <lon>").append(getLongitude()).append("</lon>\n");
    sb.append("    <spd>").append(getSpeed()).append("</spd>\n");
    sb.append("    <dist>").append(getDistance()).append("</dist>\n");
    sb.append("    <psc>").append(getPsc()).append("</psc>\n");
    sb.append("    <type>").append(getType()).append("</type>\n");
    sb.append("    <net>").append(getNetworkName()).append("</net>\n");
    sb.append("    <lvl>").append(getLvl()).append("</lvl>\n");
    sb.append("    <asu>").append(getAsu()).append("</asu>\n");
    sb.append("    <ss>").append(getSignalStrength()).append("</ss>\n");
    sb.append("    <ssp>").append(getSignalStrengthPercent()).append("</ssp>\n");
    sb.append("    <neighborings>\n");
    for(NeighboringInfo ni : getNeighboring()) {
      sb.append(ni.toXML());
    }
    sb.append("    </neighborings>\n");
    sb.append("  </tower>\n");
    return sb.toString();
  }
  
  public String toString(final String sep, final String neighboringSep) {
    StringBuilder sb = new StringBuilder();
    // #TIMESTAMP,OPE,MCC,MNC,CID,LAC,LAT,LON,SPD,DIST,PSC,TYPE,NET,LVL,ASU,SS,SSP
    sb.append(getTimestamp()).append(sep);
    sb.append(getOperator()).append(sep);
    sb.append(getMCC()).append(sep);
    sb.append(String.format("%02d", getMNC())).append(sep);
    sb.append(getCellId()).append(sep);
    sb.append(getLac()).append(sep);
    sb.append(getLatitude()).append(sep);
    sb.append(getLongitude()).append(sep);
    sb.append(getSpeed()).append(sep);
    sb.append(getDistance()).append(sep);
    sb.append(getPsc()).append(sep);
    sb.append(getType()).append(sep);
    sb.append(getNetworkName()).append(sep);
    sb.append(getLvl()).append(sep);
    sb.append(getAsu()).append(sep);
    sb.append(getSignalStrength()).append(sep);
    sb.append(getSignalStrengthPercent()).append(sep);
    int size = getNeighboring().size();
    for(int i = 0; i < size; ++i) {
      NeighboringInfo ni = getNeighboring().get(i);
      sb.append(ni.toString(neighboringSep));
      if(i < size - 1) sb.append(neighboringSep);
    }
    return sb.toString();
  }

  public static String getNetworkType(final int networkType,
      final boolean nameOnly) {
    String nt = "";
    if (!nameOnly)
      nt = " (" + networkType + ")";
    switch (networkType) {
      case TelephonyManager.NETWORK_TYPE_CDMA:
        return "CDMA" + nt;
      case TelephonyManager.NETWORK_TYPE_EDGE:
        return "EDGE" + nt;
      case TelephonyManager.NETWORK_TYPE_GPRS:
        return "GPRS" + nt;
      case TelephonyManager.NETWORK_TYPE_IDEN:
        return "IDEN" + nt;
      case TelephonyManager.NETWORK_TYPE_1xRTT:
        return "1xRTT" + nt;
      case TelephonyManager.NETWORK_TYPE_EHRPD:
        return "EHRPD" + nt;
      case TelephonyManager.NETWORK_TYPE_EVDO_0:
        return "EVDO_0" + nt;
      case TelephonyManager.NETWORK_TYPE_EVDO_A:
        return "EVDO_A" + nt;
      case TelephonyManager.NETWORK_TYPE_EVDO_B:
        return "EVDO_B" + nt;
      case TelephonyManager.NETWORK_TYPE_HSDPA:
        return "HSDPA" + nt;
      case TelephonyManager.NETWORK_TYPE_HSPA:
        return "HSPA" + nt;
      case TelephonyManager.NETWORK_TYPE_HSPAP:
        return "HSPAP" + nt;
      case TelephonyManager.NETWORK_TYPE_HSUPA:
        return "HSUPA" + nt;
      case TelephonyManager.NETWORK_TYPE_UMTS:
        return "UMTS" + nt;
      case TelephonyManager.NETWORK_TYPE_LTE:
        return "LTE" + nt;
      default:
        return UNKNOWN + nt;
    }
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

}
