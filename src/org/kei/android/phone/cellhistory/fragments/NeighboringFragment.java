package org.kei.android.phone.cellhistory.fragments;

import java.util.ArrayList;
import java.util.List;

import org.kei.android.phone.cellhistory.CellHistoryApp;
import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.adapters.NeighboringArrayAdapter;
import org.kei.android.phone.cellhistory.towers.NeighboringInfo;
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
 * @file NeighboringFragment.java
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
public class NeighboringFragment extends Fragment implements UITaskFragment {
  /* UI */
  private ListView                    lvNeighboring        = null;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      ViewGroup rootView = (ViewGroup) inflater.inflate(
              R.layout.fragment_neighboring, container, false);

      return rootView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Context context = getActivity().getApplicationContext();
    /* UI */
    TextView dummy = new TextView(getActivity());
    lvNeighboring = (ListView)getView().findViewById(R.id.lvNeighboring);
    lvNeighboring.setAdapter(new NeighboringArrayAdapter(dummy.getTextColors().getDefaultColor(), context, R.layout.view_neighboring, new ArrayList<NeighboringInfo>()));
    try {
      processUI(CellHistoryApp.getApp(getActivity()).getGlobalTowerInfo());
    } catch (Throwable e) {
      Log.e(getClass().getSimpleName(), "Exception: " + e.getMessage(), e);
    }
  }
  
  @Override
  public void processUI(TowerInfo ti) throws Throwable {
    if(lvNeighboring == null) return;
    NeighboringArrayAdapter adapter = (NeighboringArrayAdapter)lvNeighboring.getAdapter();
    adapter.clear();
    List<NeighboringInfo> nis = ti.getNeighboring();
    adapter.add(new NeighboringInfo(true));
    adapter.addAll(nis);
  }

}
