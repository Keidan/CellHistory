package org.kei.android.phone.cellhistory.towers.request;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

/**
 *******************************************************************************
 * @file OpenCellIdRequestEntity.java
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
public class OpenCellIdRequestEntity implements RequestEntity,
    CellIdRequestEntity {
  protected TowerInfo ti;

  public OpenCellIdRequestEntity(final TowerInfo ti) {
    this.ti = ti;
  }

  @Override
  public boolean isRepeatable() {
    return true;
  }

  /**
   * Pretend to be a French Sony_Ericsson-K750 that wants to receive its
   * lat/long-values =) The data written is highly proprietary !!!
   */
  @Override
  public void writeRequest(final OutputStream outputStream) throws IOException {
  }

  @Override
  public long getContentLength() {
    return -1;
  }

  @Override
  public String getContentType() {
    return "application/json";
  }
  
  @Override
  public int decode(final String url,
      final HttpConnection connection, final int timeout) throws Exception {
    int ret = OK;
    final GetMethod getMethod = new GetMethod(url);
    getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
        new DefaultHttpMethodRetryHandler(1, false));
    // socket timeout (connection timeout already set in HttpClient)
    getMethod.getParams().setSoTimeout(timeout);
    final int resCode = getMethod.execute(new HttpState(), connection);
    final InputStream response = getMethod.getResponseBodyAsStream();
    final DataInputStream dis = new DataInputStream(response);
    if (resCode == HttpStatus.SC_OK) {
      final int av = dis.available();
      final byte[] json = new byte[av];
      dis.readFully(json);
      final String sjson = new String(json);
      final String ljson = sjson.toLowerCase(Locale.US);
      if (ljson.indexOf("err") == -1) {
        final JSONObject object = new JSONObject(sjson);
        String lat, lng;
        lat = object.getString("lat");
        lng = object.getString("lon");
        ti.setLatitude(Double.parseDouble(lat));
        ti.setLongitude(Double.parseDouble(lng));
      } else if (ljson.indexOf("not found") != -1)
        ret = NOT_FOUND;
      else
        ret = EXCEPTION;
    } else if (resCode == HttpStatus.SC_NOT_FOUND)
      ret = NOT_FOUND;
    else if (resCode == HttpStatus.SC_INTERNAL_SERVER_ERROR)
      ret = BAD_REQUEST;
    else
      ret = EXCEPTION;
    getMethod.releaseConnection();
    return ret;
  }
}
