package org.kei.android.phone.cellhistory.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *******************************************************************************
 * @file SqlHelper.java
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
public class SqlHelper extends SQLiteOpenHelper implements SqlConstants {

  private static final String CREATE_BDD_ACCOUNT = "CREATE TABLE IF NOT EXISTS "
                                                     + TABLE_AREAS
                                                     + " ("
                                                     + COL_ID
                                                     + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                     + COL_NAME
                                                     + " TEXT NOT NULL, "
                                                     + COL_LATITUDE
                                                     + " REAL, "
                                                     + COL_LONGITUDE
                                                     + " REAL, "
                                                     + COL_RADIUS
                                                     + " REAL);";
  
  public SqlHelper(final Context context, final String name,
      final CursorFactory factory, final int version) {
    super(context, name, factory, version);
  }

  @Override
  public void onCreate(final SQLiteDatabase db) {
    db.execSQL(CREATE_BDD_ACCOUNT);
  }
  
  @Override
  public void onOpen(final SQLiteDatabase db) {
    onCreate(db);
  }
  
  @Override
  public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
      final int newVersion) {
  }
  
}
