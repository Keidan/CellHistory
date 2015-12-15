package org.kei.android.phone.cellhistory.tasks;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.prefs.PreferencesGeolocation;
import org.kei.android.phone.cellhistory.prefs.PreferencesRecorder;
import org.kei.android.phone.cellhistory.towers.NeighboringInfo;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.app.Activity;
import android.content.Context;
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
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

/**
 *******************************************************************************
 * @file TowerTask.java
 * @author Keidan
 * @date 12/12/2015
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
public class TowerTask extends PhoneStateListener implements Runnable {
  private ScheduledThreadPoolExecutor stpe = null;
  private CellHistoryApp              app  = null;
  private Activity                    activity;
  private SharedPreferences           prefs;
  private TelephonyManager            telephonyManager     = null;
  
  public TowerTask(final CellHistoryApp app) {
    this.app = app;
  }

  public void initialize(final Activity activity, final SharedPreferences prefs) {
    this.activity = activity;
    this.prefs = prefs;
  }

  public void start(final int delay) {
    stop();
    telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
    if(telephonyManager != null) telephonyManager.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    stpe = new ScheduledThreadPoolExecutor(1);
    stpe.scheduleWithFixedDelay(this, 0L, delay, TimeUnit.MILLISECONDS);
  }

  public void stop() {
    if (stpe != null) {
      stpe.shutdown();
      stpe = null;
    }
    if(telephonyManager != null) {
      telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
      telephonyManager = null;
    }
  }
  
  @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
      CellHistoryApp.addLog(activity, signalStrength);
      app.getGlobalTowerInfo().lock();
      try {
          TowerInfo.decodeInformations(app.getGlobalTowerInfo(), signalStrength);
      } catch(Exception e) {
        Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
        CellHistoryApp.addLog(activity, "Exception: " + e.getMessage());
      }finally {
        app.getGlobalTowerInfo().unlock();
      }
    }
  
  public void run() {
    app.getGlobalTowerInfo().lock();
    try {
      app.getGlobalTowerInfo().setTimestamp(new Date().getTime());
      if(prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE, PreferencesGeolocation.PREFS_DEFAULT_LOCATE)) {
        app.getGlobalTowerInfo().setProvider(prefs.getString(PreferencesGeolocation.PREFS_KEY_CURRENT_PROVIDER, PreferencesGeolocation.PREFS_DEFAULT_CURRENT_PROVIDER));
      }
      TelephonyManager tm = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
      if(tm != null) {
        app.getGlobalTowerInfo().setOperator(tm.getNetworkOperatorName());
        String op = tm.getNetworkOperator();
        if(op != null && op.length() > 3) {
          String mcc = op.substring(0, 3);
          String mnc = op.substring(3);
          app.getGlobalTowerInfo().setMNC(Integer.parseInt(mnc));
          app.getGlobalTowerInfo().setMCC(Integer.parseInt(mcc));
        }
        app.getGlobalTowerInfo().setNetwork(tm.getNetworkType());
        app.getGlobalTowerInfo().setNetworkName(TowerInfo.getNetworkType(app.getGlobalTowerInfo().getNetwork(), true));
        
        CellLocation clocation = tm.getCellLocation();
        if(CdmaCellLocation.class.isInstance(clocation)) {
          CellHistoryApp.addLog(activity, "CdmaCellLocation: " + clocation);
          CdmaCellLocation cl = (CdmaCellLocation)clocation;
          app.getGlobalTowerInfo().setCellId(cl.getBaseStationId());
          app.getProviderCtx().update(app.getGlobalTowerInfo().getCellId(), cl.getBaseStationLatitude() + "," + cl.getBaseStationLatitude());
          if(prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE, PreferencesGeolocation.PREFS_DEFAULT_LOCATE)) {
            Editor editor = prefs.edit();
            editor.putBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE, false);
            editor.commit();
            app.getProviderTask().stop();
          }
        } else {
          CellHistoryApp.addLog(activity, "GsmCellLocation: " + clocation);
          GsmCellLocation cl = (GsmCellLocation)clocation;
          app.getGlobalTowerInfo().setCellId(cl.getCid());
          app.getGlobalTowerInfo().setLac(cl.getLac());
          app.getGlobalTowerInfo().setPsc(cl.getPsc());
        }
        List<CellInfo> cinfos = tm.getAllCellInfo();
        if(cinfos != null && cinfos.size() != 0) {
          CellInfo allCellInfo = (CellInfo) cinfos.get(0);
          app.getGlobalTowerInfo().setType(TowerInfo.getTelephonyType(allCellInfo));
          CellHistoryApp.addLog(activity, allCellInfo);
        }
        app.getGlobalTowerInfo().getNeighboring().clear();
        if(cinfos == null || cinfos.isEmpty() || cinfos.size() < 2) {
          List<NeighboringCellInfo> nci = tm.getNeighboringCellInfo();
          if(nci == null) app.getGlobalTowerInfo().setNeighboringNb(0);
          else {
            app.getGlobalTowerInfo().setNeighboringNb(nci.size());
            for(NeighboringCellInfo ci : nci) {
              if(ci.toString().equals("[]"))
                CellHistoryApp.addLog(activity, "NeighboringCellInfo[-1, -1, -1, -1, -1]");
              else
                CellHistoryApp.addLog(activity, ci);
                
              int rssi = ci.getRssi();
              if(rssi != NeighboringCellInfo.UNKNOWN_RSSI)
                rssi = -113 + 2 * rssi;
              app.getGlobalTowerInfo().addNeighboring(new NeighboringInfo(true, ci.getLac(), ci.getCid(), ci.getPsc(), TowerInfo.getNetworkType(ci.getNetworkType(), true), rssi));
            }
          }
        } else {
          app.getGlobalTowerInfo().setNeighboringNb(cinfos.size() - 1);
          for(int i=1; i < cinfos.size(); i++){
            CellInfo ci = cinfos.get(i);
            CellHistoryApp.addLog(activity, ci);
            if(CellInfoCdma.class.isInstance(ci)) {
              CellInfoCdma cic = (CellInfoCdma)ci;
              app.getGlobalTowerInfo().addNeighboring(new NeighboringInfo(false, cic.getCellIdentity().getSystemId(), 
                  cic.getCellIdentity().getBasestationId(), cic.getCellSignalStrength().getAsuLevel(), TowerInfo.STR_CDMA, cic.getCellSignalStrength().getDbm()));
            } else if(CellInfoGsm.class.isInstance(ci)) {
              CellInfoGsm cig = (CellInfoGsm)ci;
              app.getGlobalTowerInfo().addNeighboring(new NeighboringInfo(false, cig.getCellIdentity().getLac(), 
                  cig.getCellIdentity().getCid(), cig.getCellSignalStrength().getAsuLevel(), TowerInfo.STR_GSM, cig.getCellSignalStrength().getDbm()));
            } else if(CellInfoLte.class.isInstance(ci)) {
              CellInfoLte cil = (CellInfoLte)ci;
              int cid = cil.getCellIdentity().getCi();
              if(cid == Integer.MAX_VALUE) cid = cil.getCellIdentity().getPci();
              app.getGlobalTowerInfo().addNeighboring(new NeighboringInfo(false, cil.getCellIdentity().getTac(), 
                  cid, cil.getCellSignalStrength().getAsuLevel(), TowerInfo.STR_LTE, cil.getCellSignalStrength().getDbm()));
            } else if(CellInfoWcdma.class.isInstance(ci)) {
              CellInfoWcdma ciw = (CellInfoWcdma)ci;
              app.getGlobalTowerInfo().addNeighboring(new NeighboringInfo(false, ciw.getCellIdentity().getLac(), 
                  ciw.getCellIdentity().getCid(), ciw.getCellSignalStrength().getAsuLevel(), TowerInfo.STR_WCDMA, ciw.getCellSignalStrength().getDbm()));
            }
          }
        }
      } else
        CellHistoryApp.addLog(activity, "Invalid TelephonyManager");
      app.getGlobalTowerInfo().setRecords(app.getRecorderCtx().getCounter());
      if(app.getProviderCtx().isValid()) {
        String [] split = app.getProviderCtx().getOldLoc().split(",");
        app.getGlobalTowerInfo().setLatitude(Double.parseDouble(split[0]));
        app.getGlobalTowerInfo().setLongitude(Double.parseDouble(split[1]));
      } else {
        app.getGlobalTowerInfo().setLatitude(Double.NaN);
        app.getGlobalTowerInfo().setLongitude(Double.NaN);
      }
      /* Force a default value for the telephony type */
      if(tm != null && app.getGlobalTowerInfo().getType().equals(TowerInfo.UNKNOWN) && app.getGlobalTowerInfo().getNetwork() > 0) {
        if(app.getGlobalTowerInfo().getNetwork() == TelephonyManager.NETWORK_TYPE_LTE)
          app.getGlobalTowerInfo().setType(TowerInfo.STR_LTE);
        else if(tm.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM)
          app.getGlobalTowerInfo().setType(TowerInfo.STR_GSM);
        else if(tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA)
          app.getGlobalTowerInfo().setType(TowerInfo.STR_CDMA);
        else if(tm.getPhoneType() == TelephonyManager.PHONE_TYPE_SIP)
          app.getGlobalTowerInfo().setType("SIP");
      }
    } catch(Throwable t) {
      Log.e("TAG", "Exception: " + t.getMessage(), t);
    }finally {
      app.getGlobalTowerInfo().unlock();
    }
    app.getRecorderCtx().writeData(
        prefs.getString(PreferencesRecorder.PREFS_KEY_SEP, PreferencesRecorder.PREFS_DEFAULT_SEP), 
        prefs.getString(PreferencesRecorder.PREFS_KEY_NEIGHBORING_SEP, PreferencesRecorder.PREFS_DEFAULT_NEIGHBORING_SEP), 
        Integer.parseInt(prefs.getString(PreferencesRecorder.PREFS_KEY_FLUSH, PreferencesRecorder.PREFS_DEFAULT_FLUSH)), 
        app, 
        prefs.getBoolean(PreferencesRecorder.PREFS_KEY_DETECT_CHANGE, PreferencesRecorder.PREFS_DEFAULT_DETECT_CHANGE));
  }
  
}
