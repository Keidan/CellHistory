package org.kei.android.phone.cellhistory.towers;


import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.towers.request.CellIdRequestEntity;
import org.kei.android.phone.cellhistory.towers.request.GoogleHiddenRequestEntity;
import org.kei.android.phone.cellhistory.towers.request.OpenCellIdRequestEntity;

import android.content.Context;
import android.util.Log;


/**
 *******************************************************************************
 * @file CellIdHelper.java
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
public class CellIdHelper {
  public static final String   NONE                  = "NONE";
  public static final String   GOOGLE_HIDDENT_API    = "Google hidden API";
  public static final String   OPEN_CELL_ID_API      = "OpenCellID";
  
  public static int tryToLocate(final Context context, final TowerInfo ti, int cfg_timeout, final String mode, final String apiKeyOpenCellID) {
    int timeout = cfg_timeout * 1000, ret = CellIdRequestEntity.OK;
    HttpConnectionManager connectionManager = new SimpleHttpConnectionManager();
    connectionManager.getParams().setConnectionTimeout(timeout);
    connectionManager.getParams().setSoTimeout(timeout);
    // Create a connection to some 'hidden' Google-API
    String baseURL = null;
    if(mode.equals(OPEN_CELL_ID_API)) {
      ti.lock();
      try {
        baseURL = "http://opencellid.org/cell/get?key=" + apiKeyOpenCellID + "&mcc=" + ti.getMCC()
                      +"&mnc=" + ti.getMNC() + "&cellid=" + ti.getCellId() + "&lac=" + ti.getLac() + "&format=json";
      } finally {
        ti.unlock();
      }
    }
    else
      baseURL = "http://www.google.com/glm/mmap";
    HttpConnection connection = null;
    ti.setCellLatitude(Double.NaN);
    ti.setCellLongitude(Double.NaN);
    try { 
      // Setup the connection
      HttpURL httpURL = null;
      if(baseURL.startsWith("https")) httpURL = new HttpsURL(baseURL);
      else httpURL = new HttpURL(baseURL);
      final HostConfiguration host = new HostConfiguration();
      host.setHost(httpURL.getHost(), httpURL.getPort());
      connection = connectionManager.getConnection(host);
      // Open it
      connection.open();
      if(mode.equals(OPEN_CELL_ID_API))
        ret = new OpenCellIdRequestEntity(ti).decode(baseURL, connection, timeout);
      else
        ret = new GoogleHiddenRequestEntity(ti).decode(baseURL, connection, timeout);
    } catch(Exception e) {
      Log.e(CellIdHelper.class.getSimpleName(), "Exception: " + e.getMessage(), e);
      ret = CellIdRequestEntity.EXCEPTION;
      CellHistoryApp.addLog(context, "tryToLocate::Exception: " + e.getMessage());
    } finally {
      connection.close();
    }
    connectionManager.releaseConnection(connection);
    return ret;
  }

}
