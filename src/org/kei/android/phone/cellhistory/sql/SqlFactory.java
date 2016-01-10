package org.kei.android.phone.cellhistory.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.kei.android.phone.cellhistory.towers.AreaInfo;

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
  
  public long insertArea(final AreaInfo ai) {
    return insertArea(ai.getName(), ai.getLatitude(), ai.getLongitude(), ai.getRadius());
  }
  
  public long insertArea(final String name, final double latitude, final double longitude, final double radius) {
    final ContentValues values = new ContentValues();
    values.put(COL_NAME, name);
    values.put(COL_LATITUDE, latitude);
    values.put(COL_LONGITUDE, longitude);
    values.put(COL_RADIUS, radius);
    return bdd.insert(TABLE_AREAS, null, values);
  }

  public long updateArea(final AreaInfo ai) {
    return updateArea(ai.getId(), ai.getName(), ai.getLatitude(), ai.getLongitude(), ai.getRadius());
  }
  
  public int updateArea(final int id, final String name, final double latitude, final double longitude, final double radius) {
    final ContentValues values = new ContentValues();
    values.put(COL_NAME, name);
    values.put(COL_LATITUDE, latitude);
    values.put(COL_LONGITUDE, longitude);
    values.put(COL_RADIUS, radius);
    return bdd.update(TABLE_AREAS, values, COL_ID + " = " + id, null);
  }
  
  public List<AreaInfo> getAreas() {
    final List<AreaInfo> list = new ArrayList<AreaInfo>();
    final Cursor c = bdd.rawQuery("SELECT  * FROM " + TABLE_AREAS, null);
    if (c.moveToFirst()) {
      do {
        AreaInfo a = new AreaInfo();
        a.setId(c.getInt(NUM_COL_ID));
        a.setName(c.getString(NUM_COL_NAME));
        a.setLatitude(c.getDouble(NUM_COL_LATITUDE));
        a.setLongitude(c.getDouble(NUM_COL_LONGITUDE));
        a.setRadius(c.getDouble(NUM_COL_RADIUS));
        list.add(a);
      } while (c.moveToNext());
    }
    c.close();
    Collections.sort(list, new Comparator<AreaInfo>() {
      @Override
      public int compare(final AreaInfo lhs, final AreaInfo rhs) {
        return lhs.getName().compareTo(rhs.getName());
      }
    });
    return list;
  }
  
  public AreaInfo getArea(final String name) {
    final Cursor c = bdd.rawQuery("SELECT  * FROM " + TABLE_AREAS + " where "
        + COL_NAME + "= '" + name + "'", null);
    if (c.moveToFirst()) {
      do {
        AreaInfo a = new AreaInfo();
        a.setId(c.getInt(NUM_COL_ID));
        a.setName(c.getString(NUM_COL_NAME));
        a.setLatitude(c.getDouble(NUM_COL_LATITUDE));
        a.setLongitude(c.getDouble(NUM_COL_LONGITUDE));
        a.setRadius(c.getDouble(NUM_COL_RADIUS));
        c.close();
        return a;
      } while (c.moveToNext());
    }
    c.close();
    return null;
  }
  
  public int removeAreaWithID(final AreaInfo a) {
    return bdd.delete(TABLE_AREAS, COL_ID + " = " + a.getId(), null);
  }
  
  public void removeAll() {
    bdd.execSQL("delete from " + TABLE_AREAS + ";");
  }
  
  public void removeTable() {
    bdd.execSQL("DROP TABLE IF EXISTS " + TABLE_AREAS + ";");
  }
}
