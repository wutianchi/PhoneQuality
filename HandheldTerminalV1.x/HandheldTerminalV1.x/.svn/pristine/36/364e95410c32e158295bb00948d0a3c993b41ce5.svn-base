package com.cattsoft.phone.quality.ui.widget.chart;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import com.cattsoft.phone.quality.R;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * Created by Xiaohong on 14-3-14.
 */
public class PieChart extends AbstractChart {

    private String title;
    private String[] categorys;

    private double[] values;

    private int[] colors = new int[]{Color.BLUE, Color.GREEN};

    private DefaultRenderer renderer;
    private CategorySeries series;

    private boolean highlighted;

    private GraphicalView view = null;

    private boolean showLegend = true;

    private String valueUnit = "";

    private DecimalFormat valueFormat = new DecimalFormat("#.#");

    public PieChart(String title, String[] categorys, double[] values) {
        this.title = title;
        this.categorys = categorys;
        this.values = values;
        series = buildCategoryDataset(title, values);
    }

    public void setValueUnit(String valueUnit) {
        this.valueUnit = valueUnit;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    @Override
    public String getName() {
        return "Pie Chart";
    }

    @Override
    public String getDesc() {
        return "Pie Chart";
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public DefaultRenderer getRenderer() {
        return renderer;
    }

    public GraphicalView getView() {
        return view;
    }

    public void init(Context context) {
        for (int i = 0; i < series.getItemCount(); i++) {
            series.set(i, categorys[i], values[i]);
        }

        renderer = buildCategoryRenderer(colors);
        for (int i = 0; i < renderer.getSeriesRendererCount(); i++) {
            renderer.getSeriesRendererAt(i).setChartValuesFormat(new NumberFormat() {
                @Override
                public StringBuffer format(double v, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                    return valueFormat.format(v, stringBuffer, fieldPosition).append(valueUnit);
                }

                @Override
                public StringBuffer format(long l, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                    return valueFormat.format(l, stringBuffer, fieldPosition).append(valueUnit);
                }

                @Override
                public Number parse(String s, ParsePosition parsePosition) {
                    return null;
                }
            });
        }

        // 设置图表渲染样式
        renderer.setLabelsColor(context.getResources().getColor(R.color.text));
        renderer.setStartAngle(180);
        renderer.setDisplayValues(true);
        renderer.setFitLegend(true);

        renderer.setZoomButtonsVisible(false);
        renderer.setPanEnabled(false);
        renderer.setZoomEnabled(false);
        renderer.setApplyBackgroundColor(true);
        renderer.setShowLegend(showLegend);
        renderer.setScale(1.0f);
        renderer.setMargins(new int[]{0, 0, 0, 0});
        renderer.setLegendTextSize(context.getResources().getDimension(R.dimen.chart_legend_textsize));
        renderer.setLabelsTextSize(context.getResources().getDimension(R.dimen.chart_label_textsize));

        view = ChartFactory.getPieChartView(context, series, renderer);
        if (highlighted) {
            view.setClickable(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesSelection seriesSelection = view.getCurrentSeriesAndPoint();
                    if (seriesSelection != null) {
                        // Getting the name of the clicked slice
                        int seriesIndex = seriesSelection.getPointIndex();

                        for (int i = 0; i < series.getItemCount(); i++) {
                            if (i != seriesIndex)
                                renderer.getSeriesRendererAt(i).setHighlighted(false);
                        }

                        renderer.getSeriesRendererAt(seriesIndex).setHighlighted(!renderer.getSeriesRendererAt(seriesIndex).isHighlighted());
                        view.repaint();
                    }
                }
            });
        }
    }

    @Override
    public GraphicalView execute(Context context) {
        if (null == renderer)
            init(context);
        return view;
    }
}
