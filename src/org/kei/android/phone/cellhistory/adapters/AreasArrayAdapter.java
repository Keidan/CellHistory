package org.kei.android.phone.cellhistory.adapters;

import java.util.List;

import org.kei.android.atk.utils.fx.Color;
import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.towers.AreaInfo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 *******************************************************************************
 * @file AreasArrayAdapter.java
 * @author Keidan
 * @date 16/01/2016
 * @par Project ATK
 *
 * @par Copyright 2016 Keidan, all right reserved
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
public class AreasArrayAdapter extends ArrayAdapter<AreaInfo> implements
    OnClickListener {
  
  private final Context  c;
  private final int      id;
  private final int      defaultColor;
  private int            color_green_dark = Color.BLACK;
  private CellHistoryApp app              = null;
  private String            unit_m                       = "";
  private String            unit_km                      = "";
  
  public AreasArrayAdapter(final int defaultColor, final Context context,
      final int textViewResourceId, final List<AreaInfo> objects) {
    super(context, textViewResourceId, objects);
    this.defaultColor = defaultColor;
    c = context;
    id = textViewResourceId;
    Resources resources = context.getResources();
    color_green_dark = resources.getColor(Color.GREEN_DARK);
    app = CellHistoryApp.getApp(context);
    unit_m = resources.getString(R.string.unit_m);
    unit_km = resources.getString(R.string.unit_km);
  }
  
  @Override
  public View getView(final int position, final View convertView,
      final ViewGroup parent) {
    View v = convertView;
    if (v == null) {
      final LayoutInflater vi = (LayoutInflater) c
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      v = vi.inflate(id, null);
    }
    final AreaInfo o = getItem(position);
    if (o != null) {
      final TextView name = (TextView) v.findViewById(R.id.name);
      final TextView distance = (TextView) v.findViewById(R.id.distance);
      if (o.isTitle()) {
        updateTextViewColor(name, "NAME", defaultColor);
        updateTextViewColor(distance, "DISTANCE", defaultColor);
      } else {
        int color = defaultColor;
        if (o.isUsed())
          color = color_green_dark;
        updateTextViewColor(name, o.getName(), color);
        distance.setTextColor(color);
        if (o.getDistance() > 1000) {
          distance.setText(String.format("%.02f", o.getDistance() / 1000) + " " + unit_km);
        }
        else {
          distance.setText(String.format("%.02f", o.getDistance()) + " " + unit_m);
        }
        name.setOnClickListener(this);
      }
    }
    return v;
  }

  private void updateTextViewColor(final TextView tv, final String s,
      final int color) {
    if (tv != null) {
      tv.setText(s);
      tv.setTextColor(color);
    }
  }
  
  @Override
  public void onClick(final View v) {
    final TextView tv = (TextView) v;
    final String name = tv.getText().toString();
    if (name != null && !name.equals(AreaInfo.UNKNOWN) && !name.isEmpty()) {
      String currentAreaPosition = "";
      
      app.getGlobalTowerInfo().lock();
      try {
        for (final AreaInfo ai : app.getGlobalTowerInfo().getAreas())
          if (ai.getName().equals(name)) {
            currentAreaPosition = ai.getLatitude() + "," + ai.getLongitude();
            break;
          }
      } finally {
        app.getGlobalTowerInfo().unlock();
      }
      final String encodedQuery = Uri.encode(currentAreaPosition + "(" + name
          + ")");
      final Uri uri = Uri.parse("geo:" + currentAreaPosition + "?q="
          + encodedQuery + "&z=16&iwloc=A");
      final Intent mapViewIntent = new Intent(Intent.ACTION_VIEW, uri);
      mapViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      c.startActivity(mapViewIntent);
    }
  }
}