package fi.vincit.babyschedule.graphviews;

import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class StatisticsGraphViewUtils {


    public static View createGraphViewFromData(Context context, String[] seriesTitles, List<double[]> data, double maxYValue, int numberOfDays, String title) {

        if( numberOfDays == 0 || data.size() == 0 || seriesTitles.length == 0) {
            TextView empty = new TextView(context);
            return empty;
        }

        int[] colors = new int[] { Color.CYAN, Color.BLUE, Color.RED };
        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors, seriesTitles.length);
        renderer.setOrientation(Orientation.HORIZONTAL);
        setChartSettings(renderer, title, "", "", 
                numberOfDays-7, numberOfDays, 
                0, maxYValue, 
                Color.GRAY, Color.BLACK);
        
        renderer.setXLabels(0);
        setDateLabels(renderer, numberOfDays);
        renderer.setYLabels(10);
        renderer.setMarginsColor(0xb0b0ff);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
            seriesRenderer.setDisplayChartValues(true);
        }

        XYMultipleSeriesDataset dataSet = buildBarDataset(seriesTitles, data);
        GraphicalView view = ChartFactory.getBarChartView(context, dataSet, renderer, BarChart.Type.DEFAULT);
        return view;
    }

    private static void setDateLabels(XYMultipleSeriesRenderer renderer, int numberOfDays) {
        Date date = new Date();
        for( int i = numberOfDays; i >= 0; i--) {
            renderer.addXTextLabel(i, ""+date.getDate()+"."+(date.getMonth()+1));
            date.setTime(date.getTime()-24*60*60*1000);
        }
    }

    /**
     * Builds a bar multiple series renderer to use the provided colors.
     * 
     * @param colors the series renderers colors
     * @return the bar multiple series renderer
     */
    protected static XYMultipleSeriesRenderer buildBarRenderer(int[] colors, int numberOfSeries) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        int length = numberOfSeries;
        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(colors[i]);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }


    /**
     * Sets a few of the series renderer settings.
     * 
     * @param renderer the renderer to set the properties to
     * @param title the chart title
     * @param xTitle the title for the X axis
     * @param yTitle the title for the Y axis
     * @param xMin the minimum value on the X axis
     * @param xMax the maximum value on the X axis
     * @param yMin the minimum value on the Y axis
     * @param yMax the maximum value on the Y axis
     * @param axesColor the axes color
     * @param labelsColor the labels color
     */
    protected static void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
            String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
            int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.BLACK);
        renderer.setBarSpacing(0.3);
        renderer.setZoomEnabled(true, false);
        renderer.setPanEnabled(true, false);
        renderer.setMargins(new int[] { 20, 15, 0, 0 });
    }

    /**
     * Builds a bar multiple series dataset using the provided values.
     * 
     * @param titles the series titles
     * @param values the values
     * @return the XY multiple bar dataset
     */
    protected static XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            CategorySeries series = new CategorySeries(titles[i]);
            double[] v = values.get(i);
            int seriesLength = v.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(Math.round(v[k]*100) / 100.0);
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }

    /**
     * Builds an XY multiple time dataset using the provided values.
     * 
     * @param titles the series titles
     * @param xValues the values for the X axis
     * @param yValues the values for the Y axis
     * @return the XY multiple time dataset
     */
    protected static XYMultipleSeriesDataset buildDateDataset(String[] titles, List<Date[]> xValues,
            List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            TimeSeries series = new TimeSeries(titles[i]);
            Date[] xV = xValues.get(i);
            double[] yV = yValues.get(i);
            int seriesLength = xV.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(xV[k], yV[k]);
            }
            dataset.addSeries(series);
        }
        return dataset;
    }
}
