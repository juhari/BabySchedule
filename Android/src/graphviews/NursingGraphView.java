package graphviews;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import utils.BabyEvent;
import utils.ScheduleDatabase;
import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.activities.Settings;

public class NursingGraphView extends LinearLayout {
	Context mContext;	
	
	private double[] mLeftData = null;
	private double[] mRightData = null;
	private double[] mBothData = null;
	private double mMaxLeftTime = 0.0;
	private double mMaxRightTime = 0.0;
	private double mMaxBothTime = 0.0;
	private int mNursingDays = 0;
	
	public NursingGraphView(Context context) {
		super(context);			
		
		mContext = context;
		
		initializeGraphData();
		
		List<double[]> nursingData = new ArrayList<double[]>();
		nursingData.add(mBothData);
		nursingData.add(mRightData);
		nursingData.add(mLeftData);
		
		String[] seriesTitles = new String[] {
	        mContext.getString(R.string.bothtimes),
	        mContext.getString(R.string.righttimes),
	        mContext.getString(R.string.lefttimes)
		};
		
		addView(StatisticsGraphViewUtils.createGraphViewFromData(mContext, seriesTitles, nursingData, mMaxBothTime, mNursingDays, ""));
	}

	public void initializeGraphData() {
		ArrayList<Date> dates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), mContext.getString(R.string.milk_left));
		ArrayList<Date> napDates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), mContext.getString(R.string.milk_right));
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
		
		mMaxBothTime = 0.0;
		mMaxLeftTime = 0.0;
		mMaxRightTime = 0.0;
		
		mNursingDays = (int)numberOfDays;
		
		if( mNursingDays < 7 )
			mNursingDays = 7;
		mRightData = new double[mNursingDays];
		mLeftData = new double[mNursingDays];
		mBothData = new double[mNursingDays];
		int indexForValueArray = 0;
		
		for( int i = (int)mNursingDays-1; i >= 0; i-- ) {				
			ArrayList<BabyEvent> events = ScheduleDatabase.getAllDbActionsFromNumberOfDaysAgo(Settings.getCurrentBabyName(), 
																mContext.getString(R.string.milk_left), i);
			events.addAll(ScheduleDatabase.getAllDbActionsFromNumberOfDaysAgo(Settings.getCurrentBabyName(), 
																mContext.getString(R.string.milk_right), i));
			double daysCombined = 0.0;
			double daysLeft = 0.0;
			double daysRight = 0.0;
			boolean isEventLeft = false;
			for(BabyEvent event : events ) {
				isEventLeft = event.getActionName().equalsIgnoreCase(mContext.getString(R.string.milk_left));
				double eventDuration = (double)event.getDurationInSeconds()/60.0;
				daysCombined += eventDuration;
				if( isEventLeft ) {
					daysLeft += eventDuration;
				}						
				else {
					daysRight += eventDuration;
				}
			}
			mBothData[indexForValueArray] = daysCombined;
			mRightData[indexForValueArray] = daysLeft;
			mLeftData[indexForValueArray] = daysRight;
			
			if( mMaxBothTime < daysCombined ) mMaxBothTime = daysCombined;
			if( mMaxLeftTime < daysLeft ) mMaxLeftTime = daysLeft;
			if( mMaxRightTime < daysRight ) mMaxRightTime = daysRight;
			
			indexForValueArray++;
		}
		
	}	
}
