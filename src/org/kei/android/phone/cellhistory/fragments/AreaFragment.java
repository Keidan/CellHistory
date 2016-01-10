package org.kei.android.phone.cellhistory.fragments;

import org.kei.android.atk.utils.fx.Color;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.prefs.PreferencesGeolocation;
import org.kei.android.phone.cellhistory.services.tasks.GpsServiceTask;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *******************************************************************************
 * @file AreaFragment.java
 * @author Keidan
 * @date 21/12/2015
 * @par Project CellHistory
 *
 * @par Copyright 2015-2016 Keidan, all right reserved
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
public class AreaFragment extends Fragment implements UITaskFragment {
  
  /* context */
  private SharedPreferences prefs                        = null;
  private CellHistoryApp    app                          = null;
  /* colors */
  private int               color_red                    = Color.BLACK;
  private int               color_orange                 = Color.BLACK;
  private String            unit_m                       = "";
  private String            unit_km                      = "";
  private String            txtGpsDisabled               = "";
  private String            txtGpsWaitSatellites         = "";
  private String            txtGpsDisabledOption         = "";
  private String            txtGpsOutOfService           = "";
  private String            txtGpsTemporarilyUnavailable = "";
  private boolean           connected                    = false;
  private boolean           enabled                      = false;
  /* UI */
  private TextView          txtCurrentArea               = null;
  private TextView          txtNextArea                  = null;
  private TextView          txtCurrentAreaError          = null;
  private TextView          txtNextAreaError             = null;
  private TextView          txtNextAreaDistance          = null;
  private TextView          lblUnitM                     = null;
  private final TextView    lblUnitKM                    = null;
  private boolean           registered                   = false;
  
  @Override
  public View onCreateView(final LayoutInflater inflater,
      final ViewGroup container, final Bundle savedInstanceState) {
    final ViewGroup rootView = (ViewGroup) inflater.inflate(
        R.layout.fragment_area, container, false);
    
    return rootView;
  }
  
  @Override
  public void onViewCreated(final View view, final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    /* context */
    app = CellHistoryApp.getApp(getActivity());
    prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    /* color */
    final Resources resources = getResources();
    color_red = resources.getColor(Color.RED);
    color_orange = resources.getColor(Color.ORANGE);
    /* texts */
    txtGpsDisabled = resources.getString(R.string.txtGpsDisabled);
    txtGpsWaitSatellites = resources.getString(R.string.txtGpsWaitSatellites);
    txtGpsDisabledOption = resources.getString(R.string.txtGpsDisabledOption);
    txtGpsOutOfService = resources.getString(R.string.txtGpsOutOfService);
    txtGpsTemporarilyUnavailable = resources
        .getString(R.string.txtGpsTemporarilyUnavailable);
    unit_m = resources.getString(R.string.unit_m);
    unit_km = resources.getString(R.string.unit_km);
    /* UI */
    lblUnitM = (TextView) getView().findViewById(R.id.lblUnitM);
    txtCurrentArea = (TextView) getView().findViewById(R.id.txtCurrentArea);
    txtNextArea = (TextView) getView().findViewById(R.id.txtNextArea);
    txtCurrentAreaError = (TextView) getView().findViewById(
        R.id.txtCurrentAreaError);
    txtNextAreaError = (TextView) getView().findViewById(R.id.txtNextAreaError);
    txtNextAreaDistance = (TextView) getView().findViewById(
        R.id.txtNextAreaDistance);

    try {
      processUI(CellHistoryApp.getApp(getActivity()).getGlobalTowerInfo());
    } catch (final Throwable e) {
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
    }
  }

  @Override
  public void processUI(final TowerInfo ti) throws Throwable {
    if (!isAdded() || !connected || !enabled)
      return;
  }

  @Override
  public void onResume() {
    super.onResume();
    if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE,
        PreferencesGeolocation.PREFS_DEFAULT_LOCATE)) {
      if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_GPS,
          PreferencesGeolocation.PREFS_DEFAULT_GPS)) {
        registered = true;
        getActivity().registerReceiver(receiver,
            new IntentFilter(GpsServiceTask.NOTIFICATION));
        if (connected && enabled)
          setGpsVisibility(true);
        else
          setGpsVisibility(false);
      } else {
        resetGpsInfo(txtGpsDisabledOption, color_orange);
      }
    } else {
      resetGpsInfo(txtGpsDisabledOption, color_orange);
    }
  }
  
  @Override
  public void onPause() {
    super.onPause();
    if(registered) {
      getActivity().unregisterReceiver(receiver);
      registered = false;
    }
  }

  private final BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(
        final Context context, final Intent intent) {
      final Bundle bundle = intent.getExtras();
      if (bundle != null) {
        final int event = bundle
             .getInt(GpsServiceTask.EVENT);
         Log.i(getClass()
             .getSimpleName(),
             "Event: " + event);
         if (event == GpsServiceTask.EVENT_CONNECTED) {
           connected = true;
         } else if (event == GpsServiceTask.EVENT_DISABLED) {
           connected = false;
           enabled = false;
           resetGpsInfo(txtGpsDisabled,
               color_red);
         } else if (event == GpsServiceTask.EVENT_ENABLED) {
           enabled = true;
           setGpsVisibility(true);
         } else if (event == GpsServiceTask.EVENT_OUT_OF_SERVICE) {
           resetGpsInfo(
               txtGpsOutOfService,
               color_red);
         } else if (event == GpsServiceTask.EVENT_TEMPORARILY_UNAVAILABLE) {
           resetGpsInfo(
               txtGpsTemporarilyUnavailable,
               color_red);
         } else if (event == GpsServiceTask.EVENT_UPDATE) {
           setGpsVisibility(true);
         } else if (event == GpsServiceTask.EVENT_WAIT_FOR_SATELLITES) {
           resetGpsInfo(
               txtGpsWaitSatellites,
               color_red);
         }
       }
     }
   };

  private void resetGpsInfo(final String txt, final int color) {
    txtNextAreaError.setText(txt);
    txtCurrentAreaError.setText(txt);
    txtNextAreaError.setTextColor(color);
    txtCurrentAreaError.setTextColor(color);
    setGpsVisibility(false);
  }

  private void setGpsVisibility(final boolean visible) {
    final int v = visible ? View.VISIBLE : View.GONE;
    final int v2 = visible ? View.GONE : View.VISIBLE;
    if (txtNextArea.getVisibility() != v)
      txtNextArea.setVisibility(v);
    if (txtCurrentArea.getVisibility() != v)
      txtCurrentArea.setVisibility(v);
    if (txtNextAreaError.getVisibility() != v2)
      txtNextAreaError.setVisibility(v2);
    if (txtCurrentAreaError.getVisibility() != v2)
      txtCurrentAreaError.setVisibility(v2);
  }
}
