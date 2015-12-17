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
  private static final int   SIZE_1KB    = 0x400;
  private static final int   SIZE_1MB    = 0x100000;
  private static final int   SIZE_1GB    = 0x40000000;
  public static final String FORMAT_CSV  = "CSV";
  public static final String FORMAT_JSON = "JSON";
  public static final String FORMAT_XML  = "XML";
  private long               counter     = 0L;
  private long               size        = 0L;
  private File               currentFile = null;
  private final List<String> frames      = new ArrayList<String>();
  private PrintWriter        pw          = null;
  private String             format      = FORMAT_JSON;
  private boolean            indentation = true;
  

  public void writeData(final String sep, final String sepNb, final int limit,
      final CellHistoryApp ctx, final boolean detectChange, long records) {
    if (pw != null) {
      if (detectChange) {
        if (ctx.getBackupTowerInfo() == null
            || !ctx.getBackupTowerInfo().equals(ctx.getGlobalTowerInfo())) {
          ctx.setBackupTowerInfo(new TowerInfo(ctx.getGlobalTowerInfo()));

          if(format.equals(FORMAT_CSV)) frames.add(ctx.getGlobalTowerInfo().toString(sep, sepNb));
          else if(format.equals(FORMAT_JSON)) frames.add(ctx.getGlobalTowerInfo().toJSON(indentation));
          else frames.add(ctx.getGlobalTowerInfo().toXML(indentation));
          counter++;
        }
      } else {
        if(format.equals(FORMAT_CSV)) frames.add(ctx.getGlobalTowerInfo().toString(sep, sepNb));
        else if(format.equals(FORMAT_JSON)) frames.add(ctx.getGlobalTowerInfo().toJSON(indentation));
        else frames.add(ctx.getGlobalTowerInfo().toXML(indentation));
        counter++;
      }
      if (frames.size() >= limit)
        write();
      ctx.notificationRecorderUpdate(records, convertToHuman(size), frames.size(), limit);
    }
  }
  
  private void write() {
    if(format.equals(FORMAT_JSON)) {
      String ss = "";
      ss += "{" + ((indentation) ? "\n" : "");
      ss += (indentation ? "  " : "") + "\"towers\": [" + ((indentation) ? "\n" : "");
      pw.print(ss);
      size += ss.length();
      int len = frames.size();
      for (int i = 0; i < len; ++i) {
        String s = frames.get(i);
        pw.print(s);
        size += s.length();
        if(i < len - 1) {
          pw.print(",");
          size++;
        }
      }
      ss = (indentation ? "  " : "") + "]" + ((indentation) ? "\n" : "");
      ss += "}";
      pw.print(ss);
      size += ss.length();
    } else if(format.equals(FORMAT_CSV)) {
      for (final String s : frames) {
        pw.println(s);
        size += s.length() + 1;
      }
    } else if(format.equals(FORMAT_XML)) {
      String ss = "<towers>";
      if(indentation) ss += "\n";
      pw.print(ss);
      size += ss.length();
      for (final String s : frames) {
        String str = s;
        if(indentation) str += "\n";
        pw.println(str);
        size += str.length();
      }
      ss = "</towers>";
      if(indentation) ss += "\n";
      pw.print(ss);
      size += ss.length();
    }
    frames.clear();
    pw.flush();
  }
  
  public static String convertToHuman(float f) {
    String sf = "";
    if(f < SIZE_1KB)
      sf = String.format(Locale.US, "%d octet%s", (int)f, f > 1 ? "s" : "");
    else if(f < SIZE_1MB)
      sf = String.format("%.02f", (f/SIZE_1KB)) + " Ko";
    else if(f < SIZE_1GB)
      sf = String.format("%.02f", (f/SIZE_1MB)) + " Mo";
    else
      sf = String.format("%.02f", (f/SIZE_1GB)) + " Go";
    return sf;
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
      final String sepNb, final boolean deletePrev, final String format, final boolean indentation) throws Exception {
    this.format = format;
    this.indentation = indentation;
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
    String fmt = "json";
    if(format.equals(FORMAT_CSV)) fmt = "csv";
    else if(format.equals(FORMAT_XML)) fmt = "xml";
    currentFile = new File(root, new SimpleDateFormat(
        "yyyyMMdd_hhmmssa'_" + name + "." + fmt + "'", Locale.US).format(new Date()));
    pw = new PrintWriter(currentFile);
    if(format.equals(FORMAT_CSV)) {
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
    } else if(format.equals(FORMAT_XML)) {
      String s = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
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
