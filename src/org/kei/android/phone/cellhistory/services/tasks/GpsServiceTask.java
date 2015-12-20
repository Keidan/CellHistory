package org.kei.android.phone.cellhistory.services.tasks;

import java.util.Iterator;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.prefs.PreferencesTimers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
 * @file GpsServiceTask.java
 * @author Keidan
 * @date 19/12/2015
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
public class GpsServiceTask implements LocationListener, Listener  {
  public static final String NOTIFICATION                  = "org.kei.android.phone.cellhistory.fragments";
  public static final String EVENT                         = "event";
  public static final int    EVENT_DISABLED                = 0;
  public static final int    EVENT_ENABLED                 = 1;
  public static final int    EVENT_CONNECTED               = 2;
  public static final int    EVENT_UPDATE                  = 3;
  public static final int    EVENT_WAIT_FOR_SATELLITES     = 4;
  public static final int    EVENT_OUT_OF_SERVICE          = 5;
  public static final int    EVENT_TEMPORARILY_UNAVAILABLE = 6;
  private CellHistoryApp     app                           = null;
  private Context            context                       = null;
  private SharedPreferences  prefs                         = null;
  private LocationManager    lm                            = null;
  private boolean            disabled                      = false;
  
  public GpsServiceTask(final Context context, final CellHistoryApp app, final SharedPreferences prefs) {
    this.context = context;
    this.prefs = prefs;
    this.app = app;
  }
  
  public void register() {
    unregister();
    if (lm == null) {
      lm = (LocationManager) context
          .getSystemService(Context.LOCATION_SERVICE);
      if (lm != null) {
        lm.addGpsStatusListener(this);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, Integer.parseInt(prefs
            .getString(PreferencesTimers.PREFS_KEY_TIMERS_TASK_GPS,
                PreferencesTimers.PREFS_DEFAULT_TIMERS_TASK_GPS)), 0f,
            this);
      }
    }
  }
  
  public void unregister() {
    if (lm != null) {
      lm.removeUpdates(this);
      lm.removeGpsStatusListener(this);
      lm = null;
    }
  }
  
  private void publishEvent(int result) {
    Intent intent = new Intent(NOTIFICATION);
    intent.putExtra(EVENT, result);
    context.sendBroadcast(intent);
  }

  public void onGpsStatusChanged(int event) {
    int n = -1;
    if(disabled) {
      disabled = false;
      return;
    }
    if(lm == null) return;
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
        publishEvent(EVENT_UPDATE);
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
    publishEvent(EVENT_UPDATE);
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
          reset();
          CellHistoryApp.addLog(app, "onStatusChanged(" + provider + ", OUT_OF_SERVICE)");
          publishEvent(EVENT_OUT_OF_SERVICE);
          break;
        case LocationProvider.TEMPORARILY_UNAVAILABLE:
          reset();
          CellHistoryApp.addLog(app, "onStatusChanged(" + provider + ", TEMPORARILY_UNAVAILABLE)");
          publishEvent(EVENT_TEMPORARILY_UNAVAILABLE);
          break;
      }
    }
  }

  @Override
  public void onProviderEnabled(final String provider) {
    if (provider.equals(LocationManager.GPS_PROVIDER)) {
      CellHistoryApp.addLog(app, "onProviderEnabled(" + provider + ")");
      disabled = false;
      reset();
      publishEvent(EVENT_ENABLED);
      publishEvent(EVENT_WAIT_FOR_SATELLITES);
    }
  }
  
  private void reset() {
    app.getGlobalTowerInfo().lock();
    try {
      app.getGlobalTowerInfo().setSatellites(0);
      app.getGlobalTowerInfo().setSpeed(0.0);
      app.getGlobalTowerInfo().setDistance(0.0);
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
  }

  @Override
  public void onProviderDisabled(final String provider) {
    if (provider.equals(LocationManager.GPS_PROVIDER)) {
      CellHistoryApp.addLog(app, "onProviderDisabled(" + provider + ")");
      disabled = true;
      reset();
      publishEvent(EVENT_DISABLED);
    }
  }
}
