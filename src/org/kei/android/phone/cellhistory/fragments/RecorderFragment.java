package org.kei.android.phone.cellhistory.fragments;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.kei.android.atk.utils.fx.Fx;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.contexts.RecorderCtx;
import org.kei.android.phone.cellhistory.prefs.PreferencesRecorder;
import org.kei.android.phone.cellhistory.services.RecorderService;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Switch;
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
    OnClickListener, OnCheckedChangeListener {
  private static final String  SW_OPERATOR        = "swOperator";
  private static final String  SW_MCC             = "swMCC";
  private static final String  SW_MNC             = "swMNC";
  private static final String  SW_CELLID          = "swCellId";
  private static final String  SW_LAC             = "swLAC";
  private static final String  SW_GEOLOCATION     = "swGeolocation";
  private static final String  SW_PSC             = "swPSC";
  private static final String  SW_TYPE            = "swType";
  private static final String  SW_NETWORK         = "swNetwork";
  private static final String  SW_ASU             = "swASU";
  private static final String  SW_LVL             = "swLVL";
  private static final String  SW_SS              = "swSS";
  private static final String  SW_NEIGHBORING     = "swNeighboring";
  private static final String  SW_PROVIDER        = "swProvider";
  private static final String  SW_DISTANCE        = "swDistance";
  private static final String  SW_SATELLITES      = "swSatellites";
  private static final String  SW_SPEED           = "swSpeed";
  private static final String  SW_DISPLAY         = "chkDisplaySwitch";
  private static final boolean SW_DEFAULT         = true;
  private static final boolean SW_DEFAULT_DISPLAY = false;
  /* UI */
  private TextView             txtRecords         = null;
  private ProgressBar          pbBuffer           = null;
  private TextView             txtSize            = null;
  private ToggleButton         toggleOnOff        = null;
  /* context */
  private SharedPreferences    prefs              = null;
  private CellHistoryApp       app                = null;
  private Switch               swOperator         = null;
  private Switch               swMCC              = null;
  private Switch               swMNC              = null;
  private Switch               swCellId           = null;
  private Switch               swLAC              = null;
  private Switch               swGeolocation      = null;
  private Switch               swPSC              = null;
  private Switch               swType             = null;
  private Switch               swNetwork          = null;
  private Switch               swASU              = null;
  private Switch               swLVL              = null;
  private Switch               swSS               = null;
  private Switch               swNeighboring      = null;
  private Switch               swProvider         = null;
  private Switch               swDistance         = null;
  private Switch               swSatellites       = null;
  private Switch               swSpeed            = null;
  private CheckBox             chkDisplaySwitch   = null;
  private ScrollView           switches           = null;

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
    swOperator = (Switch) getView().findViewById(R.id.swOperator);
    swMCC = (Switch) getView().findViewById(R.id.swMCC);
    swMNC = (Switch) getView().findViewById(R.id.swMNC);
    swCellId = (Switch) getView().findViewById(R.id.swCellId);
    swLAC = (Switch) getView().findViewById(R.id.swLAC);
    swGeolocation = (Switch) getView().findViewById(R.id.swGeolocation);
    swPSC = (Switch) getView().findViewById(R.id.swPSC);
    swType = (Switch) getView().findViewById(R.id.swType);
    swNetwork = (Switch) getView().findViewById(R.id.swNetwork);
    swASU = (Switch) getView().findViewById(R.id.swASU);
    swLVL = (Switch) getView().findViewById(R.id.swLVL);
    swSS = (Switch) getView().findViewById(R.id.swSS);
    swNeighboring = (Switch) getView().findViewById(R.id.swNeighboring);
    swProvider = (Switch) getView().findViewById(R.id.swProvider);
    swDistance = (Switch) getView().findViewById(R.id.swDistance);
    swSatellites = (Switch) getView().findViewById(R.id.swSatellites);
    swSpeed = (Switch) getView().findViewById(R.id.swSpeed);
    chkDisplaySwitch = (CheckBox) getView().findViewById(R.id.chkDisplaySwitch);
    switches = (ScrollView) getView().findViewById(R.id.switches);
    swOperator.setOnCheckedChangeListener(this);
    swMCC.setOnCheckedChangeListener(this);
    swMNC.setOnCheckedChangeListener(this);
    swCellId.setOnCheckedChangeListener(this);
    swLAC.setOnCheckedChangeListener(this);
    swGeolocation.setOnCheckedChangeListener(this);
    swPSC.setOnCheckedChangeListener(this);
    swType.setOnCheckedChangeListener(this);
    swNetwork.setOnCheckedChangeListener(this);
    swASU.setOnCheckedChangeListener(this);
    swLVL.setOnCheckedChangeListener(this);
    swSS.setOnCheckedChangeListener(this);
    swNeighboring.setOnCheckedChangeListener(this);
    swProvider.setOnCheckedChangeListener(this);
    swDistance.setOnCheckedChangeListener(this);
    swSatellites.setOnCheckedChangeListener(this);
    swSpeed.setOnCheckedChangeListener(this);
    chkDisplaySwitch.setOnCheckedChangeListener(this);
    
    updateTowerInfo();
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
    swOperator.setChecked(prefs.getBoolean(SW_OPERATOR, SW_DEFAULT));
    swMCC.setChecked(prefs.getBoolean(SW_MCC, SW_DEFAULT));
    swMNC.setChecked(prefs.getBoolean(SW_MNC, SW_DEFAULT));
    swCellId.setChecked(prefs.getBoolean(SW_CELLID, SW_DEFAULT));
    swLAC.setChecked(prefs.getBoolean(SW_LAC, SW_DEFAULT));
    swGeolocation.setChecked(prefs.getBoolean(SW_GEOLOCATION, SW_DEFAULT));
    swPSC.setChecked(prefs.getBoolean(SW_PSC, SW_DEFAULT));
    swType.setChecked(prefs.getBoolean(SW_TYPE, SW_DEFAULT));
    swNetwork.setChecked(prefs.getBoolean(SW_NETWORK, SW_DEFAULT));
    swASU.setChecked(prefs.getBoolean(SW_ASU, SW_DEFAULT));
    swLVL.setChecked(prefs.getBoolean(SW_LVL, SW_DEFAULT));
    swSS.setChecked(prefs.getBoolean(SW_SS, SW_DEFAULT));
    swNeighboring.setChecked(prefs.getBoolean(SW_NEIGHBORING, SW_DEFAULT));
    swProvider.setChecked(prefs.getBoolean(SW_PROVIDER, SW_DEFAULT));
    swDistance.setChecked(prefs.getBoolean(SW_DISTANCE, SW_DEFAULT));
    swSatellites.setChecked(prefs.getBoolean(SW_SATELLITES, SW_DEFAULT));
    swSpeed.setChecked(prefs.getBoolean(SW_SPEED, SW_DEFAULT));
    chkDisplaySwitch.setChecked(prefs.getBoolean(SW_DISPLAY, SW_DEFAULT_DISPLAY));
    switches.setAnimation(null);
    if(chkDisplaySwitch.isChecked() && switches.getVisibility() != View.VISIBLE)
      switches.setVisibility(View.VISIBLE);
    else if(!chkDisplaySwitch.isChecked() && switches.getVisibility() != View.GONE)
      switches.setVisibility(View.GONE);
    updateTowerInfo();
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
        getActivity().startService(new Intent(getActivity(), RecorderService.class));
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
        getActivity().stopService(new Intent(getActivity(), RecorderService.class));
        app.getNfyRecorderHelper().hide();
      }
    });
  }
  
  @Override
  public void onCheckedChanged(CompoundButton buttonView,
    boolean isChecked) {
    Editor ed = prefs.edit();
    if(buttonView.equals(swOperator)) {
      ed.putBoolean(SW_OPERATOR, isChecked);
    } else if(buttonView.equals(swMCC)) {
      ed.putBoolean(SW_MCC, isChecked);
    } else if(buttonView.equals(swMNC)) {
      ed.putBoolean(SW_MNC, isChecked);
    } else if(buttonView.equals(swCellId)) {
      ed.putBoolean(SW_CELLID, isChecked);
    } else if(buttonView.equals(swLAC)) {
      ed.putBoolean(SW_LAC, isChecked);
    } else if(buttonView.equals(swGeolocation)) {
      ed.putBoolean(SW_GEOLOCATION, isChecked);
    } else if(buttonView.equals(swPSC)) {
      ed.putBoolean(SW_PSC, isChecked);
    } else if(buttonView.equals(swType)) {
      ed.putBoolean(SW_TYPE, isChecked);
    } else if(buttonView.equals(swNetwork)) {
      ed.putBoolean(SW_NETWORK, isChecked);
    } else if(buttonView.equals(swASU)) {
      ed.putBoolean(SW_ASU, isChecked);
    } else if(buttonView.equals(swLVL)) {
      ed.putBoolean(SW_LVL, isChecked);
    } else if(buttonView.equals(swSS)) {
      ed.putBoolean(SW_SS, isChecked);
    } else if(buttonView.equals(swNeighboring)) {
      ed.putBoolean(SW_NEIGHBORING, isChecked);
    } else if(buttonView.equals(swProvider)) {
      ed.putBoolean(SW_PROVIDER, isChecked);
    } else if(buttonView.equals(swDistance)) {
      ed.putBoolean(SW_DISTANCE, isChecked);
    } else if(buttonView.equals(swSatellites)) {
      ed.putBoolean(SW_SATELLITES, isChecked);
    } else if(buttonView.equals(swSpeed)) {
      ed.putBoolean(SW_SPEED, isChecked);
    } else if(buttonView.equals(chkDisplaySwitch)) {
      ed.putBoolean(SW_DISPLAY, isChecked);
      ed.commit();
      if(isChecked && switches.getVisibility() != View.VISIBLE) {
        Fx.setVisibilityAnimation(switches, View.VISIBLE, org.kei.android.atk.R.anim.fade_in);
        chkDisplaySwitch.setChecked(true);
      } else if(!isChecked && switches.getVisibility() != View.GONE) {
        Fx.setVisibilityAnimation(switches, View.GONE, org.kei.android.atk.R.anim.fade_out);
        chkDisplaySwitch.setChecked(false);
      }
      return;
    }
    ed.commit();
    updateTowerInfo();
  }
  
  private void updateTowerInfo() {
    app.getGlobalTowerInfo().lock();
    try {
      app.getGlobalTowerInfo().allow(
          swOperator.isChecked(), swMCC.isChecked(), swMNC.isChecked(), 
          swCellId.isChecked(), swLAC.isChecked(), swGeolocation.isChecked(), 
          swPSC.isChecked(), swType.isChecked(), swNetwork.isChecked(), 
          swASU.isChecked(), swLVL.isChecked(), swSS.isChecked(), 
          swNeighboring.isChecked(), swProvider.isChecked(), swDistance.isChecked(), 
          swSatellites.isChecked(), swSpeed.isChecked());
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
  }
}
