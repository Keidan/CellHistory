package org.kei.android.phone.cellhistory.sensors;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 *******************************************************************************
 * @file AccelSensor.java
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
public class AccelSensor extends TimerTask implements SensorEventListener {
  public static final double NOISE = 2.0;
  private static final boolean ADAPTIVE_ACCEL_FILTER = true;
  // for calibration.
  private long            lastTime  = 0;
  //private final double[]  gravity   = new double[3];
  private SensorManager   manager   = null;
  protected Context       context   = null;
  private IAccelSensor    li        = null;
  private double          velocity  = 0.0;
  private long            timestamp = 0L;
  private Timer           update    = null;
  private int             delay     = 500;
  private double lastAccel[] = new double[3];
  private double accelFilter[] = new double[3];

  public AccelSensor(final Context context, final int delay,
      final IAccelSensor li) {
    this.context = context;
    this.li = li;
    this.delay = delay;
    manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
  }
  
  public void register() {
    lastTime = new Date().getTime();
    final Sensor accel = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    manager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
    update = new Timer("AccelSensor");
    update.scheduleAtFixedRate(this, 0, delay);
  }
  
  public void unregister() {
    manager.unregisterListener(this);
    if (update != null) {
      update.cancel();
      update = null;
    }
  }

  @Override
  public void run() {
    if (li != null) {
      double v = velocity;
      if (Double.isNaN(v) || v < NOISE)
        v = 0.0;
      li.accelUpdate(timestamp, v);
    }
  }
  
  @Override
  public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
    
  }
  
  @Override
  public void onSensorChanged(final SensorEvent event) {
    final long time = new Date().getTime();
    final long diff = Math.abs(time - lastTime);
    lastTime = time;
    /* remove the gravity */
    /*final float alpha = 0.8f;
    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
    final double linearX = event.values[0] - gravity[0];
    final double linearY = event.values[1] - gravity[1];
    final double linearZ = event.values[2] - gravity[2];

    final double x = linearX * diff;
    final double y = linearY * diff;
    final double z = linearZ * diff;
    velocity = Math.sqrt(x + y + z);
    timestamp = time;
    */
    double accelX = event.values[0];
    double accelY = event.values[1];
    double accelZ = event.values[2];
    // high pass filter
    double updateFreq = 30; // match this to your update speed
    double cutOffFreq = 0.9f;
    double RC = 1.0f / cutOffFreq;
    double dt = 1.0f / updateFreq;
    double filterConstant = RC / (dt + RC);
    double alpha = filterConstant; 
    double kAccelerometerMinStep = 0.033f;

    if(ADAPTIVE_ACCEL_FILTER)
    {
      double d = clamp(Math.abs(norm(accelFilter[0], accelFilter[1], accelFilter[2]) - norm(accelX, accelY, accelZ)) / kAccelerometerMinStep - 1.0f, 0.0f, 1.0f);
        alpha = d * filterConstant / NOISE + (1.0f - d) * filterConstant;
    }

    accelFilter[0] = (alpha * (accelFilter[0] + accelX - lastAccel[0]));
    accelFilter[1] = (alpha * (accelFilter[1] + accelY - lastAccel[1]));
    accelFilter[2] = (alpha * (accelFilter[2] + accelZ - lastAccel[2]));

    lastAccel[0] = accelX;
    lastAccel[1] = accelY;
    lastAccel[2] = accelZ;
    final double x = accelFilter[0] * diff;
    final double y = accelFilter[1] * diff;
    final double z = accelFilter[2] * diff;
    velocity = Math.sqrt(x + y + z);
    timestamp = time;
  }

  private double norm(final double x, final double y, final double z) {
    return Math.sqrt(x * x + y * y + z * z);
  }
  
  private double clamp(final double v, final double min, final double max) {
    if (v > max)
      return max;
    else if (v < min)
      return min;
    else
      return v;
  }
}
