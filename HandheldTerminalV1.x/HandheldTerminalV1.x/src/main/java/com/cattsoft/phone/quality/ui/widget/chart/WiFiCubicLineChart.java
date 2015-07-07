package com.cattsoft.phone.quality.ui.widget.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.ScanResult;
import com.cattsoft.phone.quality.R;
import com.cattsoft.phone.quality.utils.Wifis;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;

/**
 * WiFi频谱图
 * Created by Xiaohong on 14-1-21.
 */
public class WiFiCubicLineChart extends AbstractChart {
    int point = 18 * 10;
    int yMin = 0;
    int yMax = 100;
    private int[] colors;
    private int colorIndex;
    private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;
    private GraphicalView mChartView;
    private Map<String, WifiSeries> seriesMap = new HashMap<String, WifiSeries>();

    public WiFiCubicLineChart(int[] colors) {
        this.colors = colors;
    }

    public GraphicalView getChartView() {
        return mChartView;
    }

    @Override
    public String getName() {
        return "WiFiCubicLineChart";
    }

    @Override
    public String getDesc() {
        return "WiFiCubicLineChart";
    }

    public void addWiFiSeries(Context context, final String bssid, String ssid, int channel, int dBm) {
        if (channel == 14)
            channel = 15;
        int dBmValue = dBm == 0 ? 0 : yMax - (Math.abs(dBm));

        colorIndex = colorIndex >= colors.length ? 0 : colorIndex;

        double[] yV = Wifis.getChannelValues(channel, dBmValue);

        if (dBm == 0) {
            yV = new double[point];
            for (int i = 0; i < yV.length; i++) {
                yV[i] = -10;
            }
        }

        double[] xV = new double[yV.length];
        for (int j = 0; j < point; j++)
            xV[j] = j;

        // create a new series of data
        WifiSeries wifiSeries = seriesMap.get(ssid);

        XYSeries series = null;
        if (null != wifiSeries) {
            series = wifiSeries.series;
        }

        if (null == series) {
            // 新增
            series = new XYSeries(ssid);
            mDataset.addSeries(series);

            int color = colors[colorIndex++];

            // create a new renderer for the new series
            XYSeriesRenderer renderer = new XYSeriesRenderer();
            // set some renderer properties
            renderer.setPointStyle(PointStyle.POINT);
            renderer.setFillPoints(false);
            renderer.setLineWidth(2f);
            renderer.setColor(color);

            renderer.setDisplayChartValues(true);
            renderer.setChartValuesTextSize(context.getResources().getDimensionPixelSize(R.dimen.chart_value_textsize));
            renderer.setChartValuesTextAlign(Paint.Align.CENTER);
            renderer.setDisplayBoundingPoints(true);
            renderer.setChartValuesSpacing(5);
            renderer.setDisplayChartValuesDistance(1);

            XYSeriesRenderer.FillOutsideLine fillOutsideLine = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BELOW);
            fillOutsideLine.setColor(Color.argb(35, Color.red(color), Color.green(color), Color.blue(color)));

            renderer.addFillOutsideLine(fillOutsideLine);

            renderer.setChartValuesFormat(new WifiTitleFormat(ssid, dBmValue));

            mRenderer.addSeriesRenderer(renderer);

            wifiSeries = new WifiSeries(ssid, channel, dBmValue, series, renderer);

            for (int i = 0; i < yV.length; i++)
                series.add(xV[i], yV[i]);
        } else {
            // 更新
            wifiSeries.channel = channel;
            wifiSeries.dBm = dBmValue;
            wifiSeries.renderer.setChartValuesFormat(new WifiTitleFormat(ssid, dBmValue));

            wifiSeries.series.clearSeriesValues();
            for (int j = 0; j < yV.length; j++)
                wifiSeries.series.add(xV[j], yV[j]);
        }

