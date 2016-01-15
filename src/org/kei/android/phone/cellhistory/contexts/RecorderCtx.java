package org.kei.android.phone.cellhistory.contexts;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.prefs.PreferencesRecorderFilters;
import org.kei.android.phone.cellhistory.towers.NeighboringInfo;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.content.SharedPreferences;

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
  private static final int        SIZE_1KB    = 0x400;
  private static final int        SIZE_1MB    = 0x100000;
  private static final int        SIZE_1GB    = 0x40000000;
  public static final String      FORMAT_CSV  = "CSV";
  public static final String      FORMAT_JSON = "JSON";
  public static final String      FORMAT_XML  = "XML";
  private long                    counter     = 0L;
  private long                    size        = 0L;
  private File                    currentFile = null;
  private final List<String>      frames      = new ArrayList<String>();
  private PrintWriter             pw          = null;
  private String                  format      = FORMAT_JSON;
  private boolean                 indentation = true;
  private SharedPreferences       prefs;
  
  public RecorderCtx() {
  }

  public void initialize(final SharedPreferences prefs) {
    this.prefs = prefs;
  }

  public void writeData(final String sep, final String sepNb, final int limit,
      final CellHistoryApp ctx, final boolean detectChange, long records) {
    if (pw != null) {
      if (detectChange) {
        if (ctx.getBackupTowerInfo() == null
            || !sameTowerInfo(ctx.getBackupTowerInfo(), ctx.getGlobalTowerInfo())) {
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
    return convertToHuman(f, true);
  }
  
  public static String convertToHuman(float f, boolean fullOctet) {
    String sf = "";
    if(f < 1000) {
      if(fullOctet)
        sf = String.format(Locale.US, "%d octet%s", (int)f, f > 1 ? "s" : "");
      else 
        sf = String.format(Locale.US, "%d o", (int)f);
    }else if(f < 1000000)
      sf = String.format("%.02f", (f/SIZE_1KB)) + " Ko";
    else if(f < 1000000000)
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
      sb.append("#TIMESTAMP").append(sep).append("OPE").append(sep).append("PROVIDER").append(sep).append("MCC")
          .append(sep).append("MNC").append(sep).append("CID").append(sep)
      .append("LAC").append(sep).append("LAT").append(sep).append("LON")
          .append(sep).append("SAT").append(sep).append("SPD").append(sep).append("DIST").append(sep).append("PSC").append(sep)
      .append("TYPE").append(sep).append("NET").append(sep).append("LVL")
          .append(sep).append("ASU").append(sep).append("STR").append(sep)
      .append("PER").append(sep).append("RX").append(sep).append("TX").append(sep).append("DIR").append(sep)
      .append("IPv4").append(sep).append("IPv6")
      .append(sep).append("AREAS_NAME").append(sep).append("AREAS_LATITUDE").append(sep).append("AREAS_LONGITUDE").append(sep).append("AREAS_RADIUS")
      .append(sep).append("NEIGBORING(").append("OLD").append(sepNb).append("LAC")
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
  
  public boolean sameTowerInfo(final TowerInfo backup, final TowerInfo current) {
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_PROVIDER,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_PROVIDER)) 
      if(!backup.getProvider().equals(current.getProvider())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_OPERATOR,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_OPERATOR)) 
      if(!backup.getOperator().equals(current.getOperator())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_MCC,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_MNC)) 
      if(!(backup.getMCC() == current.getMCC())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_MNC,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_MNC)) 
      if(!(backup.getMNC() == current.getMNC())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_CELL_ID,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_CELL_ID)) 
      if(!(backup.getCellId() == current.getCellId())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_LAC,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_LAC)) 
      if(!(backup.getLac() == current.getLac())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_PSC,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_PSC)) 
      if(!(backup.getPsc() == current.getPsc())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_SIGNAL_STRENGTH,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_SIGNAL_STRENGTH)) {
      if(!(backup.getSignalStrength() == current.getSignalStrength())) return false;
      if(!(backup.getSignalStrengthPercent() == current.getSignalStrengthPercent())) return false;
    }
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_TYPE,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_TYPE)) 
      if(!backup.getType().equals(current.getType())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_ASU,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_ASU)) 
      if(!(backup.getAsu() == current.getAsu())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_LEVEL,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_LEVEL)) 
      if(!(backup.getLvl() == current.getLvl())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_NETOWRK_ID,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_NETWORK_ID)) {
      if(!(backup.getNetwork() == current.getNetwork())) return false;
      if(!backup.getNetworkName().equals(current.getNetworkName())) return false;
    }
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_SPEED,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_SPEED)) 
      if(!(backup.getSpeed() == current.getSpeed())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_DISTANCE,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_DISTANCE)) 
      if(!(backup.getDistance() == current.getDistance())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_SATELLITES,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_SATELLITES)) 
      if(!(backup.getSatellites() == current.getSatellites())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_DATA_RX_SPEED,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_DATA_RX_SPEED)) 
      if(!(backup.getMobileNetworkInfo().getRxSpeed() == current.getMobileNetworkInfo().getRxSpeed())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_DATA_TX_SPEED,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_DATA_TX_SPEED)) 
      if(!(backup.getMobileNetworkInfo().getTxSpeed() == current.getMobileNetworkInfo().getTxSpeed())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_DATA_DIRECTION,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_DATA_DIRECTION)) 
      if(!(backup.getMobileNetworkInfo().getDataActivity() == current.getMobileNetworkInfo().getDataActivity())) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_IPV4,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_IPV4)) 
      if(!(backup.getMobileNetworkInfo().getIp4Address().equals(current.getMobileNetworkInfo().getIp4Address()))) return false;
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_IPV6,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_IPV6)) 
      if(!(backup.getMobileNetworkInfo().getIp6Address().equals(current.getMobileNetworkInfo().getIp6Address()))) return false;

    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_AREAS,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_AREAS)) {
      if(current.getCurrentArea() != null && backup.getCurrentArea() != null) {
        if(!backup.getCurrentArea().getName().equals(current.getCurrentArea().getName())) return false;
        if(!(backup.getCurrentArea().getLatitude() == current.getCurrentArea().getLatitude())) return false;
        if(!(backup.getCurrentArea().getLongitude() == current.getCurrentArea().getLongitude())) return false;
        if(!(backup.getCurrentArea().getRadius() == current.getCurrentArea().getRadius())) return false;
      }
    }
    
    if(prefs.getBoolean(
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_KEY_NEIGHBORING,
        PreferencesRecorderFilters.PREFS_RECORDER_FILTERS_DEFAULT_NEIGHBORING)) {
      int size1 = backup.getNeighboring().size();
      int size2 = current.getNeighboring().size();
      if(size1 != size2) return false;
      boolean found = false;
      for(int i = 0; i < size1; ++i) {
        NeighboringInfo ni1 = backup.getNeighboring().get(i);
        for(int j = 0; j < size2; ++j) {
          NeighboringInfo ni2 = current.getNeighboring().get(j);
          if(ni1.toString().equals(ni2.toString())) {
            found = true;
            break;
          }
        }
        if(!found) return false;
      }
    }
    return true;
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
