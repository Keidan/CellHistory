package org.kei.android.phone.cellhistory.services;


import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.services.tasks.GpsServiceTask;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

/**
 *******************************************************************************
 * @file GpsService.java
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
public class GpsService extends Service {
  private CellHistoryApp    app              = null;
  private SharedPreferences prefs            = null;
  private GpsServiceTask    gpsServiceTask   = null;

  @Override
  public void onCreate() {
    app = CellHistoryApp.getApp(this);
    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    if (gpsServiceTask != null)
      gpsServiceTask.unregister();
    gpsServiceTask = new GpsServiceTask(this, app, prefs);
    gpsServiceTask.register();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (gpsServiceTask != null) {
      gpsServiceTask.unregister();
      gpsServiceTask = null;
    }
  }

  @Override
  public IBinder onBind(final Intent intent) {
    return null;
  }
}
