package org.kei.android.phone.cellhistory.utils;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

/**
 *******************************************************************************
 * @file ZoomOutPageTransformer.java
 * @author http://developer.android.com/training/animation/screen-slide.html
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
public class ZoomOutPageTransformer implements PageTransformer {
  private static final float MIN_SCALE = 0.85f;
  private static final float MIN_ALPHA = 0.5f;

  public void transformPage(View view, float position) {
      int pageWidth = view.getWidth();
      int pageHeight = view.getHeight();

      if (position < -1) { // [-Infinity,-1)
          // This page is way off-screen to the left.
          view.setAlpha(0);

      } else if (position <= 1) { // [-1,1]
          // Modify the default slide transition to shrink the page as well
          float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
          float vertMargin = pageHeight * (1 - scaleFactor) / 2;
          float horzMargin = pageWidth * (1 - scaleFactor) / 2;
          if (position < 0) {
              view.setTranslationX(horzMargin - vertMargin / 2);
          } else {
              view.setTranslationX(-horzMargin + vertMargin / 2);
          }

          // Scale the page down (between MIN_SCALE and 1)
          view.setScaleX(scaleFactor);
          view.setScaleY(scaleFactor);

          // Fade the page relative to its size.
          view.setAlpha(MIN_ALPHA +
                  (scaleFactor - MIN_SCALE) /
                  (1 - MIN_SCALE) * (1 - MIN_ALPHA));

      } else { // (1,+Infinity]
          // This page is way off-screen to the right.
          view.setAlpha(0);
      }
  }
}
