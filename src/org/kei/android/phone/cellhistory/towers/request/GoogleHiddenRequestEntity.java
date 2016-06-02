package org.kei.android.phone.cellhistory.towers.request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

/**
 *******************************************************************************
 * @file GoogleApiRequestEntity.java
 * @author http://www.anddev.org/poor_mans_gps_-_celltowerid_-_location_area_code_-lookup-t257.html
 *******************************************************************************
 */
public class GoogleHiddenRequestEntity implements RequestEntity, CellIdRequestEntity {
  protected TowerInfo  ti;
  
  public GoogleHiddenRequestEntity(final TowerInfo ti) {
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
    final DataOutputStream os = new DataOutputStream(outputStream);
    os.writeShort(21);
    os.writeLong(0);
    os.writeUTF("fr");
    os.writeUTF("Sony_Ericsson-K750");
    os.writeUTF("1.3.1");
    os.writeUTF("Web");
    os.writeByte(27);
    
    os.writeInt(0);
    os.writeInt(0);
    os.writeInt(3);
    os.writeUTF("");
    os.writeInt(ti.getCellId()); // CELL-ID
    os.writeInt(ti.getLac()); // LAC
    os.writeInt(0);
    os.writeInt(0);
    os.writeInt(0);
    os.writeInt(0);
    os.flush();
  }
  
  @Override
  public long getContentLength() {
    return -1;
  }
  
  @Override
  public String getContentType() {
    return "application/binary";
  }



  @Override
  public int decode(final String url, HttpConnection connection, final int timeout) throws Exception {
    int ret = OK;
    // Post (send) data to the connection
    final PostMethod postMethod = new PostMethod(url);
    postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, false));
    //socket timeout (connection timeout already set in HttpClient)
    postMethod.getParams().setSoTimeout(timeout);
    postMethod.setRequestEntity(this);
    postMethod.execute(new HttpState(), connection);
    final InputStream response = postMethod.getResponseBodyAsStream();
    final DataInputStream dis = new DataInputStream(response);
    // Read some prior data
    dis.readShort();
    dis.readByte();
    // Read the error-code
    final int errorCode = dis.readInt();
    if (errorCode == 0) {
      ti.setCellLatitude((double) dis.readInt() / 1000000D);
      ti.setCellLongitude((double) dis.readInt() / 1000000D);
      // Read the rest of the data
      dis.readInt();
      dis.readInt();
      dis.readUTF();
    } else
      ret = NOT_FOUND;
    postMethod.releaseConnection();
    return ret;
  }
}
