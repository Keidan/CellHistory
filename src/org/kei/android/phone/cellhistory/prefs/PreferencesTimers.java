package org.kei.android.phone.cellhistory.prefs;

import org.kei.android.atk.view.EffectPreferenceActivity;
import org.kei.android.phone.cellhistory.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 *******************************************************************************
 * @file PreferencesTimers.java
 * @author Keidan
 * @date 13/12/2015
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
public class PreferencesTimers extends EffectPreferenceActivity implements
OnSharedPreferenceChangeListener {
  public static final String   PREFS_KEY_TIMERS_UI                = "timersUI";
  public static final String   PREFS_KEY_TIMERS_TASK_TOWER        = "timersTaskTower";
  public static final String   PREFS_KEY_TIMERS_TASK_PROVIDER     = "timersTaskProvider";

  public static final String   PREFS_DEFAULT_TIMERS_UI            = "1000";
  public static final String   PREFS_DEFAULT_TIMERS_TASK_TOWER    = "1000";
  public static final String   PREFS_DEFAULT_TIMERS_TASK_PROVIDER = "1000";
  private MyPreferenceFragment prefFrag                           = null;
  private SharedPreferences    prefs                              = null;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    prefFrag = new MyPreferenceFragment();
    getFragmentManager().beginTransaction()
    .replace(android.R.id.content, prefFrag).commit();
    checkValues();
  }

  @Override
  protected boolean exitOnDoubleBack() {
    return false;
  }

  @Override
  public void themeUpdate() {
    Preferences.performTheme(this);
  }

  private void updateSummaries() {
    String summary;
    EditTextPreference ep = (EditTextPreference) prefFrag
        .findPreference(PREFS_KEY_TIMERS_UI);
    summary = getResources().getString(R.string.pref_timers_ui_summary);
    summary += "\nTimer: "
        + prefs.getString(PREFS_KEY_TIMERS_UI, PREFS_DEFAULT_TIMERS_UI)
        + " ms.";
    ep.setSummary(summary);
    ep = (EditTextPreference) prefFrag
        .findPreference(PREFS_KEY_TIMERS_TASK_TOWER);
    summary = getResources().getString(R.string.pref_timers_task_tower_summary);
    summary += "\nTimer: "
        + prefs.getString(PREFS_KEY_TIMERS_TASK_TOWER,
            PREFS_DEFAULT_TIMERS_TASK_TOWER) + " ms.";
    ep.setSummary(summary);
    ep = (EditTextPreference) prefFrag
        .findPreference(PREFS_KEY_TIMERS_TASK_PROVIDER);
    summary = getResources().getString(
        R.string.pref_timers_task_provider_summary);
    summary += "\nTimer: "
        + prefs.getString(PREFS_KEY_TIMERS_TASK_PROVIDER,
            PREFS_DEFAULT_TIMERS_TASK_PROVIDER) + " ms.";
    ep.setSummary(summary);
  }

  private void checkValues() {
    // addPreferencesFromResource is not done at the start
    getFragmentManager().executePendingTransactions();
    updateSummaries();
  }

  @Override
  public void onSharedPreferenceChanged(
      final SharedPreferences sharedPreferences, final String key) {
    if (key.equals(PREFS_KEY_TIMERS_UI)
        || key.equals(PREFS_KEY_TIMERS_TASK_TOWER)
        || key.equals(PREFS_KEY_TIMERS_TASK_PROVIDER)) {
      updateSummaries();
    }
  }

  private static class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRetainInstance(true);
      addPreferencesFromResource(R.xml.preferences_timers);
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