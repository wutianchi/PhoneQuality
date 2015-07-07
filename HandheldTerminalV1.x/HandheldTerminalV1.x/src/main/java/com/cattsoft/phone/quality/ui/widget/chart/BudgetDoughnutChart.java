/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cattsoft.phone.quality.ui.widget.chart;

import android.content.Context;
import android.graphics.Color;
import com.cattsoft.phone.quality.R;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.renderer.DefaultRenderer;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;

/**
 * Budget demo pie chart.
 */
public class BudgetDoughnutChart extends AbstractChart {
    private String title;
    private List<String[]> titles;
    private List<double[]> values;

    private GraphicalView view;
    private DefaultRenderer renderer;

    private DecimalFormat valueFormat = new DecimalFormat("#.#");

    private int[] colors = new int[]{Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN};

    public BudgetDoughnutChart(String title, List<String[]> titles, List<double[]> values) {
        this.title = title;
        this.titles = titles;
        this.values = values;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    /**
     * Returns the chart name.
     *
     * @return the chart name
     */
    public String getName() {
        return "Budget chart for several years";
    }

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    public String getDesc() {
        return "The budget per project for several years (doughnut chart)";
    }

    public DefaultRenderer getRenderer() {
        return renderer;
    }

    public GraphicalView getView() {
        return view;
    }

    public void init(Context context) {
        renderer = buildCategoryRenderer(colors);
        renderer.setApplyBackgroundColor(true);
        renderer.setLabelsColor(context.getResources().getColor(R.color.text_light));
        renderer.setStartAngle(180);
        renderer.setFitLegend(true);

        renderer.setZoomButtonsVisible(false);
        renderer.setMargins(new int[]{0, 0, 0, 0});
        renderer.setPanEnabled(false);
        renderer.setZoomEnabled(false);
        renderer.setShowLegend(false);
        renderer.setDisplayValues(true);
        renderer.setLegendHeight(0);
        renderer.setShowLabels(true);

        renderer.getSeriesRendererAt(0).setChartValuesFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double v, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                return valueFormat.format(v, stringBuffer, fieldPosition);
            }

            @Override
            public StringBuffer format(long l, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                return valueFormat.format(l, stringBuffer, fieldPosition);
            }

            @Override
            public Number parse(String s, ParsePosition parsePosition) {
                return null;
            }
        });

        renderer.setBackgroundColor((context.getResources().getColor(R.color.pager_background_alternate)));
        renderer.setLegendTextSize(context.getResources().getDimension(R.dimen.chart_legend_textsize));
        renderer.setLabelsTextSize(context.getResources().getDimension(R.dimen.chart_label_textsize));
        renderer.setChartTitle(title);
    }

    /**
     * Executes the chart demo.
     *
     * @return the built intent
     */
    public GraphicalView execute(Context context) {
        if (null == renderer)
            init(context);

        return (view = ChartFactory.getDoughnutChartView(context, buildMultipleCategoryDataset(title, titles, values), renderer));
    }

}
