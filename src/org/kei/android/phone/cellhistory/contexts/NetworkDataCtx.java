package org.kei.android.phone.cellhistory.contexts;

/**
 *******************************************************************************
 * @file NetworkDataCtx.java
 * @author Keidan
 * @date 31/12/2015
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
public class NetworkDataCtx {
  private long startRX    = 0L;
  private long prevTimeRX = 0L;
  private long prevRX     = 0L;
  private long rx         = 0L;
  private long rxps       = 0L;
  private long startTX    = 0L;
  private long prevTimeTX = 0L;
  private long prevTX     = 0L;
  private long tx         = 0L;
  private long txps       = 0L;

  public void intialize(final long rx, final long tx) {
    startRX = rx;
    startTX = tx;
  }
  
  public void update(final long time, final long rx, final long tx) {
    final long newRX = rx;
    final long newTX = tx;
    final long transferedRX = newRX - prevRX;
    final long transferedTX = newTX - prevTX;
    final long transferedTimeRX = time - prevTimeRX;
    final long transferedTimeTX = time - prevTimeTX;
    if (transferedRX != startRX && transferedTimeRX != 0)
      this.rxps = transferedRX / transferedTimeRX;
    else
      this.rxps = 0L;
    if (transferedTX != startTX && transferedTimeTX != 0)
      this.txps = transferedTX / transferedTimeTX;
    else
      this.txps = 0L;
    prevRX = newRX;
    prevTX = newTX;
    prevTimeRX = time;
    prevTimeTX = time;

    this.rx = newRX - startRX;
    this.tx = newTX - startTX;
  }
  
  public long getRx() {
    return rx;
  }
  
  public long getTx() {
    return tx;
  }
  
  public long getRxSpeed() {
    return rxps;
  }
  
  public long getTxSpeed() {
    return txps;
  }
}
