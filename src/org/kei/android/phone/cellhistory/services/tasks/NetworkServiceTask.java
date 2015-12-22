package org.kei.android.phone.cellhistory.services.tasks;

import java.util.Date;
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
  private long                      startTimeRX               = 0;
  private long                      startTimeTX               = 0;

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
    startRX = TrafficStats.getMobileRxBytes();
    startTX = TrafficStats.getMobileTxBytes();
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
      if(startRX == 0) startRX = TrafficStats.getMobileRxBytes();
      if(startTX == 0) startTX = TrafficStats.getMobileTxBytes();
      Date d = new Date();
      long ld = d.getTime();
      if(startTimeRX == 0) startTimeRX = ld;
      if(startTimeTX == 0) startTimeTX = ld;
      long newRX = TrafficStats.getMobileRxBytes();
      long newTX = TrafficStats.getMobileTxBytes();
      long transferedRX = newRX - mni.getRx();
      long transferedTX = newTX - mni.getTx();
      long timeRx = 0;
      long timeTx = 0;
      if(transferedRX != startRX)
        timeRx = ld - startTimeRX;
      if(transferedTX != startTX)
        timeTx = ld - startTimeTX;
      if(timeRx != 0) mni.setRxSpeed(transferedRX / timeRx);
      else mni.setRxSpeed(0);
      if(timeTx != 0) mni.setTxSpeed(transferedTX / timeTx);
      else mni.setTxSpeed(0);
      mni.setRx(newRX - startRX);
      mni.setTx(newTX - startTX);
      mni.setDataConnectivity(MobileNetworkInfo.getConnectivityStatus(service));
      if(mni.getDataConnectivity() != MobileNetworkInfo.TYPE_MOBILE) {
        startRX = startTX = 0;
      }
      mni.setEstimatedSpeed(TowerInfo.UNKNOWN);
      mni.setType(TowerInfo.UNKNOWN);
      final ConnectivityManager cm = (ConnectivityManager) service
          .getSystemService(Context.CONNECTIVITY_SERVICE);
      if (cm != null) {
        final NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
          mni.setEstimatedSpeed(MobileNetworkInfo.getEstimatedSpeed(ni));
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
