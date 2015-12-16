package org.kei.android.phone.cellhistory.tasks;

import org.kei.android.phone.cellhistory.CellHistoryApp;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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
public class GpsTask implements LocationListener {
  /* context */
  private CellHistoryApp  app         = null;
  private Activity        activity    = null;
  private LocationManager lm          = null;
  private GpsListener     gpsListener = null;
  private GpsTaskEvent    lastEvent   = null;
  private int             oldDelay    = 0;
 
  public static enum GpsTaskEvent {
    OUT_OF_SERVICE, TEMPORARILY_UNAVAILABLE, COLD_START, UPDATE, ENABLED, DISABLED;
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
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, delay, 0f,
            this);
      }
    }
  }
 
  public void stop() {
    if (lm != null) {
      lm.removeUpdates(this);
      lm = null;
    }
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
      lastEvent = GpsTaskEvent.COLD_START;
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