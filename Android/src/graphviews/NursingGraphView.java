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

public class NursingGraphView extends LinearLayout implements OnClickListener {
	LinearLayout mLayout;
	Context mContext;
	
	private LinearLayout mGraphLayout;	
	private LinearLayout mLeftLayout;
	private LinearLayout mRightLayout;
	private LinearLayout mBothLayout;
	
	
	private GraphViewData[] mLeftData = null;
	private GraphViewData[] mRightData = null;
	private GraphViewData[] mBothData = null;
	private double mMaxLeftTime = 0.0;
	private double mMaxRightTime = 0.0;
	private double mMaxBothTime = 0.0;
	private int mNursingDays = 0;
	
	public NursingGraphView(Context context) {
		super(context);			
		
		mContext = context;
		
		initializeGraphData();
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View nursingView = inflater.inflate(R.layout.nursingstatisticsgraphview, null);
		
		addView(nursingView);
		
		findViewById(R.id.leftSelection).setOnClickListener(this);
		findViewById(R.id.rightSelection).setOnClickListener(this);
		findViewById(R.id.bothSelection).setOnClickListener(this);
		
		mGraphLayout = (LinearLayout)(findViewById(R.id.nursingBarGraphLayout));
		mLeftLayout = (LinearLayout)(findViewById(R.id.leftLayout));
		mRightLayout = (LinearLayout)(findViewById(R.id.rightLayout));
		mBothLayout = (LinearLayout)(findViewById(R.id.bothLayout));
		
		double maxSingle = mMaxLeftTime;
		if( mMaxRightTime > mMaxLeftTime ) maxSingle = mMaxRightTime;
		
		mBothLayout.addView(StatisticsGraphViewUtils.createGraphViewFromData(mContext, mBothData, mMaxBothTime, mNursingDays, mContext.getString(R.string.bothtimes)));
		mLeftLayout.addView(StatisticsGraphViewUtils.createGraphViewFromData(mContext, mRightData, maxSingle, mNursingDays, mContext.getString(R.string.lefttimes)));
		mRightLayout.addView(StatisticsGraphViewUtils.createGraphViewFromData(mContext, mLeftData, maxSingle, mNursingDays, mContext.getString(R.string.righttimes)));
		
		updateWhatSleepIsShown();
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
		mRightData = new GraphViewData[mNursingDays];
		mLeftData = new GraphViewData[mNursingDays];
		mBothData = new GraphViewData[mNursingDays];
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
			mBothData[indexForValueArray] = new GraphViewData(i, daysCombined);
			mRightData[indexForValueArray] = new GraphViewData(i, daysLeft);
			mLeftData[indexForValueArray] = new GraphViewData(i, daysRight);
			
			if( mMaxBothTime < daysCombined ) mMaxBothTime = daysCombined;
			if( mMaxLeftTime < daysLeft ) mMaxLeftTime = daysLeft;
			if( mMaxRightTime < daysRight ) mMaxRightTime = daysRight;
			
			indexForValueArray++;
		}
		
	}	

	private void updateWhatSleepIsShown() {
		Log.i("BabySchedule", "updateWhatSleepIsShown()");
		CheckBox napCheckBox = (CheckBox) findViewById(R.id.leftSelection);
		CheckBox sleepCheckBox = (CheckBox) findViewById(R.id.rightSelection);
		CheckBox combinedCheckBox = (CheckBox) findViewById(R.id.bothSelection);
		mGraphLayout.removeAllViews();
		
		if( !napCheckBox.isChecked() && !sleepCheckBox.isChecked() && !combinedCheckBox.isChecked() ) {
			mGraphLayout.addView(StatisticsGraphViewUtils.createGraphViewFromData(mContext, null, 0, 0, ""));
			return;
		}
		
		if(combinedCheckBox.isChecked() ) {
			mGraphLayout.addView(mBothLayout);
		}
		if( napCheckBox.isChecked() ) {
			mGraphLayout.addView(mLeftLayout);
		}
		if( sleepCheckBox.isChecked() ) {
			mGraphLayout.addView(mRightLayout);
		}
		
	}

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.leftSelection || v.getId() == R.id.rightSelection || v.getId() == R.id.bothSelection )
			updateWhatSleepIsShown();
		
	}
}
