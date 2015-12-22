package org.kei.android.phone.cellhistory.services.tasks;

import java.util.TimerTask;

import org.kei.android.phone.cellhistory.CellHistoryApp;
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
  private long                      startRX                   = 0;
  private long                      startTX                   = 0;
  private TelephonyManager          telephonyManager          = null;
  private NetworkPhoneStateListener networkPhoneStateListener = null;

  public NetworkServiceTask(final Service service, final CellHistoryApp app,
      final SharedPreferences prefs) {
    this.service = service;
    this.app = app;
  }

  public void register() {
    unregister();
    telephonyManager = (TelephonyManager) service
        .getSystemService(Context.TELEPHONY_SERVICE);
    networkPhoneStateListener = new NetworkPhoneStateListener(service, app);
    if (telephonyManager != null)
      telephonyManager.listen(networkPhoneStateListener,
          PhoneStateListener.LISTEN_DATA_ACTIVITY);
    startRX = TrafficStats.getTotalRxBytes();
    startTX = TrafficStats.getTotalTxBytes();
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
      final long lr = TrafficStats.getTotalRxBytes();
      final long lt = TrafficStats.getTotalTxBytes();
      mni.setBootRX(lr);
      mni.setBootTX(lt);
      mni.setStartRX(lr - startRX);
      mni.setStartTX(lt - startTX);
      mni.setDataConnectivity(MobileNetworkInfo.getConnectivityStatus(service));
      final ConnectivityManager cm = (ConnectivityManager) service
          .getSystemService(Context.CONNECTIVITY_SERVICE);
      if (cm != null) {
        final NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
          mni.setEstimatedSpeed(MobileNetworkInfo.getEstimatedSpeed(ni));
          mni.setType(MobileNetworkInfo.getNetworkType(ni.getSubtype(), true));
        } else {
          mni.setEstimatedSpeed(TowerInfo.UNKNOWN);
        }
      } else {
        mni.setEstimatedSpeed(TowerInfo.UNKNOWN);
        mni.setType(TowerInfo.UNKNOWN);
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
