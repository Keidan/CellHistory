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
import org.kei.android.phone.cellhistory.services.ProviderService;
import org.kei.android.phone.cellhistory.services.tasks.GpsServiceTask;
import org.kei.android.phone.cellhistory.towers.AreaInfo;
import org.kei.android.phone.cellhistory.towers.CellIdHelper;
import org.kei.android.phone.cellhistory.towers.TowerInfo;
import org.kei.android.phone.cellhistory.views.TimeChartHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
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
 * @file GeolocationFragment.java
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
public class GeolocationFragment extends Fragment implements UITaskFragment,
OnItemSelectedListener, OnClickListener {
  /* UI */
  private Spinner           spiGeoProvider               = null;
  private TextView          txtGeoProvider               = null;
  private TextView          txtGeolocation               = null;
  private TextView          txtSatellites                = null;
  private TextView          txtSpeedMS                   = null;
  private TextView          txtSpeedKMH                  = null;
  private TextView          txtSpeedMPH                  = null;
  private RadioButton       rbSpeedMS                    = null;
  private RadioButton       rbSpeedKMH                   = null;
  private RadioButton       rbSpeedMPH                   = null;
  private TextView          txtDistance                  = null;
  private TextView          txtSpeedError                = null;
  private TextView          txtDistanceError             = null;
  private TextView          lblUnitM                     = null;
  private TextView          lblUnitMS                    = null;
  private TextView          lblUnitKMH                   = null;
  private TextView          lblUnitMPH                   = null;
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
  private String            unit_m                       = "";
  private String            unit_km                      = "";
  private String            txtGpsDisabled               = "";
  private String            txtGpsWaitSatellites         = "";
  private String            txtGpsDisabledOption         = "";
  private String            txtGpsOutOfService           = "";
  private String            txtGpsTemporarilyUnavailable = "";
  private int               default_color                = android.graphics.Color.TRANSPARENT;
  private boolean           connected                    = false;
  private boolean           enabled                      = false;
  private boolean           registered                   = false;

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
    color_blue_dark_transparent = resources.getColor(Color.BLUE_DARK_TRANSPARENT);
    default_color = new TextView(getActivity()).getTextColors().getDefaultColor();
    /* texts */
    txtGpsDisabled = resources.getString(R.string.txtGpsDisabled);
    txtGpsWaitSatellites = resources.getString(R.string.txtGpsWaitSatellites);
    txtGpsDisabledOption = resources
        .getString(R.string.txtGpsDisabledOption);
    txtGpsOutOfService = resources
        .getString(R.string.txtGpsOutOfService);
    txtGpsTemporarilyUnavailable = resources
        .getString(R.string.txtGpsTemporarilyUnavailable);
    unit_m = resources.getString(R.string.unit_m);
    unit_km = resources.getString(R.string.unit_km);
    
    /* UI */
    chartSeparator = (LinearLayout) getView().findViewById(R.id.chartSeparator);
    spiGeoProvider = (Spinner) getView().findViewById(R.id.spiGeoProvider);
    txtGeoProvider = (TextView) getView().findViewById(R.id.txtGeoProvider);
    txtGeolocation = (TextView) getView().findViewById(R.id.txtGeolocation);

    txtSatellites = (TextView) getView().findViewById(R.id.txtSatellites);
    txtSpeedMS = (TextView) getView().findViewById(R.id.txtSpeedMS);
    txtSpeedKMH = (TextView) getView().findViewById(R.id.txtSpeedKMH);
    txtSpeedMPH = (TextView) getView().findViewById(R.id.txtSpeedMPH);
    rbSpeedMS = (RadioButton) getView().findViewById(R.id.rbSpeedMS);
    rbSpeedKMH = (RadioButton) getView().findViewById(R.id.rbSpeedKMH);
    rbSpeedMPH = (RadioButton) getView().findViewById(R.id.rbSpeedMPH);
    lblUnitM = (TextView) getView().findViewById(R.id.lblUnitM);
    lblUnitMS = (TextView) getView().findViewById(R.id.lblUnitMS);
    lblUnitKMH = (TextView) getView().findViewById(R.id.lblUnitKMH);
    lblUnitMPH = (TextView) getView().findViewById(R.id.lblUnitMPH);
    txtDistance = (TextView) getView().findViewById(R.id.txtDistance);
    txtSpeedError = (TextView) getView().findViewById(R.id.txtSpeedError);
    txtDistanceError = (TextView) getView().findViewById(R.id.txtDistanceError);
    txtSpeedError.setText(txtGpsDisabled);
    txtSpeedError.setTextColor(color_red);
    txtDistanceError.setText(txtGpsDisabled);
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
    chart.setFrequency(Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_UI, 
              PreferencesTimers.PREFS_DEFAULT_TIMERS_UI)));
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
    if(!isAdded()) return;
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
    txtSpeedMS.setText(String.format("%.02f", s_ms));
    txtSpeedKMH.setText(String.format("%.02f", s_kmh));
    txtSpeedMPH.setText(String.format("%.02f", speed * 2.2369362920544));
      
    double dist = app.getGlobalTowerInfo().getDistance();
    if (dist > 1000) {
      lblUnitM.setText(unit_km);
      txtDistance.setText(String.format("%.02f", dist / 1000));
    }
    else {
      lblUnitM.setText(unit_m);
      txtDistance.setText(String.format("%.02f", dist));
    }
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
    txtSatellites.setText("" + app.getGlobalTowerInfo().getSatellites());
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
      getActivity().startService(new Intent(getActivity(), ProviderService.class));
    }
  }

  @Override
  public void onNothingSelected(final AdapterView<?> parent) {
  }

  @Override
  public void onResume() {
    super.onResume();
    app.getProviderCtx().clear();
    txtSpeedMS.setTextColor(color_red);
    txtSpeedKMH.setTextColor(color_red);
    txtSpeedMPH.setTextColor(color_red);
    txtDistance.setTextColor(color_red);
    chart.setFrequency(Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_UI, 
              PreferencesTimers.PREFS_DEFAULT_TIMERS_UI)));
    setChartVisible(prefs.getBoolean(Preferences.PREFS_KEY_CHART_ENABLE,
        Preferences.PREFS_DEFAULT_CHART_ENABLE));

    if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE,
        PreferencesGeolocation.PREFS_DEFAULT_LOCATE)) {
      txtGeoProvider.setVisibility(View.GONE);
      spiGeoProvider.setVisibility(View.VISIBLE);
      if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_GPS,
          PreferencesGeolocation.PREFS_DEFAULT_GPS)) {
        registered = true;
        getActivity().registerReceiver(receiver, new IntentFilter(GpsServiceTask.NOTIFICATION));
        if(connected && enabled)
          setGpsVisibility(true);
        else
          setGpsVisibility(false);
      } else {
        resetGpsInfo(txtGpsDisabledOption, color_orange);
      }
    } else {
      txtGeoProvider.setVisibility(View.VISIBLE);
      spiGeoProvider.setVisibility(View.GONE);
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
      if(chart.getVisibility() == View.VISIBLE) chart.clear();
      CellHistoryApp.addLog(getActivity(), "Select MS display");
      Editor e = prefs.edit();
      e.putInt(PreferencesGeolocation.PREFS_KEY_CURRENT_SPEED, PreferencesGeolocation.PREFS_SPEED_MS);
      e.commit();
      rbSpeedKMH.setChecked(false);
      rbSpeedMPH.setChecked(false);
    } else if(v.equals(rbSpeedKMH)) {
      if(chart.getVisibility() == View.VISIBLE) chart.clear();
      CellHistoryApp.addLog(getActivity(), "Select KMH display");
      Editor e = prefs.edit();
      e.putInt(PreferencesGeolocation.PREFS_KEY_CURRENT_SPEED, PreferencesGeolocation.PREFS_SPEED_KMH);
      e.commit();
      rbSpeedMS.setChecked(false);
      rbSpeedMPH.setChecked(false);
    } else if(v.equals(rbSpeedMPH)) {
      if(chart.getVisibility() == View.VISIBLE) chart.clear();
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
  
  private BroadcastReceiver receiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
      Bundle bundle = intent.getExtras();
      if (bundle != null) {
        int event = bundle.getInt(GpsServiceTask.EVENT);
        Log.d(getClass().getSimpleName(), "Event: " + event);
        if (event == GpsServiceTask.EVENT_CONNECTED) {
          connected = true;
        } else if (event == GpsServiceTask.EVENT_DISABLED) {
          connected = false;
          enabled = false;
          resetGpsInfo(txtGpsDisabled, color_red);
        } else if (event == GpsServiceTask.EVENT_ENABLED) {
          enabled = true;
          txtSpeedMS.setTextColor(color_red);
          txtSpeedKMH.setTextColor(color_red);
          txtSpeedMPH.setTextColor(color_red);
          txtDistance.setTextColor(color_red);
          setGpsVisibility(true);
        } else if (event == GpsServiceTask.EVENT_OUT_OF_SERVICE) {
          resetGpsInfo(txtGpsOutOfService, color_red);
        } else if (event == GpsServiceTask.EVENT_TEMPORARILY_UNAVAILABLE) {
          resetGpsInfo(txtGpsTemporarilyUnavailable, color_red);
        } else if (event == GpsServiceTask.EVENT_UPDATE) {
          txtSpeedMS.setTextColor(default_color);
          txtSpeedKMH.setTextColor(default_color);
          txtSpeedMPH.setTextColor(default_color);
          txtDistance.setTextColor(default_color);
          setGpsVisibility(true);
        } else if (event == GpsServiceTask.EVENT_WAIT_FOR_SATELLITES) {
          resetGpsInfo(txtGpsWaitSatellites, color_red);
        }
      }
    }
  };
  
  private void resetGpsInfo(final String txt, int color) {
    app.getGlobalTowerInfo().lock();
    try {
      app.getGlobalTowerInfo().setDistance(0.0);
      app.getGlobalTowerInfo().setSpeed(0.0);
      for(AreaInfo ai : app.getGlobalTowerInfo().getAreas()) {
        ai.reset();
      }
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
    txtDistanceError.setText(txt);
    txtDistanceError.setTextColor(color);
    txtSpeedError.setText(txt);
    txtSpeedError.setTextColor(color);
    txtSpeedMS.setTextColor(color_red);
    txtSpeedKMH.setTextColor(color_red);
    txtSpeedMPH.setTextColor(color_red);
    txtDistance.setTextColor(color_red);
    setGpsVisibility(false);
  }
  
  private void setGpsVisibility(boolean visible) {
    int v = visible ? View.VISIBLE : View.GONE;
    int v2 = visible ? View.GONE : View.VISIBLE;
    if (txtDistance.getVisibility() != v) txtDistance.setVisibility(v);
    if (txtSpeedMS.getVisibility() != v) txtSpeedMS.setVisibility(v);
    if (txtSpeedKMH.getVisibility() != v) txtSpeedKMH.setVisibility(v);
    if (txtSpeedMPH.getVisibility() != v) txtSpeedMPH.setVisibility(v);
    if (lblUnitM.getVisibility() != v) lblUnitM.setVisibility(v);
    if (lblUnitMS.getVisibility() != v) lblUnitMS.setVisibility(v);
    if (lblUnitKMH.getVisibility() != v) lblUnitKMH.setVisibility(v);
    if (lblUnitMPH.getVisibility() != v) lblUnitMPH.setVisibility(v);
    if (chart.getVisibility() == View.VISIBLE) {
      if (rbSpeedMS.getVisibility() != v) rbSpeedMS.setVisibility(v);
      if (rbSpeedKMH.getVisibility() != v) rbSpeedKMH.setVisibility(v);
      if (rbSpeedMPH.getVisibility() != v) rbSpeedMPH.setVisibility(v);
    }
    if (txtDistanceError.getVisibility() != v2) txtDistanceError.setVisibility(v2);
    if (txtSpeedError.getVisibility() != v2) txtSpeedError.setVisibility(v2);
   }

}
