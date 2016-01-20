package org.kei.android.phone.cellhistory.fragments;

import org.kei.android.atk.utils.fx.Fx;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.contexts.FilterCtx;
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
  private static final String  SW_DISPLAY         = "chkDisplaySwitch";
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
  private Switch               swDataSpeedRx      = null;
  private Switch               swDataSpeedTx      = null;
  private Switch               swDataDirection    = null;
  private Switch               swIpv4             = null;
  private Switch               swIpv6             = null;
  private Switch               swAreas            = null;
  private CheckBox             chkDisplaySwitch   = null;
  private ScrollView           switches           = null;
  private boolean              fromResume         = false;
  
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
    swDataSpeedRx = (Switch) getView().findViewById(R.id.swDataSpeedRx);
    swDataSpeedTx = (Switch) getView().findViewById(R.id.swDataSpeedTx);
    swDataDirection = (Switch) getView().findViewById(R.id.swDataDirection);
    swIpv4 = (Switch) getView().findViewById(R.id.swIpv4);
    swIpv6 = (Switch) getView().findViewById(R.id.swIpv6);
    swAreas = (Switch) getView().findViewById(R.id.swAreas);
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
    swDataSpeedRx.setOnCheckedChangeListener(this);
    swDataSpeedTx.setOnCheckedChangeListener(this);
    swDataDirection.setOnCheckedChangeListener(this);
    swIpv4.setOnCheckedChangeListener(this);
    swIpv6.setOnCheckedChangeListener(this);
    swAreas.setOnCheckedChangeListener(this);
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
    fromResume = true;
    FilterCtx f = app.getFilterCtx();
    f.read(getActivity());
    swOperator.setChecked(f.getOperator().allowSave);
    swMCC.setChecked(f.getMCC().allowSave);
    swMNC.setChecked(f.getMNC().allowSave);
    swCellId.setChecked(f.getCellID().allowSave);
    swLAC.setChecked(f.getLAC().allowSave);
    swGeolocation.setChecked(f.getGeolocation().allowSave);
    swPSC.setChecked(f.getPSC().allowSave);
    swType.setChecked(f.getType().allowSave);
    swNetwork.setChecked(f.getNetworkId().allowSave);
    swASU.setChecked(f.getASU().allowSave);
    swLVL.setChecked(f.getLevel().allowSave);
    swSS.setChecked(f.getSignalStrength().allowSave);
    swNeighboring.setChecked(f.getNeighboring().allowSave);
    swProvider.setChecked(f.getProvider().allowSave);
    swDistance.setChecked(f.getDistance().allowSave);
    swSatellites.setChecked(f.getSatellites().allowSave);
    swSpeed.setChecked(f.getSpeed().allowSave);
    swDataSpeedRx.setChecked(f.getDataRxSpeed().allowSave);
    swDataSpeedTx.setChecked(f.getDataTxSpeed().allowSave);
    swDataDirection.setChecked(f.getDataDirection().allowSave);
    swIpv4.setChecked(f.getIPv4().allowSave);
    swIpv6.setChecked(f.getIPv6().allowSave);
    swAreas.setChecked(f.getAreas().allowSave);
    fromResume = false;
    chkDisplaySwitch.setChecked(prefs.getBoolean(SW_DISPLAY, SW_DEFAULT_DISPLAY));
    switches.setAnimation(null);
    if(chkDisplaySwitch.isChecked() && switches.getVisibility() != View.VISIBLE)
      switches.setVisibility(View.VISIBLE);
    else if(!chkDisplaySwitch.isChecked() && switches.getVisibility() != View.GONE)
      switches.setVisibility(View.GONE);
  }
  
  @Override
  public void processUI(final TowerInfo ti) throws Throwable {
    if(!isAdded()) return;
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
    if(buttonView.equals(swOperator) ||
            buttonView.equals(swMCC) ||
            buttonView.equals(swMNC) ||
            buttonView.equals(swCellId) ||
            buttonView.equals(swLAC) ||
            buttonView.equals(swGeolocation) ||
            buttonView.equals(swPSC) ||
            buttonView.equals(swType) ||
            buttonView.equals(swNetwork) ||
            buttonView.equals(swASU) ||
            buttonView.equals(swLVL) ||
            buttonView.equals(swSS) ||
            buttonView.equals(swNeighboring) ||
            buttonView.equals(swProvider) ||
            buttonView.equals(swDistance) ||
            buttonView.equals(swSatellites) ||
            buttonView.equals(swSpeed) ||
            buttonView.equals(swDataSpeedRx) ||
            buttonView.equals(swDataSpeedTx) ||
            buttonView.equals(swDataDirection) ||
            buttonView.equals(swIpv4) ||
            buttonView.equals(swIpv6) ||
            buttonView.equals(swAreas)) {
      if(fromResume) return;
      FilterCtx f = app.getFilterCtx();
      f.getOperator().allowSave = swOperator.isChecked();
      f.getMCC().allowSave = swMCC.isChecked();
      f.getMNC().allowSave = swMNC.isChecked();
      f.getCellID().allowSave = swCellId.isChecked();
      f.getLAC().allowSave = swLAC.isChecked();
      f.getPSC().allowSave = swPSC.isChecked();
      f.getType().allowSave = swType.isChecked();
      f.getNetworkId().allowSave = swNetwork.isChecked();
      f.getGeolocation().allowSave = swGeolocation.isChecked();
      f.getASU().allowSave = swASU.isChecked();
      f.getLevel().allowSave = swLVL.isChecked();
      f.getSignalStrength().allowSave = swSS.isChecked();
      f.getNeighboring().allowSave = swNeighboring.isChecked();
      f.getProvider().allowSave = swProvider.isChecked();
      f.getDistance().allowSave = swDistance.isChecked();
      f.getSatellites().allowSave = swSatellites.isChecked();
      f.getSpeed().allowSave = swSpeed.isChecked();
      f.getDataTxSpeed().allowSave = swDataSpeedTx.isChecked();
      f.getDataRxSpeed().allowSave = swDataSpeedRx.isChecked();
      f.getDataDirection().allowSave = swDataDirection.isChecked();
      f.getIPv4().allowSave = swIpv4.isChecked();
      f.getIPv6().allowSave = swIpv6.isChecked();
      f.getAreas().allowSave = swAreas.isChecked();
      f.writeSave(getActivity());
    } else if(buttonView.equals(chkDisplaySwitch)) {
      Editor ed = prefs.edit();
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
  }
}
