package org.kei.android.phone.cellhistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.kei.android.phone.cellhistory.contexts.ProviderCtx;
import org.kei.android.phone.cellhistory.contexts.RecorderCtx;
import org.kei.android.phone.cellhistory.tasks.ProviderTask;
import org.kei.android.phone.cellhistory.tasks.TowerTask;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.app.Application;
import android.content.Context;

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
  private static final int CIRCULAR_BUFFER_DEPTH = 1500;
  private Buffer           logs                  = null;
  private Lock             lock                  = null;
  private final TowerInfo  globalTi              = new TowerInfo();
  private TowerInfo        backupTi              = null;
  private ProviderCtx      providerCtx           = null;
  private RecorderCtx      recorderCtx           = null;
  private ProviderTask     providerTask          = null;
  private TowerTask        towerTask             = null;
  private int              currentSlideIndex     = 0;

  public CellHistoryApp() {
    lock = new ReentrantLock();
    providerCtx = new ProviderCtx();
    recorderCtx = new RecorderCtx();
    providerTask = new ProviderTask(this);
    towerTask = new TowerTask(this);
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
    final String head = new SimpleDateFormat("yyyyMMdd [hhmmssa]: \n",
        Locale.US).format(new Date());
    ctx.getLogBuffer().add(head + msg);
    ctx.unlock();
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

  public int getCurrentSlideIndex() {
    return currentSlideIndex;
  }

  public void setCurrentSlideIndex(int currentSlideIndex) {
    this.currentSlideIndex = currentSlideIndex;
  }
  
}
