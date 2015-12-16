package org.kei.android.phone.cellhistory.activities;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.kei.android.atk.utils.Tools;
import org.kei.android.atk.utils.fx.Fx;
import org.kei.android.atk.view.IThemeActivity;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.adapters.ScreenSlidePagerAdapter;
import org.kei.android.phone.cellhistory.fragments.ProviderFragment;
import org.kei.android.phone.cellhistory.fragments.NeighboringFragment;
import org.kei.android.phone.cellhistory.fragments.RecorderFragment;
import org.kei.android.phone.cellhistory.fragments.TowerFragment;
import org.kei.android.phone.cellhistory.fragments.UITaskFragment;
import org.kei.android.phone.cellhistory.prefs.Preferences;
import org.kei.android.phone.cellhistory.prefs.PreferencesTimers;
import org.kei.android.phone.cellhistory.prefs.PreferencesUI;
import org.kei.android.phone.cellhistory.prefs.PreferencesGeolocation;
import org.kei.android.phone.cellhistory.utils.DepthPageTransformer;
import org.kei.android.phone.cellhistory.utils.ZoomOutPageTransformer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

/**
 *******************************************************************************
 * @file ScreenSlidePagerActivity.java
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
public class CellHistoryPagerActivity extends FragmentActivity implements
IThemeActivity, OnPageChangeListener {
  private static final int            BACK_TIME_DELAY = 2000;
  private static long                 lastBackPressed = -1;
  private ViewPager                   mPager;
  private PagerAdapter                mPagerAdapter;
  /* context */
  private SharedPreferences           prefs           = null;
  private CellHistoryApp              app             = null;
  /* tasks */
  private ScheduledThreadPoolExecutor execUpdateUI    = null;
  private List<Fragment>              fragments       = null;
  private boolean                     exit            = false;
  private boolean                     preferences     = false;

  static {
    Fx.default_animation = Fx.ANIMATION_FADE;
    Fx.default_theme = PreferencesUI.THEME_DARK;
  }

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    themeUpdate();
    super.onCreate(savedInstanceState);
    Fx.updateTransition(this, true);
    setContentView(R.layout.activity_cellhistorypager);
    /* context */
    app = CellHistoryApp.getApp(this);
    prefs = PreferenceManager.getDefaultSharedPreferences(this);
    app.getProviderTask().initialize(this, prefs);
    app.getTowerTask().initialize(this, prefs);
    app.getGpsTask().initialize(this);

    fragments = new Vector<Fragment>();
    fragments.add(new TowerFragment());
    fragments.add(new ProviderFragment());
    fragments.add(new NeighboringFragment());
    fragments.add(new RecorderFragment());
    
    mPager = (ViewPager) findViewById(R.id.pager);
    
    setTransformer();
    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),
        fragments);
    mPager.setAdapter(mPagerAdapter);
    mPager.setCurrentItem(prefs.getInt(Preferences.PREFS_KEY_CURRENT_TAB,
        Preferences.PREFS_DEFAULT_CURRENT_TAB));
    mPager.setOnPageChangeListener(this);
    /* task */
    if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE,
        PreferencesGeolocation.PREFS_DEFAULT_LOCATE)) {
      app.getProviderTask().start(
          Integer.parseInt(prefs.getString(
              PreferencesTimers.PREFS_KEY_TIMERS_TASK_TOWER,
              PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_TOWER)));

      app.getGpsTask().start(
          Integer.parseInt(prefs.getString(
              PreferencesTimers.PREFS_KEY_TIMERS_TASK_GPS,
              PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_GPS)));
    } else {
      app.getProviderTask().stop();
    }

    app.getTowerTask().start(
        Integer.parseInt(prefs.getString(
            PreferencesTimers.PREFS_KEY_TIMERS_TASK_TOWER,
            PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_TOWER)));
  }

  private void setTransformer() {
    final String transition = prefs.getString(
        PreferencesUI.PREFS_KEY_SLIDE_TRANSITION,
        PreferencesUI.PREFS_DEFAULT_SLIDE_TRANSITION);
    if (transition.equals(PreferencesUI.TRANSITION_DEPTH))
      mPager.setPageTransformer(true, new DepthPageTransformer());
    else
      mPager.setPageTransformer(true, new ZoomOutPageTransformer());
  }

  @Override
  protected void onPause() {
    if (execUpdateUI != null) {
      execUpdateUI.shutdownNow();
      execUpdateUI = null;
    }
    super.onPause();
    Fx.updateTransition(this, false);
    if (!app.getRecorderCtx().isRunning() && !exit && !preferences)
      app.notificationShow();
  }

  @Override
  protected void onResume() {
    super.onResume();
    preferences = false;
    app.getNfyHelper().hide();
    setTransformer();
    if (prefs.getBoolean(PreferencesUI.PREFS_KEY_KEEP_SCREEN,
        PreferencesUI.PREFS_DEFAULT_KEEP_SCREEN))
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    else
      getWindow().clearFlags(
          WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    
    if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_LOCATE,
        PreferencesGeolocation.PREFS_DEFAULT_LOCATE)) {
      app.getProviderTask().start(
          Integer.parseInt(prefs.getString(
              PreferencesTimers.PREFS_KEY_TIMERS_TASK_TOWER,
              PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_TOWER)));
      app.getGpsTask().start(
          Integer.parseInt(prefs.getString(
              PreferencesTimers.PREFS_KEY_TIMERS_TASK_GPS,
              PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_GPS)));
    } else {
      app.getProviderTask().stop();
      app.getGpsTask().stop();
    }
    execUpdateUI = new ScheduledThreadPoolExecutor(1);
    execUpdateUI.scheduleWithFixedDelay(uiTask, 0L, Integer.parseInt(prefs
        .getString(PreferencesTimers.PREFS_KEY_TIMERS_UI,
            PreferencesTimers.PREFS_DEFAULT_TIMERS_UI)), TimeUnit.MILLISECONDS);
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    app.getNfyHelper().hide();
    if (execUpdateUI != null) {
      execUpdateUI.shutdownNow();
      execUpdateUI = null;
    }
    app.getProviderTask().stop();
    app.getGpsTask().stop();
    app.getTowerTask().stop();
    app.getRecorderCtx().flushAndClose();
  }
  
  private final Runnable uiTask = new Runnable() {
    @Override
    public void run() {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          for (final Fragment f : fragments) {
            app.getGlobalTowerInfo().lock();
            try {
              final UITaskFragment tf = (UITaskFragment) f;
              tf.processUI(app.getGlobalTowerInfo());
            } catch (final Throwable e) {
              Log.e(CellHistoryPagerActivity.class.getSimpleName(), "Exception: " + e.getMessage(), e);
            } finally {
              app.getGlobalTowerInfo().unlock();
            }
          }
        }
      });
    }
  };

  @Override
  public void onBackPressed() {
    if (!exitOnDoubleBack()) {
      super.onBackPressed();
    } else {
      if (lastBackPressed + BACK_TIME_DELAY > System.currentTimeMillis()) {
        exit = true;
        super.onBackPressed();
      } else {
        Tools.toast(getBaseContext(), getToastIconId(),
            getResources().getText(getOnDoubleBackExitTextId()));
      }
      lastBackPressed = System.currentTimeMillis();
    }
  }
  
  protected boolean exitOnDoubleBack() {
    return true;
  }
  
  protected int getToastIconId() {
    return R.drawable.ic_launcher;
  }
  
  protected int getOnDoubleBackExitTextId() {
    return org.kei.android.atk.R.string.onDoubleBackExitText;
  }

  @Override
  public void themeUpdate() {
    Preferences.performTheme(this);
  }
  
  @Override
  public int getAnime(final AnimationType at) {
    return Fx.getAnimationFromPref(this, at);
  }
  
  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    getMenuInflater().inflate(R.menu.activity_cellhistorypager, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        preferences = true;
        final Intent intent = new Intent(this, Preferences.class);
        startActivity(intent);
        return true;
    }
    return false;
  }
  
  @Override
  public void onPageScrollStateChanged(final int state) {
  }
  
  @Override
  public void onPageScrolled(final int position, final float positionOffset,
      final int positionOffsetPixels) {
  }
  
  @Override
  public void onPageSelected(final int position) {
    final Editor e = prefs.edit();
    e.putInt(Preferences.PREFS_KEY_CURRENT_TAB, position);
    e.commit();
  }

}
