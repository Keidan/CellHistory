package org.kei.android.phone.cellhistory.services;

import java.util.Timer;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.prefs.PreferencesTimers;
import org.kei.android.phone.cellhistory.services.tasks.NetworkServiceTask;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 *******************************************************************************
 * @file NetworkService.java
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
public class NetworkService extends Service {
  private CellHistoryApp     app                = null;
  private SharedPreferences  prefs              = null;
  private NetworkServiceTask networkServiceTask = null;
  private Timer              timer              = null;

  @Override
  public void onCreate() {
    app = CellHistoryApp.getApp(this);
    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    // cancel if already existed
    if (timer != null) {
      timer.cancel();
    } else {
      // recreate new
      timer = new Timer();
    }
    if (networkServiceTask != null)
      networkServiceTask.unregister();
    networkServiceTask = new NetworkServiceTask(this, app, prefs);
    try {
      networkServiceTask.register();
    } catch (final UnsupportedOperationException un) {
      Log.e(getClass().getSimpleName(), "Exception: " + un.getMessage(), un);
      CellHistoryApp.addLog(this, "Exception: " + un.getMessage());
      stopSelf();
      return;
    }
    // schedule task
    timer.scheduleAtFixedRate(networkServiceTask, 0, Integer.parseInt(prefs
        .getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_NETWORK,
            PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_NETWORK)));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (networkServiceTask != null) {
      networkServiceTask.unregister();
      networkServiceTask = null;
    }
    if (timer != null) {
      timer.cancel();
      timer.purge();
      timer = null;
    }
  }

  @Override
  public IBinder onBind(final Intent intent) {
    return null;
  }
}
