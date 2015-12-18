package org.kei.android.phone.cellhistory.tasks;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.kei.android.atk.utils.Tools;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.prefs.PreferencesRecorder;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

/**
 *******************************************************************************
 * @file RecorderTask.java
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
public class RecorderTask implements Runnable {
  private ScheduledThreadPoolExecutor stpe     = null;
  private CellHistoryApp              app      = null;
  private SharedPreferences           prefs;
  private Activity                    activity = null;
  
  public RecorderTask(final CellHistoryApp app) {
    this.app = app;
  }

  public void initialize(final Activity activity, final SharedPreferences prefs) {
    this.prefs = prefs;
    this.activity = activity;
  }

  public void start(final int delay) {
    stop();
    try {
      app.getRecorderCtx().writeHeader(
          prefs.getString(PreferencesRecorder.PREFS_KEY_SAVE_PATH,
              PreferencesRecorder.PREFS_DEFAULT_SAVE_PATH),
              activity.getResources().getString(R.string.file_name),
          prefs.getString(PreferencesRecorder.PREFS_KEY_SEP,
              PreferencesRecorder.PREFS_DEFAULT_SEP),
          prefs.getString(PreferencesRecorder.PREFS_KEY_NEIGHBORING_SEP,
              PreferencesRecorder.PREFS_DEFAULT_NEIGHBORING_SEP),
          prefs.getBoolean(PreferencesRecorder.PREFS_KEY_DEL_PREV_FILE,
              PreferencesRecorder.PREFS_DEFAULT_DEL_PREV_FILE),
          prefs.getString(PreferencesRecorder.PREFS_KEY_FORMATS,
              PreferencesRecorder.PREFS_DEFAULT_FORMATS),
          prefs.getBoolean(PreferencesRecorder.PREFS_KEY_INDENTATION,
              PreferencesRecorder.PREFS_DEFAULT_INDENTATION));
      app.notificationRecorderShow(Integer.parseInt(prefs.getString(PreferencesRecorder.PREFS_KEY_FLUSH,
              PreferencesRecorder.PREFS_DEFAULT_FLUSH)));
    } catch (final Exception e) {
      Tools.toast(activity, R.drawable.ic_launcher,
          "Unable to start the capture: " + e.getMessage());
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
    }
    stpe = new ScheduledThreadPoolExecutor(1);
    stpe.scheduleWithFixedDelay(this, 0L, delay, TimeUnit.MILLISECONDS);
  }

  public void stop() {
    if (stpe != null) {
      stpe.shutdown();
      stpe = null;
    }
    app.getRecorderCtx().flushAndClose();
  }
  
  public void run() {
    app.getGlobalTowerInfo().lock();
    try {
      app.getRecorderCtx().writeData(
          prefs.getString(PreferencesRecorder.PREFS_KEY_SEP, PreferencesRecorder.PREFS_DEFAULT_SEP), 
          prefs.getString(PreferencesRecorder.PREFS_KEY_NEIGHBORING_SEP, PreferencesRecorder.PREFS_DEFAULT_NEIGHBORING_SEP), 
          Integer.parseInt(prefs.getString(PreferencesRecorder.PREFS_KEY_FLUSH, PreferencesRecorder.PREFS_DEFAULT_FLUSH)), 
          app, 
          prefs.getBoolean(PreferencesRecorder.PREFS_KEY_DETECT_CHANGE, PreferencesRecorder.PREFS_DEFAULT_DETECT_CHANGE),
          app.getGlobalTowerInfo().getRecords()
          );
    } catch(Throwable t) {
      Log.e(getClass().getSimpleName(), "Exception: " + t.getMessage(), t);
    }finally {
      app.getGlobalTowerInfo().unlock();
    }
  }
  
}
