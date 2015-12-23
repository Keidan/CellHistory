package org.kei.android.phone.cellhistory.views;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
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
  private static final int        SIZE_1KB    = 0x400;
  private static final int        SIZE_1MB    = 0x100000;
  private static final int        SIZE_1GB    = 0x40000000;
  private static final int         MAX            = 60;
  private GraphicalView            chart          = null;
  private XYMultipleSeriesDataset  dataset        = null;
  private XYMultipleSeriesRenderer renderer       = null;
  private TimeSeries               timeSeries[]   = null;
  private LinearLayout             chartContainer = null;
  private long                     frequency      = 1000;

  public void install(final Activity a, final int lblColors,
      final boolean fillLine) {
    install(a, lblColors, fillLine, 1, false);
  }
  
  public void install(final Activity a, final int lblColors,
      final boolean fillLine, int nSeries, final boolean humanYAxis) {
    dataset = new XYMultipleSeriesDataset();
    renderer = new XYMultipleSeriesRenderer();
    timeSeries = new TimeSeries[nSeries];
    for(int i = 0; i < nSeries; ++i) {
      final XYSeriesRenderer r = new XYSeriesRenderer();
      r.setColor(Color.GREEN);
      r.setPointStyle(PointStyle.CIRCLE);
      r.setFillPoints(true);
      r.setShowLegendItem(false);
      if (fillLine) {
        final FillOutsideLine fill = new FillOutsideLine(
            FillOutsideLine.Type.BOUNDS_ALL);
        fill.setColor(Color.GREEN);
        r.addFillOutsideLine(fill);
      }
      renderer.addSeriesRenderer(r);

      timeSeries[i] = new TimeSeries("test " + i);
      dataset.addSeries(timeSeries[i]);
    }
    renderer.setPointSize(1f);
    renderer.setClickEnabled(true);
    renderer.setSelectableBuffer(20);
    renderer.setPanEnabled(false, false);

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
    if (chartContainer == null)
      chartContainer = (LinearLayout) a.findViewById(R.id.graph);

    TimeChart tchart = new TimeChart(dataset, renderer) {
      private static final long serialVersionUID = -2629784563963398554L;
      @Override protected String getLabel(NumberFormat format, double label) {
        if(!humanYAxis)
          return super.getLabel(format, label);
        else
          return convertToHuman((long)label);
      }
    };
    tchart.setDateFormat("H:mm:ss");
    chart = new GraphicalView(a, tchart);

    // Adding the Line Chart to the LinearLayout
    chartContainer.addView(chart);
  }

  public void setChartContainer(final LinearLayout chartContainer) {
    this.chartContainer = chartContainer;
  }

  public void addTimePoint(final int color, final long timestamp,
      final double percent) {
    addTimePoint(color, 0, timestamp, percent);
  }
  
  public void addTimePoints(final int color1_1, final int color1_2, final int color2_1, final int color2_2,
      final long timestamp, final double v1, final double v2) {
    addTimePoint(color1_1, color1_2, timestamp, v1, 0);
    addTimePoint(color2_1, color2_2, timestamp, v2, 1);
  }
  
  public void addTimePoints(final int color1, final int color2,
      final long timestamp, final double v1, final double v2) {
    addTimePoint(color1, 0, timestamp, v1, 0);
    addTimePoint(color2, 0, timestamp, v2, 1);
  }

  public void addTimePoint(final int color1, final int color2,
      final long timestamp, final double percent) {
    addTimePoint(color1, color2, timestamp, percent, 0);
  }
  
  public void addTimePoint(final int color1, final int color2,
      final long timestamp, final double value, int serie) {
    renderer.getSeriesRendererAt(serie).setColor(color1);
    if (((XYSeriesRenderer) renderer.getSeriesRendererAt(serie))
        .getFillOutsideLine() != null
        && ((XYSeriesRenderer) renderer.getSeriesRendererAt(serie))
            .getFillOutsideLine().length != 0)
      ((XYSeriesRenderer) renderer.getSeriesRendererAt(serie)).getFillOutsideLine()[0]
          .setColor(color2);
    if (timeSeries[serie].getItemCount() == 0) {
      long time = new Date().getTime();
      if (timestamp < time)
        time = timestamp;
      updateXAxis(time);
    }
    /* sanity check */
    if (timestamp > ((long) renderer.getXAxisMax())) {
      double min = Double.MAX_VALUE;
      for(int i = 0; i < timeSeries.length; i++)
        timeSeries[i].remove(0);
      for(int i = 0; i < timeSeries.length; i++)
        if(timeSeries[i].getX(0) < min) min = timeSeries[i].getX(0);
      renderer.setXAxisMin(min);
      renderer.setXAxisMax(timestamp);
    }
    if (timestamp < ((long) renderer.getXAxisMin())) {
      timeSeries[serie].clear();
      updateXAxis(timestamp);
    }
    timeSeries[serie].add(timestamp, value);
    chart.invalidate();
  }

  public void checkYAxisMax(final double value) {
    double v = value;
    if(v < 0) v = 1;
    if (v > renderer.getYAxisMax())
      renderer.setYAxisMax(v + (v/2));
    else {
      double max = 0.0;
      for(int i = 0; i < timeSeries.length; ++i) {
      for (int j = 0; j < timeSeries[i].getItemCount(); ++j)
        if (timeSeries[i].getY(j) > max)
          max = timeSeries[i].getY(j);
      }
      if (max != 0 && renderer.getYAxisMax() > max)
        renderer.setYAxisMax(max + (max / 2));
      else if(max == 0)
        renderer.setYAxisMax(100);

    }
  }

  private void updateXAxis(final long timestamp) {
    renderer.setXAxisMin(timestamp);
    renderer.setXAxisMax(timestamp + (MAX * frequency));
  }

  public void clear() {
    for(int i = 0; i < timeSeries.length; ++i)
      timeSeries[i].clear();
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

  public long getFrequency() {
    return frequency;
  }

  public void setFrequency(final long frequency) {
    this.frequency = frequency;
    if (this.frequency < 1)
      this.frequency = 1;
  }

  public void setXAxisMin(final double d) {
    renderer.setXAxisMin(d);
  }

  public void setXAxisMax(final double d) {
    renderer.setXAxisMax(d);
  }

  public void setYAxisMin(final double d) {
    renderer.setYAxisMin(d);
  }

  public void setYAxisMax(final double d) {
    renderer.setYAxisMax(d);
  }
  
  public static String convertToHuman(float f) {
    String sf = "";
    if(f < 0)
      sf = "0";
    else if(f < SIZE_1KB)
      sf = String.format(Locale.US, "%do", (int)f);
    else if(f < SIZE_1MB)
      sf = String.format("%d", (int)(f/SIZE_1KB)) + " Ko";
    else if(f < SIZE_1GB)
      sf = String.format("%d", (int)(f/SIZE_1MB)) + " Mo";
    else
      sf = String.format("%d", (int)(f/SIZE_1GB)) + " Go";
    return sf;
  }

}
