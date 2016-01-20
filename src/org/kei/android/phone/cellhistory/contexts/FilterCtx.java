package org.kei.android.phone.cellhistory.contexts;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 *******************************************************************************
 * @file FilterCtx.java
 * @author Keidan
 * @date 20/01/2016
 * @par Project CellHistory
 *
 * @par Copyright 2016 Keidan, all right reserved
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
public class FilterCtx {
  private static final String PREFS_RECORDER_FILTERS_OPERATOR        = "recorderFilterOperator";
  private static final String PREFS_RECORDER_FILTERS_MCC             = "recorderFilterMCC";
  private static final String PREFS_RECORDER_FILTERS_MNC             = "recorderFilterMNC";
  private static final String PREFS_RECORDER_FILTERS_CELL_ID         = "recorderFilterCellID";
  private static final String PREFS_RECORDER_FILTERS_LAC             = "recorderFilterLAC";
  private static final String PREFS_RECORDER_FILTERS_PSC             = "recorderFilterPSC";
  private static final String PREFS_RECORDER_FILTERS_TYPE            = "recorderFilterType";
  private static final String PREFS_RECORDER_FILTERS_NETOWRK_ID      = "recorderFilterNetworkId";
  private static final String PREFS_RECORDER_FILTERS_GEOLOCATION     = "recorderFilterGeolocation";
  private static final String PREFS_RECORDER_FILTERS_ASU             = "recorderFilterASU";
  private static final String PREFS_RECORDER_FILTERS_LEVEL           = "recorderFilterLevel";
  private static final String PREFS_RECORDER_FILTERS_SIGNAL_STRENGTH = "recorderFilterSignalStrength";
  private static final String PREFS_RECORDER_FILTERS_NEIGHBORING     = "recorderFilterNeighboring";
  private static final String PREFS_RECORDER_FILTERS_PROVIDER        = "recorderFilterProvider";
  private static final String PREFS_RECORDER_FILTERS_DISTANCE        = "recorderFilterDistance";
  private static final String PREFS_RECORDER_FILTERS_SATELLITES      = "recorderFilterSatellites";
  private static final String PREFS_RECORDER_FILTERS_SPEED           = "recorderFilterSpeed";
  private static final String PREFS_RECORDER_FILTERS_DATA_TX_SPEED   = "recorderFilterDataTxSpeed";
  private static final String PREFS_RECORDER_FILTERS_DATA_RX_SPEED   = "recorderFilterDataRxSpeed";
  private static final String PREFS_RECORDER_FILTERS_DATA_DIRECTION  = "recorderFilterDataDirection";
  private static final String PREFS_RECORDER_FILTERS_IPV4            = "recorderFilterIPv4";
  private static final String PREFS_RECORDER_FILTERS_IPV6            = "recorderFilterIPv6";
  private static final String PREFS_RECORDER_FILTERS_AREAS           = "recorderFilterAreas";
  private static final String SAVES_RECORDER_FILTERS_OPERATOR        = "swOperator";
  private static final String SAVES_RECORDER_FILTERS_MCC             = "swMCC";
  private static final String SAVES_RECORDER_FILTERS_MNC             = "swMNC";
  private static final String SAVES_RECORDER_FILTERS_CELL_ID         = "swCellID";
  private static final String SAVES_RECORDER_FILTERS_LAC             = "swLAC";
  private static final String SAVES_RECORDER_FILTERS_GEOLOCATION     = "swGeolocation";
  private static final String SAVES_RECORDER_FILTERS_PSC             = "swPSC";
  private static final String SAVES_RECORDER_FILTERS_TYPE            = "swType";
  private static final String SAVES_RECORDER_FILTERS_NETWORK_ID      = "swNetworkId";
  private static final String SAVES_RECORDER_FILTERS_ASU             = "swASU";
  private static final String SAVES_RECORDER_FILTERS_LVL             = "swLVL";
  private static final String SAVES_RECORDER_FILTERS_SIGNAL_STRENGTH = "swSignalStrength";
  private static final String SAVES_RECORDER_FILTERS_NEIGHBORING     = "swNeighboring";
  private static final String SAVES_RECORDER_FILTERS_PROVIDER        = "swProvider";
  private static final String SAVES_RECORDER_FILTERS_DISTANCE        = "swDistance";
  private static final String SAVES_RECORDER_FILTERS_SATELLITES      = "swSatellites";
  private static final String SAVES_RECORDER_FILTERS_SPEED           = "swSpeed";
  private static final String SAVES_RECORDER_FILTERS_DATA_RX_SPEED   = "swDataRxSpeed";
  private static final String SAVES_RECORDER_FILTERS_DATA_TX_SPEED   = "swDataTxSpeed";
  private static final String SAVES_RECORDER_FILTERS_DATA_DIRECTION  = "swDirection";
  private static final String SAVES_RECORDER_FILTERS_IPV4            = "swIpv4";
  private static final String SAVES_RECORDER_FILTERS_IPV6            = "swIpv6";
  private static final String SAVES_RECORDER_FILTERS_AREAS           = "swAreas";
  private final Filter        operator                               = new Filter();
  private final Filter        mcc                                    = new Filter();
  private final Filter        mnc                                    = new Filter();
  private final Filter        cellID                                 = new Filter();
  private final Filter        lac                                    = new Filter();
  private final Filter        psc                                    = new Filter();
  private final Filter        type                                   = new Filter();
  private final Filter        networkId                              = new Filter();
  private final Filter        geolocation                            = new Filter();
  private final Filter        asu                                    = new Filter();
  private final Filter        level                                  = new Filter();
  private final Filter        signalStrength                         = new Filter();
  private final Filter        neighboring                            = new Filter();
  private final Filter        provider                               = new Filter();
  private final Filter        distance                               = new Filter();
  private final Filter        satellites                             = new Filter();
  private final Filter        speed                                  = new Filter();
  private final Filter        dataTxSpeed                            = new Filter();
  private final Filter        dataRxSpeed                            = new Filter();
  private final Filter        dataDirection                          = new Filter();
  private final Filter        ipv4                                   = new Filter();
  private final Filter        ipv6                                   = new Filter();
  private final Filter        areas                                  = new Filter();

