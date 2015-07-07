package com.cattsoft.phone.quality.ui.widget.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import com.cattsoft.phone.quality.R;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.util.XYEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Xiaohong on 14-3-17.
 */
public class LineChart extends AbstractChart {
    private GraphicalView view;
    private XYMultipleSeriesRenderer renderer;
    private XYMultipleSeriesDataset dataset;

    private String[] titles;
    private String title = "";
    private String xTitle = "";
    private String yTitle = "";
    private List<double[]> values;
    /** 最大数据量 */
    private int maxChartValues = 0;

    private float smoothness = 0.35f;

    private boolean fillPoint = false, fillOutside = false;

    private int[] colors = new int[]{Color.BLUE, Color.GREEN};
    private PointStyle[] styles = new PointStyle[]{PointStyle.CIRCLE};

    private int[] margins = new int[]{0, -1, 0, 0};

    public LineChart(String[] titles, List<double[]> values) {
        this.values = values;
        this.titles = titles;
    }

    @Override
    public String getName() {
        return "CubicLine Chart";
    }

    @Override
    public String getDesc() {
        return "CubicLine Chart";
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setStyles(PointStyle[] styles) {
        this.styles = styles;
    }

    public void setMargins(int[] margins) {
        this.margins = margins;
    }

    public void setFillOutside(boolean fillOutside) {
        this.fillOutside = fillOutside;
    }

    public void setFillPoint(boolean fillPoint) {
        this.fillPoint = fillPoint;
    }

    public void setSmoothness(float smoothness) {
        this.smoothness = smoothness;
    }

    public GraphicalView getView() {
        return view;
    }

    public void setMaxChartValues(int maxChartValues) {
        this.maxChartValues = maxChartValues;
    }

    public void push(double[] values) {
        for (int i = 0; i < titles.length; i++) {
            XYSeries series = dataset.getSeriesAt(i);
            series.add((series.getMaxX() > 0 ? series.getMaxX() : 0) + 1, values[i]);

            if (maxChartValues > 0) {
                int overflow = series.getItemCount() - maxChartValues;
                if (overflow > 0) {
                    List<XYEntry<Double, Double>> oldValues = new ArrayList<XYEntry<Double, Double>>();
                    for (int j = 0; j < series.getItemCount(); j++)
                        oldValues.add(series.getXYMap().getByIndex(j));
                    List<XYEntry<Double, Double>> newValues = new ArrayList<XYEntry<Double, Double>>();
                    for (int j = overflow; j < series.getItemCount(); j++)
                        newValues.add(series.getXYMap().getByIndex(j));

                    series.clear();
                    for (int j = 0; j < newValues.size(); j++)
                        series.add(oldValues.get(j).getKey(), newValues.get(j).getValue());
                }
            }
        }
    }

    public XYMultipleSeriesRenderer getRenderer() {
        return renderer;
    }

    public XYMultipleSeriesDataset getDataset() {
        return dataset;
    }

    public void init(Context context) {
        double[] lengths = new double[titles.length];
        List<double[]> x = new ArrayList<double[]>();
        for (int i = 0; i < titles.length; i++) {
            lengths[i] = values.get(i).length;
            double[] xV = new double[Double.valueOf(getMax(lengths)).intValue()];
            for (int j = 0; j < xV.length; j++)
                xV[j] = j + 1;
            x.add(xV);
        }
        if (styles.length < colors.length) {
            List<PointStyle> styleList = new ArrayList<PointStyle>();
            Collections.addAll(styleList, styles);
            for (int i = styles.length; i < colors.length; i++)
                styleList.add(styles[i % styles.length]);
            styles = styleList.toArray(new PointStyle[styleList.size()]);
        }

        renderer = buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer xySeriesRenderer = ((XYSeriesRenderer) renderer.getSeriesRendererAt(i));
            xySeriesRenderer.setDisplayChartValuesDistance(1);
            xySeriesRenderer.setDisplayChartValues(true);
            xySeriesRenderer.setFillPoints(fillPoint);
            xySeriesRenderer.setChartValuesSpacing(8);
            xySeriesRenderer.setChartValuesTextSize(context.getResources().getDimension(R.dimen.chart_value_textsize));

            if (fillOutside) {
                XYSeriesRenderer.FillOutsideLine fillOutsideLine = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ABOVE);
                fillOutsideLine.setColor(Color.argb(35, Color.red(colors[i]), Color.green(colors[i]), Color.blue(colors[i])));
                xySeriesRenderer.addFillOutsideLine(fillOutsideLine);
            }
        }
        setChartSettings(renderer, title, xTitle, yTitle, 0.5, Math.round(getMax(lengths)) + 0.5, 0, Math.round(getMax(values) + ((getMax(values) * 0.1) * 0.8)),
                Color.LTGRAY, Color.LTGRAY);
        renderer.setXLabels(Double.valueOf(getMax(lengths)).intValue());
        renderer.setYLabels(10);
        renderer.setShowGrid(true);

        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setFitLegend(true);
        renderer.setZoomEnabled(false, false);
        renderer.setExternalZoomEnabled(false);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(false, false);
        renderer.setMargins(margins);
        renderer.setApplyBackgroundColor(true);
        renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        renderer.setBackgroundColor(context.getResources().getColor(R.color.pager_background_alternate));
        renderer.setLegendTextSize(context.getResources().getDimension(R.dimen.chart_legend_textsize));
        renderer.setLabelsTextSize(context.getResources().getDimension(R.dimen.chart_label_textsize));

        dataset = buildDataset(titles, x, values);
//        XYSeries series = dataset.getSeriesAt(0);
//        series.addAnnotation("差", renderer.getXAxisMax() * 0.5, 10);
//
//        XYSeriesRenderer r = (XYSeriesRenderer) renderer.getSeriesRendererAt(0);
//        r.setAnnotationsColor(Color.GREEN);
//        r.setAnnotationsTextSize(15);
//        r.setAnnotationsTextAlign(Paint.Align.CENTER);
    }

    public void repaint() {
        if (null != view) {
            try {
                this.view.repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public GraphicalView execute(Context context) {
        if (null == renderer)
            init(context);
        if (0 == smoothness)
            return (this.view = ChartFactory.getLineChartView(context, dataset, renderer));
        return (this.view = ChartFactory.getCubeLineChartView(context, dataset, renderer, smoothness));
    }
}
