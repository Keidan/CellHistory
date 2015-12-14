package org.kei.android.phone.cellhistory.fragments;

import java.util.Locale;

import org.kei.android.atk.utils.Tools;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.activities.CellHistoryPagerActivity;
import org.kei.android.phone.cellhistory.prefs.PreferencesRecorder;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
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
  private static final int           SIZE_1KB      = 0x400;
  private static final int           SIZE_1MB      = 0x100000;
  private static final int           SIZE_1GB      = 0x40000000;
  /* UI */
  private TextView                   txtRecords    = null;
  private ProgressBar                pbBuffer      = null;
  private TextView                   txtSize       = null;
  private ToggleButton               toggleOnOff   = null;
  /* context */
  private SharedPreferences          prefs         = null;
  private CellHistoryApp             app           = null;
  private NotificationCompat.Builder notifyBuilder = null;
  private NotificationManager notificationManager = null;
  private int notifyID = 1;
  //private int notificationNum = 0;


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
    try {
      processUI(app.getGlobalTowerInfo());
    } catch (Throwable e) {
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
    }
  }
  
  public void onResume() {
    super.onResume();
    pbBuffer.setProgress(app.getRecorderCtx().getFrames().size());
    pbBuffer.setMax(Integer.parseInt(prefs.getString(PreferencesRecorder.PREFS_KEY_FLUSH,
            PreferencesRecorder.PREFS_DEFAULT_FLUSH)));
  }
  
  @Override
  public void processUI(final TowerInfo ti) throws Throwable {
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
    
    /*notificationUpdate("Records: " + txtRecords.getText().toString(),
        "Buffer: " + app.getRecorderCtx().getFrames().size() + "/"  + pbBuffer.getMax(),
        "Size: " + ss);*/

    /*String message = "Records: " + txtRecords.getText().toString() + "\n";
    message += "Buffer: " + app.getRecorderCtx().getFrames().size() + "/"  + pbBuffer.getMax() + "\n";
    message += "Size: " + ss + "\n";
    notificationUpdate(message);*/
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
          notificationShow();
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
        notificationRemove();
        notifyBuilder = null;
        notificationManager = null;
      }
    });
  }

  public void notificationShow() {
    final Activity a = getActivity();
    notificationManager = (NotificationManager)a.getSystemService(Context.NOTIFICATION_SERVICE);
    Intent toLaunch = new Intent(a.getApplicationContext(), CellHistoryPagerActivity.class);
    toLaunch.setAction("android.intent.action.MAIN");
    toLaunch.addCategory("android.intent.category.LAUNCHER");
    //notificationNum = 0;
    PendingIntent intentBack = PendingIntent.getActivity(a.getApplicationContext(), 0, toLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
    notifyBuilder = new NotificationCompat.Builder(a)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notificationtext))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(intentBack);

    notificationManager.notify(notifyID, notifyBuilder.build());
  }
  
  private void notificationRemove() {
    if(notificationManager == null) return;
    notificationManager.cancel(notifyID);
  }
  
  /*private void notificationUpdate(String message) {
    if(notificationManager == null || notifyBuilder == null) return;
    notifyBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
    //notifyBuilder.setNumber(++notificationNum);
    notificationManager.notify(notifyID, notifyBuilder.build());
  }*/
}
