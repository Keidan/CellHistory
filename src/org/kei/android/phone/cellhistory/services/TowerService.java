package org.kei.android.phone.cellhistory.services;

import java.util.Timer;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.prefs.PreferencesTimers;
import org.kei.android.phone.cellhistory.services.tasks.TowerServiceTask;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

/**
 *******************************************************************************
 * @file TowerService.java
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
public class TowerService extends Service {
  private CellHistoryApp    app              = null;
  private SharedPreferences prefs            = null;
  private TowerServiceTask  towerServiceTask = null;
  private Timer             timer            = null;

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
    if (towerServiceTask != null)
      towerServiceTask.unregister();
    towerServiceTask = new TowerServiceTask(this, app, prefs);
    towerServiceTask.register();
    // schedule task
    timer.scheduleAtFixedRate(towerServiceTask, 0, Integer.parseInt(prefs
        .getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_TOWER,
            PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_TOWER)));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (towerServiceTask != null) {
      towerServiceTask.unregister();
      towerServiceTask = null;
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
