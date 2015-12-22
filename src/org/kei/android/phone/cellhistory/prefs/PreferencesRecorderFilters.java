package org.kei.android.phone.cellhistory.prefs;

import org.kei.android.atk.view.EffectPreferenceActivity;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 *******************************************************************************
 * @file PreferencesRecorderFilters.java
 * @author Keidan
 * @date 18/12/2015
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
public class PreferencesRecorderFilters extends EffectPreferenceActivity {
  public static final String   PREFS_RECORDER_FILTERS_KEY_OPERATOR            = "recorderFilterOperator";
  public static final String   PREFS_RECORDER_FILTERS_KEY_MCC                 = "recorderFilterMCC";
  public static final String   PREFS_RECORDER_FILTERS_KEY_MNC                 = "recorderFilterMNC";
  public static final String   PREFS_RECORDER_FILTERS_KEY_CELL_ID             = "recorderFilterCellID";
  public static final String   PREFS_RECORDER_FILTERS_KEY_LAC                 = "recorderFilterLAC";
  public static final String   PREFS_RECORDER_FILTERS_KEY_PSC                 = "recorderFilterPSC";
  public static final String   PREFS_RECORDER_FILTERS_KEY_TYPE                = "recorderFilterType";
  public static final String   PREFS_RECORDER_FILTERS_KEY_NETOWRK_ID          = "recorderFilterNetworkId";
  public static final String   PREFS_RECORDER_FILTERS_KEY_ASU                 = "recorderFilterASU";
  public static final String   PREFS_RECORDER_FILTERS_KEY_LEVEL               = "recorderFilterLevel";
  public static final String   PREFS_RECORDER_FILTERS_KEY_SIGNAL_STRENGTH     = "recorderFilterSignalStrength";
  public static final String   PREFS_RECORDER_FILTERS_KEY_NEIGHBORING         = "recorderFilterNeighboring";
  public static final String   PREFS_RECORDER_FILTERS_KEY_PROVIDER            = "recorderFilterProvider";
  public static final String   PREFS_RECORDER_FILTERS_KEY_DISTANCE            = "recorderFilterDistance";
  public static final String   PREFS_RECORDER_FILTERS_KEY_SATELLITES          = "recorderFilterSatellites";
  public static final String   PREFS_RECORDER_FILTERS_KEY_SPEED               = "recorderFilterSpeed";
  public static final String   PREFS_RECORDER_FILTERS_KEY_DATA_TX_SPEED       = "recorderFilterDataTxSpeed";
  public static final String   PREFS_RECORDER_FILTERS_KEY_DATA_RX_SPEED       = "recorderFilterDataRxSpeed";
  public static final String   PREFS_RECORDER_FILTERS_KEY_DATA_DIRECTION      = "recorderFilterDataDirection";
  public static final String   PREFS_RECORDER_FILTERS_KEY_IPV4                = "recorderFilterIPv4";
  public static final String   PREFS_RECORDER_FILTERS_KEY_IPV6                = "recorderFilterIPv6";
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_OPERATOR        = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_MCC             = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_MNC             = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_CELL_ID         = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_LAC             = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_PSC             = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_TYPE            = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_NETWORK_ID      = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_ASU             = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_LEVEL           = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_SIGNAL_STRENGTH = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_NEIGHBORING     = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_PROVIDER        = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_DISTANCE        = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_SATELLITES      = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_SPEED           = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_DATA_TX_SPEED   = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_DATA_RX_SPEED   = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_DATA_DIRECTION  = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_IPV4            = true;
  public static final boolean  PREFS_RECORDER_FILTERS_DEFAULT_IPV6            = true;
  private MyPreferenceFragment prefFrag                                       = null;
  private boolean              exit                                           = false;
  private boolean              preferences                                    = false;
  private CellHistoryApp       app                                            = null;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    app = CellHistoryApp.getApp(this);
    prefFrag = new MyPreferenceFragment();
    getFragmentManager().beginTransaction()
        .replace(android.R.id.content, prefFrag).commit();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (!app.getRecorderCtx().isRunning())
      app.getNfyHelper().hide();
  }

  @Override
  public void onBackPressed() {
    exit = true;
    super.onBackPressed();
  }

  @Override
  public void onPause() {
    super.onPause();
    if (!app.getRecorderCtx().isRunning() && !exit && !preferences)
      app.notificationShow();
    preferences = false;
  }

  @Override
  protected boolean exitOnDoubleBack() {
    return false;
  }

  @Override
  public void themeUpdate() {
    Preferences.performTheme(this);
  }

  private static class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRetainInstance(true);
      addPreferencesFromResource(R.xml.preferences_recorder_filters);
    }
  }
}