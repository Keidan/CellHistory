package org.kei.android.phone.cellhistory.prefs;

import org.kei.android.atk.utils.Tools;
import org.kei.android.atk.utils.fx.Fx;
import org.kei.android.atk.view.EffectPreferenceActivity;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.activities.LogActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

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
public class Preferences extends EffectPreferenceActivity {
  public static final String   PREFS_KEY_CURRENT_TAB      = "prefCurrentTab";
  public static final String   PREFS_KEY_CHART_ENABLE     = "prefChartEnable";
  public static final String   PREFS_KEY_LOG              = "prefLogs";
  public static final String   PREFS_KEY_UI               = "prefUI";
  public static final String   PREFS_KEY_GEOLOCATION      = "prefGeolocation";
  public static final String   PREFS_KEY_RECORDER         = "prefRecorder";
  public static final String   PREFS_KEY_TECHNICIAN       = "prefTechnician";
  public static final String   PREFS_KEY_VERSION          = "prefVersion";
  public static final boolean  PREFS_DEFAULT_CHART_ENABLE = true;
  public static final int      PREFS_DEFAULT_CURRENT_TAB  = 0;
  private MyPreferenceFragment prefFrag                   = null;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
    performTheme(this);
  }
  
  public static void performTheme(final Activity a) {
    if (!Fx.switchThemeFromPref(a)) {
      final String theme = Tools.getPrefString(a.getBaseContext(),
          Fx.KEY_THEMES, Fx.default_theme);
      if (theme.equals(PreferencesUI.THEME_DARK)) {
        Fx.switchTheme(a, R.style.themeDark, false);
      } else if (theme.equals(PreferencesUI.THEME_LIGHT)) {
        Fx.switchTheme(a, R.style.themeLight, false);
      }
    }
  }

  private void updateSummaries() {
  }
  
  private void checkValues() {
    // addPreferencesFromResource is not done at the start
    getFragmentManager().executePendingTransactions();
    updateSummaries();
    prefFrag.findPreference(PREFS_KEY_UI)
    .setOnPreferenceClickListener(
        new Preference.OnPreferenceClickListener() {
          @Override
          public boolean onPreferenceClick(final Preference preference) {
            Tools.switchTo(Preferences.this, PreferencesUI.class);
            return true;
          }
        });
    prefFrag.findPreference(PREFS_KEY_GEOLOCATION)
    .setOnPreferenceClickListener(
        new Preference.OnPreferenceClickListener() {
          @Override
          public boolean onPreferenceClick(final Preference preference) {
            Tools.switchTo(Preferences.this, PreferencesGeolocation.class);
            return true;
          }
        });
    prefFrag.findPreference(PREFS_KEY_RECORDER).setOnPreferenceClickListener(
        new Preference.OnPreferenceClickListener() {
          @Override
          public boolean onPreferenceClick(final Preference preference) {
            Tools.switchTo(Preferences.this, PreferencesRecorder.class);
            return true;
          }
        });
    prefFrag.findPreference(PREFS_KEY_TECHNICIAN).setOnPreferenceClickListener(
        new Preference.OnPreferenceClickListener() {
          @Override
          public boolean onPreferenceClick(final Preference preference) {
            final Intent intent = new Intent(
                "android.provider.Telephony.SECRET_CODE", Uri
                .parse("android_secret_code://4636"));
            sendBroadcast(intent);
            return true;
          }
        });
    prefFrag.findPreference(PREFS_KEY_LOG).setOnPreferenceClickListener(
        new Preference.OnPreferenceClickListener() {
          @Override
          public boolean onPreferenceClick(final Preference preference) {
            Tools.switchTo(Preferences.this, LogActivity.class);
            return true;
          }
        });
    
    /* author + versions */

    prefFrag.findPreference(PREFS_KEY_VERSION).setTitle(
        getResources().getString(R.string.app_name));
    try {
      final PackageInfo pInfo = getPackageManager().getPackageInfo(
          getPackageName(), 0);
      prefFrag.findPreference(PREFS_KEY_VERSION).setSummary(pInfo.versionName);
    } catch (final Exception e) {
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
      prefFrag.findPreference(PREFS_KEY_VERSION).setSummary(e.getMessage());
    }
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