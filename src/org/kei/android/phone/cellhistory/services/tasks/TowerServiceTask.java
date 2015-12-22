package org.kei.android.phone.cellhistory.services.tasks;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.prefs.PreferencesGeolocation;
import org.kei.android.phone.cellhistory.services.ProviderService;
import org.kei.android.phone.cellhistory.towers.MobileNetworkInfo;
import org.kei.android.phone.cellhistory.towers.NeighboringInfo;
import org.kei.android.phone.cellhistory.towers.TowerInfo;
import org.kei.android.phone.cellhistory.towers.TowerPhoneStateListener;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
/**
 *******************************************************************************
 * @file TowerServiceTask.java
 * @author Keidan
 * @date 19/12/2015
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
public class TowerServiceTask extends TimerTask {
  private CellHistoryApp          app                     = null;
  private Service                 service                 = null;
  private SharedPreferences       prefs                   = null;
  private TelephonyManager        telephonyManager        = null;
  private TowerPhoneStateListener towerPhoneStateListener = null;
  
  public TowerServiceTask(final Service service, final CellHistoryApp app, final SharedPreferences prefs) {
    this.service = service;
    this.prefs = prefs;
    this.app = app;
  }
  
  public void register() {
    unregister();
    telephonyManager = (TelephonyManager) service
        .getSystemService(Context.TELEPHONY_SERVICE);
    towerPhoneStateListener = new TowerPhoneStateListener(service, app);
    if (telephonyManager != null)
      telephonyManager.listen(towerPhoneStateListener,
          PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
  }
  
  public void unregister() {
    if (telephonyManager != null) {
      telephonyManager.listen(towerPhoneStateListener,
          PhoneStateListener.LISTEN_NONE);
      telephonyManager = null;
    }
  }

  @Override
  public void run() {
    app.getGlobalTowerInfo().lock();
    try {
      app.getGlobalTowerInfo().setTimestamp(new Date().getTime());
      if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE,
          PreferencesGeolocation.PREFS_DEFAULT_LOCATE)) {
        app.getGlobalTowerInfo().setProvider(
            prefs.getString(PreferencesGeolocation.PREFS_KEY_CURRENT_PROVIDER,
                PreferencesGeolocation.PREFS_DEFAULT_CURRENT_PROVIDER));
      }
      final TelephonyManager tm = (TelephonyManager) service
          .getSystemService(Context.TELEPHONY_SERVICE);
      if (tm != null) {
        app.getGlobalTowerInfo().setOperator(tm.getNetworkOperatorName());
        final String op = tm.getNetworkOperator();
        if (op != null && op.length() > 3) {
          final String mcc = op.substring(0, 3);
          final String mnc = op.substring(3);
          app.getGlobalTowerInfo().setMNC(Integer.parseInt(mnc));
          app.getGlobalTowerInfo().setMCC(Integer.parseInt(mcc));
        }
        app.getGlobalTowerInfo().setNetwork(tm.getNetworkType());
        app.getGlobalTowerInfo().setNetworkName(
            MobileNetworkInfo.getNetworkType(app.getGlobalTowerInfo().getNetwork(),
                true));

        final CellLocation clocation = tm.getCellLocation();
        if (CdmaCellLocation.class.isInstance(clocation)) {
          CellHistoryApp.addLog(service, "CdmaCellLocation: " + clocation);
          final CdmaCellLocation cl = (CdmaCellLocation) clocation;
          app.getGlobalTowerInfo().setCellId(cl.getBaseStationId());
          app.getProviderCtx().update(app.getGlobalTowerInfo().getCellId(),
              cl.getBaseStationLatitude() + "," + cl.getBaseStationLatitude());
          if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE,
              PreferencesGeolocation.PREFS_DEFAULT_LOCATE)) {
            final Editor editor = prefs.edit();
            editor.putBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE, false);
            editor.commit();
            service.stopService(new Intent(service, ProviderService.class));
          }
        } else {
          CellHistoryApp.addLog(service, "GsmCellLocation: " + clocation);
          final GsmCellLocation cl = (GsmCellLocation) clocation;
          app.getGlobalTowerInfo().setCellId(cl.getCid());
          app.getGlobalTowerInfo().setLac(cl.getLac());
          app.getGlobalTowerInfo().setPsc(cl.getPsc());
        }
        final List<CellInfo> cinfos = tm.getAllCellInfo();
        if (cinfos != null && cinfos.size() != 0) {
          final CellInfo allCellInfo = cinfos.get(0);
          app.getGlobalTowerInfo().setType(
              TowerInfo.getTelephonyType(allCellInfo));
          CellHistoryApp.addLog(service, allCellInfo);
        }
        app.getGlobalTowerInfo().getNeighboring().clear();
        if (cinfos == null || cinfos.isEmpty() || cinfos.size() < 2) {
          final List<NeighboringCellInfo> nci = tm.getNeighboringCellInfo();
          if (nci == null)
            app.getGlobalTowerInfo().setNeighboringNb(0);
          else {
            app.getGlobalTowerInfo().setNeighboringNb(nci.size());
            for (final NeighboringCellInfo ci : nci) {
              if (ci.toString().equals("[]"))
                CellHistoryApp.addLog(service,
                    "NeighboringCellInfo[-1, -1, -1, -1, -1]");
              else
                CellHistoryApp.addLog(service, ci);
              
              int rssi = ci.getRssi();
              if (rssi != NeighboringCellInfo.UNKNOWN_RSSI)
                rssi = -113 + 2 * rssi;
              app.getGlobalTowerInfo().addNeighboring(
                  new NeighboringInfo(true, ci.getLac(), ci.getCid(), ci
                      .getPsc(), MobileNetworkInfo.getNetworkType(ci.getNetworkType(),
                      true), rssi));
            }
          }
        } else {
          app.getGlobalTowerInfo().setNeighboringNb(cinfos.size() - 1);
          for (int i = 1; i < cinfos.size(); i++) {
            final CellInfo ci = cinfos.get(i);
            CellHistoryApp.addLog(service, ci);
            if (CellInfoCdma.class.isInstance(ci)) {
              final CellInfoCdma cic = (CellInfoCdma) ci;
              app.getGlobalTowerInfo()
                  .addNeighboring(
                      new NeighboringInfo(false, cic.getCellIdentity()
                          .getSystemId(), cic.getCellIdentity()
                          .getBasestationId(), cic.getCellSignalStrength()
                          .getAsuLevel(), TowerInfo.STR_CDMA, cic
                          .getCellSignalStrength().getDbm()));
            } else if (CellInfoGsm.class.isInstance(ci)) {
              final CellInfoGsm cig = (CellInfoGsm) ci;
              app.getGlobalTowerInfo().addNeighboring(
                  new NeighboringInfo(false, cig.getCellIdentity().getLac(),
                      cig.getCellIdentity().getCid(), cig
                          .getCellSignalStrength().getAsuLevel(),
                      TowerInfo.STR_GSM, cig.getCellSignalStrength().getDbm()));
            } else if (CellInfoLte.class.isInstance(ci)) {
              final CellInfoLte cil = (CellInfoLte) ci;
              int cid = cil.getCellIdentity().getCi();
              if (cid == Integer.MAX_VALUE)
                cid = cil.getCellIdentity().getPci();
              app.getGlobalTowerInfo().addNeighboring(
                  new NeighboringInfo(false, cil.getCellIdentity().getTac(),
                      cid, cil.getCellSignalStrength().getAsuLevel(),
                      TowerInfo.STR_LTE, cil.getCellSignalStrength().getDbm()));
            } else if (CellInfoWcdma.class.isInstance(ci)) {
              final CellInfoWcdma ciw = (CellInfoWcdma) ci;
              app.getGlobalTowerInfo()
                  .addNeighboring(
                      new NeighboringInfo(false,
                          ciw.getCellIdentity().getLac(), ciw.getCellIdentity()
                              .getCid(), ciw.getCellSignalStrength()
                              .getAsuLevel(), TowerInfo.STR_WCDMA, ciw
                              .getCellSignalStrength().getDbm()));
            }
          }
        }
      } else
        CellHistoryApp.addLog(service, "Invalid TelephonyManager");
      app.getGlobalTowerInfo().setRecords(app.getRecorderCtx().getCounter());
      if (app.getProviderCtx().isValid()) {
        final String[] split = app.getProviderCtx().getOldLoc().split(",");
        app.getGlobalTowerInfo().setLatitude(Double.parseDouble(split[0]));
        app.getGlobalTowerInfo().setLongitude(Double.parseDouble(split[1]));
      } else {
        app.getGlobalTowerInfo().setLatitude(Double.NaN);
        app.getGlobalTowerInfo().setLongitude(Double.NaN);
      }
      /* Force a default value for the telephony type */
      if (tm != null
          && app.getGlobalTowerInfo().getType().equals(TowerInfo.UNKNOWN)
          && app.getGlobalTowerInfo().getNetwork() > 0) {
        if (app.getGlobalTowerInfo().getNetwork() == TelephonyManager.NETWORK_TYPE_LTE)
          app.getGlobalTowerInfo().setType(TowerInfo.STR_LTE);
        else if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM)
          app.getGlobalTowerInfo().setType(TowerInfo.STR_GSM);
        else if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA)
          app.getGlobalTowerInfo().setType(TowerInfo.STR_CDMA);
        else if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_SIP)
          app.getGlobalTowerInfo().setType("SIP");
      }
      app.notificationUpdate(app.getGlobalTowerInfo().getType(), app
          .getGlobalTowerInfo().getNetworkName(), app.getGlobalTowerInfo()
          .getCellId(), app.getGlobalTowerInfo().getLac(), app
          .getGlobalTowerInfo().getSignalStrength(), app.getGlobalTowerInfo()
          .getSignalStrengthPercent());
    } catch (final Throwable t) {
      Log.e(getClass().getSimpleName(), "Exception: " + t.getMessage(), t);
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
  }
  
}
