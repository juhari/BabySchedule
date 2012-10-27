package fi.vincit.babyschedule.graphviews;

import android.graphics.Canvas;
import android.graphics.Paint;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

/**
 * Created with IntelliJ IDEA.
 * User: JuJoVeTu
 * Date: 13.10.2012
 * Time: 9:57
 * To change this template use File | Settings | File Templates.
 */
public class SleepRangeBar extends BarChart {

    /** The chart type. */
    public static final String TYPE = "RangeBar";

    /**
     * Builds a new range bar chart instance.
     *
     * @param dataset the multiple series dataset
     * @param renderer the multiple series renderer
     * @param type the range bar chart type
     */
    public SleepRangeBar(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer, Type type) {
        super(dataset, renderer, type);
    }

    /**
     * The graphical representation of a series.
     *
     * @param canvas the canvas to paint to
     * @param paint the paint to be used for drawing
     * @param points the array of points to be used for drawing the series
     * @param seriesRenderer the series renderer
     * @param yAxisValue the minimum value of the y axis
     * @param seriesIndex the index of the series currently being drawn
     * @param startIndex the start index of the rendering points
     */
    public void drawSeries(Canvas canvas, Paint paint, float[] points,
                           SimpleSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, int startIndex) {
        int seriesNr = mDataset.getSeriesCount();
        int length = points.length;
        paint.setColor(seriesRenderer.getColor());
        paint.setStyle(Paint.Style.FILL);
        float halfDiffX = getHalfDiffX(points, length, seriesNr);
        int start = 0;
        if (startIndex > 0) {
            start = 2;
        }
        for (int i = start; i < length; i += 4) {
            if (points.length > i + 3) {
                float xMin = points[i];
                float yMin = points[i + 1];
                // xMin = xMax
                float xMax = points[i + 2];
                float yMax = points[i + 3];
                drawBar(canvas, xMin, yMin, xMax, yMax, halfDiffX, seriesNr, seriesIndex, paint);
            }
        }
        paint.setColor(seriesRenderer.getColor());
    }

    /**
     * The graphical representation of the series values as text.
     *
     * @param canvas the canvas to paint to
     * @param series the series to be painted
     * @param renderer the series renderer
     * @param paint the paint to be used for drawing
     * @param points the array of points to be used for drawing the series
     * @param seriesIndex the index of the series currently being drawn
     * @param startIndex the start index of the rendering points
     */
    protected void drawChartValuesText(Canvas canvas, XYSeries series, SimpleSeriesRenderer renderer,
                                       Paint paint, float[] points, int seriesIndex, int startIndex) {
        int seriesNr = mDataset.getSeriesCount();
        float halfDiffX = getHalfDiffX(points, points.length, seriesNr);
        int start = 0;
        if (startIndex > 0) {
            start = 2;
        }
        for (int i = start; i < points.length; i += 4) {
            int index = startIndex + i / 2;
            float x = points[i];
            if (mType == Type.DEFAULT) {
                x += seriesIndex * 2 * halfDiffX - (seriesNr - 1.5f) * halfDiffX;
            }

            if (!isNullValue(series.getY(index + 1)) && points.length > i + 3) {
                // draw the maximum value
                drawText(canvas, getLabel(series.getY(index + 1)), x,
                        points[i + 3] - renderer.getChartValuesSpacing(), paint, 0);
            }
            if (!isNullValue(series.getY(index)) && points.length > i + 1) {
                // draw the minimum value
                drawText(canvas, getLabel(series.getY(index)), x,
                        points[i + 1] + renderer.getChartValuesTextSize() + renderer.getChartValuesSpacing()
                                - 3, paint, 0);
            }
        }
    }

    /**
     * Returns the value of a constant used to calculate the half-distance.
     *
     * @return the constant value
     */
    protected float getCoeficient() {
        return 0.5f;
    }

    /**
     * Returns the chart type identifier.
     *
     * @return the chart type
     */
    public String getChartType() {
        return TYPE;
    }
}