        seriesMap.put(ssid, wifiSeries);
    }

    public void addWiFiSeries(Context context, ScanResult scanResult) {
        int channel = Wifis.getChannelFromFrequency(scanResult.frequency);
        int dbm = scanResult.level;

        addWiFiSeries(context, scanResult.BSSID, scanResult.SSID, channel, dbm);
    }

    public void repaint() {
        mChartView.repaint();
    }

    public void removeNoSignal(Set<String> keys) {
        Set<String> oriSet = new HashSet<String>(seriesMap.keySet());
        oriSet.removeAll(keys);

        for (String key : oriSet) {
            if (null == key || "".equals(key))
                continue;
            WifiSeries series = seriesMap.get(key);
            mDataset.removeSeries(series.series);
            mRenderer.removeSeriesRenderer(series.renderer);

            seriesMap.remove(key);
        }
    }

    @Override
    public GraphicalView execute(Context context) {
        List<double[]> values = new ArrayList<double[]>();

        String[] titles = new String[]{};
        PointStyle[] styles = new PointStyle[titles.length];

        List<double[]> x = new ArrayList<double[]>();
        for (int i = 0; i < titles.length; i++) {
            double[] val = new double[point];
            for (int j = 0; j < point; j++) {
                val[j] = j;
            }
            x.add(val);

            styles[i] = PointStyle.POINT;
        }

        mRenderer = new XYMultipleSeriesRenderer(1);
        setRenderer(mRenderer, new int[]{}, styles);
        int length = mRenderer.getSeriesRendererCount();

        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = (XYSeriesRenderer) mRenderer.getSeriesRendererAt(i);

            XYSeriesRenderer.FillOutsideLine fillOutsideLine = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BELOW);
            fillOutsideLine.setColor(colors[i]);

            r.addFillOutsideLine(fillOutsideLine);
            r.setLineWidth(3f);
        }
        /** Anything worse than or equal to this will show 0 bars. */
        // private static final int MIN_RSSI = -100;

        /** Anything better than or equal to this will show the max bars. */
        // private static final int MAX_RSSI = -55;

        setChartSettings(mRenderer, "", "WiFi 信道", "信号强度 [dBm]", 0, 180, yMin, yMax - 20,
                context.getResources().getColor(R.color.light_grey), context.getResources().getColor(R.color.text_light));
        mRenderer.setXLabels(0);
        mRenderer.setYLabels(0);
        mRenderer.setShowGrid(true);
        mRenderer.setXLabelsAlign(Paint.Align.CENTER);
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setPanLimits(new double[]{0, 0, 0, 0});
        mRenderer.setZoomLimits(new double[]{0, 0, 0, 0});
        mRenderer.setMargins(new int[]{5, 45, 5, 5});
        mRenderer.setZoomRate(1.05f);
        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setLabelsTextSize(context.getResources().getDimensionPixelSize(R.dimen.chart_label_textsize));
        mRenderer.setXLabelsColor(context.getResources().getColor(R.color.blue));
        mRenderer.setYLabelsPadding(4);
        mRenderer.setYLabelsVerticalPadding(-3);
        mRenderer.setInScroll(true);
        mRenderer.setShowCustomTextGridY(true);
        mRenderer.setShowGridX(false);
        mRenderer.setYLabelsColor(0, context.getResources().getColor(R.color.blue));
        mRenderer.setChartTitleTextSize(context.getResources().getDimensionPixelSize(R.dimen.chart_label_textsize));
        mRenderer.setAxisTitleTextSize(context.getResources().getDimensionPixelSize(R.dimen.chart_label_textsize));

        mRenderer.setShowLegend(false);
        mRenderer.setZoomEnabled(false, false);
        mRenderer.setZoomButtonsVisible(false);

        mRenderer.setMarginsColor(context.getResources().getColor(R.color.pager_background_alternate));

        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.parseColor("#e8eaed"));

        for (int i = 0; i < point; i++) {
            if (0 == i % 10) {
                int val = i / 10;
                if (val > 1) {
                    if (val < 14)
                        mRenderer.addXTextLabel(i, Integer.toString(val - 1));
                    else if (val == 16)
                        mRenderer.addXTextLabel(i, "14");
                }
            }
        }
        for (int i = yMax - 30; i > 0; i -= 10) {
            mRenderer.addYTextLabel(yMax - 20 - i, Integer.toString(-(i + 20)));
        }
        return (mChartView = ChartFactory.getCubeLineChartView(context, mDataset = buildDataset(titles, x, values), mRenderer, 0.5f));
    }


    private class WifiSeries {
        String title;
        int channel;
        double dBm;
        XYSeries series;
        XYSeriesRenderer renderer;

        private WifiSeries(String title, int channel, double dBm, XYSeries series, XYSeriesRenderer renderer) {
            this.title = title;
            this.channel = channel;
            this.dBm = dBm;
            this.series = series;
            this.renderer = renderer;
        }
    }

    private class WifiTitleFormat extends NumberFormat {
        private String title;
        private double dBm;

        private WifiTitleFormat(String title, double dBm) {
            this.title = title;
            this.dBm = dBm;
        }

        @Override
        public StringBuffer format(double v, StringBuffer stringBuffer, FieldPosition fieldPosition) {
            return stringBuffer.append(v == dBm ? title : "");
        }

        @Override
        public StringBuffer format(long l, StringBuffer stringBuffer, FieldPosition fieldPosition) {
            return stringBuffer.append(l == dBm ? title : "");
        }

        @Override
        public Number parse(String s, ParsePosition parsePosition) {
            return Integer.parseInt(s);
        }
    }
}
