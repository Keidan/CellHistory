package org.kei.android.phone.cellhistory.towers;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 *******************************************************************************
 * @file MobileNetworkInfo.java
 * @author Keidan
 * @date 22/12/2015
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
@SuppressWarnings("deprecation")
public class MobileNetworkInfo {
  public static final int TYPE_WIFI          = 1;
  public static final int TYPE_MOBILE        = 2;
  public static final int TYPE_NOT_CONNECTED = 0;
  private long            startRX            = 0;
  private long            startTX            = 0;
  private long            bootRX             = 0;
  private long            bootTX             = 0;
  private int             connectivity       = TYPE_NOT_CONNECTED;
  private String          estimatedSpeed     = TowerInfo.UNKNOWN;
  private String          type               = TowerInfo.UNKNOWN;
  private String          ip4Address         = TowerInfo.UNKNOWN;
  private String          ip6Address         = TowerInfo.UNKNOWN;

  public MobileNetworkInfo() {

  }

  public MobileNetworkInfo(final MobileNetworkInfo ni) {
    this.startRX = ni.startRX;
    this.startTX = ni.startTX;
    this.bootRX = ni.bootRX;
    this.bootTX = ni.bootTX;
    this.estimatedSpeed = ni.estimatedSpeed;
    this.type = ni.type;
    this.ip4Address = ni.ip4Address;
    this.ip6Address = ni.ip6Address;
  }

  /**
   * @return the startRX
   */
  public long getStartRX() {
    return startRX;
  }

  /**
   * @param startRX
   *          the startRX to set
   */
  public void setStartRX(final long startRX) {
    this.startRX = startRX;
  }

  /**
   * @return the startTX
   */
  public long getStartTX() {
    return startTX;
  }

  /**
   * @param startTX
   *          the startTX to set
   */
  public void setStartTX(final long startTX) {
    this.startTX = startTX;
  }

  /**
   * @return the bootRX
   */
  public long getBootRX() {
    return bootRX;
  }

  /**
   * @param bootRX
   *          the bootRX to set
   */
  public void setBootRX(final long bootRX) {
    this.bootRX = bootRX;
  }

  /**
   * @return the bootTX
   */
  public long getBootTX() {
    return bootTX;
  }

  /**
   * @param bootTX
   *          the bootTX to set
   */
  public void setBootTX(final long bootTX) {
    this.bootTX = bootTX;
  }

  /**
   * @return the connectivity
   */
  public int getConnectivity() {
    return connectivity;
  }

  /**
   * @param connectivity
   *          the connectivity to set
   */
  public void setConnectivity(final int connectivity) {
    this.connectivity = connectivity;
  }

  /**
   * @return the estimatedSpeed
   */
  public String getEstimatedSpeed() {
    return estimatedSpeed;
  }

  /**
   * @param estimatedSpeed
   *          the estimatedSpeed to set
   */
  public void setEstimatedSpeed(final String estimatedSpeed) {
    this.estimatedSpeed = estimatedSpeed;
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
   * @return the ip4Address
   */
  public String getIp4Address() {
    return ip4Address;
  }

  /**
   * @param ip4Address the ip4Address to set
   */
  public void setIp4Address(String ip4Address) {
    this.ip4Address = ip4Address;
  }
  
  /**
   * @return the ip6Address
   */
  public String getIp6Address() {
    return ip6Address;
  }

  /**
   * @param ip6Address the ip6Address to set
   */
  public void setIp6Address(String ip6Address) {
    this.ip6Address = ip6Address;
  }
  

  /** Get IP For mobile */
  public static String getMobileIP(boolean useIPv4) {
    try {
      List<NetworkInterface> interfaces = Collections.list(NetworkInterface
          .getNetworkInterfaces());
      for (NetworkInterface intf : interfaces) {
        List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
        for (InetAddress addr : addrs) {
          if (!addr.isLoopbackAddress()) {
            String sAddr = addr.getHostAddress().toUpperCase(Locale.US);
            boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
            if (useIPv4) {
              if (isIPv4)
                return sAddr;
            } else {
              if (!isIPv4) {
                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                return delim < 0 ? sAddr : sAddr.substring(0, delim);
              }
            }
          }
        }
      }
    } catch (Exception ex) {
      Log.e(MobileNetworkInfo.class.getSimpleName(),
          "Exception in Get IP Address: " + ex.getMessage(), ex);
    }
    return TowerInfo.UNKNOWN;
  }

  public static int getConnectivityStatus(final Context context) {
    final ConnectivityManager cm = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    if (null != activeNetwork) {
      if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
        return TYPE_WIFI;
      if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
        return TYPE_MOBILE;
    }
    return TYPE_NOT_CONNECTED;
  }

  public static String getNetworkType(final int networkType) {
    return getNetworkType(networkType, true);
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
        return TowerInfo.UNKNOWN + nt;
    }
  }

  public static String getEstimatedSpeed(final NetworkInfo ni) {
    if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
      switch (ni.getSubtype()) {
        case TelephonyManager.NETWORK_TYPE_1xRTT:
          return "~ 50-100 kbps";
        case TelephonyManager.NETWORK_TYPE_CDMA:
          return "~ 14-64 kbps";
        case TelephonyManager.NETWORK_TYPE_EDGE:
          return "~ 50-100 kbps";
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
          return "~ 400-1000 kbps";
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
          return "~ 600-1400 kbps";
        case TelephonyManager.NETWORK_TYPE_GPRS:
          return "~ 100 kbps";
        case TelephonyManager.NETWORK_TYPE_HSDPA:
          return "~ 2-14 Mbps";
        case TelephonyManager.NETWORK_TYPE_HSPA:
          return "~ 700-1700 kbps";
        case TelephonyManager.NETWORK_TYPE_HSUPA:
          return "~ 1-23 Mbps";
        case TelephonyManager.NETWORK_TYPE_UMTS:
          return "~ 400-7000 kbps";
        case TelephonyManager.NETWORK_TYPE_EHRPD:
          return "~ 1-2 Mbps";
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
          return "~ 5 Mbps";
        case TelephonyManager.NETWORK_TYPE_HSPAP:
          return "~ 10-20 Mbps";
        case TelephonyManager.NETWORK_TYPE_IDEN:
          return "~ 25 kbps";
        case TelephonyManager.NETWORK_TYPE_LTE:
          return "~ 10+ Mbps";
      }
    }
    return TowerInfo.UNKNOWN;
  }
}
