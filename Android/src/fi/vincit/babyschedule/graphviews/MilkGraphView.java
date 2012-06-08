package fi.vincit.babyschedule.graphviews;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.activities.Settings;
import fi.vincit.babyschedule.utils.BabyEvent;
import fi.vincit.babyschedule.utils.ScheduleDatabase;

public class MilkGraphView extends LinearLayout {

	Context mContext;
	
	private double[] mMilkData = null;
	private double mMaxMilk = 0.0;
	private int mMilkDays = 0;
	
	public MilkGraphView(Context context) {
		super(context);			
		mContext = context;		
	
		initializeGraphData();
		List<double[]> milkData = new ArrayList<double[]>();
		milkData.add(mMilkData);
		String[] seriesTitles = new String[] { mContext.getString(R.string.milk_per_day) };
		View gv = StatisticsGraphViewUtils.createGraphViewFromData(
				mContext, seriesTitles, milkData, mMaxMilk, mMilkDays, "");
		addView(gv);
	}

	public void initializeGraphData() {
		ArrayList<Date> dates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), 
																		 mContext.getString(R.string.milk_activity));
		if( dates.size() == 0 ) {
			return;
		}
		Date oldest = dates.get(0);
		Date now = new Date();
		long numberOfDays = (now.getTime() - oldest.getTime());
		Log.d("Babyschedule", "BarStatistics: how many ms ago: " + numberOfDays);
		numberOfDays /= 24*60*60*1000;		
		numberOfDays++;
		Log.d("Babyschedule", "BarStatistics: how many days ago: " + numberOfDays);
		
		mMilkDays = (int)numberOfDays;
		if( mMilkDays < 7 )
			mMilkDays = 7;
		mMilkDays++;
		mMilkData = new double[mMilkDays];
		mMaxMilk = 0;
		// Acquire milk data for milk / day
		int indexForValueArray = 0;
		
		for( int i = (int)mMilkDays-1; i >= 0; i-- ) {
			ArrayList<BabyEvent> events = ScheduleDatabase.getAllDbActionsFromNumberOfDaysAgo(Settings.getCurrentBabyName(), 
																mContext.getString(R.string.milk_activity), i);
			int daysCombined = 0;
			for(BabyEvent event : events ) {
				daysCombined += event.getFreeValue();				
			}
			
			if( mMaxMilk < daysCombined ) 
                mMaxMilk = daysCombined;
			
			Log.d("Babyschedule", "data for day #" + i + ": " + daysCombined);
			mMilkData[indexForValueArray] = daysCombined;
			
			indexForValueArray++;
		}			
		
	}	

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
	}
}
