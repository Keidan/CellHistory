package org.kei.android.phone.cellhistory.activities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.kei.android.atk.view.EffectActivity;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.prefs.Preferences;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;

/**
 *******************************************************************************
 * @file LogActivity.java
 * @author Keidan
 * @date 09/12/2015
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
public class LogActivity extends EffectActivity {

  private ShareActionProvider shareActionProvider = null;
  private CircularFifoBuffer  cfb                 = null;
  private String              content             = null;
  private ListView            logs                = null;
  private boolean             exit                = false;
  private CellHistoryApp      app                 = null;

  @SuppressWarnings("unchecked")
  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    app = CellHistoryApp.getApp(this);
    setContentView(R.layout.activity_logs);
    logs = (ListView) findViewById(R.id.logs);
    
    cfb = (CircularFifoBuffer) CellHistoryApp.getApp(this).getLogBuffer();
    final String[] lines = (String[]) cfb.toArray(new String[] {});
    final StringBuilder sb = new StringBuilder();
    for (final String s : lines)
      sb.append(s).append("\n");
    content = sb.toString();
    logs.setAdapter(null);
    logs.setAdapter(new ArrayAdapter<String>(this,
        R.layout.listview_simple_row, R.id.label1, lines));
  }
  
  public void onResume() {
    super.onResume();
    if (!app.getRecorderCtx().isRunning())
      app.getNfyHelper().hide();
  }
  
  public void onBackPressed() {
    exit = true;
    super.onBackPressed();
  }
  
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

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    getMenuInflater().inflate(R.menu.activity_logs, menu);
    shareActionProvider = (ShareActionProvider) menu.findItem(R.id.sharing)
        .getActionProvider();
    shareActionProvider.setShareIntent(getDefaultShareIntent());
    return super.onCreateOptionsMenu(menu);
  }
  
  /** Returns a share intent */
  private Intent getDefaultShareIntent() {
    String version = "?.??";
    final String name = getResources().getString(R.string.app_name);
    final String date = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a", Locale.US)
    .format(new Date());
    try {
      final PackageInfo pInfo = getPackageManager().getPackageInfo(
          getPackageName(), 0);
      version = pInfo.versionName;
    } catch (final Exception e) {
    }
    final Intent intent = new Intent(Intent.ACTION_SEND);
    intent.setType("text/plain");
    intent.putExtra(Intent.EXTRA_SUBJECT, name + " v" + version + " logs "
        + date);
    intent.putExtra(Intent.EXTRA_TEXT, content);
    return intent;
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    final int id = item.getItemId();
    if (id == R.id.action_clear) {
      content = "";
      cfb.clear();
      logs.setAdapter(null);
      /* reload the contents */
      if (shareActionProvider != null)
        shareActionProvider.setShareIntent(getDefaultShareIntent());
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
