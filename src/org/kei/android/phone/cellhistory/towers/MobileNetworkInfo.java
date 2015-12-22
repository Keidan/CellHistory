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
  public static final int TYPE_WIFI             = 1;
  public static final int TYPE_MOBILE           = 2;
  public static final int TYPE_NOT_CONNECTED    = 0;
  public static final int DATA_ACTIVITY_NONE    = 0;
  public static final int DATA_ACTIVITY_IN      = 1;
  public static final int DATA_ACTIVITY_OUT     = 2;
  public static final int DATA_ACTIVITY_INOUT   = 3;
  public static final int DATA_ACTIVITY_DORMANT = 4;
  private long            rx                    = 0;
  private long            tx                    = 0;
  private long            rxSpeed               = 0;
  private long            txSpeed               = 0;
  private int             dataConnectivity      = TYPE_NOT_CONNECTED;
  private String          theoreticalSpeed      = TowerInfo.UNKNOWN;
  private String          type                  = TowerInfo.UNKNOWN;
  private String          ip4Address            = TowerInfo.UNKNOWN;
  private String          ip6Address            = TowerInfo.UNKNOWN;
  private int             dataActivity          = DATA_ACTIVITY_NONE;

  public MobileNetworkInfo() {

  }

  public MobileNetworkInfo(final MobileNetworkInfo ni) {
    this.rx = ni.rx;
    this.tx = ni.tx;
    this.theoreticalSpeed = ni.theoreticalSpeed;
    this.type = ni.type;
    this.ip4Address = ni.ip4Address;
    this.ip6Address = ni.ip6Address;
    this.dataActivity = ni.dataActivity;
    this.dataConnectivity = ni.dataConnectivity;
    this.rxSpeed = ni.rxSpeed;
    this.txSpeed = ni.txSpeed;
  }

  /**
   * @return the rx
   */
  public long getRx() {
    return rx;
  }

  /**
   * @param rx the rx to set
   */
  public void setRx(long rx) {
    this.rx = rx;
  }

  /**
   * @return the tx
   */
  public long getTx() {
    return tx;
  }

  /**
   * @param tx the tx to set
   */
  public void setTx(long tx) {
    this.tx = tx;
  }
  
  /**
   * @return the rxSpeed
   */
  public long getRxSpeed() {
    return rxSpeed;
  }

  /**
   * @param rxSpeed the rxSpeed to set
   */
  public void setRxSpeed(long rxSpeed) {
    this.rxSpeed = rxSpeed;
  }

  /**
   * @return the txSpeed
   */
  public long getTxSpeed() {
    return txSpeed;
  }

  /**
   * @param txSpeed the txSpeed to set
   */
  public void setTxSpeed(long txSpeed) {
    this.txSpeed = txSpeed;
  }

  /**
   * @return the dataConnectivity
   */
  public int getDataConnectivity() {
    return dataConnectivity;
  }

  /**
   * @param dataConnectivity
   *          the dataConnectivity to set
   */
  public void setDataConnectivity(final int dataConnectivity) {
    this.dataConnectivity = dataConnectivity;
  }

  /**
   * @return the theoreticalSpeed
   */
  public String getTheoreticalSpeed() {
    return theoreticalSpeed;
  }

  /**
   * @param theoreticalSpeed
   *          the theoreticalSpeed to set
   */
  public void setTheoreticalSpeed(final String theoreticalSpeed) {
    this.theoreticalSpeed = theoreticalSpeed;
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
   * @param ip4Address
   *          the ip4Address to set
   */
  public void setIp4Address(final String ip4Address) {
    this.ip4Address = ip4Address;
  }

  /**
   * @return the ip6Address
   */
  public String getIp6Address() {
    return ip6Address;
  }

  /**
   * @param ip6Address
   *          the ip6Address to set
   */
  public void setIp6Address(final String ip6Address) {
    this.ip6Address = ip6Address;
  }

  /**
   * @return the dataActivity
   */
  public int getDataActivity() {
    return dataActivity;
  }

  /**
   * @param dataActivity
   *          the dataActivity to set
   */
  public void setDataActivity(final int dataActivity) {
    this.dataActivity = dataActivity;
  }

  public static String getDataActivityMin(final int activity) {
    if (activity == DATA_ACTIVITY_DORMANT)
      return "dormant";
    else if (activity == DATA_ACTIVITY_IN)
      return "rx";
    else if (activity == DATA_ACTIVITY_OUT)
      return "tx";
    else if (activity == DATA_ACTIVITY_INOUT)
      return "rx/tx";
    else
      return "none";
  }

  public static String getDataActivity(final int activity) {
    if (activity == DATA_ACTIVITY_DORMANT)
      return "Dormant";
    else if (activity == DATA_ACTIVITY_IN)
      return "Input";
    else if (activity == DATA_ACTIVITY_OUT)
      return "Output";
    else if (activity == DATA_ACTIVITY_INOUT)
      return "Input / Output";
    else
      return "None";
  }

  /** Get IP For mobile */
  public static String getMobileIP(final boolean useIPv4) {
    try {
      final List<NetworkInterface> interfaces = Collections
          .list(NetworkInterface.getNetworkInterfaces());
      for (final NetworkInterface intf : interfaces) {
        final List<InetAddress> addrs = Collections.list(intf
            .getInetAddresses());
        for (final InetAddress addr : addrs) {
          if (!addr.isLoopbackAddress()) {
            final String sAddr = addr.getHostAddress().toUpperCase(Locale.US);
            final boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
            if (useIPv4) {
              if (isIPv4)
                return sAddr;
            } else {
              if (!isIPv4) {
                final int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                return delim < 0 ? sAddr : sAddr.substring(0, delim);
              }
            }
          }
        }
      }
    } catch (final Exception ex) {
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

  public static String getTheoreticalSpeed(final NetworkInfo ni) {
    if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
      switch (ni.getSubtype()) {
        case TelephonyManager.NETWORK_TYPE_1xRTT:
          return "~50-100 kbps";
        case TelephonyManager.NETWORK_TYPE_CDMA:
          return "~14-64 kbps";
        case TelephonyManager.NETWORK_TYPE_EDGE:
          return "~50-100 kbps";
        case TelephonyManager.NETWORK_TYPE_EVDO_0:
          return "~400-1000 kbps";
        case TelephonyManager.NETWORK_TYPE_EVDO_A:
          return "~600-1400 kbps";
        case TelephonyManager.NETWORK_TYPE_GPRS:
          return "~100 kbps";
        case TelephonyManager.NETWORK_TYPE_HSDPA:
          return "~2-14 Mbps";
        case TelephonyManager.NETWORK_TYPE_HSPA:
          return "~700-1700 kbps";
        case TelephonyManager.NETWORK_TYPE_HSUPA:
          return "~1-23 Mbps";
        case TelephonyManager.NETWORK_TYPE_UMTS:
          return "~400-7000 kbps";
        case TelephonyManager.NETWORK_TYPE_EHRPD:
          return "~1-2 Mbps";
        case TelephonyManager.NETWORK_TYPE_EVDO_B:
          return "~5 Mbps";
        case TelephonyManager.NETWORK_TYPE_HSPAP:
          return "~10-20 Mbps";
        case TelephonyManager.NETWORK_TYPE_IDEN:
          return "~25 kbps";
        case TelephonyManager.NETWORK_TYPE_LTE:
          return "~10+ Mbps";
      }
    }
    return TowerInfo.UNKNOWN;
  }
}
