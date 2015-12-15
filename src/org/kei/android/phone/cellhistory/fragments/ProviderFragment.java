package org.kei.android.phone.cellhistory.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kei.android.atk.utils.fx.Color;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.contexts.ProviderCtx;
import org.kei.android.phone.cellhistory.prefs.Preferences;
import org.kei.android.phone.cellhistory.prefs.PreferencesGeolocation;
import org.kei.android.phone.cellhistory.prefs.PreferencesTimers;
import org.kei.android.phone.cellhistory.towers.CellIdHelper;
import org.kei.android.phone.cellhistory.towers.TowerInfo;
import org.kei.android.phone.cellhistory.views.TimeChartHelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 *******************************************************************************
 * @file ProviderFragment.java
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
public class ProviderFragment extends Fragment implements UITaskFragment,
OnItemSelectedListener, OnClickListener, LocationListener {
  /* UI */
  private Spinner           spiGeoProvider               = null;
  private TextView          txtGeoProvider               = null;
  private TextView          txtGeolocation               = null;
  private TextView          txtSpeedMS                   = null;
  private TextView          txtSpeedKMH                  = null;
  private TextView          txtSpeedMPH                  = null;
  private RadioButton       rbSpeedMS                    = null;
  private RadioButton       rbSpeedKMH                   = null;
  private RadioButton       rbSpeedMPH                   = null;
  private TextView          txtDistance                  = null;
  private TextView          txtSpeedError                = null;
  private TextView          txtDistanceError             = null;
  private TimeChartHelper   chart                        = null;
  private LinearLayout      chartSeparator               = null;
  /* context */
  private SharedPreferences prefs                        = null;
  private CellHistoryApp    app                          = null;
  /* colors */
  private int               color_red                    = Color.BLACK;
  private int               color_orange                 = Color.BLACK;
  private int               color_blue_dark              = Color.BLACK;
  private int               color_blue_dark_transparent  = Color.BLACK;
  private LocationManager   lm                           = null;
  private String            txtGpsDisabled               = "";
  private String            txtGpsDisabledOption         = "";
  private String            txtGpsOutOfService           = "";
  private String            txtGpsTemporarilyUnavailable = "";
  private int               default_color                = android.graphics.Color.TRANSPARENT;

  @Override
  public View onCreateView(final LayoutInflater inflater,
      final ViewGroup container, final Bundle savedInstanceState) {
    final ViewGroup rootView = (ViewGroup) inflater.inflate(
        R.layout.fragment_geolocation, container, false);

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
    color_blue_dark = resources.getColor(Color.BLUE_DARK);
    color_blue_dark_transparent = resources
        .getColor(Color.BLUE_DARK_TRANSPARENT);
    default_color = new TextView(getActivity()).getTextColors().getDefaultColor();
    /* texts */
    txtGpsDisabled = resources.getString(R.string.txtGpsDisabled);
    txtGpsDisabledOption = resources
        .getString(R.string.txtGpsDisabledOption);
    txtGpsOutOfService = resources
        .getString(R.string.txtGpsOutOfService);
    txtGpsTemporarilyUnavailable = resources
        .getString(R.string.txtGpsTemporarilyUnavailable);
    
    /* UI */
    chartSeparator = (LinearLayout) getView().findViewById(R.id.chartSeparator);
    spiGeoProvider = (Spinner) getView().findViewById(R.id.spiGeoProvider);
    txtGeoProvider = (TextView) getView().findViewById(R.id.txtGeoProvider);
    txtGeolocation = (TextView) getView().findViewById(R.id.txtGeolocation);

    txtSpeedMS = (TextView) getView().findViewById(R.id.txtSpeedMS);
    txtSpeedKMH = (TextView) getView().findViewById(R.id.txtSpeedKMH);
    txtSpeedMPH = (TextView) getView().findViewById(R.id.txtSpeedMPH);
    rbSpeedMS = (RadioButton) getView().findViewById(R.id.rbSpeedMS);
    rbSpeedKMH = (RadioButton) getView().findViewById(R.id.rbSpeedKMH);
    rbSpeedMPH = (RadioButton) getView().findViewById(R.id.rbSpeedMPH);
    
    txtDistance = (TextView) getView().findViewById(R.id.txtDistance);
    txtSpeedError = (TextView) getView().findViewById(R.id.txtSpeedError);
    txtDistanceError = (TextView) getView().findViewById(R.id.txtDistanceError);
    txtSpeedError.setText(getResources().getString(R.string.txtGpsDisabled));
    txtSpeedError.setTextColor(color_red);
    txtDistanceError.setText(getResources().getString(R.string.txtGpsDisabled));
    txtDistanceError.setTextColor(color_red);
    
    txtGeolocation.setOnClickListener(this);
    rbSpeedMS.setOnClickListener(this);
    rbSpeedKMH.setOnClickListener(this);
    rbSpeedMPH.setOnClickListener(this);
    int i = prefs.getInt(PreferencesGeolocation.PREFS_KEY_CURRENT_SPEED, PreferencesGeolocation.PREFS_DEFAULT_CURRENT_SPEED);
    if(i == PreferencesGeolocation.PREFS_SPEED_KMH) rbSpeedKMH.setChecked(true);
    else if(i == PreferencesGeolocation.PREFS_SPEED_MPH) rbSpeedMPH.setChecked(true);
    else rbSpeedMS.setChecked(true);
    
    final List<String> list = new ArrayList<String>();
    list.add(CellIdHelper.GOOGLE_HIDDENT_API);
    list.add(CellIdHelper.OPEN_CELL_ID_API);
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
        getActivity(), android.R.layout.simple_spinner_item, list);
    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
    spiGeoProvider.setAdapter(adapter);
    spiGeoProvider.setOnItemSelectedListener(this);
    if (prefs.getString(PreferencesGeolocation.PREFS_KEY_CURRENT_PROVIDER,
        PreferencesGeolocation.PREFS_DEFAULT_CURRENT_PROVIDER).equals(
            CellIdHelper.GOOGLE_HIDDENT_API))
      spiGeoProvider.setSelection(0);
    else
      spiGeoProvider.setSelection(1);
    chart = new TimeChartHelper();
    chart.setChartContainer((LinearLayout) getView().findViewById(R.id.graph));
    chart.setFrequency(Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_PROVIDER, 
              PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_PROVIDER)));
    chart.install(getActivity(), default_color, true);
    chart.setYAxisMax(15);
    chart.addTimePoint(color_blue_dark, color_blue_dark_transparent,
        new Date().getTime(), 0);
    try {
      processUI(app.getGlobalTowerInfo());
    } catch (Throwable e) {
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
    }
  }

  @Override
  public void processUI(final TowerInfo ti) throws Throwable {
    if (txtGeoProvider == null)
      return;
    if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE,
        PreferencesGeolocation.PREFS_DEFAULT_LOCATE)) {
      txtGeoProvider.setVisibility(View.GONE);
      spiGeoProvider.setVisibility(View.VISIBLE);
    } else {
      txtGeoProvider.setVisibility(View.VISIBLE);
      spiGeoProvider.setVisibility(View.GONE);
    }
    
    final String oldLoc = app.getProviderCtx().getOldLoc();
    if (oldLoc.startsWith(ProviderCtx.LOC_NONE))
      txtGeolocation.setTextColor(color_red);
    else if (oldLoc.startsWith(ProviderCtx.LOC_NOT_FOUND)
        || oldLoc.startsWith(ProviderCtx.LOC_BAD_REQUEST))
      txtGeolocation.setTextColor(color_orange);
    else {
      txtGeolocation.setTextColor(color_blue_dark);
    }
    txtGeolocation.setText(oldLoc);
   
    double speed = app.getGlobalTowerInfo().getSpeed();
    double s_ms = speed;
    double s_kmh = speed * 3.6;
    double s_mph = speed * 2.2369362920544;
    txtSpeedMS.setText(String.format("%.02f", s_ms) + " m/s");
    txtSpeedKMH.setText(String.format("%.02f", s_kmh) + " km/h");
    txtSpeedMPH.setText(String.format("%.02f", speed * 2.2369362920544) + " mph");
      
    final double dist = app.getGlobalTowerInfo().getDistance();
    if (dist > 1000)
      txtDistance.setText(String.format("%.02f", dist / 1000) + " km");
    else
      txtDistance.setText(String.format("%.02f", dist) + " m");
    if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE,
        PreferencesGeolocation.PREFS_DEFAULT_LOCATE) && prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_GPS,
            PreferencesGeolocation.PREFS_DEFAULT_GPS)) {
      if (chart.getVisibility() == View.VISIBLE) {
        double s = s_ms;
        int i = prefs.getInt(PreferencesGeolocation.PREFS_KEY_CURRENT_SPEED, PreferencesGeolocation.PREFS_DEFAULT_CURRENT_SPEED);
        if(i == PreferencesGeolocation.PREFS_SPEED_KMH) s = s_kmh;
        else if(i == PreferencesGeolocation.PREFS_SPEED_MPH) s = s_mph;
        chart.checkYAxisMax(s);
        chart.addTimePoint(color_blue_dark, color_blue_dark_transparent,
            new Date().getTime(), s);
      }
    }
  }
  
  @Override
  public void onItemSelected(final AdapterView<?> parent, final View view,
      final int pos, final long id) {
    final String newP = parent.getItemAtPosition(pos).toString();
    final String oldP = prefs.getString(
        PreferencesGeolocation.PREFS_KEY_CURRENT_PROVIDER,
        PreferencesGeolocation.PREFS_DEFAULT_CURRENT_PROVIDER);
    if (!oldP.equals(newP)) {
      final Editor editor = prefs.edit();
      editor.putString(PreferencesGeolocation.PREFS_KEY_CURRENT_PROVIDER, newP);
      editor.commit();
      app.getProviderCtx().clear();
      app.getProviderTask().start(
          Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_PROVIDER, 
              PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_PROVIDER)));
    }
  }

  @Override
  public void onNothingSelected(final AdapterView<?> parent) {
  }

  @Override
  public void onDestroy() {
    if (lm != null) {
      lm.removeUpdates(this);
      lm = null;
    }
    super.onDestroy();
  }

  @Override
  public void onResume() {
    super.onResume();
    app.getProviderCtx().clear();
    
    chart.setFrequency(Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_PROVIDER, 
              PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_PROVIDER)));
    setChartVisible(prefs.getBoolean(Preferences.PREFS_KEY_CHART_ENABLE,
        Preferences.PREFS_DEFAULT_CHART_ENABLE));

    if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE,
        PreferencesGeolocation.PREFS_DEFAULT_LOCATE)) {
      txtGeoProvider.setVisibility(View.GONE);
      spiGeoProvider.setVisibility(View.VISIBLE);
      if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_GPS,
          PreferencesGeolocation.PREFS_DEFAULT_GPS)) {
        if (lm == null) {
          lm = (LocationManager) getActivity().getSystemService(
              Context.LOCATION_SERVICE);
          if (lm != null) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_PROVIDER, 
                PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_PROVIDER)), 10f,
                this);
          }
          setGpsVisibility(true);
        }
      } else {
        if (lm != null) {
          lm.removeUpdates(this);
          lm = null;
        }
        resetGpsInfo(txtGpsDisabledOption, color_orange);
      }
    } else {
      txtGeoProvider.setVisibility(View.VISIBLE);
      spiGeoProvider.setVisibility(View.GONE);
      if (lm != null) {
        lm.removeUpdates(this);
        lm = null;
      }
      resetGpsInfo(txtGpsDisabledOption, color_orange);
    }
  }
  
  private void setChartVisible(final boolean visible) {
    final int visibility = visible ? View.VISIBLE : View.GONE;
    if(chart == null) return;
    if(visible)chart.clear();
    if (chartSeparator.getVisibility() != visibility)
      chartSeparator.setVisibility(visibility);
    if (visible && chart.getVisibility() == View.GONE) {
      chart.setVisibility(View.VISIBLE);
      rbSpeedMS.setVisibility(View.VISIBLE);
      rbSpeedKMH.setVisibility(View.VISIBLE);
      rbSpeedMPH.setVisibility(View.VISIBLE);
    }
    else if (!visible && chart.getVisibility() == View.VISIBLE) {
      chart.setVisibility(View.GONE);
      rbSpeedMS.setVisibility(View.GONE);
      rbSpeedKMH.setVisibility(View.GONE);
      rbSpeedMPH.setVisibility(View.GONE);
    }
    if(chart.getVisibility() == View.VISIBLE) {
      chart.checkYAxisMax(0.0);
      chart.addTimePoint(color_blue_dark, color_blue_dark_transparent,
          new Date().getTime(), 0.0);
    }
  }
  
  @Override
  public void onClick(final View v) {
    
    if(v.equals(rbSpeedMS)) {
      CellHistoryApp.addLog(getActivity(), "Select MS display");
      Editor e = prefs.edit();
      e.putInt(PreferencesGeolocation.PREFS_KEY_CURRENT_SPEED, PreferencesGeolocation.PREFS_SPEED_MS);
      e.commit();
      rbSpeedKMH.setChecked(false);
      rbSpeedMPH.setChecked(false);
    } else if(v.equals(rbSpeedKMH)) {
      CellHistoryApp.addLog(getActivity(), "Select KMH display");
      Editor e = prefs.edit();
      e.putInt(PreferencesGeolocation.PREFS_KEY_CURRENT_SPEED, PreferencesGeolocation.PREFS_SPEED_KMH);
      e.commit();
      rbSpeedMS.setChecked(false);
      rbSpeedMPH.setChecked(false);
    } else if(v.equals(rbSpeedMPH)) {
      CellHistoryApp.addLog(getActivity(), "Select MPH display");
      Editor e = prefs.edit();
      e.putInt(PreferencesGeolocation.PREFS_KEY_CURRENT_SPEED, PreferencesGeolocation.PREFS_SPEED_MPH);
      e.commit();
      rbSpeedKMH.setChecked(false);
      rbSpeedMS.setChecked(false);
    } else if (v.equals(txtGeolocation)) {
      final String geo = txtGeolocation.getText().toString();
      if (!geo.isEmpty() && app.getProviderCtx().isValid()) {
        String cid = "-1";
        String lac = "-1";
        app.getGlobalTowerInfo().lock();
        try {
          cid = String.valueOf(app.getGlobalTowerInfo().getCellId());
          lac = String.valueOf(app.getGlobalTowerInfo().getLac());
        } finally {
          app.getGlobalTowerInfo().unlock();
        }
        final String uriBegin = "geo:" + geo;
        final String query = geo + "(CellID: " + cid + ", LAC: " + lac + ")";
        final String encodedQuery = Uri.encode(query);
        final String uriString = uriBegin + "?q=" + encodedQuery
            + "&z=16&iwloc=A";
        final Uri uri = Uri.parse(uriString);
        final Intent mapViewIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(mapViewIntent);
      }
    }
  }
  
  @Override
  public void onLocationChanged(final Location location) {
    CellHistoryApp.addLog(getActivity(), location);
    double speed = 0.0;
    final Location loc1 = new Location("");
    app.getGlobalTowerInfo().lock();
    try {
      speed = location.getSpeed();
      app.getGlobalTowerInfo().setSpeed(speed);
      if (!Double.isNaN(app.getGlobalTowerInfo().getLatitude())
          && !Double.isNaN(app.getGlobalTowerInfo().getLongitude())) {
        loc1.setLatitude(app.getGlobalTowerInfo().getLatitude());
        loc1.setLongitude(app.getGlobalTowerInfo().getLongitude());
        app.getGlobalTowerInfo().setDistance(loc1.distanceTo(location));
        CellHistoryApp.addLog(getActivity(), "New distance: " + app.getGlobalTowerInfo().getDistance() + " m.");
      }
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
  }

  @Override
  public void onStatusChanged(final String provider, final int status,
      final Bundle extras) {
    if(provider.equals(LocationManager.GPS_PROVIDER)) {
      switch (status) {
        case LocationProvider.OUT_OF_SERVICE:
          CellHistoryApp.addLog(getActivity(), "onStatusChanged("+provider+", OUT_OF_SERVICE)");
          resetGpsInfo(txtGpsOutOfService, color_red);
          break;
        case LocationProvider.TEMPORARILY_UNAVAILABLE:
          CellHistoryApp.addLog(getActivity(), "onStatusChanged("+provider+", TEMPORARILY_UNAVAILABLE)");
          resetGpsInfo(txtGpsTemporarilyUnavailable, color_red);
          break;
        case LocationProvider.AVAILABLE:
          setGpsVisibility(true);
          break;
      }
    }
  }

  @Override
  public void onProviderEnabled(final String provider) {
    if(provider.equals(LocationManager.GPS_PROVIDER)) {
      CellHistoryApp.addLog(getActivity(), "onProviderEnabled("+provider+")");
      setGpsVisibility(true);
    }
  }

  @Override
  public void onProviderDisabled(final String provider) {
    if(provider.equals(LocationManager.GPS_PROVIDER)) {
      CellHistoryApp.addLog(getActivity(), "onProviderDisabled("+provider+")");
      resetGpsInfo(txtGpsDisabled, color_red);
    }
  }

  private void resetGpsInfo(final String txt, int color) {
    app.getGlobalTowerInfo().lock();
    try {
      app.getGlobalTowerInfo().setDistance(0.0);
      app.getGlobalTowerInfo().setSpeed(0.0);
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
    txtDistanceError.setText(txt);
    txtDistanceError.setTextColor(color);
    txtSpeedError.setText(txt);
    txtSpeedError.setTextColor(color);
    setGpsVisibility(false);
  }
  
  private void setGpsVisibility(boolean visible) {
    int v = visible ? View.VISIBLE : View.GONE;
    int v2 = visible ? View.GONE : View.VISIBLE;
    if (txtDistance.getVisibility() != v) txtDistance.setVisibility(v);
    if (txtSpeedMS.getVisibility() != v) txtSpeedMS.setVisibility(v);
    if (txtSpeedKMH.getVisibility() != v) txtSpeedKMH.setVisibility(v);
    if (txtSpeedMPH.getVisibility() != v) txtSpeedMPH.setVisibility(v);
    if (chart.getVisibility() == View.VISIBLE) {
      if (rbSpeedMS.getVisibility() != v) rbSpeedMS.setVisibility(v);
      if (rbSpeedKMH.getVisibility() != v) rbSpeedKMH.setVisibility(v);
      if (rbSpeedMPH.getVisibility() != v) rbSpeedMPH.setVisibility(v);
    }
    if (txtDistanceError.getVisibility() != v2) txtDistanceError.setVisibility(v2);
    if (txtSpeedError.getVisibility() != v2) txtSpeedError.setVisibility(v2);
    
  }
}