  public void read(final Context context) {
    final SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(context);
    final boolean defaults = true;
    /* changes */
    operator.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_OPERATOR, defaults);
    mcc.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_MCC, defaults);
    mnc.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_MNC, defaults);
    cellID.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_CELL_ID, defaults);
    lac.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_LAC, defaults);
    psc.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_PSC, defaults);
    type.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_TYPE, defaults);
    networkId.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_NETOWRK_ID, defaults);
    geolocation.allowChange = prefs.getBoolean(
        PREFS_RECORDER_FILTERS_GEOLOCATION, defaults);
    asu.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_ASU, defaults);
    level.allowChange = prefs
        .getBoolean(PREFS_RECORDER_FILTERS_LEVEL, defaults);
    signalStrength.allowChange = prefs.getBoolean(
        PREFS_RECORDER_FILTERS_SIGNAL_STRENGTH, defaults);
    neighboring.allowChange = prefs.getBoolean(
        PREFS_RECORDER_FILTERS_NEIGHBORING, defaults);
    provider.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_PROVIDER, defaults);
    distance.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_DISTANCE, defaults);
    satellites.allowChange = prefs.getBoolean(
        PREFS_RECORDER_FILTERS_SATELLITES, defaults);
    speed.allowChange = prefs
        .getBoolean(PREFS_RECORDER_FILTERS_SPEED, defaults);
    dataTxSpeed.allowChange = prefs.getBoolean(
        PREFS_RECORDER_FILTERS_DATA_TX_SPEED, defaults);
    dataRxSpeed.allowChange = prefs.getBoolean(
        PREFS_RECORDER_FILTERS_DATA_RX_SPEED, defaults);
    dataDirection.allowChange = prefs.getBoolean(
        PREFS_RECORDER_FILTERS_DATA_DIRECTION, defaults);
    ipv4.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_IPV4, defaults);
    ipv6.allowChange = prefs.getBoolean(PREFS_RECORDER_FILTERS_IPV6, defaults);
    areas.allowChange = prefs
        .getBoolean(PREFS_RECORDER_FILTERS_AREAS, defaults);

    /* saves */
    operator.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_OPERATOR,
        defaults);
    mcc.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_MCC, defaults);
    mnc.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_MNC, defaults);
    cellID.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_CELL_ID,
        defaults);
    lac.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_LAC, defaults);
    psc.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_PSC, defaults);
    type.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_TYPE, defaults);
    networkId.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_NETWORK_ID,
        defaults);
    geolocation.allowSave = prefs.getBoolean(
        SAVES_RECORDER_FILTERS_GEOLOCATION, defaults);
    asu.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_ASU, defaults);
    level.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_LVL, defaults);
    signalStrength.allowSave = prefs.getBoolean(
        SAVES_RECORDER_FILTERS_SIGNAL_STRENGTH, defaults);
    neighboring.allowSave = prefs.getBoolean(
        SAVES_RECORDER_FILTERS_NEIGHBORING, defaults);
    provider.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_PROVIDER,
        defaults);
    distance.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_DISTANCE,
        defaults);
    satellites.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_SATELLITES,
        defaults);
    speed.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_SPEED, defaults);
    dataTxSpeed.allowSave = prefs.getBoolean(
        SAVES_RECORDER_FILTERS_DATA_TX_SPEED, defaults);
    dataRxSpeed.allowSave = prefs.getBoolean(
        SAVES_RECORDER_FILTERS_DATA_RX_SPEED, defaults);
    dataDirection.allowSave = prefs.getBoolean(
        SAVES_RECORDER_FILTERS_DATA_DIRECTION, defaults);
    ipv4.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_IPV4, defaults);
    ipv6.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_IPV6, defaults);
    areas.allowSave = prefs.getBoolean(SAVES_RECORDER_FILTERS_AREAS, defaults);
  }

  public void writeSave(final Context context) {
    final SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(context);
    final Editor e = prefs.edit();
    /* saves */
    e.putBoolean(SAVES_RECORDER_FILTERS_OPERATOR, operator.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_MCC, mcc.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_MNC, mnc.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_CELL_ID, cellID.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_LAC, lac.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_PSC, psc.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_TYPE, type.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_NETWORK_ID, networkId.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_GEOLOCATION, geolocation.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_ASU, asu.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_LVL, level.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_SIGNAL_STRENGTH, signalStrength.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_NEIGHBORING, neighboring.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_PROVIDER, provider.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_DISTANCE, distance.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_SATELLITES, satellites.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_SPEED, speed.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_DATA_TX_SPEED, dataTxSpeed.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_DATA_RX_SPEED, dataRxSpeed.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_DATA_DIRECTION, dataDirection.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_IPV4, ipv4.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_IPV6, ipv6.allowSave);
    e.putBoolean(SAVES_RECORDER_FILTERS_AREAS, areas.allowSave);
    e.commit();
  }

  /**
   * @return the operator
   */
  public Filter getOperator() {
    return operator;
  }

  /**
   * @return the mcc
   */
  public Filter getMCC() {
    return mcc;
  }

  /**
   * @return the mnc
   */
  public Filter getMNC() {
    return mnc;
  }

  /**
   * @return the cellID
   */
  public Filter getCellID() {
    return cellID;
  }

  /**
   * @return the lac
   */
  public Filter getLAC() {
    return lac;
  }

  /**
   * @return the psc
   */
  public Filter getPSC() {
    return psc;
  }

  /**
   * @return the type
   */
  public Filter getType() {
    return type;
  }

  /**
   * @return the networkId
   */
  public Filter getNetworkId() {
    return networkId;
  }

  /**
   * @return the geolocation
   */
  public Filter getGeolocation() {
    return geolocation;
  }

  /**
   * @return the asu
   */
  public Filter getASU() {
    return asu;
  }

  /**
   * @return the level
   */
  public Filter getLevel() {
    return level;
  }

  /**
   * @return the signalStrength
   */
  public Filter getSignalStrength() {
    return signalStrength;
  }

  /**
   * @return the neighboring
   */
  public Filter getNeighboring() {
    return neighboring;
  }

  /**
   * @return the provider
   */
  public Filter getProvider() {
    return provider;
  }

  /**
   * @return the distance
   */
  public Filter getDistance() {
    return distance;
  }

  /**
   * @return the satellites
   */
  public Filter getSatellites() {
    return satellites;
  }

  /**
   * @return the speed
   */
  public Filter getSpeed() {
    return speed;
  }

  /**
   * @return the dataTxSpeed
   */
  public Filter getDataTxSpeed() {
    return dataTxSpeed;
  }

  /**
   * @return the dataRxSpeed
   */
  public Filter getDataRxSpeed() {
    return dataRxSpeed;
  }

  /**
   * @return the dataDirection
   */
  public Filter getDataDirection() {
    return dataDirection;
  }

  /**
   * @return the ipv4
   */
  public Filter getIPv4() {
    return ipv4;
  }

  /**
   * @return the ipv6
   */
  public Filter getIPv6() {
    return ipv6;
  }

  /**
   * @return the areas
   */
  public Filter getAreas() {
    return areas;
  }

  public class Filter {
    public boolean allowChange = true;
    public boolean allowSave   = true;
  }
}
