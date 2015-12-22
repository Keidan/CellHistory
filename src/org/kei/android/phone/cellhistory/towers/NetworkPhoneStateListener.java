package org.kei.android.phone.cellhistory.towers;

import org.kei.android.phone.cellhistory.CellHistoryApp;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 *******************************************************************************
 * @file NetworkPhoneStateListener.java
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
public class NetworkPhoneStateListener extends PhoneStateListener {
  private CellHistoryApp app     = null;
  private Context        context = null;

  public NetworkPhoneStateListener(final Context context, final CellHistoryApp app) {
    this.context = context;
    this.app = app;
  }
  
  @Override
  public void onDataActivity(int direction) {
    app.getGlobalTowerInfo().lock();
    try {
      if(direction == TelephonyManager.DATA_ACTIVITY_DORMANT)
        app.getGlobalTowerInfo().getMobileNetworkInfo().setDataActivity(MobileNetworkInfo.DATA_ACTIVITY_DORMANT);
      else if(direction == TelephonyManager.DATA_ACTIVITY_IN)
        app.getGlobalTowerInfo().getMobileNetworkInfo().setDataActivity(MobileNetworkInfo.DATA_ACTIVITY_IN);
      else if(direction == TelephonyManager.DATA_ACTIVITY_OUT)
        app.getGlobalTowerInfo().getMobileNetworkInfo().setDataActivity(MobileNetworkInfo.DATA_ACTIVITY_OUT);
      else if(direction == TelephonyManager.DATA_ACTIVITY_INOUT)
        app.getGlobalTowerInfo().getMobileNetworkInfo().setDataActivity(MobileNetworkInfo.DATA_ACTIVITY_INOUT);
      else
        app.getGlobalTowerInfo().getMobileNetworkInfo().setDataActivity(MobileNetworkInfo.DATA_ACTIVITY_NONE);
    } catch (final Exception e) {
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
      CellHistoryApp.addLog(context, "Exception: " + e.getMessage());
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
  }
}
