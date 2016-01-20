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
  private MyPreferenceFragment prefFrag    = null;
  private boolean              exit        = false;
  private boolean              preferences = false;
  private CellHistoryApp       app         = null;

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