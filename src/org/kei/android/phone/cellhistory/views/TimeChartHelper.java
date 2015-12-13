package org.kei.android.phone.cellhistory.views;

import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer.FillOutsideLine;
import org.kei.android.phone.cellhistory.R;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

/**
 *******************************************************************************
 * @file TimeChartHelper.java
 * @author Keidan
 * @date 06/12/2015
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
public class TimeChartHelper {
  private static final int         MAX     = 30;
  private static final int         ONE_SEC = 1000;
  private GraphicalView            chart;
  private XYMultipleSeriesDataset  dataset;
  private XYMultipleSeriesRenderer renderer;
  private TimeSeries               timeSeries;
  private LinearLayout             chartContainer;
  
  public void install(final Activity a, final int lblColors, boolean fillLine) {
    dataset = new XYMultipleSeriesDataset();
    renderer = new XYMultipleSeriesRenderer();
    final XYSeriesRenderer r = new XYSeriesRenderer();
    r.setColor(Color.GREEN);
    r.setPointStyle(PointStyle.CIRCLE);
    r.setFillPoints(true);
    r.setShowLegendItem(false);
    if(fillLine) {
      final FillOutsideLine fill = new FillOutsideLine(
          FillOutsideLine.Type.BOUNDS_ALL);
      fill.setColor(Color.GREEN);
      r.addFillOutsideLine(fill);
    }
    renderer.setPointSize(1f);
    renderer.addSeriesRenderer(r);
    renderer.setClickEnabled(true);
    renderer.setSelectableBuffer(20);
    renderer.setPanEnabled(false, false);
    
    timeSeries = new TimeSeries("test");
    dataset.addSeries(timeSeries);
    
    renderer.setShowLegend(false);
    renderer.setShowTickMarks(true);
    renderer.setShowCustomTextGrid(false);
    renderer.setShowLabels(true);
    renderer.setXAxisMin(0);
    renderer.setXAxisMax(10);
    renderer.setYAxisMin(0);
    renderer.setYAxisMax(100);
    renderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
    renderer.setYLabelsPadding(10);
    renderer.setYLabelsColor(0, lblColors);
    renderer.setXLabelsColor(lblColors);
    renderer.setYAxisColor(lblColors);
    renderer.setXAxisColor(lblColors);
    if(chartContainer == null)
      chartContainer = (LinearLayout) a.findViewById(R.id.graph);
    
    chart = ChartFactory.getTimeChartView(a, dataset, renderer, "H:mm:ss");
    
    // Adding the Line Chart to the LinearLayout
    chartContainer.addView(chart);
  }
  
  public void setChartContainer(LinearLayout chartContainer) {
    this.chartContainer = chartContainer;
  }
  
  public void addTimePoint(final int color,
      final long timestamp, final double percent) {
    addTimePoint(color, 0, timestamp, percent);
  }
  
  public void addTimePoint(final int color1, final int color2,
      final long timestamp, final double percent) {
    renderer.getSeriesRendererAt(0).setColor(color1);
    if(((XYSeriesRenderer) renderer.getSeriesRendererAt(0)).getFillOutsideLine() != null 
        && ((XYSeriesRenderer) renderer.getSeriesRendererAt(0)).getFillOutsideLine().length != 0)
    ((XYSeriesRenderer) renderer.getSeriesRendererAt(0)).getFillOutsideLine()[0]
        .setColor(color2);
    if (timeSeries.getItemCount() == 0) {
      long time = new Date().getTime();
      if (timestamp < time)
        time = timestamp;
      updateXAxis(time);
    } else if (timeSeries.getItemCount() == MAX) {
      timeSeries.remove(0);
      updateXAxis((long) timeSeries.getX(0));
    }
    /* sanity check */
    if (timestamp > ((long) renderer.getXAxisMax())) {
      timeSeries.clear();
      updateXAxis(timestamp);
    }
    if (timestamp < ((long) renderer.getXAxisMin())) {
      timeSeries.clear();
      updateXAxis(timestamp);
    }
    timeSeries.add(timestamp, percent);
    chart.invalidate();
  }

  public void checkYAxisMax(final double value) {
    if(value > renderer.getYAxisMax())
      renderer.setYAxisMax(value+2);
    else {
      double max = 0.0;
      for(int i = 0; i < timeSeries.getItemCount(); ++i)
        if(timeSeries.getY(i) > max) max = timeSeries.getY(i);
      if(max != 0 && renderer.getYAxisMax() > max)
        renderer.setYAxisMax(max+2);
        
    }
  }

  private void updateXAxis(final long timestamp) {
    renderer.setXAxisMin(timestamp);
    renderer.setXAxisMax(timestamp + (MAX * ONE_SEC));
  }

  public void clear() {
    timeSeries.clear();
    chart.invalidate();
  }

  public void setOnClickListener(final OnClickListener li) {
    chart.setOnClickListener(li);
  }

  public int getVisibility() {
    return chartContainer.getVisibility();
  }

  public void setVisibility(final int visibility) {
    chartContainer.setVisibility(visibility);
    chart.invalidate();
  }

  public View getView() {
    return chartContainer;
  }
  

  public void setXAxisMin(double d) {
    renderer.setXAxisMin(d);
  }
  public void setXAxisMax(double d) {
    renderer.setXAxisMax(d);
  }

  public void setYAxisMin(double d) {
    renderer.setYAxisMin(d);
  }
  public void setYAxisMax(double d) {
    renderer.setYAxisMax(d);
  }
}
