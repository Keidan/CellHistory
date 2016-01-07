package org.kei.android.phone.cellhistory.sql;

/**
 *******************************************************************************
 * @file SqlConstants.java
 * @author Keidan
 * @date 07/01/2016
 * @par Project CellHistory
 *
 * @par Copyright 2015-2016 Keidan, all right reserved
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
public interface SqlConstants {
  public static final int    VERSION_BDD       = 1;
  public static final String DB_NAME           = "cellhistory.db";
  public static final String TABLE_AREAS       = "areas";
  public static final String COL_ID            = "ID";
  public static final String COL_NAME          = "NAME";
  public static final String COL_LATITUDE      = "LATITUDE";
  public static final String COL_LONGITUDE     = "LONGITUDE";
  public static final int    NUM_COL_ID        = 0;
  public static final int    NUM_COL_NAME      = 1;
  public static final int    NUM_COL_LATITUDE  = 2;
  public static final int    NUM_COL_LONGITUDE = 3;
}
