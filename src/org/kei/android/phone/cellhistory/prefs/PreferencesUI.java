package org.kei.android.phone.cellhistory.prefs;


import org.kei.android.atk.utils.Tools;
import org.kei.android.atk.utils.fx.Fx;
import org.kei.android.atk.view.EffectPreferenceActivity;
import org.kei.android.phone.cellhistory.R;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 *******************************************************************************
 * @file PreferencesUI.java
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
public class PreferencesUI extends EffectPreferenceActivity {
  public static final String   TRANSITION_ZOOM                  = "org.kei.android.phone.cellhistory.prefs.TRANSITION_ZOOM";
  public static final String   TRANSITION_DEPTH                 = "org.kei.android.phone.cellhistory.prefs.TRANSITION_DEPTH";
  public static final String   THEME_LIGHT                      = "org.kei.android.phone.cellhistory.prefs.THEME_LIGHT";
  public static final String   THEME_DARK                       = "org.kei.android.phone.cellhistory.prefs.THEME_DARK";
  public static final String   PREFS_KEY_KEEP_SCREEN            = "uiKeepScreenOn";
  public static final String   PREFS_KEY_SLIDE_TRANSITION       = "uiSlideTransition";
  public static final boolean  PREFS_DEFAULT_KEEP_SCREEN        = true;
  public static final String   PREFS_DEFAULT_SLIDE_TRANSITION   = TRANSITION_ZOOM;
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
  
  private void checkValues() {
    // addPreferencesFromResource is not done at the start
    getFragmentManager().executePendingTransactions();

    final ListPreference themes = (ListPreference)prefFrag.findPreference(Fx.KEY_THEMES);
    themes.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(prefs.getString(Fx.KEY_THEMES, Fx.default_theme).compareTo(""+newValue) != 0) {
          Tools.restartApplication(PreferencesUI.this);
        }
        return true;
      }
    });
  }
  
  private static class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRetainInstance(true);
      addPreferencesFromResource(R.xml.preferences_ui);
    }
  }
}