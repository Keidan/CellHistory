package org.kei.android.phone.cellhistory.fragments;

import java.util.Locale;

import org.kei.android.atk.utils.Tools;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.prefs.PreferencesRecorder;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 *******************************************************************************
 * @file RecorderFragment.java
 * @author Keidan
 * @date 11/12/2015
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
public class RecorderFragment extends Fragment implements UITaskFragment,
    OnClickListener {
  private static final int SIZE_1KB = 0x400;
  private static final int SIZE_1MB = 0x100000;
  private static final int SIZE_1GB = 0x40000000;
  /* UI */
  private TextView          txtRecords  = null;
  private ProgressBar       pbBuffer    = null;
  private TextView          txtSize     = null;
  private ToggleButton      toggleOnOff = null;
  /* context */
  private SharedPreferences prefs       = null;
  private CellHistoryApp    app         = null;

  @Override
  public View onCreateView(final LayoutInflater inflater,
      final ViewGroup container, final Bundle savedInstanceState) {
    final ViewGroup rootView = (ViewGroup) inflater.inflate(
        R.layout.fragment_recorder, container, false);
    
    return rootView;
  }

  @Override
  public void onViewCreated(final View view, final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    /* context */
    app = CellHistoryApp.getApp(getActivity());
    prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

    /* UI */
    txtSize = (TextView) getView().findViewById(R.id.txtSize);
    pbBuffer = (ProgressBar) getView().findViewById(R.id.pbBuffer);
    txtRecords = (TextView) getView().findViewById(R.id.txtRecords);
    toggleOnOff = (ToggleButton) getView().findViewById(R.id.toggleOnOff);
    toggleOnOff.setOnClickListener(this);
    pbBuffer.setMax(Integer.parseInt(prefs.getString(PreferencesRecorder.PREFS_KEY_FLUSH,
        PreferencesRecorder.PREFS_DEFAULT_FLUSH)));
    processUI(app.getGlobalTowerInfo());
  }
  
  public void onResume() {
    super.onResume();
    pbBuffer.setProgress(app.getRecorderCtx().getFrames().size());
    pbBuffer.setMax(Integer.parseInt(prefs.getString(PreferencesRecorder.PREFS_KEY_FLUSH,
            PreferencesRecorder.PREFS_DEFAULT_FLUSH)));
  }
  
  @Override
  public void processUI(final TowerInfo ti) {
    if(txtRecords == null) return;
    txtRecords.setText(String.valueOf(ti.getRecords()));
    pbBuffer.setProgress(app.getRecorderCtx().getFrames().size());
    
    float s = app.getRecorderCtx().getSize();
    String ss;
    if(s < SIZE_1KB)
      ss = String.format(Locale.US, "%d octet%s", (int)s, s > 1 ? "s" : "");
    else if(s < SIZE_1MB)
      ss = String.format("%.02f", (s/SIZE_1KB)) + " Ko";
    else if(s < SIZE_1GB)
      ss = String.format("%.02f", (s/SIZE_1MB)) + " Mo";
    else
      ss = String.format("%.02f", (s/SIZE_1GB)) + " Go";
    txtSize.setText(ss);
  }

  @Override
  public void onClick(final View v) {
    if (v.equals(toggleOnOff)) {
      if (toggleOnOff.isChecked()) {
        try {
          app.getRecorderCtx().writeHeader(
              prefs.getString(PreferencesRecorder.PREFS_KEY_SAVE_PATH,
                  PreferencesRecorder.PREFS_DEFAULT_SAVE_PATH),
              getResources().getString(R.string.file_name),
              prefs.getString(PreferencesRecorder.PREFS_KEY_SEP,
                  PreferencesRecorder.PREFS_DEFAULT_SEP),
              prefs.getString(PreferencesRecorder.PREFS_KEY_NEIGHBORING_SEP,
                  PreferencesRecorder.PREFS_DEFAULT_NEIGHBORING_SEP),
              prefs.getBoolean(PreferencesRecorder.PREFS_KEY_DEL_PREV_FILE,
                  PreferencesRecorder.PREFS_DEFAULT_DEL_PREV_FILE));
          notification(true);
        } catch (final Exception e) {
          Tools.toast(getActivity(), R.drawable.ic_launcher,
              "Unable to start the capture: " + e.getMessage());
          Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
        }
      } else {
        stopProcess();
      }
    }
  }
  
  private void stopProcess() {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        toggleOnOff.setChecked(false);
        app.getRecorderCtx().flushAndClose();
        notification(false);
      }
    });
  }

  @SuppressWarnings("deprecation")
  public void notification(boolean add) {
    final Activity a = getActivity();
    long when = System.currentTimeMillis(); //now
    NotificationManager notificationManager = (NotificationManager)a.getSystemService(Context.NOTIFICATION_SERVICE);
    if(!add) {
      notificationManager.cancel(0);
    } else {
      Notification notification = new Notification(R.drawable.ic_launcher, getString(R.string.app_name), when);
      notification.setLatestEventInfo(a, getString(R.string.app_name), getString(R.string.notificationtext), null);
      notificationManager.notify(0, notification);
    }
  }
}
