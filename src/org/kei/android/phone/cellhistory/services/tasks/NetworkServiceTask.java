package org.kei.android.phone.cellhistory.services.tasks;

import java.util.TimerTask;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.towers.MobileNetworkInfo;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
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
  private CellHistoryApp    app                = null;
  private Service           service            = null;
  private long              startRX            = 0;
  private long              startTX            = 0;

  public NetworkServiceTask(final Service service, final CellHistoryApp app,
      final SharedPreferences prefs) {
    this.service = service;
    this.app = app;
  }

  public void register() {
    unregister();
    startRX = TrafficStats.getTotalRxBytes();
    startTX = TrafficStats.getTotalTxBytes();
    if (startRX == TrafficStats.UNSUPPORTED
        || startTX == TrafficStats.UNSUPPORTED)
      throw new UnsupportedOperationException("Unsupported traffic stats");
  }

  public void unregister() {
  }

  @Override
  public void run() {
    Log.e(getClass().getSimpleName(), "Loop");
    app.getGlobalTowerInfo().lock();
    try {
      MobileNetworkInfo mni = app.getGlobalTowerInfo().getMobileNetworkInfo();
      long lr = TrafficStats.getTotalRxBytes();
      long lt = TrafficStats.getTotalTxBytes();
      mni.setBootRX(lr);
      mni.setBootTX(lt);
      mni.setStartRX(lr - startRX);
      mni.setStartTX(lt - startTX);
      mni.setConnectivity(MobileNetworkInfo.getConnectivityStatus(service));
      ConnectivityManager cm = (ConnectivityManager) service.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo ni = cm.getActiveNetworkInfo();
      if(ni != null && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
        mni.setEstimatedSpeed(MobileNetworkInfo.getEstimatedSpeed(ni));
        mni.setType(MobileNetworkInfo.getNetworkType(ni.getSubtype(), true));
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
