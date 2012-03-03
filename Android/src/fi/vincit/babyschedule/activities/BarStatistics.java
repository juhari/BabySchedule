package fi.vincit.babyschedule.activities;

import java.util.ArrayList;
import java.util.Date;

import utils.BabyEvent;
import utils.ConsumedTime;
import utils.ScheduleDatabase;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;

import fi.vincit.babyschedule.R;

public class BarStatistics extends Activity implements OnItemSelectedListener, OnClickListener {
	
	private Spinner mStatsChooser;
	private LinearLayout mGraphLayout;	
	private LinearLayout mSleepSelectionLayout;
	
	private GraphViewData[] mSleepData = null;
	private GraphViewData[] mNapData = null;
	private GraphViewData[] mCombinedSleepData = null;
	private double mMaxNapTime = 0.0;
	private double mMaxSleepTime = 0.0;
	private double mMaxCombinedSleepTime = 0.0;
	private int mSleepDays = 0;
	
	private GraphViewData[] mMilkData = null;
	private double mMaxMilk = 0.0;
	private int mMilkDays = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barstatistics);					
		
		mStatsChooser = (Spinner) findViewById(R.id.stats_chooser);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
				 R.array.statistics_types, 
				 android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mStatsChooser.setAdapter(adapter);   
		mStatsChooser.setOnItemSelectedListener(this);
		
		findViewById(R.id.napSelection).setOnClickListener(this);
		findViewById(R.id.sleepSelection).setOnClickListener(this);
		
		mGraphLayout = (LinearLayout) findViewById(R.id.bargraphlayout);	
		
		mSleepSelectionLayout = (LinearLayout) findViewById(R.id.sleepSelectionLayout);
		generateMilkDataGraphView();
		generateSleepGraphView();
	}
	
	private void generateMilkDataGraphView() {				
		
		ArrayList<Date> dates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), getString(R.string.milk_activity));
		Date oldest = dates.get(0);
		Date now = new Date();
		long numberOfDays = (now.getTime() - oldest.getTime());
		Log.i("BabySchedule", "BarStatistics: how many ms ago: " + numberOfDays);
		numberOfDays /= 24*60*60*1000;
		Log.i("BabySchedule", "BarStatistics: how many days ago: " + numberOfDays);
		numberOfDays++;
		
		mMilkDays = (int)numberOfDays;
		if( mMilkDays < 7 )
			mMilkDays = 7;
		mMilkData = new GraphViewData[mMilkDays];
		mMaxMilk = 0;
		// Acquire milk data for milk / day
		int indexForValueArray = 0;
		
		for( int i = (int)numberOfDays-1; i >= 0; i-- ) {
			ArrayList<BabyEvent> events = ScheduleDatabase.getAllDbActionsFromNumberOfDaysAgo(Settings.getCurrentBabyName(), 
																getString(R.string.milk_activity), i);
			int daysCombined = 0;
			for(BabyEvent event : events ) {
				daysCombined += event.getFreeValue();
				if( mMaxMilk < event.getFreeValue() ) 
					mMaxMilk = event.getFreeValue();
			}
			
			mMilkData[indexForValueArray] = new GraphViewData(indexForValueArray+1, daysCombined);
			
			indexForValueArray++;
		}			
	}		
	
	private void generateSleepGraphView() {
		
		ArrayList<Date> dates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), getString(R.string.go_to_sleep));
		ArrayList<Date> napDates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), getString(R.string.go_to_nap));
		Date oldest = dates.get(0);
		Date oldestNap  = napDates.get(0);
		if( oldestNap.getDate() < oldest.getDate() ) {
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
		
		for( int i = (int)numberOfDays-1; i >= 0; i-- ) {				
			ArrayList<BabyEvent> events = ScheduleDatabase.getAllDbActionsFromNumberOfDaysAgo(Settings.getCurrentBabyName(), 
																getString(R.string.go_to_sleep), i);
			events.addAll(ScheduleDatabase.getAllDbActionsFromNumberOfDaysAgo(Settings.getCurrentBabyName(), 
																getString(R.string.go_to_nap), i));
			ConsumedTime daysCombined = new ConsumedTime();
			ConsumedTime daysNap = new ConsumedTime();
			ConsumedTime daysSleep = new ConsumedTime();
			boolean isEventNap = false;
			for(BabyEvent event : events ) {
				isEventNap = event.getActionName().equalsIgnoreCase(getString(R.string.go_to_nap));
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
			mCombinedSleepData[indexForValueArray] = new GraphViewData(indexForValueArray+1, combinedSleepToday);
			mNapData[indexForValueArray] = new GraphViewData(indexForValueArray+1, napToday);
			mSleepData[indexForValueArray] = new GraphViewData(indexForValueArray+1, sleepToday);
			
			if( mMaxCombinedSleepTime < combinedSleepToday ) mMaxCombinedSleepTime = combinedSleepToday;
			if( mMaxNapTime < napToday ) mMaxNapTime = napToday;
			if( mMaxSleepTime < sleepToday ) mMaxSleepTime = sleepToday;
			
			indexForValueArray++;
		}
	}
	
	private GraphView createGraphViewFromData(GraphViewData[] data, double maxYValue, int numberOfDays, String title) {
		if( data == null ) {
			return new BarGraphView(this, getString(R.string.nodata));
		}
		GraphViewSeries dataSeries = new GraphViewSeries(data);

		// graph with dynamically generated horizontal and vertical labels
		GraphView graphView = new BarGraphView(this, title);
		
		// custom static labels
		graphView.addSeries(dataSeries); // data
		graphView.setManualYAxisBounds(maxYValue, 0);
		graphView.setScrollable(true);		
		
		if( numberOfDays > 30 ) {
			graphView.setViewPort(numberOfDays-30, 30);
		}
		
		return graphView;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		String item = (String)mStatsChooser.getSelectedItem();
		if( item.equalsIgnoreCase(getString(R.string.milk_stats_str)) ) {
			mSleepSelectionLayout.setVisibility(View.GONE);
			mGraphLayout.removeAllViews();
			mGraphLayout.addView(createGraphViewFromData(mMilkData, mMaxMilk, mMilkDays, getString(R.string.milk_per_day)));
		}
		else if( item.equalsIgnoreCase(getString(R.string.sleep_stats_str)) ) {
			mSleepSelectionLayout.setVisibility(View.VISIBLE);
			updateWhatSleepIsShown();
		}
		
	}

	private void updateWhatSleepIsShown() {
		Log.i("BabySchedule", "updateWhatSleepIsShown()");
		CheckBox napCheckBox = (CheckBox) findViewById(R.id.napSelection);
		CheckBox sleepCheckBox = (CheckBox) findViewById(R.id.sleepSelection);
		mGraphLayout.removeAllViews();
		if( napCheckBox.isChecked() && sleepCheckBox.isChecked() ) {
			mGraphLayout.addView(createGraphViewFromData(mCombinedSleepData, mMaxCombinedSleepTime, mSleepDays, getString(R.string.allsleeptimes)));
		}
		else if( napCheckBox.isChecked() ) {
			mGraphLayout.addView(createGraphViewFromData(mNapData, mMaxNapTime, mSleepDays, getString(R.string.naptimes)));
		}
		else if( sleepCheckBox.isChecked() ) {
			mGraphLayout.addView(createGraphViewFromData(mSleepData, mMaxSleepTime, mSleepDays, getString(R.string.sleeptimes)));
		}
		else if( !napCheckBox.isChecked() && !sleepCheckBox.isChecked() ) {
			mGraphLayout.addView(createGraphViewFromData(null, 0, 0, ""));
		}
	}

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.napSelection || v.getId() == R.id.sleepSelection )
			updateWhatSleepIsShown();
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
