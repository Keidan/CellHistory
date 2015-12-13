package org.kei.android.phone.cellhistory.adapters;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 *******************************************************************************
 * @file ScreenSlidePagerAdapter.java
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
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
  private final List<Fragment> fragments;
  
  public ScreenSlidePagerAdapter(final FragmentManager fm, List<Fragment> fragments) {
    super(fm);
    this.fragments = fragments;
  }
  
  @Override
  public Fragment getItem(int position) {
      return fragments.get(position);
  }

  @Override
  public int getCount() {
      return fragments.size();
  }
  
  @Override
  public CharSequence getPageTitle(final int position) {
    switch (position) {
      case 0:
        return "Tower";
      case 1:
        return "Geolocation";
      case 2:
        return "Neighboring";
      case 3:
        return "Recorder";
      default:
        return null;
    }
  }
}
