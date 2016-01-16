package org.kei.android.phone.cellhistory.services.tasks;

import java.util.TimerTask;

import org.kei.android.atk.utils.Tools;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.prefs.PreferencesRecorder;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 *******************************************************************************
 * @file RecorderServiceTask.java
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
public class RecorderServiceTask extends TimerTask {
  private CellHistoryApp    app    = null;
  private SharedPreferences prefs  = null;
  private Context           contex = null;

  public RecorderServiceTask(final Service contex, final CellHistoryApp app,
      final SharedPreferences prefs) {
    this.contex = contex;
    this.prefs = prefs;
    this.app = app;
  }
  
  public void register() {
    unregister();
    try {
      app.getRecorderCtx().writeHeader(
          prefs.getString(PreferencesRecorder.PREFS_KEY_SAVE_PATH,
              PreferencesRecorder.PREFS_DEFAULT_SAVE_PATH),
          contex.getResources().getString(R.string.file_name),
              prefs.getString(PreferencesRecorder.PREFS_KEY_SEP,
                  PreferencesRecorder.PREFS_DEFAULT_SEP),
                  prefs.getString(PreferencesRecorder.PREFS_KEY_NEIGHBORING_SEP,
                      PreferencesRecorder.PREFS_DEFAULT_NEIGHBORING_SEP),
                      prefs.getString(PreferencesRecorder.PREFS_KEY_AREAS_SEP,
                          PreferencesRecorder.PREFS_DEFAULT_AREAS_SEP),
                      prefs.getBoolean(PreferencesRecorder.PREFS_KEY_DEL_PREV_FILE,
                          PreferencesRecorder.PREFS_DEFAULT_DEL_PREV_FILE),
                          prefs.getString(PreferencesRecorder.PREFS_KEY_FORMATS,
                              PreferencesRecorder.PREFS_DEFAULT_FORMATS),
                              prefs.getBoolean(PreferencesRecorder.PREFS_KEY_INDENTATION,
                                  PreferencesRecorder.PREFS_DEFAULT_INDENTATION));
      app.notificationRecorderShow(Integer.parseInt(prefs.getString(
          PreferencesRecorder.PREFS_KEY_FLUSH,
          PreferencesRecorder.PREFS_DEFAULT_FLUSH)));
    } catch (final Exception e) {
      Tools.toast(contex, R.drawable.ic_launcher,
          "Unable to start the capture: " + e.getMessage());
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
    }
  }
  
  public void unregister() {
    app.getRecorderCtx().flushAndClose();
  }

  @Override
  public void run() {
    app.getGlobalTowerInfo().lock();
    try {
      app.getRecorderCtx().writeData(
          prefs.getString(PreferencesRecorder.PREFS_KEY_SEP,
              PreferencesRecorder.PREFS_DEFAULT_SEP),
          prefs.getString(PreferencesRecorder.PREFS_KEY_NEIGHBORING_SEP,
              PreferencesRecorder.PREFS_DEFAULT_NEIGHBORING_SEP),
          prefs.getString(PreferencesRecorder.PREFS_KEY_AREAS_SEP,
              PreferencesRecorder.PREFS_DEFAULT_AREAS_SEP),
          Integer.parseInt(prefs.getString(PreferencesRecorder.PREFS_KEY_FLUSH,
              PreferencesRecorder.PREFS_DEFAULT_FLUSH)),
          app,
          prefs.getBoolean(PreferencesRecorder.PREFS_KEY_DETECT_CHANGE,
              PreferencesRecorder.PREFS_DEFAULT_DETECT_CHANGE),
          app.getGlobalTowerInfo().getRecords());
    } catch (final Throwable t) {
      Log.e(getClass().getSimpleName(), "Exception: " + t.getMessage(), t);
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
  }

}
