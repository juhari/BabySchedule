package fi.vincit.babyschedule.graphviews;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.activities.Settings;
import fi.vincit.babyschedule.chartengine.PeriodBarChartData;
import fi.vincit.babyschedule.chartengine.PeriodBarChartView;
import fi.vincit.babyschedule.utils.BabyEvent;
import fi.vincit.babyschedule.utils.ScheduleDatabase;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.ArrayList;
import java.util.Date;

public class SleepRangeGraphView extends LinearLayout {

    private Context mContext;

    private double[] mSleepData = null;
    private double[] mNapData = null;
    private int mSleepDays = 0;
    private Date mNow;
    private int mShowingEventsFromDaysAgo = 7;
    private PeriodBarChartView mChart;

    public SleepRangeGraphView(Context context) {
        super(context);
        mContext = context;

        mNow = new Date();
        mNow.setHours(0);
        mNow.setMinutes(0);
        mNow.setSeconds(0);
        mNow.setTime(mNow.getTime()+24*60*60*1000);

        initGraphHistoryLength();

        mChart = new PeriodBarChartView(context);
        PeriodBarChartData data = loadData(mShowingEventsFromDaysAgo);
        updateChartHorizontalLabels();
        mChart.setData(data);

        addView(mChart);
    }

    private void initGraphHistoryLength() {
        ArrayList<Date> dates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), mContext.getString(R.string.go_to_sleep));
        ArrayList<Date> napDates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), mContext.getString(R.string.go_to_nap));

        Date oldest = null;
        Date oldestNap = null;
        if( dates.size() > 0 )	oldest = dates.get(0);
        if( napDates.size() > 0 ) oldestNap  = napDates.get(0);

        if( oldest == null && oldestNap == null ) {
            return;
        }
        else if( oldest == null && oldestNap != null ) {
            oldest = oldestNap;
        }
        else if( oldestNap != null && oldest != null && oldestNap.getDate() < oldest.getDate() ) {
            oldest = oldestNap;
        }
        mSleepDays = howManyDaysAgo(oldest);
    }

    public PeriodBarChartData loadData(int fromDaysAgo) {
        ArrayList<BabyEvent> dates = new ArrayList<BabyEvent>();
        ArrayList<BabyEvent> napDates = new ArrayList<BabyEvent>();

        String babyName = Settings.getCurrentBabyName();
        String sleepActionName = mContext.getString(R.string.go_to_sleep);
        String napActionName = mContext.getString(R.string.go_to_nap);
        for( int i = fromDaysAgo-7; i <= fromDaysAgo; i++ ) {
            dates.addAll(ScheduleDatabase.getAllDbActionsFromNumberOfDaysAgo(babyName, sleepActionName, i));
            napDates.addAll(ScheduleDatabase.getAllDbActionsFromNumberOfDaysAgo(babyName, napActionName, i));
        }

        PeriodBarChartData data = new PeriodBarChartData();

        addSleepDatesToData(dates, data);
        addSleepDatesToData(napDates, data);


        return data;
    }

    private void addSleepDatesToData(ArrayList<BabyEvent> dates, PeriodBarChartData data) {
        String babyName = Settings.getCurrentBabyName();
        for( BabyEvent event : dates ) {
            Date date = event.getActionDate();
            Date wakeUpDate = ScheduleDatabase.getWakeUpDateFromSleepDate(babyName, date);
            int dayIndexForSleep = mShowingEventsFromDaysAgo - howManyDaysAgo(date);
            int dayIndexForWakeUp = mShowingEventsFromDaysAgo - howManyDaysAgo(wakeUpDate);
            if( dayIndexForSleep == dayIndexForWakeUp ) {
                data.addItem(dayIndexForSleep, hourDecimalFromDate(date), hourDecimalFromDate(wakeUpDate));
            }
            else {
                data.addItem(dayIndexForSleep, hourDecimalFromDate(date), 24);
                data.addItem(dayIndexForWakeUp, 0, hourDecimalFromDate(wakeUpDate));
            }
        }
    }

    private float hourDecimalFromDate(Date date) {
        int hours = date.getHours();
        int minutes = date.getMinutes();
        float hourDecimals = hours + (float)(minutes)/60.0f;
        return hourDecimals;
    }

    private int howManyDaysAgo(Date date) {
        long numberOfDays = (mNow.getTime() - date.getTime());
        Log.i("BabySchedule", "BarStatistics: how many ms ago: " + numberOfDays);
        numberOfDays /= 24*60*60*1000;
        Log.i("BabySchedule", "BarStatistics: how many days ago: " + numberOfDays);
        numberOfDays++;

        return (int)numberOfDays;
    }

    public void loadPreviousItems() {
        mShowingEventsFromDaysAgo += 7;
        if( mShowingEventsFromDaysAgo > mSleepDays) {
            mShowingEventsFromDaysAgo = mSleepDays;
        }

        PeriodBarChartData data = loadData(mShowingEventsFromDaysAgo);

        updateChartHorizontalLabels();
        mChart.setData(data);
    }

    public void loadNextItems() {
        mShowingEventsFromDaysAgo -= 7;
        if( mShowingEventsFromDaysAgo < 7 ) {
            mShowingEventsFromDaysAgo = 7;
        }

        PeriodBarChartData data = loadData(mShowingEventsFromDaysAgo);

        updateChartHorizontalLabels();
        mChart.setData(data);
    }

    private void updateChartHorizontalLabels() {
        Date date = (Date)mNow.clone();
        long dateAdjust = 24*60*60*1000*(long)mShowingEventsFromDaysAgo;
        date.setTime(date.getTime()-dateAdjust);
        String labels[] = new String[7];
        for( int i = 0; i < 7; i++) {
            labels[i] = (""+date.getDate()+"."+(date.getMonth()+1));
            date.setTime(date.getTime()+24*60*60*1000);
        }
        mChart.setHorizontalLabels(labels);
    }
}
