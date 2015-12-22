package org.kei.android.phone.cellhistory.fragments;

import org.kei.android.phone.cellhistory.R;
import org.kei.android.phone.cellhistory.contexts.RecorderCtx;
import org.kei.android.phone.cellhistory.towers.MobileNetworkInfo;
import org.kei.android.phone.cellhistory.towers.TowerInfo;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
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
  
  private TextView txtTxBytesSinceAppStart = null;
  private TextView txtRxBytesSinceAppStart = null;
  private TextView txtDataConnectivity         = null;
  private TextView txtDataActivity         = null;
  private TextView txtEstimatedSpeed       = null;
  private TextView txtIp4Address           = null;
  private TextView txtIp6Address           = null;
  private int defaultColor = 0;
  private int redColor = 0;
  private int greenColor = 0;
  private int orangeColor = 0;
  private Shader gradientColor = null;
  

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      ViewGroup rootView = (ViewGroup) inflater.inflate(
              R.layout.fragment_network, container, false);
      return rootView;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    txtTxBytesSinceAppStart = (TextView) getView().findViewById(R.id.txtTxBytesSinceAppStart);
    txtRxBytesSinceAppStart = (TextView) getView().findViewById(R.id.txtRxBytesSinceAppStart);
    txtDataConnectivity = (TextView) getView().findViewById(R.id.txtDataConnectivity);
    txtDataActivity = (TextView) getView().findViewById(R.id.txtDataActivity);
    txtEstimatedSpeed = (TextView) getView().findViewById(R.id.txtEstimatedSpeed);
    txtIp4Address = (TextView) getView().findViewById(R.id.txtIp4Address);
    txtIp6Address = (TextView) getView().findViewById(R.id.txtIp6Address);
    defaultColor = new TextView(getActivity()).getTextColors().getDefaultColor();
    redColor = getResources().getColor(R.color.red);
    greenColor = getResources().getColor(R.color.green);
    orangeColor = getResources().getColor(R.color.orange_dark);
    gradientColor = new LinearGradient(180, 0, 0, 0,
        new int[]{Color.RED, Color.GREEN},
        new float[]{0, 1}, TileMode.CLAMP);
  }
  
  @Override
  public void processUI(TowerInfo ti) throws Throwable {
    if(txtTxBytesSinceAppStart == null) return;
    MobileNetworkInfo mni = ti.getMobileNetworkInfo();
    txtTxBytesSinceAppStart.setText(RecorderCtx.convertToHuman(mni.getTx()));
    txtRxBytesSinceAppStart.setText(RecorderCtx.convertToHuman(mni.getRx()));
    int n = mni.getDataConnectivity();
    if(n == MobileNetworkInfo.TYPE_MOBILE) {
      String s = getResources().getString(R.string.connectivity_mobile);
      s += " (" + mni.getType() + ")";
      txtDataConnectivity.setText(s);
    } else if(n == MobileNetworkInfo.TYPE_WIFI)
      txtDataConnectivity.setText(getResources().getString(R.string.connectivity_wifi));
    else
      txtDataConnectivity.setText(getResources().getString(R.string.connectivity_none));
    txtEstimatedSpeed.setText(mni.getEstimatedSpeed());
    txtIp4Address.setText(mni.getIp4Address());
    txtIp6Address.setText(mni.getIp6Address());
    txtDataActivity.setText(MobileNetworkInfo.getDataActivity(mni.getDataActivity()));
    if(mni.getDataActivity() == MobileNetworkInfo.DATA_ACTIVITY_IN) {
      txtDataActivity.setTextColor(redColor);
      txtDataActivity.getPaint().setShader(null);
    } else if(mni.getDataActivity() == MobileNetworkInfo.DATA_ACTIVITY_OUT) {
      txtDataActivity.setTextColor(greenColor);
      txtDataActivity.getPaint().setShader(null);
    } else if(mni.getDataActivity() == MobileNetworkInfo.DATA_ACTIVITY_INOUT) {
      txtDataActivity.getPaint().setShader(gradientColor);
    } else if(mni.getDataActivity() == MobileNetworkInfo.DATA_ACTIVITY_DORMANT) {
      txtDataActivity.setTextColor(orangeColor);
    } else {
      txtDataActivity.setTextColor(defaultColor);
      txtDataActivity.getPaint().setShader(null);
    } 
  }
}
