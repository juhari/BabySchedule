package fi.vincit.babyschedule.chartengine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.*;

public class PeriodBarChartView extends View {

    private Paint mChartPaint;
    private Paint mTextPaint;
    private Paint mGraphLinePaint;
    private Paint mSleepBarPaint;

    private boolean mDrawHorizontalLines = true;
    private boolean mDrawVerticalLines = true;

    private int mDataWindowSize = 7;
    private String[] mHorizontalLabels = null;

    final static int VERTICAL_OFFSET_TOP = 10;
    final static int VERTICAL_OFFSET_BOTTOM = 20;
    final static int HORIZONTAL_OFFSET_LEFT = 20;
    final static int HORIZONTAL_OFFSET_RIGHT = 10;
    final static int OFFSET_FOR_TEXT = 15;

    private PeriodBarChartData mData = null;

    public PeriodBarChartView(Context context) {
        super(context);

        mChartPaint = new Paint();
        mChartPaint.setColor(Color.BLACK);
        mChartPaint.setStrokeWidth(3.0f);

        mSleepBarPaint = new Paint();
        mSleepBarPaint.setColor(Color.BLUE);

        mGraphLinePaint = new Paint();
        mGraphLinePaint.setColor(Color.GRAY);
        mGraphLinePaint.setStrokeWidth(1.0f);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(12.0f);

        populateWithTestData();
    }

    private void populateWithTestData() {
        PeriodBarChartData data = new PeriodBarChartData();

        for( int i = 0; i < 7; i+=2 ) {
            data.addItem(i, 0, (float)(Math.random()*2 + 6.5));
            float napStart = (float)(Math.random()*2 + 13);
            data.addItem(i, napStart, napStart + (float)(Math.random()*1+1.5));
            data.addItem(i, (float)(Math.random()*2 + 19.5), 24);
        }

        setData(data);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

    }

    public void setDataWindowSize(int size) {
        mDataWindowSize = size;
        invalidate();
    }

    public void setData(PeriodBarChartData data) {
        mData = data;
        invalidate();
    }

    public void setHorizontalLabels(String[] labels) {
        mHorizontalLabels = labels;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawLine(HORIZONTAL_OFFSET_LEFT, VERTICAL_OFFSET_TOP,
                        HORIZONTAL_OFFSET_LEFT, getHeight()-VERTICAL_OFFSET_BOTTOM,
                        mChartPaint);
        canvas.drawLine(HORIZONTAL_OFFSET_LEFT, getHeight()-VERTICAL_OFFSET_BOTTOM,
                        getWidth()-HORIZONTAL_OFFSET_RIGHT, getHeight()-VERTICAL_OFFSET_BOTTOM,
                        mChartPaint);

        drawTimeLabels(canvas);
        drawHorizontalLabels(canvas);
        drawBars(canvas);
    }

    private void drawTimeLabels(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();
        final int AMOUNT_OF_STEPS = 12;
        final int NUMBER_OF_HOURS = 24;
        final int HOURS_STEP = NUMBER_OF_HOURS / AMOUNT_OF_STEPS;
        int hourNumber = 0;
        int stepSize = (height - (VERTICAL_OFFSET_BOTTOM+VERTICAL_OFFSET_TOP)) / AMOUNT_OF_STEPS;
        int x = HORIZONTAL_OFFSET_LEFT - OFFSET_FOR_TEXT;

        for( int y = height - VERTICAL_OFFSET_BOTTOM; y > VERTICAL_OFFSET_TOP; y -= stepSize ) {
            canvas.drawText(""+hourNumber, x, y, mTextPaint);
            hourNumber += HOURS_STEP;

            if( mDrawHorizontalLines ) {
                canvas.drawLine(HORIZONTAL_OFFSET_LEFT, y,
                                width-HORIZONTAL_OFFSET_RIGHT, y,
                                mGraphLinePaint);
            }
        }
    }

    private void drawHorizontalLabels(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();

        int stepSize = (width - (HORIZONTAL_OFFSET_LEFT+HORIZONTAL_OFFSET_RIGHT))/mDataWindowSize;
        int y = height - (VERTICAL_OFFSET_BOTTOM - OFFSET_FOR_TEXT);

        int labelIndex = 0;
        for( int x = HORIZONTAL_OFFSET_LEFT; x <= width - HORIZONTAL_OFFSET_RIGHT; x += stepSize ) {
            if( mHorizontalLabels != null && labelIndex < mDataWindowSize) {
                canvas.drawText(mHorizontalLabels[labelIndex], x, y, mTextPaint);
                labelIndex++;
            }

            if( mDrawVerticalLines ) {
                canvas.drawLine(x, VERTICAL_OFFSET_TOP,
                                x, height - VERTICAL_OFFSET_BOTTOM,
                                mGraphLinePaint);
            }
        }
    }

    private void drawBars(Canvas canvas) {
        if( mData == null ) {
            return;
        }
        int height = getHeight();
        int width = getWidth();

        final int NUMBER_OF_HOURS = 24;
        int stepSizeX = (width - (HORIZONTAL_OFFSET_LEFT+HORIZONTAL_OFFSET_RIGHT))/mDataWindowSize;
        int stepSizeY = (height - (VERTICAL_OFFSET_BOTTOM+VERTICAL_OFFSET_TOP)) / NUMBER_OF_HOURS;
        int barThickness = stepSizeX - 6;
        int y = height - (VERTICAL_OFFSET_BOTTOM - OFFSET_FOR_TEXT);

        int xOffset = 0;
        for( int i = 0; i < mDataWindowSize; i++ ) {
            if( mData.getItemsAt(i) != null ) {
                for( PeriodBarChartData.PeriodBarChartDataItem item : mData.getItemsAt(i) ) {
                    float xStart = HORIZONTAL_OFFSET_LEFT + stepSizeX*xOffset + 3;
                    float xEnd = xStart + barThickness;
                    float yStart = height - (VERTICAL_OFFSET_BOTTOM + stepSizeY*item.endTime);
                    float yEnd = height - (VERTICAL_OFFSET_BOTTOM + stepSizeY*item.startTime);
                    canvas.drawRect(xStart, yStart, xEnd, yEnd, mSleepBarPaint);
                }
            }
            xOffset++;
        }
    }
}

