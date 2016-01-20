package org.kei.android.phone.cellhistory.services.tasks;

import java.util.TimerTask;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.contexts.ProviderCtx;
import org.kei.android.phone.cellhistory.prefs.PreferencesGeolocation;
import org.kei.android.phone.cellhistory.prefs.PreferencesGeolocationOpenCellID;
import org.kei.android.phone.cellhistory.towers.CellIdHelper;
import org.kei.android.phone.cellhistory.towers.TowerInfo;
import org.kei.android.phone.cellhistory.towers.request.CellIdRequestEntity;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *******************************************************************************
 * @file ProviderServiceTask.java
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
public class ProviderServiceTask extends TimerTask {
  private CellHistoryApp    app     = null;
  private Context           context = null;
  private SharedPreferences prefs   = null;

  public ProviderServiceTask(final Context context, final CellHistoryApp app,
      final SharedPreferences prefs) {
    this.app = app;
    this.context = context;
    this.prefs = prefs;
  }

  @Override
  public void run() {
    TowerInfo ti = null;
    app.getGlobalTowerInfo().lock();
    try {
      ti = new TowerInfo(app.getFilterCtx(), app.getGlobalTowerInfo());
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
        final int r = CellIdHelper.tryToLocate(context, ti, Integer
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
        CellHistoryApp.addLog(context, "Geolocation: " + oldLoc);
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
    app.getProviderCtx().updateAll(oldCellId, oldLoc, retryLoc);
  }

}
