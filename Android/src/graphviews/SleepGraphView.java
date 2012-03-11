package graphviews;

import java.util.ArrayList;
import java.util.Date;

import utils.BabyEvent;
import utils.ConsumedTime;
import utils.ScheduleDatabase;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView.GraphViewData;

import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.activities.Settings;

public class SleepGraphView extends LinearLayout implements OnClickListener {
	LinearLayout mLayout;
	Context mContext;
	
	private LinearLayout mGraphLayout;	
	private LinearLayout mNapLayout;
	private LinearLayout mSleepLayout;
	private LinearLayout mCombinedLayout;
	
	
	private GraphViewData[] mSleepData = null;
	private GraphViewData[] mNapData = null;
	private GraphViewData[] mCombinedSleepData = null;
	private double mMaxNapTime = 0.0;
	private double mMaxSleepTime = 0.0;
	private double mMaxCombinedSleepTime = 0.0;
	private int mSleepDays = 0;
	
	public SleepGraphView(Context context) {
		super(context);			
		
		mContext = context;
		
		initializeGraphData();

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View sleepView = inflater.inflate(R.layout.sleepstatisticsgraphview, null);
		
		addView(sleepView);
		
		findViewById(R.id.sleepSelection).setOnClickListener(this);
		findViewById(R.id.napSelection).setOnClickListener(this);
		findViewById(R.id.allSleepSelection).setOnClickListener(this);
		
		mGraphLayout = (LinearLayout)(findViewById(R.id.sleepbargraphlayout));
		mNapLayout = (LinearLayout)(findViewById(R.id.naplayout));
		mSleepLayout = (LinearLayout)(findViewById(R.id.sleeplayout));
		mCombinedLayout = (LinearLayout)(findViewById(R.id.combinedlayout));
		
		mCombinedLayout.addView(StatisticsGraphViewUtils.createGraphViewFromData(mContext, mCombinedSleepData, mMaxCombinedSleepTime, mSleepDays, mContext.getString(R.string.allsleeptimes)));
		mNapLayout.addView(StatisticsGraphViewUtils.createGraphViewFromData(mContext, mNapData, mMaxNapTime, mSleepDays, mContext.getString(R.string.naptimes)));
		mSleepLayout.addView(StatisticsGraphViewUtils.createGraphViewFromData(mContext, mSleepData, mMaxSleepTime, mSleepDays, mContext.getString(R.string.sleeptimes)));
		
		updateWhatSleepIsShown();
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
		mNapData = new GraphViewData[mSleepDays];
		mSleepData = new GraphViewData[mSleepDays];
		mCombinedSleepData = new GraphViewData[mSleepDays];
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
			mCombinedSleepData[indexForValueArray] = new GraphViewData(i, combinedSleepToday);
			mNapData[indexForValueArray] = new GraphViewData(i, napToday);
			mSleepData[indexForValueArray] = new GraphViewData(i, sleepToday);
			
			if( mMaxCombinedSleepTime < combinedSleepToday ) mMaxCombinedSleepTime = combinedSleepToday;
			if( mMaxNapTime < napToday ) mMaxNapTime = napToday;
			if( mMaxSleepTime < sleepToday ) mMaxSleepTime = sleepToday;
			
			indexForValueArray++;
		}
		
	}	

	private void updateWhatSleepIsShown() {
		Log.i("BabySchedule", "updateWhatSleepIsShown()");
		CheckBox napCheckBox = (CheckBox) findViewById(R.id.napSelection);
		CheckBox sleepCheckBox = (CheckBox) findViewById(R.id.sleepSelection);
		CheckBox combinedCheckBox = (CheckBox) findViewById(R.id.allSleepSelection);
		mGraphLayout.removeAllViews();
		
		if( !napCheckBox.isChecked() && !sleepCheckBox.isChecked() && !combinedCheckBox.isChecked() ) {
			mGraphLayout.addView(StatisticsGraphViewUtils.createGraphViewFromData(mContext, null, 0, 0, ""));
			return;
		}
		
		if(combinedCheckBox.isChecked() ) {
			mGraphLayout.addView(mCombinedLayout);
		}
		if( napCheckBox.isChecked() ) {
			mGraphLayout.addView(mNapLayout);
		}
		if( sleepCheckBox.isChecked() ) {
			mGraphLayout.addView(mSleepLayout);
		}
		
	}

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.napSelection || v.getId() == R.id.sleepSelection || v.getId() == R.id.allSleepSelection )
			updateWhatSleepIsShown();
		
	}
}
