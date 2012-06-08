package fi.vincit.babyschedule.graphviews;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.activities.Settings;
import fi.vincit.babyschedule.utils.BabyEvent;
import fi.vincit.babyschedule.utils.ConsumedTime;
import fi.vincit.babyschedule.utils.ScheduleDatabase;

public class SleepGraphView extends LinearLayout {
	Context mContext;	
		
	private double[] mSleepData = null;
	private double[] mNapData = null;
	private double[] mCombinedSleepData = null;
	private double mMaxNapTime = 0.0;
	private double mMaxSleepTime = 0.0;
	private double mMaxCombinedSleepTime = 0.0;
	private int mSleepDays = 0;
	
	public SleepGraphView(Context context) {
		super(context);			
		
		mContext = context;
		
		initializeGraphData();
		
		List<double[]> sleepData = new ArrayList<double[]>();
		sleepData.add(mCombinedSleepData);
		sleepData.add(mSleepData);
		sleepData.add(mNapData);
        
		String[] seriesTitles = new String[] {
	            mContext.getString(R.string.allsleeptimes),
	            mContext.getString(R.string.sleeptimes),
	            mContext.getString(R.string.naptimes)
	        };
		
		addView(StatisticsGraphViewUtils.createGraphViewFromData(mContext, seriesTitles, sleepData, mMaxCombinedSleepTime, mSleepDays, ""));
	}

	public void initializeGraphData() {
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
		Date now = new Date();
		long numberOfDays = (now.getTime() - oldest.getTime());
		Log.i("BabySchedule", "BarStatistics: how many ms ago: " + numberOfDays);
		numberOfDays /= 24*60*60*1000;
		Log.i("BabySchedule", "BarStatistics: how many days ago: " + numberOfDays);
		numberOfDays++;
		
		mMaxCombinedSleepTime = 0.0;
		mMaxNapTime = 0.0;
		mMaxSleepTime = 0.0;
		
		mSleepDays = (int)numberOfDays;
		
		if( mSleepDays < 7 )
			mSleepDays = 7;
		mNapData = new double[mSleepDays];
		mSleepData = new double[mSleepDays];
		mCombinedSleepData = new double[mSleepDays];
		int indexForValueArray = 0;
		
		for( int i = (int)mSleepDays-1; i >= 0; i-- ) {				
			ArrayList<BabyEvent> events = ScheduleDatabase.getAllDbActionsFromNumberOfDaysAgo(Settings.getCurrentBabyName(), 
																mContext.getString(R.string.go_to_sleep), i);
			events.addAll(ScheduleDatabase.getAllDbActionsFromNumberOfDaysAgo(Settings.getCurrentBabyName(), 
																mContext.getString(R.string.go_to_nap), i));
			ConsumedTime daysCombined = new ConsumedTime();
			ConsumedTime daysNap = new ConsumedTime();
			ConsumedTime daysSleep = new ConsumedTime();
			boolean isEventNap = false;
			for(BabyEvent event : events ) {
				isEventNap = event.getActionName().equalsIgnoreCase(mContext.getString(R.string.go_to_nap));
				ConsumedTime eventDuration = ScheduleDatabase.getDurationOfSleepStartedAt(Settings.getCurrentBabyName(), event.getActionDate());
				if( eventDuration != null ) {
					daysCombined = daysCombined.addition(eventDuration);
					if( isEventNap ) {
						daysNap = daysNap.addition(eventDuration);
					}						
					else {
						daysSleep = daysSleep.addition(eventDuration);
					}
				}
			}
			double combinedSleepToday = daysCombined.getHoursDecimals();
			double napToday = daysNap.getHoursDecimals();
			double sleepToday = daysSleep.getHoursDecimals();
			mCombinedSleepData[indexForValueArray] = combinedSleepToday;
			mNapData[indexForValueArray] = napToday;
			mSleepData[indexForValueArray] = sleepToday;
			
			if( mMaxCombinedSleepTime < combinedSleepToday ) mMaxCombinedSleepTime = combinedSleepToday;
			if( mMaxNapTime < napToday ) mMaxNapTime = napToday;
			if( mMaxSleepTime < sleepToday ) mMaxSleepTime = sleepToday;
			
			indexForValueArray++;
		}
		
	}	
}
