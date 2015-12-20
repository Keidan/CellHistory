package org.kei.android.phone.cellhistory.adapters;

import java.util.List;

import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.towers.NeighboringInfo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 *******************************************************************************
 * @file NeighboringArrayAdapter.java
 * @author Keidan
 * @date 10/12/2015
 * @par Project ATK
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
public class NeighboringArrayAdapter extends ArrayAdapter<NeighboringInfo> {

  private final Context               c;
  private final int                   id;
  private final int                   defaultColor;

  public NeighboringArrayAdapter(final int defaultColor, final Context context,
      final int textViewResourceId, final List<NeighboringInfo> objects) {
    super(context, textViewResourceId, objects);
    this.defaultColor = defaultColor;
    c = context;
    id = textViewResourceId;
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
    final NeighboringInfo o = getItem(position);
    if (o != null) {
      final TextView lac = (TextView) v.findViewById(R.id.lac);
      final TextView cid = (TextView) v.findViewById(R.id.cid);
      final TextView asu = (TextView) v.findViewById(R.id.asu);
      final TextView network = (TextView) v.findViewById(R.id.network);
      final TextView strength = (TextView) v.findViewById(R.id.strength);
      if(o.isTitle()) {
        updateTextViewColor(lac, "LAC", defaultColor);
        updateTextViewColor(cid, "CID", defaultColor);
        updateTextViewColor(asu, "ASU", defaultColor);
        updateTextViewColor(network, "NT", defaultColor);
        updateTextViewColor(strength, "STR", defaultColor);
      } else {
        int color = defaultColor;
        if(o.getAsu() == 99) color = Color.RED;
        updateTextViewColor(lac, ""+ o.getLac(), color);
        updateTextViewColor(cid, ""+ o.getCid(), color);
        updateTextViewColor(asu, String.format("%02d", o.getAsu()), color);
        updateTextViewColor(network, ""+ o.getType(), color);
        if(o.getAsu() == 99)
          updateTextViewColor(strength, "??? dBm", color);
        else {
          updateTextViewColor(strength, o.getStrength() + " dBm", color);
        }
      }
    }
    return v;
  }
  
  private void updateTextViewColor(final TextView tv, final String s, final int color) {
    if (tv != null) {
      tv.setText(s);
      tv.setTextColor(color);
    }
  }
}