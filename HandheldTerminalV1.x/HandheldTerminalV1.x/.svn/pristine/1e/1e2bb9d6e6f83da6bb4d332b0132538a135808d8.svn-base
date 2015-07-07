package com.cattsoft.phone.quality.ui.widget.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import com.cattsoft.phone.quality.R;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;

/**
 * Created by Xiaohong on 14-3-16.
 */
public class BarChart extends AbstractChart {
    private String title;

    private String[] titles;

    private List<double[]> values;

    private GraphicalView view;

    private XYMultipleSeriesRenderer renderer;

    private boolean stacked = true;

    private String xTitle = "";
    private String yTitle = "";

    private int[] colors = new int[]{Color.BLUE, Color.CYAN};

    private int[] margins = new int[]{0, -1, 0, 0};

    public BarChart(String title, String[] titles, List<double[]> values) {
        this.title = title;
        this.titles = titles;
        this.values = values;
    }

    @Override
    public String getName() {
        return "Barchart with Month";
    }

    @Override
    public String getDesc() {
        return "Barchart with Month";
    }

    public void setxTitle(String xTitle) {
        this.xTitle = xTitle;
    }

    public void setyTitle(String yTitle) {
        this.yTitle = yTitle;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setMargins(int[] margins) {
        this.margins = margins;
    }

    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }

    public GraphicalView getView() {
        return view;
    }

    public XYMultipleSeriesRenderer getRenderer() {
        return renderer;
    }

    public void init(Context context) {
        renderer = buildBarRenderer(colors);
        double[] lengths = new double[values.size()];
        for (int i = 0; i < values.size(); i++)
            lengths[i] = values.get(i).length;
        setChartSettings(renderer, title, xTitle, yTitle, 0.1,
                getMax(lengths) + 0.6, 0, getMax(values) + ((getMax(values) * 0.1) * 0.8), Color.GRAY, Color.LTGRAY);
        for (int i = 0; i < renderer.getSeriesRendererCount(); i++) {
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setChartValuesTextSize(
                    context.getResources().getDimension(R.dimen.chart_value_textsize));
            ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setDisplayChartValues(true);
        }
        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        renderer.setPanEnabled(true, false);
        renderer.setZoomEnabled(false);
        renderer.setZoomRate(1.1f);
        renderer.setBarSpacing(0.8f);
        renderer.setFitLegend(true);

        renderer.setMargins(margins);
        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(false);
        renderer.setApplyBackgroundColor(true);
        renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
        renderer.setBackgroundColor((context.getResources().getColor(R.color.pager_background_alternate)));
        renderer.setLegendTextSize(context.getResources().getDimension(R.dimen.chart_legend_textsize));
        renderer.setLabelsTextSize(context.getResources().getDimension(R.dimen.chart_label_textsize));
    }

    @Override
    public GraphicalView execute(Context context) {
        if (null == renderer)
            init(context);

        return (view = ChartFactory.getBarChartView(context, buildBarDataset(titles, values), renderer,
                stacked ? org.achartengine.chart.BarChart.Type.STACKED : org.achartengine.chart.BarChart.Type.DEFAULT));
    }
}
