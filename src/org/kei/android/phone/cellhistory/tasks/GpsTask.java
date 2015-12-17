package org.kei.android.phone.cellhistory.tasks;

import java.util.Iterator;

import org.kei.android.phone.cellhistory.CellHistoryApp;

import android.app.Activity;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.GpsStatus.Listener;
import android.os.Bundle;

/**
 *******************************************************************************
 * @file ProviderTask.java
 * @author Keidan
 * @date 12/12/2015
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
public class GpsTask implements LocationListener, Listener {
  /* context */
  private CellHistoryApp  app         = null;
  private Activity        activity    = null;
  private LocationManager lm          = null;
  private GpsListener     gpsListener = null;
  private GpsTaskEvent    lastEvent   = GpsTaskEvent.WAIT_FOR_SATELLITES;
  private int             oldDelay    = 0;
  
  public static enum GpsTaskEvent {
    OUT_OF_SERVICE, TEMPORARILY_UNAVAILABLE, WAIT_FOR_SATELLITES, UPDATE, ENABLED, DISABLED;
  };

  public interface GpsListener {
    public void gpsUpdate(GpsTaskEvent event);
  }

  public GpsTask(final CellHistoryApp app) {
    this.app = app;
  }
 
  public void setGpsListener(final GpsListener li) {
    this.gpsListener = li;
    if(lastEvent != null && li != null) {
      li.gpsUpdate(lastEvent);
      lastEvent = null;
    }
  }

  public void initialize(final Activity activity) {
    this.activity = activity;
  }

  public void start(final int delay) {
    if(lm != null && oldDelay == delay) {
      stop();
      oldDelay = delay;
    }
    if (lm == null) {
      lm = (LocationManager) activity
          .getSystemService(Context.LOCATION_SERVICE);
      if (lm != null) {
        lm.addGpsStatusListener(this);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, delay, 0f,
            this);
      }
    }
  }
 
  public void stop() {
    if (lm != null) {
      lm.removeUpdates(this);
      lm.removeGpsStatusListener(this);
      lm = null;
    }
  }
  
  public void onGpsStatusChanged(int event) {
    int n = -1;
    final GpsStatus status = lm.getGpsStatus(null);
    if(status != null) {
      final Iterable<GpsSatellite> sats = status.getSatellites();
      if(sats != null) {
        final Iterator<GpsSatellite> itr = sats.iterator();
        if(itr != null) {
          n = 0;
          while (itr.hasNext()) { 
            GpsSatellite gs = itr.next();
            if(gs.usedInFix()) n++;
          }
        }
      }
    }
    if(n != -1) {
      if(n != 0) {
        lastEvent = GpsTaskEvent.UPDATE;
        if (gpsListener != null) gpsListener.gpsUpdate(lastEvent);
      }
      CellHistoryApp.addLog(app, "GPS: Satellites: " + n);
      app.getGlobalTowerInfo().lock();
      try {
        app.getGlobalTowerInfo().setSatellites(n);
      } finally {
        app.getGlobalTowerInfo().unlock();
      }
    } else
      CellHistoryApp.addLog(app, "GPS: No satellites found");
  }
 
  @Override
  public void onLocationChanged(final Location location) {
    CellHistoryApp.addLog(app, location);
    lastEvent = GpsTaskEvent.UPDATE;
    if (gpsListener != null) gpsListener.gpsUpdate(lastEvent);
    double speed = 0.0;
    final Location loc1 = new Location("");
    app.getGlobalTowerInfo().lock();
    try {
      speed = location.getSpeed();
      app.getGlobalTowerInfo().setSpeed(speed);
      if (!Double.isNaN(app.getGlobalTowerInfo().getLatitude())
          && !Double.isNaN(app.getGlobalTowerInfo().getLongitude())) {
        loc1.setLatitude(app.getGlobalTowerInfo().getLatitude());
        loc1.setLongitude(app.getGlobalTowerInfo().getLongitude());
        app.getGlobalTowerInfo().setDistance(loc1.distanceTo(location));
        CellHistoryApp.addLog(app, "New distance: "
            + app.getGlobalTowerInfo().getDistance() + " m.");
      }
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
  }

  @Override
  public void onStatusChanged(final String provider, final int status,
      final Bundle extras) {
    if (provider.equals(LocationManager.GPS_PROVIDER)) {
      switch (status) {
        case LocationProvider.OUT_OF_SERVICE:
          CellHistoryApp.addLog(app, "onStatusChanged(" + provider + ", OUT_OF_SERVICE)");
          lastEvent = GpsTaskEvent.OUT_OF_SERVICE;
          if (gpsListener != null) gpsListener.gpsUpdate(lastEvent);
          break;
        case LocationProvider.TEMPORARILY_UNAVAILABLE:
          CellHistoryApp.addLog(app, "onStatusChanged(" + provider + ", TEMPORARILY_UNAVAILABLE)");
          lastEvent = GpsTaskEvent.TEMPORARILY_UNAVAILABLE;
          if (gpsListener != null) gpsListener.gpsUpdate(lastEvent);
          break;
      }
    }
  }

  @Override
  public void onProviderEnabled(final String provider) {
    if (provider.equals(LocationManager.GPS_PROVIDER)) {
      CellHistoryApp.addLog(app, "onProviderEnabled(" + provider + ")");
      lastEvent = GpsTaskEvent.ENABLED;
      if (gpsListener != null) gpsListener.gpsUpdate(lastEvent);
      lastEvent = GpsTaskEvent.WAIT_FOR_SATELLITES;
      if (gpsListener != null) gpsListener.gpsUpdate(lastEvent);
    }
  }

  @Override
  public void onProviderDisabled(final String provider) {
    if (provider.equals(LocationManager.GPS_PROVIDER)) {
      CellHistoryApp.addLog(app, "onProviderDisabled(" + provider + ")");
      lastEvent = GpsTaskEvent.DISABLED;
      if (gpsListener != null) gpsListener.gpsUpdate(lastEvent);
    }
  }
}