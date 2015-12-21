package org.kei.android.phone.cellhistory.prefs;

import org.kei.android.atk.utils.Tools;
import org.kei.android.atk.utils.changelog.ChangeLog;
import org.kei.android.atk.utils.changelog.ChangeLogIds;
import org.kei.android.atk.utils.fx.Fx;
import org.kei.android.atk.view.EffectPreferenceActivity;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.activities.LogActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 *******************************************************************************
 * @file Preferences.java
 * @author Keidan
 * @date 24/11/2015
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
public class Preferences extends EffectPreferenceActivity implements Preference.OnPreferenceClickListener{
  public static final String   PREFS_KEY_SCREEN           = "prefScreen";
  public static final String   PREFS_KEY_CURRENT_TAB      = "prefCurrentTab";
  public static final String   PREFS_KEY_CHART_ENABLE     = "prefChartEnable";
  public static final String   PREFS_KEY_TIMERS           = "prefTimers";
  public static final String   PREFS_KEY_LOG              = "prefLogs";
  public static final String   PREFS_KEY_LOG_ENABLE       = "prefLogsEnable";
  public static final String   PREFS_KEY_UI               = "prefUI";
  public static final String   PREFS_KEY_GEOLOCATION      = "prefGeolocation";
  public static final String   PREFS_KEY_RECORDER         = "prefRecorder";
  public static final String   PREFS_KEY_VERSION          = "prefVersion";
  public static final String   PREFS_KEY_CHANGELOG        = "prefChangelog";
  public static final String   PREFS_KEY_CAT_LOGS         = "prefCatLogs";
  public static final String   PREFS_KEY_CAT_SETTINGS     = "prefCatSettings";
  public static final String   PREFS_KEY_ADVANCED         = "prefAdvanced";
  public static final boolean  PREFS_DEFAULT_CHART_ENABLE = true;
  public static final boolean  PREFS_DEFAULT_LOG_ENABLE   = false;
  public static final boolean  PREFS_DEFAULT_ADVANCED     = false;
  public static final int      PREFS_DEFAULT_CURRENT_TAB  = 0;
  private MyPreferenceFragment prefFrag                   = null;
  private ChangeLog            changeLog                  = null;
  private boolean              exit                       = false;
  private boolean              preferences                = false;
  private CellHistoryApp       app                        = null;
  private SharedPreferences    prefs                      = null;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    app = CellHistoryApp.getApp(this);
    prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    prefFrag = new MyPreferenceFragment();
    getFragmentManager().beginTransaction()
    .replace(android.R.id.content, prefFrag).commit();
    changeLog = new ChangeLog(
        new ChangeLogIds(
            R.raw.changelog, 
            R.string.changelog_ok_button, 
            R.string.background_color, 
            R.string.changelog_title, 
            R.string.changelog_full_title, 
            R.string.changelog_show_full), this);
    checkValues();
  }

  public void onResume() {
    super.onResume();
    if (!app.getRecorderCtx().isRunning())
      app.getNfyHelper().hide();
  }
  
  public void onBackPressed() {
    exit = true;
    super.onBackPressed();
  }
  
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
    performTheme(this);
  }
  
  public static void performTheme(final Activity a) {
    if (!Fx.switchThemeFromPref(a)) {
      final String theme = Tools.getPrefString(a.getBaseContext(),
          Fx.KEY_THEMES, Fx.default_theme);
      if (theme.equals(PreferencesUI.THEME_DARK_BLUE)) {
        Fx.switchTheme(a, R.style.themeDarkBlue, false);
      } else if (theme.equals(PreferencesUI.THEME_LIGHT_BLUE)) {
        Fx.switchTheme(a, R.style.themeLightBlue, false);
      }
    }
  }

  private void updateSummaries() {
  }

  @Override
  public boolean onPreferenceClick(final Preference preference) {
    if (preference.equals(prefFrag.findPreference(PREFS_KEY_UI))) {
      preferences = true;
      Tools.switchTo(Preferences.this, PreferencesUI.class);
    } else if (preference.equals(prefFrag.findPreference(PREFS_KEY_GEOLOCATION))) {
      preferences = true;
      Tools.switchTo(Preferences.this, PreferencesGeolocation.class);
    } else if (preference.equals(prefFrag.findPreference(PREFS_KEY_RECORDER))) {
      preferences = true;
      Tools.switchTo(Preferences.this, PreferencesRecorder.class);
    } else if (preference.equals(prefFrag.findPreference(PREFS_KEY_LOG_ENABLE))) {
      prefFrag.findPreference(PREFS_KEY_LOG).setEnabled(
          ((CheckBoxPreference) preference).isChecked());
    } else if (preference.equals(prefFrag.findPreference(PREFS_KEY_TIMERS))) {
      preferences = true;
      Tools.switchTo(Preferences.this, PreferencesTimers.class);
    } else if (preference.equals(prefFrag.findPreference(PREFS_KEY_LOG))) {
      preferences = true;
      Tools.switchTo(Preferences.this, LogActivity.class);
      return true;
    } else if (preference.equals(prefFrag.findPreference(PREFS_KEY_CHANGELOG))) {
      preferences = true;
      changeLog.getFullLogDialog().show();
    }
    
    return true;
  }
  
  private void checkValues() {
    // addPreferencesFromResource is not done at the start
    getFragmentManager().executePendingTransactions();
    updateSummaries();
    prefFrag.findPreference(PREFS_KEY_UI)
    .setOnPreferenceClickListener(this);
    prefFrag.findPreference(PREFS_KEY_GEOLOCATION)
    .setOnPreferenceClickListener(this);
    prefFrag.findPreference(PREFS_KEY_RECORDER).setOnPreferenceClickListener(this);

    Preference p = prefFrag.findPreference(PREFS_KEY_LOG_ENABLE);
    prefFrag.findPreference(PREFS_KEY_LOG).setEnabled(((CheckBoxPreference)p).isChecked());
    p.setOnPreferenceClickListener(this);
    prefFrag.findPreference(PREFS_KEY_TIMERS).setOnPreferenceClickListener(this);
    prefFrag.findPreference(PREFS_KEY_LOG).setOnPreferenceClickListener(this);
    
    /* author + versions */

    prefFrag.findPreference(PREFS_KEY_VERSION).setTitle(
        getResources().getString(R.string.app_name));
    try {
      prefFrag.findPreference(PREFS_KEY_VERSION).setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
    } catch (final Exception e) {
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
      prefFrag.findPreference(PREFS_KEY_VERSION).setSummary(e.getMessage());
    }
    prefFrag.findPreference(PREFS_KEY_CHANGELOG).setOnPreferenceClickListener(this);
    if(!prefs.getBoolean(PREFS_KEY_ADVANCED, PREFS_DEFAULT_ADVANCED)) {
      PreferenceScreen screen = (PreferenceScreen)prefFrag.findPreference(PREFS_KEY_SCREEN);
      Preference pref = prefFrag.findPreference(PREFS_KEY_CAT_LOGS);
      screen.removePreference(pref);
      PreferenceCategory pc = (PreferenceCategory)prefFrag.findPreference(PREFS_KEY_CAT_SETTINGS);
      pref = prefFrag.findPreference(PREFS_KEY_TIMERS);
      pc.removePreference(pref);
    }
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_advanced:
        Editor ed = prefs.edit();
        ed.putBoolean(PREFS_KEY_ADVANCED, !prefs.getBoolean(PREFS_KEY_ADVANCED, PREFS_DEFAULT_ADVANCED));
        ed.commit();
        Tools.switchTo(Preferences.this, Preferences.class);
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menu_preferences, menu);
      menu.findItem(R.id.action_advanced).setChecked(prefs.getBoolean(PREFS_KEY_ADVANCED, PREFS_DEFAULT_ADVANCED));
      return true;
  }
  
  private static class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRetainInstance(true);
      addPreferencesFromResource(R.xml.preferences);
    }
  }
}