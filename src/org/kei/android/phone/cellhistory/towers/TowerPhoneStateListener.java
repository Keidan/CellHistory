package org.kei.android.phone.cellhistory.towers;

import org.kei.android.phone.cellhistory.CellHistoryApp;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;

/**
 *******************************************************************************
 * @file TowerPhoneStateListener.java
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
public class TowerPhoneStateListener extends PhoneStateListener {
  private CellHistoryApp app     = null;
  private Context        context = null;

  public TowerPhoneStateListener(final Context context, final CellHistoryApp app) {
    this.context = context;
    this.app = app;
  }
  
  @Override
  public void onSignalStrengthsChanged(final SignalStrength signalStrength) {
    CellHistoryApp.addLog(context, signalStrength);
    app.getGlobalTowerInfo().lock();
    try {
      TowerInfo.decodeInformations(app.getGlobalTowerInfo(), signalStrength);
    } catch (final Exception e) {
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
      CellHistoryApp.addLog(context, "Exception: " + e.getMessage());
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
  }
}
