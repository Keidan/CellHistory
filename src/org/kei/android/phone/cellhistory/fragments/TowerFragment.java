package org.kei.android.phone.cellhistory.fragments;

import org.kei.android.atk.utils.fx.Color;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.prefs.Preferences;
import org.kei.android.phone.cellhistory.prefs.PreferencesTimers;
import org.kei.android.phone.cellhistory.towers.TowerInfo;
import org.kei.android.phone.cellhistory.views.TimeChartHelper;

import android.content.SharedPreferences;
import android.content.res.Resources;
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
 * @file TowerFragment.java
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
public class TowerFragment extends Fragment implements UITaskFragment {
  /* UI */
  private LinearLayout      chartSeparator       = null;
  private TextView          txtOperator          = null;
  private TextView          txtMCC               = null;
  private TextView          txtMNC               = null;
  private TextView          txtCellId            = null;
  private TextView          txtLAC               = null;
  private TextView          txtPSC               = null;
  private TextView          txtASU               = null;
  private TextView          txtLVL               = null;
  private TextView          txtSS                = null;
  private TextView          txtType              = null;
  private TextView          txtNetwork           = null;
  private TimeChartHelper   chart                = null;
  /* colors */
  private int               color_poor           = Color.BLACK;
  private int               color_moderate       = Color.BLACK;
  private int               color_good           = Color.BLACK;
  private int               color_great          = Color.BLACK;
  private int               color_poor_alpha     = Color.BLACK;
  private int               color_moderate_alpha = Color.BLACK;
  private int               color_good_alpha     = Color.BLACK;
  private int               color_great_alpha    = Color.BLACK;
  private int               color_red            = Color.BLACK;
  private SharedPreferences prefs                = null;

  @Override
  public View onCreateView(final LayoutInflater inflater,
      final ViewGroup container, final Bundle savedInstanceState) {
    final ViewGroup rootView = (ViewGroup) inflater.inflate(
        R.layout.fragment_tower, container, false);
    return rootView;
  }
  
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
    /* color */
    Resources resources = getResources();
    color_red = resources.getColor(Color.RED);
    color_poor = color_red;
    color_moderate = resources.getColor(Color.ORANGE_DARK);
    color_good = resources.getColor(Color.ORANGE_LIGHT);
    color_great = resources.getColor(Color.GREEN);
    color_poor_alpha = resources.getColor(Color.RED_TRANSPARENT);
    color_moderate_alpha = resources.getColor(Color.ORANGE_DARK_TRANSPARENT);
    color_good_alpha = resources.getColor(Color.ORANGE_LIGHT_TRANSPARENT);
    color_great_alpha = resources.getColor(Color.GREEN_TRANSPARENT);
    
    /* UI */
    chartSeparator = (LinearLayout) getView().findViewById(R.id.chartSeparator);
    txtOperator = (TextView) getView().findViewById(R.id.txtOperator);
    txtMCC = (TextView) getView().findViewById(R.id.txtMCC);
    txtMNC = (TextView) getView().findViewById(R.id.txtMNC);
    txtCellId = (TextView) getView().findViewById(R.id.txtCellId);
    txtLAC = (TextView) getView().findViewById(R.id.txtLAC);
    txtPSC = (TextView) getView().findViewById(R.id.txtPSC);
    txtSS = (TextView) getView().findViewById(R.id.txtSS);
    txtType = (TextView) getView().findViewById(R.id.txtType);
    txtNetwork = (TextView) getView().findViewById(R.id.txtNetwork);
    txtASU = (TextView) getView().findViewById(R.id.txtASU);
    txtLVL = (TextView) getView().findViewById(R.id.txtLVL);
    
    chart = new TimeChartHelper();
    chart.setChartContainer((LinearLayout)getView().findViewById(R.id.graph));
    chart.setFrequency(Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_PROVIDER, 
              PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_PROVIDER)));
    chart.install(getActivity(), txtOperator.getTextColors().getDefaultColor(), true);
    try {
      processUI(CellHistoryApp.getApp(getActivity()).getGlobalTowerInfo());
    } catch (Throwable e) {
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
    }
  }
  

  @Override
  public void processUI(final TowerInfo ti) throws Throwable {
    int percent = ti.getSignalStrengthPercent();
    if(txtOperator == null) return;
    txtOperator.setText(ti.getOperator());
    txtMCC.setText(String.valueOf(ti.getMCC()));
    txtMNC.setText(String.format("%02d", ti.getMNC()));
    txtCellId.setText(String.valueOf(ti.getCellId()));
    txtLAC.setText(String.valueOf(ti.getLac()));
    txtPSC.setText(String.valueOf(ti.getPsc()));
    txtType.setText(ti.getType());
    txtNetwork.setText(ti.getNetworkName() + " (" + ti.getNetwork() + ")");
    txtLVL.setText(ti.getLvl()
        + " ("
        + TowerInfo.LEVEL_NAMES[ti.getLvl() < TowerInfo.LEVEL_NAMES.length ? ti
            .getLvl() : 0] + ")");
    txtSS.setText(ti.getSignalStrength() + " dBm (" + String.format("%02d", percent) + "%)");
    txtASU.setText(String.format("%02d", ti.getAsu()));
    int color = getColor(percent, false);
    if (color == -1)
      color = txtLVL.getTextColors().getDefaultColor();
    txtLVL.setTextColor(color);
    txtSS.setTextColor(color);
    txtASU.setTextColor(color);
    if (chart.getVisibility() == View.VISIBLE) {
      percent = ti.getSignalStrengthPercent();
      final long timestamp = ti.getTimestamp();
      // percent = new Random().nextInt(100);
      int color1 = getColor(percent, false);
      if (color1 == -1)
        color1 = color_poor;
      int color2 = getColor(percent, true);
      if (color2 == -1)
        color2 = color_poor_alpha;
      chart.addTimePoint(color1, color2, timestamp, percent);
    }
  }
  
  @Override
  public void onResume() {
    super.onResume();
    chart.setFrequency(Integer.parseInt(prefs.getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_PROVIDER, 
              PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_PROVIDER)));
    setChartVisible(prefs.getBoolean(Preferences.PREFS_KEY_CHART_ENABLE,
        Preferences.PREFS_DEFAULT_CHART_ENABLE));
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
  
  public int getColor(final int ssp, final boolean alpha) {
    if (ssp <= 25) { // poor
      return alpha ? color_poor_alpha : color_poor;
    } else if (ssp <= 50) { // moderate
      return alpha ? color_moderate_alpha : color_moderate;
    } else if (ssp <= 75) { // good
      return alpha ? color_good_alpha : color_good;
    } else { // great
      return alpha ? color_great_alpha : color_great;
    }
  }
}
