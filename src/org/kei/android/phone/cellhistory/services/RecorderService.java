package org.kei.android.phone.cellhistory.services;

import java.util.Timer;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.prefs.PreferencesTimers;
import org.kei.android.phone.cellhistory.services.tasks.RecorderServiceTask;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

/**
 *******************************************************************************
 * @file RecorderService.java
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
public class RecorderService extends Service {
  private CellHistoryApp      app                 = null;
  private SharedPreferences   prefs               = null;
  private RecorderServiceTask recorderServiceTask = null;
  private Timer               timer               = null;

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
    if (recorderServiceTask != null)
      recorderServiceTask.unregister();
    recorderServiceTask = new RecorderServiceTask(this, app, prefs);
    recorderServiceTask.register();
    // schedule task
    timer.scheduleAtFixedRate(recorderServiceTask, 0, Integer.parseInt(prefs
        .getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_RECORDER,
            PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_RECORDER)));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (recorderServiceTask != null) {
      recorderServiceTask.unregister();
      recorderServiceTask = null;
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
