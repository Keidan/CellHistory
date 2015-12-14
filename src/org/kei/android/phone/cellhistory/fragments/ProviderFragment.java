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
  private TextView          txtSpeed                     = null;
  private TextView          txtDistance                  = null;
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
  private boolean           connected                    = false;

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
    txtSpeed = (TextView) getView().findViewById(R.id.txtSpeed);
    txtDistance = (TextView) getView().findViewById(R.id.txtDistance);
    txtDistance.setText(getResources().getString(R.string.txtGpsDisabled));
    txtDistance.setTextColor(color_red);
    txtSpeed.setText(getResources().getString(R.string.txtGpsDisabled));
    txtSpeed.setTextColor(color_red);
    txtGeolocation.setOnClickListener(this);
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
   
    if(connected) {
      txtDistance.setTextColor(default_color);
      txtSpeed.setTextColor(default_color);
      double speed = app.getGlobalTowerInfo().getSpeed();
      String spd = String.format("%.02f", speed) + " m/s\n";
        spd += String.format("%.02f", speed * 3.6) + " km/h\n";
        spd += String.format("%.02f", speed * 2.2369362920544) + " mph";
        txtSpeed.setText(spd);
        
      final double dist = app.getGlobalTowerInfo().getDistance();
      if (dist > 1000)
        txtDistance.setText(String.format("%.02f", dist / 1000) + " km");
      else
        txtDistance.setText(String.format("%.02f", dist) + " m");
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

    restoreText();
    txtSpeed.setTextColor(color_orange);
    if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE,
        PreferencesGeolocation.PREFS_DEFAULT_LOCATE)) {
      txtGeoProvider.setVisibility(View.GONE);
      spiGeoProvider.setVisibility(View.VISIBLE);
      if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_GPS,
          PreferencesGeolocation.PREFS_DEFAULT_GPS)) {
        txtDistance.setTextColor(color_red);
        txtSpeed.setTextColor(color_red);
        if (lm == null) {
          lm = (LocationManager) getActivity().getSystemService(
              Context.LOCATION_SERVICE);
          if (lm != null)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_PROVIDER, 
                PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_PROVIDER)), 10f,
                this);
        }
      } else {
        connected = false;
        if (lm != null) {
          lm.removeUpdates(this);
          lm = null;
        }
        txtDistance.setText(txtGpsDisabledOption);
        txtDistance.setTextColor(color_orange);
        txtSpeed.setText(txtGpsDisabledOption);
        txtSpeed.setTextColor(color_orange);
      }
    } else {
      connected = false;
      txtGeoProvider.setVisibility(View.VISIBLE);
      spiGeoProvider.setVisibility(View.GONE);
      if (lm != null) {
        lm.removeUpdates(this);
        lm = null;
      }
      txtDistance.setText(txtGpsDisabledOption);
      txtDistance.setTextColor(color_orange);
      txtSpeed.setText(txtGpsDisabledOption);
      txtSpeed.setTextColor(color_orange);
    }
  }
  
  private void restoreText() {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        txtDistance.setText("0.00 m");
        txtSpeed.setText("0.00 m/s\n0.00 km/h\n0.00 mph");
        if (chart.getVisibility() == View.VISIBLE) {
          chart.checkYAxisMax(0.0);
          chart.addTimePoint(color_blue_dark, color_blue_dark_transparent,
              new Date().getTime(), 0.0);
        }
      }
    });
  }

  private void setChartVisible(final boolean visible) {
    final int visibility = visible ? View.VISIBLE : View.GONE;
    if(chart == null) return;
    if(visible)chart.clear();
    if (chartSeparator.getVisibility() != visibility)
      chartSeparator.setVisibility(visibility);
    if (visible && chart.getVisibility() == View.GONE)
      chart.setVisibility(View.VISIBLE);
    else if (!visible && chart.getVisibility() == View.VISIBLE)
      chart.setVisibility(View.GONE);
  }
  
  @Override
  public void onClick(final View v) {
    if (v.equals(txtGeolocation)) {
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
  
  private void speedUpdate(final double velocity) {
    if (chart != null && getActivity() != null)
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (chart.getVisibility() == View.VISIBLE) {
            chart.checkYAxisMax(velocity);
            chart.addTimePoint(color_blue_dark, color_blue_dark_transparent,
                new Date().getTime(), velocity);
          }
        }
      });
  }

  @Override
  public void onLocationChanged(final Location location) {
    connected = true;
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
      }
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
    speedUpdate(speed);
  }

  @Override
  public void onStatusChanged(final String provider, final int status,
      final Bundle extras) {
    if(provider.equals(LocationManager.GPS_PROVIDER)) {
      switch (status) {
        case LocationProvider.OUT_OF_SERVICE:
          resetGpsInfo(txtGpsOutOfService);
          break;
        case LocationProvider.TEMPORARILY_UNAVAILABLE:
          resetGpsInfo(txtGpsTemporarilyUnavailable);
          break;
      }
    }
  }

  @Override
  public void onProviderEnabled(final String provider) {
    if(provider.equals(LocationManager.GPS_PROVIDER)) {
      txtDistance.setTextColor(default_color);
      txtSpeed.setTextColor(default_color);
      restoreText();
    }
  }

  @Override
  public void onProviderDisabled(final String provider) {
    if(provider.equals(LocationManager.GPS_PROVIDER)) {
      resetGpsInfo(txtGpsDisabled);
    }
  }

  private void resetGpsInfo(final String txt) {
    connected = false;
    app.getGlobalTowerInfo().lock();
    try {
      app.getGlobalTowerInfo().setDistance(0.0);
      app.getGlobalTowerInfo().setSpeed(0.0);
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
    txtDistance.setText(txt);
    txtSpeed.setText(txt);
  }
}
