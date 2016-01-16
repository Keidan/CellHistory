package org.kei.android.phone.cellhistory.fragments;

import java.util.ArrayList;
import java.util.List;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.adapters.AreasArrayAdapter;
import org.kei.android.phone.cellhistory.towers.AreaInfo;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 *******************************************************************************
 * @file AreasFragment.java
 * @author Keidan
 * @date 16/01/2016
 * @par Project CellHistory
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
public class AreasFragment extends Fragment implements UITaskFragment {
  /* UI */
  private ListView                    lvAreas        = null;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      ViewGroup rootView = (ViewGroup) inflater.inflate(
              R.layout.fragment_areas, container, false);

      return rootView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Context context = getActivity().getApplicationContext();
    /* UI */
    TextView dummy = new TextView(getActivity());
    lvAreas = (ListView)getView().findViewById(R.id.lvAreas);
    lvAreas.setAdapter(new AreasArrayAdapter(dummy.getTextColors().getDefaultColor(), context, R.layout.view_area, new ArrayList<AreaInfo>()));
    CellHistoryApp app = CellHistoryApp.getApp(getActivity());
    List<AreaInfo> areas = app.getSQL().getAreas();
    app.getGlobalTowerInfo().lock();
    try {
      app.getGlobalTowerInfo().getAreas().clear();
      app.getGlobalTowerInfo().getAreas().addAll(areas);
    } finally {
      app.getGlobalTowerInfo().unlock();
    }
    try {
      processUI(CellHistoryApp.getApp(getActivity()).getGlobalTowerInfo());
    } catch (Throwable e) {
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
    }
  }
  
  @Override
  public void processUI(TowerInfo ti) throws Throwable {
    if(!isAdded()) return;
    AreasArrayAdapter adapter = (AreasArrayAdapter)lvAreas.getAdapter();
    adapter.clear();
    List<AreaInfo> nis = ti.getAreas();
    adapter.add(new AreaInfo(true));
    adapter.addAll(nis);
  }

}

