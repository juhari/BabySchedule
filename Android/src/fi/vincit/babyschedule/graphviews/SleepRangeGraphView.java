package fi.vincit.babyschedule.graphviews;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.activities.Settings;
import fi.vincit.babyschedule.chartengine.PeriodBarChartData;
import fi.vincit.babyschedule.chartengine.PeriodBarChartView;
import fi.vincit.babyschedule.utils.ScheduleDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Juha Riippi
 * Date: 13.10.2012
 * Time: 9:04
 * To change this template use File | Settings | File Templates.
 */
public class SleepRangeGraphView extends LinearLayout {

    private Context mContext;

    private double[] mSleepData = null;
    private double[] mNapData = null;
    private int mSleepDays = 0;
    private Date mNow;
    private int mCurrentShownDayIndex = 0;
    private PeriodBarChartView mChart;

    public SleepRangeGraphView(Context context) {
        super(context);

        mNow = new Date();
        mNow.setHours(0);
        mNow.setMinutes(0);
        mNow.setSeconds(0);
        mNow.setTime(mNow.getTime()+24*60*60*1000);

        mContext = context;

        mChart = new PeriodBarChartView(context);
        PeriodBarChartData data = loadData();
        mChart.setData(data);
        mChart.setDataShownStartIndex(mCurrentShownDayIndex);

        addView(mChart);
    }

    public PeriodBarChartData loadData() {
        ArrayList<Date> dates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), mContext.getString(R.string.go_to_sleep));
        ArrayList<Date> napDates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), mContext.getString(R.string.go_to_nap));

        Date oldest = null;
        Date oldestNap = null;
        if( dates.size() > 0 )	oldest = dates.get(0);
        if( napDates.size() > 0 ) oldestNap  = napDates.get(0);

        if( oldest == null && oldestNap == null ) {
            return null;
        }
        else if( oldest == null && oldestNap != null ) {
            oldest = oldestNap;
        }
        else if( oldestNap != null && oldest != null && oldestNap.getDate() < oldest.getDate() ) {
            oldest = oldestNap;
        }
        mSleepDays = howManyDaysAgo(oldest);
        mCurrentShownDayIndex = mSleepDays - 7;
        PeriodBarChartData data = new PeriodBarChartData();

        addSleepDatesToData(dates, data);
        addSleepDatesToData(napDates, data);


        return data;
    }

    private void addSleepDatesToData(ArrayList<Date> dates, PeriodBarChartData data) {
        String babyName = Settings.getCurrentBabyName();
        for( Date date : dates ) {
            Date wakeUpDate = ScheduleDatabase.getWakeUpDateFromSleepDate(babyName, date);
            int dayIndexForSleep = mSleepDays - howManyDaysAgo(date);
            int dayIndexForWakeUp = mSleepDays - howManyDaysAgo(wakeUpDate);
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
        mCurrentShownDayIndex -= 7;
        if( mCurrentShownDayIndex < 0 ) {
            mCurrentShownDayIndex = 0;
        }

        mChart.setDataShownStartIndex(mCurrentShownDayIndex);
    }

    public void loadNextItems() {
        mCurrentShownDayIndex += 7;
        if( mCurrentShownDayIndex > mSleepDays - 7) {
            mCurrentShownDayIndex = mSleepDays -7;
        }

        mChart.setDataShownStartIndex(mCurrentShownDayIndex);
    }
}
