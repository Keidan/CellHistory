package org.kei.android.phone.cellhistory.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.kei.android.atk.utils.Tools;
import org.kei.android.atk.view.EffectActivity;
import org.kei.android.atk.view.chooser.FileChooser;
import org.kei.android.atk.view.chooser.FileChooserActivity;
import org.kei.android.atk.view.dialog.DialogHelper;
import org.kei.android.atk.view.dialog.DialogResult;
import org.kei.android.atk.view.dialog.IDialog;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.prefs.Preferences;
import org.kei.android.phone.cellhistory.towers.AreaInfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

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
public class AreasActivity extends EffectActivity implements OnClickListener, OnItemClickListener, OnItemLongClickListener, IDialog {
  public static final String ACTION_IMPORT = "ACTION_IMPORT";
  public static final String ACTION_EXPORT = "ACTION_EXPORT";
  private ListView       areas = null;
  private boolean        exit  = false;
  private CellHistoryApp app   = null;
  private List<AreaInfo> listAreas = null;
  
  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    app = CellHistoryApp.getApp(this);
    setContentView(R.layout.activity_areas);
    areas = (ListView) findViewById(R.id.areas);
    ((Button)findViewById(R.id.buttonArea)).setOnClickListener(this);
    reloadAdapter();
    areas.setOnItemClickListener(this);
    areas.setOnItemLongClickListener(this);
    areas.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
  }
  
  private void reloadAdapter() {
    if(listAreas != null) listAreas.clear();
    areas.setAdapter(null);
    listAreas = app.getSQL().getAreas();
    areas.setAdapter(new ArrayAdapter<AreaInfo>(this,
        R.layout.row_listarea, R.id.label1, listAreas));
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
  public boolean onCreateOptionsMenu(final Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.activity_area, menu);
    return super.onCreateOptionsMenu(menu);
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
  public void onClick(View v) {
    DialogHelper.showCustomDialog(this, R.layout.dialog_add_area,
        getString(R.string.titleArea), this, null, R.id.buttonOk, R.id.buttonCancel);
  }
  
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    DialogHelper.showCustomDialog(this, R.layout.dialog_add_area,
        getString(R.string.titleArea), this, 
        areas.getAdapter().getItem(position), R.id.buttonOk, R.id.buttonCancel);
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> parent, View view,
      int position, long id) {
    final AreaInfo ai = (AreaInfo)areas.getAdapter().getItem(position);
    Tools.showConfirmDialog(this, "Delete", "Are you sure you want to delete area '" + ai.getName() + "' ?", 
        new android.view.View.OnClickListener() {
        @Override
        public void onClick(final View v) {
          app.getSQL().removeAreaWithID(ai);
          reloadAdapter();
        }
    }, null);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    final int id = item.getItemId();
    if (id == R.id.action_import) {
      Map<String, String> extra = new HashMap<String, String>();
      extra.put(FileChooser.FILECHOOSER_TYPE_KEY, "" + FileChooser.FILECHOOSER_TYPE_FILE_ONLY);
      extra.put(FileChooser.FILECHOOSER_TITLE_KEY, "Load");    
      extra.put(FileChooser.FILECHOOSER_MESSAGE_KEY, "Load this file:? ");
      extra.put(FileChooser.FILECHOOSER_SHOW_KEY, "" + FileChooser.FILECHOOSER_SHOW_FILE_AND_DIRECTORY);
      extra.put(FileChooser.FILECHOOSER_USER_MESSAGE, ACTION_IMPORT);
      Tools.switchToForResult(this, FileChooserActivity.class,
          extra, FileChooserActivity.FILECHOOSER_SELECTION_TYPE_FILE);
      return true;
    } else if (id == R.id.action_export) {
      Map<String, String> extra = new HashMap<String, String>();
      extra.put(FileChooser.FILECHOOSER_TYPE_KEY, "" + FileChooser.FILECHOOSER_TYPE_DIRECTORY_ONLY);
      extra.put(FileChooser.FILECHOOSER_TITLE_KEY, "Save");    
      extra.put(FileChooser.FILECHOOSER_MESSAGE_KEY, "Select this directory:? ");
      extra.put(FileChooser.FILECHOOSER_SHOW_KEY, "" + FileChooser.FILECHOOSER_SHOW_DIRECTORY_ONLY);
      extra.put(FileChooser.FILECHOOSER_USER_MESSAGE, ACTION_EXPORT);
      Tools.switchToForResult(this, FileChooserActivity.class,
          extra, FileChooserActivity.FILECHOOSER_SELECTION_TYPE_FILE);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    // Check which request we're responding to
    if (requestCode == FileChooserActivity.FILECHOOSER_SELECTION_TYPE_FILE) {
      if (resultCode == RESULT_OK) {
        final String selectedFile = data
            .getStringExtra(FileChooserActivity.FILECHOOSER_SELECTION_KEY);
        final String action = data.getStringExtra(FileChooserActivity.FILECHOOSER_USER_MESSAGE);
        if(action.equals(ACTION_IMPORT)) {
          if(selectedFile.isEmpty()) {
            Tools.showAlertDialog(this, "Error", "Empty source file!");
            return;
          }
          File fsrc = new File(selectedFile);
          if(!fsrc.isFile()) {
            Tools.showAlertDialog(this, "Error", "The file is not a valid file!");
            return;
          } else if(!fsrc.canRead()) {
            Tools.showAlertDialog(this, "Error", "Unable to read the areas file!");
            return;
          }
          try {
            String line = null;
            BufferedReader br = new BufferedReader(new FileReader(selectedFile));
            List<AreaInfo> list = new ArrayList<AreaInfo>();
            int n = 0;
            while((line = br.readLine()) != null) {
              n++;
              if(line.isEmpty() || line.startsWith("#")) continue;
              StringTokenizer token = new StringTokenizer(line, ";");
              try {
                AreaInfo ai = new AreaInfo();
                ai.setName(token.nextToken());
                ai.setLatitude(Double.parseDouble(token.nextToken()));
                ai.setLongitude(Double.parseDouble(token.nextToken()));
                ai.setRadius(Double.parseDouble(token.nextToken()));
                list.add(ai);
              } catch(Exception e) {
                String err = "Invalid line (" + n + "): " + e.getMessage();
                Tools.toast(this, R.drawable.ic_launcher, err);
                Log.e(getClass().getSimpleName(), err, e);
              }
            }
            br.close();
            if(!list.isEmpty()) {
              app.getSQL().removeAll();
              for(AreaInfo ai : list)
                app.getSQL().insertArea(ai);
              list.clear();
              reloadAdapter();
            }
            Tools.toast(this, R.drawable.ic_launcher, "Import success.");
          } catch(Exception e) {
            Tools.toast(this, R.drawable.ic_launcher,
                "Unable to import the areas: " + e.getMessage());
            Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
          }
        } else if(action.equals(ACTION_EXPORT)) {
          try {
            File file = new File(new File(selectedFile), new SimpleDateFormat(
                "yyyyMMdd_hhmmssa'_" + getResources().getString(R.string.areas_file_name) + ".csv'", Locale.US).format(new Date()));
            PrintWriter pw = new PrintWriter(file);
            pw.println("#name;latitude;longitude;radius");
            for(int i = 0; i < listAreas.size(); ++i) {
              AreaInfo ai = listAreas.get(i);
              pw.println(ai.toString(";"));
            }
            pw.flush();
            pw.close();

            Tools.toast(this, R.drawable.ic_launcher, "The file '" + file.getName() + "' has been created.", Tools.TOAST_LENGTH_LONG);
          } catch(Exception e) {
            Tools.toast(this, R.drawable.ic_launcher,
                "Unable to export the areas: " + e.getMessage());
            Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
          }
        }
      }
    }
  }

  @Override
  public DialogResult doAction(View owner, Object model) {
    EditText txtAreaName = (EditText)owner.findViewById(R.id.txtAreaName);
    EditText txtAreaLatitude = (EditText)owner.findViewById(R.id.txtAreaLatitude);
    EditText txtAreaLongitude = (EditText)owner.findViewById(R.id.txtAreaLongitude);
    EditText txtAreaRadius = (EditText)owner.findViewById(R.id.txtAreaRadius);
    
    if (!isValidString(txtAreaName.getText().toString())) {
      Tools.showAlertDialog(owner.getContext(), "Error", "Invalid area name");
      return DialogResult.ERROR;
    }
    if (!isValidDouble(txtAreaLatitude.getText().toString())) {
      Tools.showAlertDialog(owner.getContext(), "Error", "Invalid area latitude");
      return DialogResult.ERROR;
    }
    if (!isValidDouble(txtAreaLongitude.getText().toString())) {
      Tools.showAlertDialog(owner.getContext(), "Error", "Invalid area longitude");
      return DialogResult.ERROR;
    }
    if (!isValidDouble(txtAreaRadius.getText().toString())) {
      Tools.showAlertDialog(owner.getContext(), "Error", "Invalid area radius");
      return DialogResult.ERROR;
    }

    AreaInfo ai = (AreaInfo)model;
    if(model == null) ai = new AreaInfo();
    ai.setName(txtAreaName.getText().toString());
    ai.setLatitude(Double.parseDouble(txtAreaLatitude.getText().toString()));
    ai.setLongitude(Double.parseDouble(txtAreaLongitude.getText().toString()));
    ai.setRadius(Double.parseDouble(txtAreaRadius.getText().toString()));

    if(model == null) {
      for(int i = 0; i < listAreas.size(); ++i) {
        AreaInfo aiItem = listAreas.get(i);
        if(aiItem.getName().equals(ai.getName())) {
          Tools.showAlertDialog(this, "Error", "Area '" + ai.getName() + "' already found!");
          break;
        }
      }
      app.getSQL().insertArea(ai);
    } else
      app.getSQL().updateArea(ai);
    reloadAdapter();
    return DialogResult.SUCCESS;
  }

  @Override
  public void doLoad(View owner, Object model) {
    EditText txtAreaName = (EditText)owner.findViewById(R.id.txtAreaName);
    EditText txtAreaLatitude = (EditText)owner.findViewById(R.id.txtAreaLatitude);
    EditText txtAreaLongitude = (EditText)owner.findViewById(R.id.txtAreaLongitude);
    EditText txtAreaRadius = (EditText)owner.findViewById(R.id.txtAreaRadius);
    if(model == null) {
      txtAreaName.setEnabled(true);
      txtAreaName.setText(allocateName());
      app.getGlobalTowerInfo().lock();
      try {
        txtAreaLatitude.setText(""+app.getGlobalTowerInfo().getCurrentLocation().getLatitude());
        txtAreaLongitude.setText(""+app.getGlobalTowerInfo().getCurrentLocation().getLongitude());
        txtAreaRadius.setText(""+AreaInfo.DEFAULT_RADIUS);
      } finally {
        app.getGlobalTowerInfo().unlock();
      }
    } else {
      txtAreaName.setEnabled(false);
      AreaInfo ai = (AreaInfo)model;
      txtAreaName.setText(ai.getName());
      txtAreaLatitude.setText(""+ai.getLatitude());
      txtAreaLongitude.setText(""+ai.getLongitude());
      txtAreaRadius.setText(""+ai.getRadius());
    }
  }
  
  private String allocateName() {
    String sarea = "Area ";
    if(listAreas.isEmpty()) return sarea + "1";
    int index = 1, z = 0;
    for(int i = 0; i < listAreas.size(); ++i) {
      AreaInfo ai = listAreas.get(i);
      String n = ai.getName();
      if(n.startsWith(sarea)) {
        try {
          z = Integer.parseInt(n.substring(n.indexOf(' ') + 1));
          if(z >= index) index = z + 1;
        } catch(Exception e) {
        }
      }
    }
    return sarea + index;
  }

  private static boolean isValidString(final String str) {
    return (str != null && !str.equals("") && !str.equals("null"));
  }

  private static boolean isValidDouble(final String str) {
    try {
      Double.parseDouble(str);
      return true;
    } catch (final Exception e) {
      return false;
    }
  }

}
