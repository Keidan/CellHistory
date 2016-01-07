package org.kei.android.phone.cellhistory.activities;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.kei.android.atk.utils.Tools;
import org.kei.android.atk.utils.changelog.ChangeLog;
import org.kei.android.atk.utils.changelog.ChangeLogIds;
import org.kei.android.atk.utils.fx.Fx;
import org.kei.android.atk.view.IThemeActivity;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.adapters.ScreenSlidePagerAdapter;
import org.kei.android.phone.cellhistory.fragments.AreaFragment;
import org.kei.android.phone.cellhistory.fragments.NetworkFragment;
import org.kei.android.phone.cellhistory.fragments.ProviderFragment;
import org.kei.android.phone.cellhistory.fragments.NeighboringFragment;
import org.kei.android.phone.cellhistory.fragments.RecorderFragment;
import org.kei.android.phone.cellhistory.fragments.TowerFragment;
import org.kei.android.phone.cellhistory.fragments.UITaskFragment;
import org.kei.android.phone.cellhistory.prefs.Preferences;
import org.kei.android.phone.cellhistory.prefs.PreferencesTimers;
import org.kei.android.phone.cellhistory.prefs.PreferencesUI;
import org.kei.android.phone.cellhistory.prefs.PreferencesGeolocation;
import org.kei.android.phone.cellhistory.services.GpsService;
import org.kei.android.phone.cellhistory.services.NetworkService;
import org.kei.android.phone.cellhistory.services.ProviderService;
import org.kei.android.phone.cellhistory.services.RecorderService;
import org.kei.android.phone.cellhistory.services.TowerService;
import org.kei.android.phone.cellhistory.sql.SqlFactory;
import org.kei.android.phone.cellhistory.transformers.DepthPageTransformer;
import org.kei.android.phone.cellhistory.transformers.ZoomOutPageTransformer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
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
    Fx.default_theme = PreferencesUI.THEME_DARK_BLUE;
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
    app.getRecorderCtx().initialize(prefs);

    fragments = new Vector<Fragment>();
    fragments.add(new TowerFragment());
    fragments.add(new NetworkFragment());
    fragments.add(new ProviderFragment());
    fragments.add(new AreaFragment());
    fragments.add(new NeighboringFragment());
    fragments.add(new RecorderFragment());
    
    mPager = (ViewPager) findViewById(R.id.pager);
    PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
    pagerTabStrip.setDrawFullUnderline(false);
    TypedArray ta = getTheme().obtainStyledAttributes(new int [] { R.attr.pagerTitleColor });
    pagerTabStrip.setTabIndicatorColor(ta.getColor(0, Color.BLACK));
    ta.recycle();
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
      startService(new Intent(this, ProviderService.class));
      startService(new Intent(this, GpsService.class));
    } else {
      stopService(new Intent(this, ProviderService.class));
      stopService(new Intent(this, GpsService.class));
    }

    startService(new Intent(this, NetworkService.class));
    startService(new Intent(this, TowerService.class));
    

    ChangeLog changeLog = new ChangeLog(
        new ChangeLogIds(
            R.raw.changelog, 
            R.string.changelog_ok_button, 
            R.string.background_color, 
            R.string.changelog_title, 
            R.string.changelog_full_title, 
            R.string.changelog_show_full), this);
    if(changeLog.firstRun())
      changeLog.getLogDialog().show();
    
    try {
      app.setSQL(new SqlFactory(this));
      app.getSQL().open();
    } catch (final Exception e) {
      Tools.showAlertDialog(this, "Exception", e.getMessage());
    }
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
      startService(new Intent(this, ProviderService.class));
      if (prefs.getBoolean(PreferencesGeolocation.PREFS_KEY_GPS,
          PreferencesGeolocation.PREFS_DEFAULT_GPS))
        startService(new Intent(this, GpsService.class));
      else
        stopService(new Intent(this, GpsService.class));
    } else {
      stopService(new Intent(this, ProviderService.class));
      stopService(new Intent(this, GpsService.class));
    }
    execUpdateUI = new ScheduledThreadPoolExecutor(1);
    execUpdateUI.scheduleWithFixedDelay(uiTask, 0L, Integer.parseInt(prefs
        .getString(PreferencesTimers.PREFS_KEY_TIMERS_UI,
            PreferencesTimers.PREFS_DEFAULT_TIMERS_UI)), TimeUnit.MILLISECONDS);
    super.onResume();
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    app.getNfyHelper().hide();
    if (execUpdateUI != null) {
      execUpdateUI.shutdownNow();
      execUpdateUI = null;
    }
    stopService(new Intent(this, ProviderService.class));
    stopService(new Intent(this, GpsService.class));
    stopService(new Intent(this, RecorderService.class));
    stopService(new Intent(this, NetworkService.class));
    stopService(new Intent(this, TowerService.class));
    app.getSQL().close();
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
