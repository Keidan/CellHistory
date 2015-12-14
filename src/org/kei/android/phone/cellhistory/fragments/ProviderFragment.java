package org.kei.android.phone.cellhistory.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kei.android.atk.utils.fx.Color;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.contexts.ProviderCtx;
import org.kei.android.phone.cellhistory.prefs.PreferencesGeolocation;
import org.kei.android.phone.cellhistory.prefs.PreferencesTimers;
import org.kei.android.phone.cellhistory.sensors.IAccelSensor;
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
OnItemSelectedListener, OnClickListener, IAccelSensor, LocationListener {
  /* UI */
  private Spinner           spiGeoProvider               = null;
  private TextView          txtGeoProvider               = null;
  private TextView          txtGeolocation               = null;
  private TextView          txtSpeed                     = null;
  private TextView          txtDistance                  = null;
  private TimeChartHelper   chart                        = null;
  /* context */
  private SharedPreferences prefs                        = null;
  private CellHistoryApp    app                          = null;
  /* colors */
  private int               color_red                    = Color.BLACK;
  private int               color_orange                 = Color.BLACK;
  private int               color_blue_dark              = Color.BLACK;
  private int               color_blue_dark_transparent  = Color.BLACK;
  private LocationManager   lm                           = null;
  private Location          lastLocation                 = null;
  private String            txtGpsDisabled               = "";
  private String            txtGpsDisabledOption         = "";
  private String            txtGpsOutOfService           = "";
  private String            txtGpsTemporarilyUnavailable = "";
  private String            txtGpsInvalid                = "";
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
    txtGpsInvalid = resources.getString(R.string.txtGpsInvalid);
    
    /* UI */
    spiGeoProvider = (Spinner) getView().findViewById(R.id.spiGeoProvider);
    txtGeoProvider = (TextView) getView().findViewById(R.id.txtGeoProvider);
    txtGeolocation = (TextView) getView().findViewById(R.id.txtGeolocation);
    txtSpeed = (TextView) getView().findViewById(R.id.txtSpeed);
    txtDistance = (TextView) getView().findViewById(R.id.txtDistance);
    txtDistance.setText(getResources().getString(R.string.txtGpsDisabled));
    txtDistance.setTextColor(color_red);
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
    app.getProviderTask().setBroadcastListener(this);
    chart = new TimeChartHelper();
    chart.setChartContainer((LinearLayout) getView().findViewById(R.id.graph));
    chart.setFrequency(Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_PROVIDER, 
              PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_PROVIDER)));
    chart.install(getActivity(), txtSpeed.getTextColors().getDefaultColor(),
        true);
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
    if (oldLoc.startsWith(ProviderCtx.LOC_NONE)
        || oldLoc.startsWith(ProviderCtx.LOC_RETRY))
      txtGeolocation.setTextColor(color_red);
    else if (oldLoc.startsWith(ProviderCtx.LOC_NOT_FOUND)
        || oldLoc.startsWith(ProviderCtx.LOC_BAD_REQUEST))
      txtGeolocation.setTextColor(color_orange);
    else {
      txtGeolocation.setTextColor(color_blue_dark);
    }
    txtGeolocation.setText(oldLoc);
    double speed = app.getGlobalTowerInfo().getSpeed();
    String spd = String
        .format("%.02f", speed) + " m/s\n";
    spd += String
        .format("%.02f", speed * 3.6) + " km/h\n";
    spd += String
        .format("%.02f", speed * 2.2369362920544) + " mph";
    txtSpeed.setText(spd);
    
    
    updateLocation();
    if (lastLocation != null) {
      final double dist = app.getGlobalTowerInfo().getDistance();
      txtDistance.setTextColor(default_color);
      if (dist > 1000)
        txtDistance.setText(String.format("%.02f", dist / 1000) + " km");
      else
        txtDistance.setText(String.format("%.02f", dist) + " m");
    } else if (!txtDistance.getText().toString()
        .equals(txtGpsDisabledOption))
      txtDistance.setTextColor(color_red);
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
    chart.clear();
    chart.setFrequency(Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_PROVIDER, 
              PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_PROVIDER)));
    if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE,
        PreferencesGeolocation.PREFS_DEFAULT_LOCATE)) {
      txtGeoProvider.setVisibility(View.GONE);
      spiGeoProvider.setVisibility(View.VISIBLE);
      if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_GPS,
          PreferencesGeolocation.PREFS_DEFAULT_GPS)) {
        txtDistance.setTextColor(color_red);
        if (lm == null) {
          lm = (LocationManager) getActivity().getSystemService(
              Context.LOCATION_SERVICE);
          if (lm != null)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_PROVIDER, 
                PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_PROVIDER)), 10f,
                this);
        }
        if(!prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_GPS_SPEED, PreferencesGeolocation.PREFS_DEFAULT_GPS_SPEED)) {
          app.getProviderTask().startSensor(
              Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_ACCEL, 
                  PreferencesTimers.PREFS_DEFAULT_TIMERS_ACCEL)));
        } else {
          app.getProviderTask().stopSensor();
        }
      } else {
        lastLocation = null;
        if (lm != null) {
          lm.removeUpdates(this);
          lm = null;
        }
        txtDistance.setText(txtGpsDisabledOption);
        txtDistance.setTextColor(color_orange);
        app.getProviderTask().startSensor(
            Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_ACCEL, 
                PreferencesTimers.PREFS_DEFAULT_TIMERS_ACCEL)));
      }
    } else {
      lastLocation = null;
      txtGeoProvider.setVisibility(View.VISIBLE);
      spiGeoProvider.setVisibility(View.GONE);
      if (lm != null) {
        lm.removeUpdates(this);
        lm = null;
      }
      txtDistance.setText(txtGpsDisabledOption);
      txtDistance.setTextColor(color_orange);
      app.getProviderTask().stopSensor();
    }
    accelUpdate(0.0f, 0.0);
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
  
  @Override
  public void accelUpdate(final float timestamp, final double velocity) {
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
    lastLocation = location;
    if (!app.getProviderCtx().isValid())
      resetDistance(txtGpsInvalid);
    updateLocation();
  }

  @Override
  public void onStatusChanged(final String provider, final int status,
      final Bundle extras) {
    switch (status) {
      case LocationProvider.OUT_OF_SERVICE:
        resetDistance(txtGpsOutOfService);
        break;
      case LocationProvider.TEMPORARILY_UNAVAILABLE:
        resetDistance(txtGpsTemporarilyUnavailable);
        break;
    }
  }

  @Override
  public void onProviderEnabled(final String provider) {
  }

  @Override
  public void onProviderDisabled(final String provider) {
    resetDistance(txtGpsDisabled);
    lastLocation = null;
  }

  private void resetDistance(final String txt) {
    app.getGlobalTowerInfo().lock();
    try {
      app.getGlobalTowerInfo().setDistance(0.0);
      if(prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_GPS_SPEED, PreferencesGeolocation.PREFS_DEFAULT_GPS_SPEED))
        app.getGlobalTowerInfo().setSpeed(0.0);
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
    txtDistance.setText(txt);
  }

  private void updateLocation() {
    if (lastLocation != null) {
      final Location loc1 = new Location("");
      app.getGlobalTowerInfo().lock();
      try {
        if(prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_GPS_SPEED, PreferencesGeolocation.PREFS_DEFAULT_GPS_SPEED)) {
          app.getGlobalTowerInfo().setSpeed(lastLocation.getSpeed());
          accelUpdate(.0f, app.getGlobalTowerInfo().getSpeed());
        }
        if (!Double.isNaN(app.getGlobalTowerInfo().getLatitude())
            && !Double.isNaN(app.getGlobalTowerInfo().getLongitude())) {
          loc1.setLatitude(app.getGlobalTowerInfo().getLatitude());
          loc1.setLongitude(app.getGlobalTowerInfo().getLongitude());
          app.getGlobalTowerInfo().setDistance(loc1.distanceTo(lastLocation));
        }
      } finally {
        app.getGlobalTowerInfo().unlock();
      }
    }
  }
}
