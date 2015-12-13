package org.kei.android.phone.cellhistory.prefs;


import org.kei.android.atk.view.EffectPreferenceActivity;
import org.kei.android.phone.cellhistory.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 *******************************************************************************
 * @file PreferencesGeolocationOpenCellID.java
 * @author Keidan
 * @date 04/12/2015
 * @par Project
 * CellHistory
 *
 * @par 
 * Copyright 2015 Keidan, all right reserved
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY.
 *
 * License summary : 
 *    You can modify and redistribute the sources code and binaries.
 *    You can send me the bug-fix
 *
 * Term of the license in in the file license.txt.
 *
 *******************************************************************************
 */
public class PreferencesGeolocationOpenCellID extends EffectPreferenceActivity implements OnSharedPreferenceChangeListener {
  public static final String   PREFS_KEY_API_KEY     = "geolocationOpenCellIDKey";
  public static final String   PREFS_KEY_GET_API_KEY = "geolocationOpenCellIDGet";
  public static final String   PREFS_DEFAULT_API_KEY = "";
  private MyPreferenceFragment prefFrag              = null;
  private SharedPreferences    prefs                 = null;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    prefFrag = new MyPreferenceFragment();
    getFragmentManager().beginTransaction().replace(android.R.id.content, prefFrag).commit();
    checkValues();
  }
  
  @Override
  protected boolean exitOnDoubleBack() {
    return false;
  }

  public void themeUpdate() {
    Preferences.performTheme(this);
  }

  private void updateSummaries() {
    Preference pref = (Preference)prefFrag.findPreference(PREFS_KEY_API_KEY);
    String summary = getResources().getString(R.string.pref_geolocation_key_opencellid_summary);
    summary += "\nKey: " + prefs.getString(PREFS_KEY_API_KEY, PREFS_DEFAULT_API_KEY);
    pref.setSummary(summary);
  }
  
  private void checkValues() {
    // addPreferencesFromResource is not done at the start
    getFragmentManager().executePendingTransactions();
    updateSummaries();
  }
  
  @Override
  public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
    if (key.equals(PREFS_KEY_API_KEY)) {
      updateSummaries();
    }
  }
  
  private static class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRetainInstance(true);
      addPreferencesFromResource(R.xml.preferences_geolocation_opencellid);
    }
  }
  
  @Override
  protected void onResume() {
    super.onResume();
    prefs.registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    prefs.unregisterOnSharedPreferenceChangeListener(this);
  }
}