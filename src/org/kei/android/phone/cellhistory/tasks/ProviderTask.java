package org.kei.android.phone.cellhistory.tasks;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.contexts.ProviderCtx;
import org.kei.android.phone.cellhistory.prefs.PreferencesGeolocation;
import org.kei.android.phone.cellhistory.prefs.PreferencesGeolocationOpenCellID;
import org.kei.android.phone.cellhistory.towers.CellIdHelper;
import org.kei.android.phone.cellhistory.towers.TowerInfo;
import org.kei.android.phone.cellhistory.towers.request.CellIdRequestEntity;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 *******************************************************************************
 * @file ProviderTask.java
 * @author Keidan
 * @date 12/12/2015
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
public class ProviderTask implements Runnable {
  private ScheduledThreadPoolExecutor stpe         = null;
  private CellHistoryApp              app          = null;
  private Activity                    activity;
  private SharedPreferences           prefs;
  
  public ProviderTask(final CellHistoryApp app) {
    this.app = app;
  }
  
  public void initialize(final Activity activity, final SharedPreferences prefs) {
    this.activity = activity;
    this.prefs = prefs;
  }
  
  public void start(final int delay) {
    stop();
    stpe = new ScheduledThreadPoolExecutor(1);
    stpe.scheduleWithFixedDelay(this, 0L, delay, TimeUnit.MICROSECONDS);
  }
  
  public void stop() {
    if (stpe != null) {
      stpe.shutdown();
      stpe = null;
    }
  }
  
  @Override
  public void run() {
    TowerInfo ti = null;
    app.getGlobalTowerInfo().lock();
    try {
      ti = new TowerInfo(app.getGlobalTowerInfo());
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
    String oldLoc = app.getProviderCtx().getOldLoc();
    int oldCellId = app.getProviderCtx().getOldCellId();
    long retryLoc = app.getProviderCtx().getRetryLoc();
    if (ti.getCellId() != -1 && oldCellId != ti.getCellId()
        || oldLoc.startsWith(ProviderCtx.LOC_NONE)) {
      boolean retry = oldCellId != ti.getCellId();
      oldCellId = ti.getCellId();
      if (!retry && oldLoc.startsWith(ProviderCtx.LOC_NONE)
          && (retryLoc % 30) == 0)
        retry = true;
      oldLoc = ProviderCtx.LOC_NONE;
      if (retry) {
        final int r = CellIdHelper.tryToLocate(activity, ti, Integer
            .parseInt(prefs.getString(
                PreferencesGeolocation.PREFS_KEY_LOCATION_TIMEOUT, ""
                    + PreferencesGeolocation.PREFS_DEFAULT_LOCATION_TIMEOUT)),
                prefs.getString(PreferencesGeolocation.PREFS_KEY_CURRENT_PROVIDER,
                    PreferencesGeolocation.PREFS_DEFAULT_CURRENT_PROVIDER), prefs
                    .getString(PreferencesGeolocationOpenCellID.PREFS_KEY_API_KEY,
                        PreferencesGeolocationOpenCellID.PREFS_DEFAULT_API_KEY));
        retryLoc = 0;
        if (r == CellIdRequestEntity.OK)
          oldLoc = ti.getLatitude() + "," + ti.getLongitude();
        else if (r == CellIdRequestEntity.NOT_FOUND)
          oldLoc = ProviderCtx.LOC_NOT_FOUND;
        else if (r == CellIdRequestEntity.BAD_REQUEST)
          oldLoc = ProviderCtx.LOC_BAD_REQUEST;
        CellHistoryApp.addLog(activity, "Geolocation: " + oldLoc);
        app.getGlobalTowerInfo().lock();
        try {
          app.getGlobalTowerInfo().setLatitude(ti.getLatitude());
          app.getGlobalTowerInfo().setLongitude(ti.getLongitude());
        } finally {
          app.getGlobalTowerInfo().unlock();
        }
      }
    }
    retryLoc++;
    if (oldLoc.startsWith(ProviderCtx.LOC_NONE)
        || oldLoc.startsWith(ProviderCtx.LOC_RETRY))
      oldLoc = ProviderCtx.LOC_RETRY + " " + (30 - (retryLoc % 30)) + "s.";
    app.getProviderCtx().updateAll(oldCellId, oldLoc, retryLoc);
  }

  public void accelUpdate(float timestamp, double velocity) {
    app.getGlobalTowerInfo().lock();
    try {
      app.getGlobalTowerInfo().setSpeed(velocity);
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
  }
  
}
