package org.kei.android.phone.cellhistory.prefs;


import org.kei.android.atk.utils.Tools;
import org.kei.android.atk.view.EffectPreferenceActivity;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.towers.CellIdHelper;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 *******************************************************************************
 * @file PreferencesGeolocation.java
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
public class PreferencesGeolocation extends EffectPreferenceActivity implements OnSharedPreferenceChangeListener {
  public static final String   PREFS_KEY_GPS                    = "geolocationGPS";
  public static final String   PREFS_KEY_LOCATE                 = "geolocationLocate";
  public static final String   PREFS_KEY_OPENCELLID_PROVIDER    = "geolocationOpenCellIdProvider";
  public static final String   PREFS_KEY_LOCATION_TIMEOUT       = "geolocationLocateTimeout";
  public static final String   PREFS_KEY_CURRENT_PROVIDER       = "currentProvider";
  public static final boolean  PREFS_DEFAULT_LOCATE             = true;
  public static final boolean  PREFS_DEFAULT_GPS                = true;
  public static final int      PREFS_DEFAULT_LOCATION_TIMEOUT   = 30;
  public static final String   PREFS_DEFAULT_CURRENT_PROVIDER   = CellIdHelper.GOOGLE_HIDDENT_API;
  private MyPreferenceFragment prefFrag                         = null;
  private SharedPreferences    prefs                            = null;

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
    int t = PREFS_DEFAULT_LOCATION_TIMEOUT;
    try {
      t = Integer.parseInt(prefs.getString(PREFS_KEY_LOCATION_TIMEOUT, ""+PREFS_DEFAULT_LOCATION_TIMEOUT));
      if(t < 1) {
        t = PREFS_DEFAULT_LOCATION_TIMEOUT;
        final SharedPreferences.Editor edit = prefs.edit();
        edit.putString(PREFS_KEY_LOCATION_TIMEOUT, ""+t);
        edit.commit();
      }
    } catch(Exception e) {
      t = PREFS_DEFAULT_LOCATION_TIMEOUT;
      final SharedPreferences.Editor edit = prefs.edit();
      edit.putString(PREFS_KEY_LOCATION_TIMEOUT, ""+t);
      edit.commit();
    }
    EditTextPreference timeout = (EditTextPreference)prefFrag.findPreference(PREFS_KEY_LOCATION_TIMEOUT);
    String summary = getResources().getString(R.string.pref_geolocation_timeout_summary);
    summary += "\nTimeout: '" + t + "'";
    timeout.setSummary(summary);
    
    Preference povider = prefFrag.findPreference(PREFS_KEY_OPENCELLID_PROVIDER);
    Preference gps = prefFrag.findPreference(PREFS_KEY_GPS);
    boolean en = prefs.getBoolean(PREFS_KEY_LOCATE, PREFS_DEFAULT_LOCATE);
    povider.setEnabled(en);
    timeout.setEnabled(en);
    gps.setEnabled(en);
  }
  
  private void checkValues() {
    // addPreferencesFromResource is not done at the start
    getFragmentManager().executePendingTransactions();
    updateSummaries();
    final Preference povider = (Preference)prefFrag.findPreference(PREFS_KEY_OPENCELLID_PROVIDER);
    povider.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        Tools.switchTo(PreferencesGeolocation.this, PreferencesGeolocationOpenCellID.class);
        return true;
      }
    });
  }
  @Override
  public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
    if (key.equals(PREFS_KEY_LOCATE) || key.equals(PREFS_KEY_LOCATION_TIMEOUT) || key.equals(PREFS_KEY_GPS)) {
      updateSummaries();
    }
  }
  
  private static class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRetainInstance(true);
      addPreferencesFromResource(R.xml.preferences_geolocation);
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