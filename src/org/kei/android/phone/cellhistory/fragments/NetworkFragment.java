package org.kei.android.phone.cellhistory.fragments;

import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *******************************************************************************
 * @file NetworkFragment.java
 * @author Keidan
 * @date 21/12/2015
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
public class NetworkFragment extends Fragment implements UITaskFragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      ViewGroup rootView = (ViewGroup) inflater.inflate(
              R.layout.fragment_neighboring, container, false);

      TextView dummy = new TextView(getActivity());
      dummy.setText("Under construction");
      rootView.addView(dummy);
      return rootView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }
  
  @Override
  public void processUI(TowerInfo ti) throws Throwable {
  }

}
