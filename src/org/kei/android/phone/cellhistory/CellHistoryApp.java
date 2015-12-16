package org.kei.android.phone.cellhistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.kei.android.atk.utils.NotificationHelper;
import org.kei.android.phone.cellhistory.activities.CellHistoryPagerActivity;
import org.kei.android.phone.cellhistory.contexts.ProviderCtx;
import org.kei.android.phone.cellhistory.contexts.RecorderCtx;
import org.kei.android.phone.cellhistory.prefs.Preferences;
import org.kei.android.phone.cellhistory.tasks.GpsTask;
import org.kei.android.phone.cellhistory.tasks.ProviderTask;
import org.kei.android.phone.cellhistory.tasks.TowerTask;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 *******************************************************************************
 * @file CellHistoryApp.java
 * @author Keidan
 * @date 09/12/2015
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
public class CellHistoryApp extends Application {
  private static final int   CIRCULAR_BUFFER_DEPTH = 1500;
  private Buffer             logs                  = null;
  private Lock               lock                  = null;
  private final TowerInfo    globalTi              = new TowerInfo();
  private TowerInfo          backupTi              = null;
  private ProviderCtx        providerCtx           = null;
  private RecorderCtx        recorderCtx           = null;
  private ProviderTask       providerTask          = null;
  private GpsTask            gpsTask               = null;
  private TowerTask          towerTask             = null;
  private int                currentSlideIndex     = 0;
  private NotificationHelper nfyHelper             = null;
  private final int          notifyID              = 2;

  public CellHistoryApp() {
    lock = new ReentrantLock();
    providerCtx = new ProviderCtx();
    recorderCtx = new RecorderCtx();
    providerTask = new ProviderTask(this);
    towerTask = new TowerTask(this);
    gpsTask = new GpsTask(this);
  }

  public Buffer getLogBuffer() {
    if (logs == null)
      logs = new CircularFifoBuffer(CIRCULAR_BUFFER_DEPTH);
    return logs;
  }

  public void lock() {
    lock.lock();
  }

  public void unlock() {
    lock.unlock();
  }
  
  public static CellHistoryApp getApp(final Context c) {
    return (CellHistoryApp) c.getApplicationContext();
  }
  
  @SuppressWarnings("unchecked")
  public static void addLog(final Context c, final Object msg) {
    final CellHistoryApp ctx = CellHistoryApp.getApp(c);
    ctx.lock();
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    if(prefs.getBoolean(Preferences.PREFS_KEY_LOG_ENABLE, Preferences.PREFS_DEFAULT_LOG_ENABLE)) {
      String head = new SimpleDateFormat("yyyyMMdd [hhmmssa]: \n",
          Locale.US).format(new Date());
      try {
        throw new Exception();
      } catch(Exception e) {
        StackTraceElement ste = e.getStackTrace()[1];
        String name = ste.getClassName();
        int n = -1;
        if((n = name.lastIndexOf('.')) != -1)
          name = name.substring(n+1);
        head += name + "->" + ste.getMethodName() + "(" + ste.getLineNumber() + ")\n";
      }
      ctx.getLogBuffer().add(head + msg);
    }
    ctx.unlock();
  }
  

  
  public void notificationShow() {
    final Intent toLaunch = new Intent(getApplicationContext(),
        CellHistoryPagerActivity.class);
    toLaunch.setAction("android.intent.action.MAIN");
    toLaunch.addCategory("android.intent.category.LAUNCHER");
    final PendingIntent intentBack = PendingIntent
        .getActivity(getApplicationContext(), 0, toLaunch,
            PendingIntent.FLAG_UPDATE_CURRENT);
    if(nfyHelper == null)
      nfyHelper = new NotificationHelper(getApplicationContext(), notifyID);
    nfyHelper.setExtra(false, false);
    nfyHelper.show(R.drawable.ic_launcher, null, getString(R.string.app_name),
        getString(R.string.notification), intentBack);
  }
  
  public NotificationHelper getNfyHelper() {
    if(nfyHelper == null)
      nfyHelper = new NotificationHelper(getApplicationContext(), notifyID);
    return nfyHelper;
  }
  
  public TowerInfo getGlobalTowerInfo() {
    return globalTi;
  }
  
  public TowerInfo getBackupTowerInfo() {
    return backupTi;
  }
  
  public void setBackupTowerInfo(final TowerInfo ti) {
    backupTi = ti;
  }
  
  public ProviderCtx getProviderCtx() {
    return providerCtx;
  }
  
  public RecorderCtx getRecorderCtx() {
    return recorderCtx;
  }
  
  public ProviderTask getProviderTask() {
    return providerTask;
  }
  
  public TowerTask getTowerTask() {
    return towerTask;
  }
  
  public GpsTask getGpsTask() {
    return gpsTask;
  }

  public int getCurrentSlideIndex() {
    return currentSlideIndex;
  }

  public void setCurrentSlideIndex(int currentSlideIndex) {
    this.currentSlideIndex = currentSlideIndex;
  }
  
}
