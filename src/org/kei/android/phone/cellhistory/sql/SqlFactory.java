package org.kei.android.phone.cellhistory.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kei.android.phone.cellhistory.contexts.AreaCtx;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 *******************************************************************************
 * @file SqlFactory.java
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
public class SqlFactory implements SqlConstants {
  private SQLiteDatabase      bdd     = null;
  private SqlHelper           helper  = null;
  
  public SqlFactory(final Context context) throws Exception {
    helper = new SqlHelper(context, DB_NAME, null, VERSION_BDD);
  }
  
  public void open() {
    bdd = helper.getWritableDatabase();
  }
  
  public void close() {
    bdd.close();
  }
  
  public SQLiteDatabase getBDD() {
    return bdd;
  }
  
  public long insertArea(final String name, final double latitude, final double longitude) {
    final ContentValues values = new ContentValues();
    values.put(COL_NAME, name);
    values.put(COL_LATITUDE, latitude);
    values.put(COL_LONGITUDE, longitude);
    return bdd.insert(TABLE_AREAS, null, values);
  }
  
  public int updateArea(final int id, final String name, final double latitude, final double longitude) {
    final ContentValues values = new ContentValues();
    values.put(COL_NAME, name);
    values.put(COL_LATITUDE, latitude);
    values.put(COL_LONGITUDE, longitude);
    return bdd.update(TABLE_AREAS, values, COL_ID + " = " + id, null);
  }
  
  public List<AreaCtx> getAreas() {
    final List<AreaCtx> list = new ArrayList<AreaCtx>();
    final Cursor c = bdd.rawQuery("SELECT  * FROM " + TABLE_AREAS, null);
    if (c.moveToFirst()) {
      do {
        AreaCtx a = new AreaCtx();
        a.setId(c.getInt(NUM_COL_ID));
        a.setName(c.getString(NUM_COL_NAME));
        a.setLatitude(c.getDouble(NUM_COL_LATITUDE));
        a.setLongitude(c.getDouble(NUM_COL_LONGITUDE));
        list.add(a);
      } while (c.moveToNext());
    }
    c.close();
    Collections.sort(list, new Comparator<AreaCtx>() {
      @Override
      public int compare(final AreaCtx lhs, final AreaCtx rhs) {
        return lhs.getName().compareTo(rhs.getName());
      }
    });
    return list;
  }
  
  public AreaCtx getArea(final String name) {
    final Cursor c = bdd.rawQuery("SELECT  * FROM " + TABLE_AREAS + " where "
        + COL_NAME + "= '" + name + "'", null);
    if (c.moveToFirst()) {
      do {
        AreaCtx a = new AreaCtx();
        a.setId(c.getInt(NUM_COL_ID));
        a.setName(c.getString(NUM_COL_NAME));
        a.setLatitude(c.getDouble(NUM_COL_LATITUDE));
        a.setLongitude(c.getDouble(NUM_COL_LONGITUDE));
        c.close();
        return a;
      } while (c.moveToNext());
    }
    c.close();
    return null;
  }
  
  public int removeAreaWithID(final AreaCtx a) {
    return bdd.delete(TABLE_AREAS, COL_ID + " = " + a.getId(), null);
  }
  
  public void removeAll() {
    bdd.execSQL("DROP TABLE IF EXISTS " + TABLE_AREAS + ";");
  }
}
