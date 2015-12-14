package org.kei.android.phone.cellhistory.contexts;

/**
 *******************************************************************************
 * @file ProviderCtx.java
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
public class ProviderCtx {
  public static final String LOC_NONE        = "None";
  public static final String LOC_BAD_REQUEST = "Bad request";
  public static final String LOC_NOT_FOUND   = "Not found";
  private int                oldCellId       = -1;
  private String             oldLoc          = LOC_NONE;
  private long               retryLoc        = 0L;

  public void clear() {
    oldCellId = -1;
    oldLoc = ProviderCtx.LOC_NONE;
    retryLoc = 0;
  }

  public void updateAll(int oldCellId, String oldLoc, long retryLoc) {
    update(oldCellId, oldLoc);
    this.retryLoc = retryLoc;
  }

  public void update(int oldCellId, String oldLoc) {
    this.oldLoc = oldLoc;
    this.oldCellId = oldCellId;
  }
  
  public boolean isValid() {
    return (!oldLoc.startsWith(ProviderCtx.LOC_NONE)
        && !oldLoc.equals(ProviderCtx.LOC_NOT_FOUND) && !oldLoc
          .equals(ProviderCtx.LOC_BAD_REQUEST));
  }
  
  /**
   * @return the oldCellId
   */
  public int getOldCellId() {
    return oldCellId;
  }
  
  /**
   * @return the oldLoc
   */
  public String getOldLoc() {
    return oldLoc;
  }
  
  /**
   * @return the retryLoc
   */
  public long getRetryLoc() {
    return retryLoc;
  }
  
}
