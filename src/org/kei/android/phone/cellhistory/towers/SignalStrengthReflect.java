package org.kei.android.phone.cellhistory.towers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 *******************************************************************************
 * @file SignalStrengthReflect.java
 * @author Keidan
 * @date 04/12/2015
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
public class SignalStrengthReflect {
  
  private static Class<?> mCls;
  private static Method   mGetLevel;
  private static Method   mGetAsuLevel;
  private static Method   mGetDbm;
  private static Method   mGetLteLevel;
  private static Method   mGetGsmAsuLevel;
  private static Method   mGetLteAsuLevel;

  static {
    try {
      mCls = Class.forName("android.telephony.SignalStrength");
      mGetLevel = mCls.getMethod("getLevel");
      mGetAsuLevel = mCls.getMethod("getAsuLevel");
      mGetDbm = mCls.getMethod("getDbm");
      mGetLteLevel = mCls.getMethod("getLteLevel");
      mGetGsmAsuLevel = mCls.getMethod("getGsmAsuLevel");
      mGetLteAsuLevel = mCls.getMethod("getLteAsuLevel");
    } catch (final ClassNotFoundException e) {
    } catch (final NoSuchMethodException e) {
    }
  }
  private final Object    mObj;
  
  public SignalStrengthReflect(final Object obj) {
    mObj = obj;
  }
  
  public int getLevel() throws IllegalAccessException,
      InvocationTargetException {
    return ((Integer) mGetLevel.invoke(mObj)).intValue();
  }
  
  public int getAsuLevel() throws IllegalAccessException,
      InvocationTargetException {
    return ((Integer) mGetAsuLevel.invoke(mObj)).intValue();
  }
  
  public int getDbm() throws IllegalAccessException, InvocationTargetException {
    return ((Integer) mGetDbm.invoke(mObj)).intValue();
  }
  
  public int getLteLevel() throws IllegalAccessException, InvocationTargetException {
    return ((Integer) mGetLteLevel.invoke(mObj)).intValue();
  }
  
  public int getGsmAsuLevel() throws IllegalAccessException, InvocationTargetException {
    return ((Integer) mGetGsmAsuLevel.invoke(mObj)).intValue();
  }
  
  public int getLteAsuLevel() throws IllegalAccessException, InvocationTargetException {
    return ((Integer) mGetLteAsuLevel.invoke(mObj)).intValue();
  }
  
  
  public double getAsuLimit() throws IllegalAccessException, InvocationTargetException {
    if (getLteLevel() == 0) {
      return 31.0;
    } else {
      return 70.0;
    }
  }
}
