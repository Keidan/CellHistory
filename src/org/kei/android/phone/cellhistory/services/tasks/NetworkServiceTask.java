package org.kei.android.phone.cellhistory.services.tasks;

import java.util.Date;
import java.util.TimerTask;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.contexts.NetworkDataCtx;
import org.kei.android.phone.cellhistory.towers.MobileNetworkInfo;
import org.kei.android.phone.cellhistory.towers.NetworkPhoneStateListener;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 *******************************************************************************
 * @file NetworkServiceTask.java
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
public class NetworkServiceTask extends TimerTask {
  private CellHistoryApp            app                       = null;
  private Service                   service                   = null;
  private NetworkDataCtx            mobile                    = null;
  private NetworkDataCtx            wifi                      = null;
  private int                       connectivity              = 0;
  private TelephonyManager          telephonyManager          = null;
  private NetworkPhoneStateListener networkPhoneStateListener = null;

  public NetworkServiceTask(final Service service, final CellHistoryApp app,
      final SharedPreferences prefs) {
    this.service = service;
    this.app = app;
    mobile = new NetworkDataCtx();
    wifi = new NetworkDataCtx();
  }

  public void register() {
    unregister();
    telephonyManager = (TelephonyManager) service
        .getSystemService(Context.TELEPHONY_SERVICE);
    networkPhoneStateListener = new NetworkPhoneStateListener(service, app);
    if (telephonyManager != null)
      telephonyManager.listen(networkPhoneStateListener,
          PhoneStateListener.LISTEN_DATA_ACTIVITY);
    mobile.intialize(TrafficStats.getMobileRxBytes(), TrafficStats.getMobileTxBytes());
    wifi.intialize(TrafficStats.getTotalRxBytes(), TrafficStats.getTotalTxBytes());
    connectivity = MobileNetworkInfo.getConnectivityStatus(service);
  }

  public void unregister() {
    if (telephonyManager != null) {
      telephonyManager.listen(networkPhoneStateListener,
          PhoneStateListener.LISTEN_NONE);
      telephonyManager = null;
    }
  }

  @Override
  public void run() {
    app.getGlobalTowerInfo().lock();
    try {
      final MobileNetworkInfo mni = app.getGlobalTowerInfo()
          .getMobileNetworkInfo();
      mni.setDataConnectivity(MobileNetworkInfo.getConnectivityStatus(service));
      if(mni.getDataConnectivity() != connectivity) {
        connectivity = mni.getDataConnectivity();
        mobile.intialize(TrafficStats.getMobileRxBytes(), TrafficStats.getMobileTxBytes());
        wifi.intialize(TrafficStats.getTotalRxBytes(), TrafficStats.getTotalTxBytes());
      }
      Date d = new Date();
      long ld = d.getTime() / 1000;
      if(mni.getDataConnectivity() == MobileNetworkInfo.TYPE_MOBILE) {
        mobile.update(ld, TrafficStats.getMobileRxBytes(), TrafficStats.getMobileTxBytes());
        mni.setRxSpeed(mobile.getRxSpeed());
        mni.setTxSpeed(mobile.getTxSpeed());
        mni.setRx(mobile.getRx());
        mni.setTx(mobile.getTx());
      } else if(mni.getDataConnectivity() == MobileNetworkInfo.TYPE_WIFI) {
        wifi.update(ld, TrafficStats.getTotalRxBytes(), TrafficStats.getTotalTxBytes());
        mni.setRxSpeed(wifi.getRxSpeed());
        mni.setTxSpeed(wifi.getTxSpeed());
        mni.setRx(wifi.getRx());
        mni.setTx(wifi.getTx());
      } else {
        mni.setRx(0L);
        mni.setTx(0L);
        mni.setRxSpeed(0L);
        mni.setTxSpeed(0L);
      }
      
      mni.setTheoreticalSpeed(TowerInfo.UNKNOWN);
      mni.setType(TowerInfo.UNKNOWN);
      final ConnectivityManager cm = (ConnectivityManager) service
          .getSystemService(Context.CONNECTIVITY_SERVICE);
      if (cm != null) {
        final NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
          mni.setTheoreticalSpeed(MobileNetworkInfo.getTheoreticalSpeed(ni));
          mni.setType(MobileNetworkInfo.getNetworkType(ni.getSubtype(), true));
        }
      }
      mni.setIp4Address(MobileNetworkInfo.getMobileIP(true));
      mni.setIp6Address(MobileNetworkInfo.getMobileIP(false));
    } catch (final Throwable t) {
      Log.e(getClass().getSimpleName(), "Exception: " + t.getMessage(), t);
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
  }
}
