package org.kei.android.phone.cellhistory.prefs;

import java.util.HashMap;
import java.util.Map;

import org.kei.android.atk.utils.Tools;
import org.kei.android.atk.view.EffectPreferenceActivity;
import org.kei.android.atk.view.chooser.FileChooser;
import org.kei.android.atk.view.chooser.FileChooserActivity;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.contexts.RecorderCtx;
import org.kei.android.phone.cellhistory.towers.NeighboringInfo;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 *******************************************************************************
 * @file PreferencesRecorder.java
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
public class PreferencesRecorder extends EffectPreferenceActivity implements OnSharedPreferenceChangeListener {
  public static final String   PREFS_KEY_INDENTATION         = "recorderIndentation";
  public static final String   PREFS_KEY_FORMATS             = "recorderFormats";
  public static final String   PREFS_KEY_SAVE_PATH           = "recorderSavePath";
  public static final String   PREFS_KEY_FLUSH               = "recorderFlush";
  public static final String   PREFS_KEY_SEP                 = "recorderSep";
  public static final String   PREFS_KEY_NEIGHBORING_SEP     = "recorderNeighboringSep";
  public static final String   PREFS_KEY_DEL_PREV_FILE       = "recorderDeletePrevFile";
  public static final String   PREFS_KEY_DETECT_CHANGE       = "recorderDetectChange";
  public static final String   PREFS_KEY_DETECT_CHANGE_FILTER= "recorderDetectChangeFilter";
  public static final String   PREFS_DEFAULT_FLUSH           = "25";
  public static final String   PREFS_DEFAULT_SEP             = TowerInfo.DEFAULT_TOSTRING_SEP;
  public static final String   PREFS_DEFAULT_NEIGHBORING_SEP = NeighboringInfo.DEFAULT_TOSTRING_SEP;
  public static final String   PREFS_DEFAULT_SAVE_PATH       = Environment
                                                                 .getExternalStorageDirectory()
                                                                 .getAbsolutePath();
  public static final boolean  PREFS_DEFAULT_SAVE            = true;
  public static final boolean  PREFS_DEFAULT_DEL_PREV_FILE   = true;
  public static final boolean  PREFS_DEFAULT_DETECT_CHANGE   = false;
  public static final boolean  PREFS_DEFAULT_INDENTATION     = true;
  public static final String   PREFS_DEFAULT_FORMATS         = RecorderCtx.FORMAT_CSV;
  private MyPreferenceFragment prefFrag                      = null;
  private SharedPreferences    prefs                         = null;
  private boolean              exit                          = false;
  private boolean              preferences                   = false;
  private CellHistoryApp       app                           = null;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    app = CellHistoryApp.getApp(this);
    prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    prefFrag = new MyPreferenceFragment();
    getFragmentManager().beginTransaction().replace(android.R.id.content, prefFrag).commit();
    checkValues();
  }

  public void onResume() {
    prefs.registerOnSharedPreferenceChangeListener(this);
    super.onResume();
    if (!app.getRecorderCtx().isRunning())
      app.getNfyHelper().hide();
  }
  
  public void onBackPressed() {
    exit = true;
    super.onBackPressed();
  }
  
  public void onPause() {
    prefs.unregisterOnSharedPreferenceChangeListener(this);
    super.onPause();
    if (!app.getRecorderCtx().isRunning() && !exit && !preferences)
      app.notificationShow();
    preferences = false;
  }
  
  @Override
  protected boolean exitOnDoubleBack() {
    return false;
  }
  
  public void themeUpdate() {
    Preferences.performTheme(this);
  }
  
  private void updateSummaries() {
    Preference pref = (Preference)prefFrag.findPreference(PREFS_KEY_SAVE_PATH);
    String summary = getResources().getString(R.string.pref_save_path_summary);
    summary += "\nDir: " + prefs.getString(PREFS_KEY_SAVE_PATH, PREFS_DEFAULT_SAVE_PATH);
    pref.setSummary(summary);
    EditTextPreference flush = (EditTextPreference)prefFrag.findPreference(PREFS_KEY_FLUSH);
    summary = getResources().getString(R.string.pref_flush_summary);
    summary += "\nFlush: " + prefs.getString(PREFS_KEY_FLUSH, PREFS_DEFAULT_FLUSH);
    flush.setSummary(summary);
    EditTextPreference sep1 = (EditTextPreference)prefFrag.findPreference(PREFS_KEY_SEP);
    summary = getResources().getString(R.string.pref_sep_summary);
    summary += "\nSeparator: '" + prefs.getString(PREFS_KEY_SEP, PREFS_DEFAULT_SEP) + "'";
    sep1.setSummary(summary);
    EditTextPreference sep2 = (EditTextPreference)prefFrag.findPreference(PREFS_KEY_NEIGHBORING_SEP);
    summary = getResources().getString(R.string.pref_neighboring_sep_summary);
    summary += "\nSeparator: '" + prefs.getString(PREFS_KEY_NEIGHBORING_SEP, PREFS_DEFAULT_NEIGHBORING_SEP) + "'";
    sep2.setSummary(summary);
    pref = (Preference)prefFrag.findPreference(PREFS_KEY_INDENTATION);
    boolean en = prefs.getString(PREFS_KEY_FORMATS, PREFS_DEFAULT_FORMATS).equals(RecorderCtx.FORMAT_CSV);
    sep1.setEnabled(en);
    sep2.setEnabled(en);
    pref.setEnabled(!en);
    
    pref = (Preference)prefFrag.findPreference(PREFS_KEY_FORMATS);
    summary = getResources().getString(R.string.pref_formats_summary);
    summary += "\nFormat: " + prefs.getString(PREFS_KEY_FORMATS, PREFS_DEFAULT_FORMATS);
    pref.setSummary(summary);
    
    final CheckBoxPreference dc = (CheckBoxPreference)prefFrag.findPreference(PREFS_KEY_DETECT_CHANGE);
    pref = (Preference)prefFrag.findPreference(PREFS_KEY_DETECT_CHANGE_FILTER);
    pref.setEnabled(dc.isChecked());
  }
  
  private void checkValues() {
    // addPreferencesFromResource is not done at the start
    getFragmentManager().executePendingTransactions();
    Preference pref = (Preference)prefFrag.findPreference(PREFS_KEY_SAVE_PATH);
    updateSummaries();
    pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {
        preferences = true;
        Map<String, String> extra = new HashMap<String, String>();
        extra.put(FileChooser.FILECHOOSER_TYPE_KEY, "" + FileChooser.FILECHOOSER_TYPE_DIRECTORY_ONLY);
        extra.put(FileChooser.FILECHOOSER_TITLE_KEY, "Save");    
        extra.put(FileChooser.FILECHOOSER_MESSAGE_KEY, "Use this folder:? ");
        extra.put(FileChooser.FILECHOOSER_DEFAULT_DIR, prefs.getString(PREFS_KEY_SAVE_PATH, PREFS_DEFAULT_SAVE_PATH));
        extra.put(FileChooser.FILECHOOSER_SHOW_KEY, "" + FileChooser.FILECHOOSER_SHOW_DIRECTORY_ONLY);
        Tools.switchToForResult(PreferencesRecorder.this, FileChooserActivity.class,
            extra, FileChooserActivity.FILECHOOSER_SELECTION_TYPE_DIRECTORY);
        return true;
      }
    });
    final Preference dc = prefFrag.findPreference(PREFS_KEY_DETECT_CHANGE_FILTER);
    dc
    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(final Preference preference) {
        preferences = true;
        Tools.switchTo(PreferencesRecorder.this,
            PreferencesRecorderFilters.class);
        return true;
      }
    });
  }
  @Override
  public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
    if (key.equals(PREFS_KEY_FLUSH) || key.equals(PREFS_KEY_SEP) || key.equals(PREFS_KEY_NEIGHBORING_SEP) 
        || key.equals(PREFS_KEY_FORMATS) || key.equals(PREFS_KEY_DETECT_CHANGE)) {
      updateSummaries();
    }
  }
  
  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    // Check which request we're responding to
    if (requestCode == FileChooserActivity.FILECHOOSER_SELECTION_TYPE_DIRECTORY) {
      preferences = false;
      if (resultCode == RESULT_OK) {
        final String dir = data.getStringExtra(FileChooserActivity.FILECHOOSER_SELECTION_KEY);
        final SharedPreferences.Editor edit = prefs.edit();
        edit.putString(PREFS_KEY_SAVE_PATH, dir);
        edit.commit();
        updateSummaries();
      }
    }
  }

  private static class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRetainInstance(true);
      addPreferencesFromResource(R.xml.preferences_recorder);
    }
  }
}