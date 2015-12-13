package org.kei.android.phone.cellhistory.towers.request;

import org.apache.commons.httpclient.HttpConnection;

import android.app.Activity;

/**
 *******************************************************************************
 * @file CellIdRequestEntity.java
 * @author Keidan
 * @date 01/12/2015
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
public interface CellIdRequestEntity {
  public static final int    OK              = 200;
  public static final int    NOT_FOUND       = 404;
  public static final int    BAD_REQUEST     = 400;
  public static final int    EXCEPTION       = 500;
  
  public int decode(final Activity activity, final String url, HttpConnection connection, final int timeout) throws Exception;
}
