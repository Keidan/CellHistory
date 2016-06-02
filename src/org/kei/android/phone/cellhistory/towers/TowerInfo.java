package org.kei.android.phone.cellhistory.towers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.kei.android.phone.cellhistory.contexts.FilterCtx;

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
  private double                cellLongitude         = Double.NaN;
  private double                cellLatitude          = Double.NaN;
  private Lock                  lock                  = new ReentrantLock();
  private long                  timestamp             = new Date().getTime();
  private double                speed                 = 0.0;
  private double                distance              = 0.0;
  private int                   satellites            = 0;
  private MobileNetworkInfo     mobileNetworkInfo     = null;
  private Location              currentLocation       = null;
  private List<AreaInfo>        areas                 = null;
  private FilterCtx             filterCtx             = null;
  
  public TowerInfo(final FilterCtx filterCtx) {
    this.filterCtx = filterCtx;
    mobileNetworkInfo = new MobileNetworkInfo();
    neighboring = new ArrayList<NeighboringInfo>();
    areas = new ArrayList<AreaInfo>();
  }
  
  public TowerInfo(final FilterCtx filterCtx, final TowerInfo ti) {
    this(filterCtx);
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
    cellLongitude = ti.cellLongitude;
    cellLatitude = ti.cellLatitude;
    speed = ti.speed;
    distance = ti.distance;
    satellites = ti.satellites;
    mobileNetworkInfo = new MobileNetworkInfo(ti.getMobileNetworkInfo());
    neighboring.clear();
    neighboring.addAll(ti.getNeighboring());
    if(ti.currentLocation != null) {
      currentLocation = new Location(ti.currentLocation);
    } else currentLocation = null;
    areas.clear();
    areas.addAll(ti.getAreas());
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
    if(filterCtx.getOperator().allowSave) sb.append(indentation ? "      " : "").append("\"ope\":\"").append(getOperator()).append("\",").append(indentation ? "\n" : "");
    if(filterCtx.getProvider().allowSave) sb.append(indentation ? "      " : "").append("\"provider\":\"").append(getProvider()).append("\",").append(indentation ? "\n" : "");
    if(filterCtx.getMCC().allowSave) sb.append(indentation ? "      " : "").append("\"mcc\":").append(getMCC()).append(",").append(indentation ? "\n" : "");
    if(filterCtx.getMNC().allowSave) sb.append(indentation ? "      " : "").append("\"mnc\":").append(getMNC()).append(",").append(indentation ? "\n" : "");
    if(filterCtx.getCellID().allowSave) sb.append(indentation ? "      " : "").append("\"cid\":").append(getCellId()).append(",").append(indentation ? "\n" : "");
    if(filterCtx.getLAC().allowSave) sb.append(indentation ? "      " : "").append("\"lac\":").append(getLac()).append(",").append(indentation ? "\n" : "");
    if(filterCtx.getCellGeolocation().allowSave) {
      sb.append(indentation ? "      " : "").append("\"lat\":\"").append(getCellLatitude()).append("\",").append(indentation ? "\n" : "");
      sb.append(indentation ? "      " : "").append("\"lon\":\"").append(getCellLongitude()).append("\",").append(indentation ? "\n" : "");
    }
    if(filterCtx.getCurrentGeolocation().allowSave) {
      sb.append(indentation ? "      " : "").append("\"clat\":\"").append(checkCurrentLoc(getCurrentLocation().getLatitude())).append("\",").append(indentation ? "\n" : "");
      sb.append(indentation ? "      " : "").append("\"clon\":\"").append(checkCurrentLoc(getCurrentLocation().getLongitude())).append("\",").append(indentation ? "\n" : "");
    }
    if(filterCtx.getSatellites().allowSave) sb.append(indentation ? "      " : "").append("\"satellites\":").append(getSatellites()).append(",").append(indentation ? "\n" : "");
    if(filterCtx.getSpeed().allowSave) sb.append(indentation ? "      " : "").append("\"spd\":").append(getSpeed()).append(",").append(indentation ? "\n" : "");
    if(filterCtx.getDistance().allowSave) sb.append(indentation ? "      " : "").append("\"dist\":").append(getDistance()).append(",").append(indentation ? "\n" : "");
    if(filterCtx.getPSC().allowSave) sb.append(indentation ? "      " : "").append("\"psc\":").append(getPsc()).append(",").append(indentation ? "\n" : "");
    if(filterCtx.getType().allowSave) sb.append(indentation ? "      " : "").append("\"type\":\"").append(getType()).append("\",").append(indentation ? "\n" : "");
    if(filterCtx.getNetworkId().allowSave) sb.append(indentation ? "      " : "").append("\"net\":\"").append(getNetworkName()).append("\",").append(indentation ? "\n" : "");
    if(filterCtx.getLevel().allowSave) sb.append(indentation ? "      " : "").append("\"lvl\":").append(getLvl()).append(",").append(indentation ? "\n" : "");
    if(filterCtx.getASU().allowSave) sb.append(indentation ? "      " : "").append("\"asu\":").append(getAsu()).append(",").append(indentation ? "\n" : "");
    if(filterCtx.getSignalStrength().allowSave) {
      sb.append(indentation ? "      " : "").append("\"ss\":").append(getSignalStrength()).append(",").append(indentation ? "\n" : "");
      sb.append(indentation ? "      " : "").append("\"ssp\":").append(getSignalStrengthPercent()).append(",").append(indentation ? "\n" : "");
    }
    if(filterCtx.getDataRxSpeed().allowSave) sb.append(indentation ? "      " : "").append("\"rx\":").append(getMobileNetworkInfo().getRxSpeed()).append(",").append(indentation ? "\n" : "");
    if(filterCtx.getDataTxSpeed().allowSave) sb.append(indentation ? "      " : "").append("\"tx\":").append(getMobileNetworkInfo().getTxSpeed()).append(",").append(indentation ? "\n" : "");
    if(filterCtx.getDataDirection().allowSave) sb.append(indentation ? "      " : "").append("\"dir\":\"").append(MobileNetworkInfo.getDataActivityMin(getMobileNetworkInfo().getDataActivity())).append("\",").append(indentation ? "\n" : "");
    if(filterCtx.getIPv4().allowSave) sb.append(indentation ? "      " : "").append("\"ipv4\":\"").append(getMobileNetworkInfo().getIp4Address()).append("\",").append(indentation ? "\n" : "");
    if(filterCtx.getIPv6().allowSave) sb.append(indentation ? "      " : "").append("\"ipv6\":\"").append(getMobileNetworkInfo().getIp6Address()).append("\",").append(indentation ? "\n" : "");
    sb.append(indentation ? "      " : "").append("\"areas\": [").append(indentation ? "\n" : "");
    if(filterCtx.getAreas().allowSave) {
      int size = getAreas().size();
      for(int i = 0; i < size; ++i) {
        AreaInfo ai = getAreas().get(i);
        sb.append(ai.toJSON(indentation));
        if(i < size - 1) sb.append(indentation ? "      " : "").append(",").append(indentation ? "\n" : "");
      }
    }
    sb.append(indentation ? "      " : "").append("],").append(indentation ? "\n" : "");
    sb.append(indentation ? "      " : "").append("\"neighborings\": [").append(indentation ? "\n" : "");
    if(filterCtx.getNeighboring().allowSave) {
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
    if(filterCtx.getOperator().allowSave) sb.append(lineXML(spaces, "ope", getOperator()));
    if(filterCtx.getProvider().allowSave) sb.append(lineXML(spaces, "provider", getProvider()));
    if(filterCtx.getMCC().allowSave) sb.append(lineXML(spaces, "mcc", getMCC()));
    if(filterCtx.getMNC().allowSave) sb.append(lineXML(spaces, "mnc", String.format("%02d", getMNC())));
    if(filterCtx.getCellID().allowSave) sb.append(lineXML(spaces, "cid", getCellId()));
    if(filterCtx.getLAC().allowSave) sb.append(lineXML(spaces, "lac", getLac()));
    if(filterCtx.getCellGeolocation().allowSave) {
      sb.append(lineXML(spaces, "lat", getCellLatitude()));
      sb.append(lineXML(spaces, "lon", getCellLongitude()));
    }
    if(filterCtx.getCurrentGeolocation().allowSave) {
      sb.append(lineXML(spaces, "clat", checkCurrentLoc(getCurrentLocation().getLatitude())));
      sb.append(lineXML(spaces, "clon", checkCurrentLoc(getCurrentLocation().getLongitude())));
    }
    if(filterCtx.getSatellites().allowSave) sb.append(lineXML(spaces, "satellites", getSatellites()));
    if(filterCtx.getSpeed().allowSave) sb.append(lineXML(spaces, "spd", getSpeed()));
    if(filterCtx.getDistance().allowSave) sb.append(lineXML(spaces, "dist", getDistance()));
    if(filterCtx.getPSC().allowSave) sb.append(lineXML(spaces, "psc", getPsc()));
    if(filterCtx.getType().allowSave) sb.append(lineXML(spaces, "type", getType()));
    if(filterCtx.getNetworkId().allowSave) sb.append(lineXML(spaces, "net", getNetworkName()));
    if(filterCtx.getLevel().allowSave) sb.append(lineXML(spaces, "lvl", getLvl()));
    if(filterCtx.getASU().allowSave) sb.append(lineXML(spaces, "asu", getAsu()));
    if(filterCtx.getSignalStrength().allowSave) {
      sb.append(lineXML(spaces, "ss", getSignalStrength()));
      sb.append(lineXML(spaces, "ssp", getSignalStrengthPercent()));
    }

    if(filterCtx.getDataRxSpeed().allowSave) sb.append(lineXML(spaces, "rx", getMobileNetworkInfo().getRxSpeed()));
    if(filterCtx.getDataTxSpeed().allowSave) sb.append(lineXML(spaces, "tx", getMobileNetworkInfo().getTxSpeed()));
    if(filterCtx.getDataDirection().allowSave) sb.append(lineXML(spaces, "dir", MobileNetworkInfo.getDataActivityMin(getMobileNetworkInfo().getDataActivity())));
    if(filterCtx.getIPv4().allowSave) sb.append(lineXML(spaces, "ipv4", getMobileNetworkInfo().getIp4Address()));
    if(filterCtx.getIPv6().allowSave) sb.append(lineXML(spaces, "ipv6", getMobileNetworkInfo().getIp6Address()));
    if(indentation) sb.append("    ");
    sb.append("<areas>");
    if(indentation) sb.append("\n");
    if(filterCtx.getAreas().allowSave) 
      for(AreaInfo ai : getAreas()) {
        sb.append(ai.toXML(indentation));
      }
    if(indentation) sb.append("    ");
    sb.append("</areas>");
    if(indentation) sb.append("\n");
    if(indentation) sb.append("    ");
    sb.append("<neighborings>");
    if(indentation) sb.append("\n");
    if(filterCtx.getNeighboring().allowSave) 
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
  
  public String toString(final String sep, final String neighboringSep, final String areaSep) {
    StringBuilder sb = new StringBuilder();
    sb.append(getTimestamp()).append(sep);
    if(filterCtx.getOperator().allowSave) sb.append(getOperator()).append(sep);
    else sb.append(sep);
    if(filterCtx.getProvider().allowSave) sb.append(getProvider()).append(sep);
    else sb.append(sep);
    if(filterCtx.getMCC().allowSave) sb.append(getMCC()).append(sep);
    else sb.append(sep);
    if(filterCtx.getMNC().allowSave) sb.append(String.format("%02d", getMNC())).append(sep);
    else sb.append(sep);
    if(filterCtx.getCellID().allowSave) sb.append(getCellId()).append(sep);
    else sb.append(sep);
    if(filterCtx.getLAC().allowSave) sb.append(getLac()).append(sep);
    else sb.append(sep);
    if(filterCtx.getCellGeolocation().allowSave) {
      sb.append(getCellLatitude()).append(sep);
      sb.append(getCellLongitude()).append(sep);
    } else sb.append(sep).append(sep);
    if(filterCtx.getCurrentGeolocation().allowSave) {
      sb.append(checkCurrentLoc(getCurrentLocation().getLatitude())).append(sep);
      sb.append(checkCurrentLoc(getCurrentLocation().getLongitude())).append(sep);
    } else sb.append(sep).append(sep);
    if(filterCtx.getSatellites().allowSave) sb.append(getSatellites()).append(sep);
    else sb.append(sep);
    if(filterCtx.getSpeed().allowSave) sb.append(getSpeed()).append(sep);
    else sb.append(sep);
    if(filterCtx.getDistance().allowSave) sb.append(getDistance()).append(sep);
    else sb.append(sep);
    if(filterCtx.getPSC().allowSave) sb.append(getPsc()).append(sep);
    else sb.append(sep);
    if(filterCtx.getType().allowSave) sb.append(getType()).append(sep);
    else sb.append(sep);
    if(filterCtx.getNetworkId().allowSave) sb.append(getNetworkName()).append(sep);
    else sb.append(sep);
    if(filterCtx.getLevel().allowSave) sb.append(getLvl()).append(sep);
    else sb.append(sep);
    if(filterCtx.getASU().allowSave) sb.append(getAsu()).append(sep);
    else sb.append(sep);
    if(filterCtx.getSignalStrength().allowSave) {
      sb.append(getSignalStrength()).append(sep);
      sb.append(getSignalStrengthPercent()).append(sep);
    } else sb.append(sep).append(sep);

    if(filterCtx.getDataRxSpeed().allowSave) sb.append(getMobileNetworkInfo().getRxSpeed()).append(sep);
    else sb.append(sep);
    if(filterCtx.getDataTxSpeed().allowSave) sb.append(getMobileNetworkInfo().getTxSpeed()).append(sep);
    else sb.append(sep);
    if(filterCtx.getDataDirection().allowSave) sb.append(MobileNetworkInfo.getDataActivityMin(getMobileNetworkInfo().getDataActivity())).append(sep);
    else sb.append(sep);
    if(filterCtx.getIPv4().allowSave) sb.append(getMobileNetworkInfo().getIp4Address()).append(sep);
    else sb.append(sep);
    if(filterCtx.getIPv6().allowSave) sb.append(getMobileNetworkInfo().getIp6Address()).append(sep);
    else sb.append(sep);

    if(filterCtx.getAreas().allowSave) {
      int size = getAreas().size();
      for(int i = 0; i < size; ++i) {
        AreaInfo ai = getAreas().get(i);
        sb.append(ai.toString(areaSep));
        if(i < size - 1) sb.append(areaSep);
      }
      sb.append(sep);
    }
    else sb.append(sep);
    
    if(filterCtx.getNeighboring().allowSave) {
      int size = getNeighboring().size();
      for(int i = 0; i < size; ++i) {
        NeighboringInfo ni = getNeighboring().get(i);
        sb.append(ni.toString(neighboringSep));
        if(i < size - 1) sb.append(neighboringSep);
      }
    }
    return sb.toString();
  }
  
  private double checkCurrentLoc(double d) {
    if(getSatellites() == 0 && d == 0) return Double.NaN;
    return d;
  }
  
  public static TowerInfo decodeInformations(final FilterCtx filterCtx, final TowerInfo owner, final SignalStrength ss) throws Exception {
    TowerInfo ti = owner;
    if(ti == null) ti = new TowerInfo(filterCtx);
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
   * @return the cell longitude
   */
  public double getCellLongitude() {
    return cellLongitude;
  }

  /**
   * @param longitude the cell longitude to set
   */
  public void setCellLongitude(double longitude) {
    this.cellLongitude = longitude;
  }

  /**
   * @return the cell latitude
   */
  public double getCellLatitude() {
    return cellLatitude;
  }

  /**
   * @param latitude the cell latitude to set
   */
  public void setCellLatitude(double latitude) {
    this.cellLatitude = latitude;
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
      currentLocation = new Location("");
    return currentLocation;
  }

  /**
   * @param currentLocation the currentLocation to set
   */
  public void setCurrentLocation(Location currentLocation) {
    this.currentLocation = currentLocation;
  }

  /**
   * @return the areas list
   */
  public List<AreaInfo> getAreas() {
    return areas;
  }

  /**
   * @param areas
   *          the areas to add
   */
  public void addArea(final AreaInfo area) {
    this.areas.add(area);
  }
}
