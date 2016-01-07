package org.kei.android.phone.cellhistory.activities;

import java.util.ArrayList;
import java.util.List;
import org.kei.android.atk.view.EffectActivity;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.prefs.Preferences;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 *******************************************************************************
 * @file AreasActivity.java
 * @author Keidan
 * @date 07/01/2016
 * @par Project CellHistory
 *
 * @par Copyright 2015-2016 Keidan, all right reserved
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
public class AreasActivity extends EffectActivity {
  
  private ListView       areas = null;
  private boolean        exit  = false;
  private CellHistoryApp app   = null;
  
  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    app = CellHistoryApp.getApp(this);
    setContentView(R.layout.activity_areas);
    areas = (ListView) findViewById(R.id.areas);

    final List<String> lareas = new ArrayList<String>();

    areas.setAdapter(null);
    areas.setAdapter(new ArrayAdapter<String>(this,
        R.layout.listview_simple_row, R.id.label1, lareas));
  }

  @Override
  public void onResume() {
    super.onResume();
    if (!app.getRecorderCtx().isRunning())
      app.getNfyHelper().hide();
  }

  @Override
  public void onBackPressed() {
    exit = true;
    super.onBackPressed();
  }

  @Override
  public void onPause() {
    super.onPause();
    if (!app.getRecorderCtx().isRunning() && !exit)
      app.notificationShow();
  }

  @Override
  protected boolean exitOnDoubleBack() {
    return false;
  }
  
  @Override
  public void themeUpdate() {
    Preferences.performTheme(this);
  }
}
