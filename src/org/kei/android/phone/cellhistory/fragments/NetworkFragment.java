package org.kei.android.phone.cellhistory.fragments;

import java.util.Date;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.contexts.RecorderCtx;
import org.kei.android.phone.cellhistory.prefs.Preferences;
import org.kei.android.phone.cellhistory.prefs.PreferencesTimers;
import org.kei.android.phone.cellhistory.towers.MobileNetworkInfo;
import org.kei.android.phone.cellhistory.towers.TowerInfo;
import org.kei.android.phone.cellhistory.views.TimeChartHelper;

import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *******************************************************************************
 * @file NetworkFragment.java
 * @author Keidan
 * @date 21/12/2015
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
public class NetworkFragment extends Fragment implements UITaskFragment {
  
  private TextView          txtTxBytesSinceAppStart = null;
  private TextView          txtRxBytesSinceAppStart = null;
  private TextView          txtDataConnectivity     = null;
  private TextView          txtDataActivity         = null;
  private TextView          txtTheoreticalSpeed     = null;
  private TextView          txtIp4Address           = null;
  private TextView          txtIp6Address           = null;
  private LinearLayout      chartSeparator          = null;
  private TimeChartHelper   chart                   = null;
  private int               defaultColor            = 0;
  private int               redColor                = 0;
  private int               greenColor              = 0;
  private int               orangeColor             = 0;
  private Shader            gradientColor           = null;
  private SharedPreferences prefs                   = null;
  

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      ViewGroup rootView = (ViewGroup) inflater.inflate(
              R.layout.fragment_network, container, false);
      return rootView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
    chartSeparator = (LinearLayout) getView().findViewById(R.id.chartSeparator);
    txtTxBytesSinceAppStart = (TextView) getView().findViewById(R.id.txtTxBytesSinceAppStart);
    txtRxBytesSinceAppStart = (TextView) getView().findViewById(R.id.txtRxBytesSinceAppStart);
    txtDataConnectivity = (TextView) getView().findViewById(R.id.txtDataConnectivity);
    txtDataActivity = (TextView) getView().findViewById(R.id.txtDataActivity);
    txtTheoreticalSpeed = (TextView) getView().findViewById(R.id.txtTheoreticalSpeed);
    txtIp4Address = (TextView) getView().findViewById(R.id.txtIp4Address);
    txtIp6Address = (TextView) getView().findViewById(R.id.txtIp6Address);
    defaultColor = new TextView(getActivity()).getTextColors().getDefaultColor();
    redColor = getResources().getColor(R.color.red);
    greenColor = getResources().getColor(R.color.green);
    orangeColor = getResources().getColor(R.color.orange_dark);
    gradientColor = new LinearGradient(180, 0, 0, 0,
        new int[]{greenColor, redColor},
        new float[]{0, 1}, TileMode.CLAMP);
    
    chart = new TimeChartHelper();
    chart.setChartContainer((LinearLayout)getView().findViewById(R.id.graph));
    chart.setFrequency(Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_UI, 
              PreferencesTimers.PREFS_DEFAULT_TIMERS_UI)));
    chart.install(getActivity(), txtDataConnectivity.getTextColors().getDefaultColor(), false, 2);
    try {
      processUI(CellHistoryApp.getApp(getActivity()).getGlobalTowerInfo());
    } catch (Throwable e) {
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
    }
  }
  
  @Override
  public void processUI(TowerInfo ti) throws Throwable {
    if(txtTxBytesSinceAppStart == null) return;
    MobileNetworkInfo mni = ti.getMobileNetworkInfo();
    txtTxBytesSinceAppStart.setText(RecorderCtx.convertToHuman(mni.getTx()) + " (" + RecorderCtx.convertToHuman(mni.getTxSpeed()) + "/s)");
    txtRxBytesSinceAppStart.setText(RecorderCtx.convertToHuman(mni.getRx()) + " (" + RecorderCtx.convertToHuman(mni.getRxSpeed()) + "/s)");
    int n = mni.getDataConnectivity();
    if(n == MobileNetworkInfo.TYPE_MOBILE) {
      String s = getResources().getString(R.string.connectivity_mobile);
      s += " (" + mni.getType() + ")";
      txtDataConnectivity.setText(s);
    } else if(n == MobileNetworkInfo.TYPE_WIFI)
      txtDataConnectivity.setText(getResources().getString(R.string.connectivity_wifi));
    else
      txtDataConnectivity.setText(getResources().getString(R.string.connectivity_none));
    txtTheoreticalSpeed.setText(mni.getTheoreticalSpeed());
    txtIp4Address.setText(mni.getIp4Address());
    txtIp6Address.setText(mni.getIp6Address());
    if(mni.getDataActivity() == MobileNetworkInfo.DATA_ACTIVITY_IN) {
      txtDataActivity.setTextColor(redColor);
      txtDataActivity.getPaint().setShader(null);
    } else if(mni.getDataActivity() == MobileNetworkInfo.DATA_ACTIVITY_OUT) {
      txtDataActivity.setTextColor(greenColor);
      txtDataActivity.getPaint().setShader(null);
    } else if(mni.getDataActivity() == MobileNetworkInfo.DATA_ACTIVITY_INOUT) {
      txtDataActivity.getPaint().setShader(gradientColor);
    } else if(mni.getDataActivity() == MobileNetworkInfo.DATA_ACTIVITY_DORMANT) {
      txtDataActivity.setTextColor(orangeColor);
    } else {
      txtDataActivity.setTextColor(defaultColor);
      txtDataActivity.getPaint().setShader(null);
    } 
    txtDataActivity.setText(MobileNetworkInfo.getDataActivity(mni.getDataActivity()));
    
    if (chart.getVisibility() == View.VISIBLE) {
      chart.checkYAxisMax(Math.max(mni.getTxSpeed(), mni.getRxSpeed()));
      chart.addTimePoints(greenColor, redColor, new Date().getTime(), mni.getTxSpeed(), mni.getRxSpeed());
    }
  }
  
  @Override
  public void onResume() {
    super.onResume();
    chart.setFrequency(Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_UI, 
              PreferencesTimers.PREFS_DEFAULT_TIMERS_UI)));
    setChartVisible(prefs.getBoolean(Preferences.PREFS_KEY_CHART_ENABLE,
        Preferences.PREFS_DEFAULT_CHART_ENABLE));
  }
  
  private void setChartVisible(final boolean visible) {
    final int visibility = visible ? View.VISIBLE : View.GONE;
    if(chart == null) return;
    if(visible)chart.clear();
    if (chartSeparator.getVisibility() != visibility)
      chartSeparator.setVisibility(visibility);
    if (visible && chart.getVisibility() == View.GONE) {
      chart.setVisibility(View.VISIBLE);
    }
    else if (!visible && chart.getVisibility() == View.VISIBLE) {
      chart.setVisibility(View.GONE);
    }
    if(chart.getVisibility() == View.VISIBLE) {
      chart.checkYAxisMax(0.0);
      chart.addTimePoints(greenColor, redColor, new Date().getTime(), 0.0, 0.0);
    }
  }
}
