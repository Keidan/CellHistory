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
import org.kei.android.phone.cellhistory.sql.SqlFactory;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

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
  private TowerInfo          globalTi              = null;
  private TowerInfo          backupTi              = null;
  private ProviderCtx        providerCtx           = null;
  private RecorderCtx        recorderCtx           = null;
  private int                currentSlideIndex     = 0;
  private NotificationHelper nfyHelper             = null;
  private NotificationHelper nfyRecorderHelper     = null;
  private final int          notifyID              = 2;
  private final int          notifyRecorderID      = 1;
  private PendingIntent      pendingIntent         = null;
  private SqlFactory         sql                   = null;

  public CellHistoryApp() {
    lock = new ReentrantLock();
    providerCtx = new ProviderCtx();
    recorderCtx = new RecorderCtx();
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
  
  private PendingIntent getPendingIntent() {
    if(pendingIntent == null) {
      final Intent toLaunch = new Intent(getApplicationContext(),
          CellHistoryPagerActivity.class);
      toLaunch.setAction("android.intent.action.MAIN");
      toLaunch.addCategory("android.intent.category.LAUNCHER");
      pendingIntent = PendingIntent
          .getActivity(getApplicationContext(), 0, toLaunch,
              PendingIntent.FLAG_UPDATE_CURRENT);
    }
    return pendingIntent;
  }
  
  public void notificationRecorderShow(int max) {
    if(nfyRecorderHelper == null)
      nfyRecorderHelper = new NotificationHelper(getApplicationContext(), notifyRecorderID);
    final RemoteViews contentView = getRecorderRemoteViews(0, "0 octet", 0, max);
    nfyRecorderHelper.show(contentView, R.drawable.ic_launcher_green, "ticker", getPendingIntent());
  }
  
  public void notificationRecorderUpdate(final long records, final String size, final int buffer, final int max) {
    if(nfyRecorderHelper == null) return;
    final RemoteViews contentView = getRecorderRemoteViews(records, size, buffer, max);
    nfyRecorderHelper.update(contentView);
  }
  
  private RemoteViews getRecorderRemoteViews(final long records, final String size, final int buffer, final int max) {
    final RemoteViews contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification_recorder);
    contentView.setImageViewResource(R.id.imagenotileft, R.drawable.ic_launcher_green);
    contentView.setTextViewText(R.id.title, getString(R.string.app_name));
    contentView.setTextViewText(R.id.textBuffer, "Buffer: ");
    contentView.setTextViewText(R.id.textPackets, "Records: " + records);
    contentView.setTextViewText(R.id.textSize, "Size: " + size);
    contentView.setProgressBar(R.id.pbBuffer, max, buffer, false);
    return contentView;
  }
  private RemoteViews getRemoteViews(final String name, final int cellid, final int lac, final int ss, final int ssp, final long rxsp, final long txsp) {
    final RemoteViews contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notifications_main);
    contentView.setImageViewResource(R.id.imagenotileft, R.drawable.ic_launcher);
    contentView.setTextViewText(R.id.title, getString(R.string.app_name));
    contentView.setTextViewText(R.id.textInfo, "CellID: " + cellid + ", LAC: " + lac);
    String speeds = "Rx:" + RecorderCtx.convertToHuman(rxsp, false) + "/s, Tx:" + RecorderCtx.convertToHuman(txsp, false) + "/s";
    contentView.setTextViewText(R.id.textNetwork, "Network: " + name + ", " + speeds);
    contentView.setTextViewText(R.id.textSS, "Signal strength: " + ss + " dBm (" + ssp + "%)");
    return contentView;
  }
  
  public void notificationShow() {
    if(nfyHelper == null)
      nfyHelper = new NotificationHelper(getApplicationContext(), notifyID);
    final RemoteViews contentView = getRemoteViews(TowerInfo.UNKNOWN, -1, -1, 0, 0, 0, 0);
    nfyHelper.show(contentView, R.drawable.ic_launcher, "ticker", getPendingIntent());
  }
  
  public void notificationUpdate(final String name, final int cellid, final int lac, final int ss, final int ssp, final long rxsp, final long txsp) {
    if(nfyHelper == null) return;
    final RemoteViews contentView = getRemoteViews(name, cellid, lac, ss, ssp, rxsp, txsp);
    nfyHelper.update(contentView);
  }
  
  public NotificationHelper getNfyRecorderHelper() {
    if(nfyRecorderHelper == null)
      nfyRecorderHelper = new NotificationHelper(getApplicationContext(), notifyRecorderID);
    return nfyRecorderHelper;
  }
  
  public NotificationHelper getNfyHelper() {
    if(nfyHelper == null)
      nfyHelper = new NotificationHelper(getApplicationContext(), notifyID);
    return nfyHelper;
  }
  
  public TowerInfo getGlobalTowerInfo() {
    if(globalTi == null) {
      globalTi = new TowerInfo();
    }
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
  
  public int getCurrentSlideIndex() {
    return currentSlideIndex;
  }

  public void setCurrentSlideIndex(int currentSlideIndex) {
    this.currentSlideIndex = currentSlideIndex;
  }

  public SqlFactory getSQL() {
    return sql;
  }

  public void setSQL(SqlFactory sql) {
    this.sql = sql;
  }
  
}
