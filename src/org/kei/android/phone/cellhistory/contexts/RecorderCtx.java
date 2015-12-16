package org.kei.android.phone.cellhistory.contexts;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

/**
 *******************************************************************************
 * @file RecorderFragment.java
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
public class RecorderCtx {
  private long               counter     = 0L;
  private long               size        = 0L;
  private File               currentFile = null;
  private final List<String> frames      = new ArrayList<String>();
  private PrintWriter        pw          = null;
  private boolean            json        = false;
  

  public void writeData(final String sep, final String sepNb, final int limit,
      final CellHistoryApp ctx, final boolean detectChange) {
    if (pw != null) {
      if (detectChange) {
        if (ctx.getBackupTowerInfo() == null
            || !ctx.getBackupTowerInfo().equals(ctx.getGlobalTowerInfo())) {
          ctx.setBackupTowerInfo(new TowerInfo(ctx.getGlobalTowerInfo()));
          if(!json) frames.add(ctx.getGlobalTowerInfo().toString(sep, sepNb));
          else frames.add(ctx.getGlobalTowerInfo().toJSON());
          counter++;
        }
      } else {
        if(!json) frames.add(ctx.getGlobalTowerInfo().toString(sep, sepNb));
        else frames.add(ctx.getGlobalTowerInfo().toJSON());
        counter++;
      }
      if (frames.size() >= limit)
        write();
    }
  }
  
  private void write() {
    String ss = "{\"towers\": [";
    pw.print(ss);
    size += ss.length();
    int len = frames.size();
    for (int i = 0; i < len; ++i) {
      String s = frames.get(i);
      pw.print(s);
      size += s.length() + 1;
      if(i < len - 1) pw.print(",");
    }
    ss = "]}";
    pw.print(ss);
    size += ss.length();
    frames.clear();
    pw.flush();
  }
  
  public boolean isRunning() {
    return pw != null;
  }

  public void flushAndClose() {
    if (pw != null) {
      if (!frames.isEmpty())
        write();
      pw.close();
      pw = null;
    }
    frames.clear();
  }

  public void writeHeader(final String root, final String name, final String sep,
      final String sepNb, final boolean deletePrev, boolean json) throws Exception {
    this.json = json;
    if (pw != null) {
      pw.close();
      pw = null;
    }
    if (deletePrev) {
      if (currentFile != null)
        currentFile.delete();
    }
    size = 0;
    counter = 1L;
    String fmt = json ? "json" : "csv";
    currentFile = new File(root, new SimpleDateFormat(
        "yyyyMMdd_hhmmssa'_" + name + "." + fmt + "'", Locale.US).format(new Date()));
    pw = new PrintWriter(currentFile);
    if(!json) {
      // add title
      final StringBuilder sb = new StringBuilder();
      sb.append("#TIMESTAMP").append(sep).append("OPE").append(sep).append("MCC")
          .append(sep).append("MNC").append(sep).append("CID").append(sep)
      .append("LAC").append(sep).append("LAT").append(sep).append("LON")
          .append(sep).append("SPD").append(sep).append("DIST").append(sep).append("PSC").append(sep)
      .append("TYPE").append(sep).append("NET").append(sep).append("LVL")
          .append(sep).append("ASU").append(sep).append("STR").append(sep)
      .append("PER").append(sep).append("NEIGBORING(").append("OLD").append(sepNb).append("LAC")
          .append(sepNb).append("CID").append(sepNb).append("ASU").append(sepNb)
      .append("NT").append(sepNb).append("STR").append(")...");
      String s = sb.toString();
      size = s.length() + 1;
      pw.println(s);
    }
  }
  
  /**
   * @return the counter
   */
  public long getCounter() {
    return counter;
  }

  /**
   * @return the currentFile
   */
  public File getCurrentFile() {
    return currentFile;
  }

  /**
   * @return the frames
   */
  public List<String> getFrames() {
    return frames;
  }

  /**
   * @return the size
   */
  public long getSize() {
    return size;
  }
  
}
