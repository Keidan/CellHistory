package org.kei.android.phone.cellhistory.contexts;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.towers.AreaInfo;
import org.kei.android.phone.cellhistory.towers.NeighboringInfo;
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
  
  public RecorderCtx() {
  }

  public void writeData(final String sep, final String sepNb, final String sepArea, final int limit,
      final CellHistoryApp ctx, final boolean detectChange, long records) {
    FilterCtx f = ctx.getFilterCtx();
    f.read(ctx.getApplicationContext());
    if (pw != null) {
      if (detectChange) {
        if (ctx.getBackupTowerInfo() == null
            || !sameTowerInfo(ctx.getBackupTowerInfo(), ctx.getGlobalTowerInfo(), f)) {
          ctx.setBackupTowerInfo(new TowerInfo(f, ctx.getGlobalTowerInfo()));
          if(format.equals(FORMAT_CSV)) frames.add(ctx.getGlobalTowerInfo().toString(sep, sepNb, sepArea));
          else if(format.equals(FORMAT_JSON)) frames.add(ctx.getGlobalTowerInfo().toJSON(indentation));
          else frames.add(ctx.getGlobalTowerInfo().toXML(indentation));
          counter++;
        }
      } else {
        if(format.equals(FORMAT_CSV)) frames.add(ctx.getGlobalTowerInfo().toString(sep, sepNb, sepArea));
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
      int len = frames.size();
      for (int i = 0; i < len; ++i) {
        String s = frames.get(i);
        if(i < len - 1) {
          s = s.substring(0, s.length() - 1);
          s += ",\n";
        }
        pw.print(s);
        size += s.length();
      }
    } else if(format.equals(FORMAT_CSV)) {
      for (final String s : frames) {
        pw.println(s);
        size += s.length() + 1;
      }
    } else if(format.equals(FORMAT_XML)) {
      for (final String s : frames) {
        String str = s;
        pw.println(str);
        size += str.length();
      }
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
      if(format.equals(FORMAT_XML)) {
        String ss = "</towers>";
        pw.print(ss);
        size += ss.length();
      } else if(format.equals(FORMAT_JSON)) {
        String ss = (indentation ? "  " : "") + "]" + ((indentation) ? "\n" : "");
        ss += "}";
        pw.print(ss);
        size += ss.length();
      }
      pw.close();
      pw = null;
    }
    frames.clear();
  }

  public void writeHeader(final String root, final String name, final String sep,
      final String sepNb, final String sepArea, final boolean deletePrev, final String format, final boolean indentation) throws Exception {
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
      .append(sep).append("AREAS(NAME").append(sepArea).append("LATITUDE").append(sepArea).append("LONGITUDE").append(sepArea).append("RADIUS")
      .append(sepArea).append("DISTANCE").append(sepArea).append("USED").append(")...")
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
      s = "<towers>";
      size += s.length() + 1;
      pw.println(s);
    } else if(format.equals(FORMAT_JSON)) {
      String ss = "";
      ss += "{" + ((indentation) ? "\n" : "");
      ss += (indentation ? "  " : "") + "\"towers\": [" + ((indentation) ? "\n" : "");
      pw.print(ss);
      size += ss.length();
    }
  }
  
  public boolean sameTowerInfo(final TowerInfo backup, final TowerInfo current, final FilterCtx f) {
    if(f.getProvider().allowChange && f.getProvider().allowSave) 
      if(!backup.getProvider().equals(current.getProvider())) return false;
    if(f.getOperator().allowChange && f.getOperator().allowSave) 
      if(!backup.getOperator().equals(current.getOperator())) return false;
    if(f.getMCC().allowChange && f.getMCC().allowSave) 
      if(!(backup.getMCC() == current.getMCC())) return false;
    if(f.getMNC().allowChange && f.getMNC().allowSave) 
      if(!(backup.getMNC() == current.getMNC())) return false;
    if(f.getCellID().allowChange && f.getCellID().allowSave) 
      if(!(backup.getCellId() == current.getCellId())) return false;
    if(f.getLAC().allowChange && f.getLAC().allowSave) 
      if(!(backup.getLac() == current.getLac())) return false;
    if(f.getPSC().allowChange && f.getPSC().allowSave) 
      if(!(backup.getPsc() == current.getPsc())) return false;
    if(f.getSignalStrength().allowChange && f.getSignalStrength().allowSave) {
      if(!(backup.getSignalStrength() == current.getSignalStrength())) return false;
      if(!(backup.getSignalStrengthPercent() == current.getSignalStrengthPercent())) return false;
    }
    if(f.getType().allowChange && f.getType().allowSave) 
      if(!backup.getType().equals(current.getType())) return false;
    if(f.getASU().allowChange && f.getASU().allowSave) 
      if(!(backup.getAsu() == current.getAsu())) return false;
    if(f.getLevel().allowChange && f.getLevel().allowSave) 
      if(!(backup.getLvl() == current.getLvl())) return false;
    if(f.getNetworkId().allowChange && f.getNetworkId().allowSave) {
      if(!(backup.getNetwork() == current.getNetwork())) return false;
      if(!backup.getNetworkName().equals(current.getNetworkName())) return false;
    }
    if(f.getSpeed().allowChange && f.getSpeed().allowSave) 
      if(!(backup.getSpeed() == current.getSpeed())) return false;
    if(f.getGeolocation().allowChange && f.getGeolocation().allowSave) 
      if(!(backup.getLatitude() == current.getLatitude()) || !(backup.getLongitude() == current.getLongitude())) return false;
    if(f.getDistance().allowChange && f.getDistance().allowSave) 
      if(!(backup.getDistance() == current.getDistance())) return false;
    if(f.getSatellites().allowChange && f.getSatellites().allowSave) 
      if(!(backup.getSatellites() == current.getSatellites())) return false;
    if(f.getDataRxSpeed().allowChange && f.getDataRxSpeed().allowSave) 
      if(!(backup.getMobileNetworkInfo().getRxSpeed() == current.getMobileNetworkInfo().getRxSpeed())) return false;
    if(f.getDataTxSpeed().allowChange && f.getDataTxSpeed().allowSave)
      if(!(backup.getMobileNetworkInfo().getTxSpeed() == current.getMobileNetworkInfo().getTxSpeed())) return false;
    if(f.getDataDirection().allowChange && f.getDataDirection().allowSave) 
      if(!(backup.getMobileNetworkInfo().getDataActivity() == current.getMobileNetworkInfo().getDataActivity())) return false;
    if(f.getIPv4().allowChange && f.getIPv4().allowSave) 
      if(!(backup.getMobileNetworkInfo().getIp4Address().equals(current.getMobileNetworkInfo().getIp4Address()))) return false;
    if(f.getIPv6().allowChange && f.getIPv6().allowSave) 
      if(!(backup.getMobileNetworkInfo().getIp6Address().equals(current.getMobileNetworkInfo().getIp6Address()))) return false;

    if(f.getAreas().allowChange && f.getAreas().allowSave) {
      int size1 = backup.getAreas().size();
      int size2 = current.getAreas().size();
      if(size1 != size2) return false;
      boolean found = false;
      for(int i = 0; i < size1; ++i) {
        AreaInfo ni1 = backup.getAreas().get(i);
        for(int j = 0; j < size2; ++j) {
          AreaInfo ni2 = current.getAreas().get(j);
          if(ni1.toString(",").equals(ni2.toString(","))) {
            found = true;
            break;
          }
        }
        if(!found) return false;
      }
    }
    
    if(f.getNeighboring().allowChange && f.getNeighboring().allowSave) {
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
