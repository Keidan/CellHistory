package org.kei.android.phone.cellhistory.fragments;

import org.kei.android.atk.utils.Tools;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.contexts.RecorderCtx;
import org.kei.android.phone.cellhistory.prefs.PreferencesRecorder;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

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
  /* UI */
  private TextView                   txtRecords    = null;
  private ProgressBar                pbBuffer      = null;
  private TextView                   txtSize       = null;
  private ToggleButton               toggleOnOff   = null;
  /* context */
  private SharedPreferences          prefs         = null;
  private CellHistoryApp             app           = null;


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
    pbBuffer.setProgress(app.getRecorderCtx().getFrames().size());
    txtRecords.setText(String.valueOf(ti.getRecords()));
    txtSize.setText(RecorderCtx.convertToHuman(app.getRecorderCtx().getSize()));
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
                  PreferencesRecorder.PREFS_DEFAULT_DEL_PREV_FILE),
              prefs.getString(PreferencesRecorder.PREFS_KEY_FORMATS,
                  PreferencesRecorder.PREFS_DEFAULT_FORMATS),
              prefs.getBoolean(PreferencesRecorder.PREFS_KEY_INDENTATION,
                  PreferencesRecorder.PREFS_DEFAULT_INDENTATION));
          app.notificationRecorderShow(pbBuffer.getMax());
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
        app.getNfyRecorderHelper().hide();
      }
    });
  }

}
