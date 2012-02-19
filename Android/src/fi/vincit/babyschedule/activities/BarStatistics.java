package fi.vincit.babyschedule.activities;

import java.util.ArrayList;
import java.util.Date;

import utils.BabyEvent;
import utils.ScheduleDatabase;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;

import fi.vincit.babyschedule.R;

public class BarStatistics extends Activity {
	
	int[] values;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barstatistics);				
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.bargraphlayout);
		ArrayList<Date> dates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), getString(R.string.milk_activity));
		Date oldest = dates.get(0);
		Date now = new Date();
		long howManyDaysAgo = (now.getTime() - oldest.getTime());
		Log.i("BabySchedule", "BarStatistics: how many ms ago: " + howManyDaysAgo);
		howManyDaysAgo /= 24*60*60*1000;
		Log.i("BabySchedule", "BarStatistics: how many days ago: " + howManyDaysAgo);
		howManyDaysAgo++;
		
		layout.addView(generateMilkDataGraphView((int)howManyDaysAgo));	
	}
	
	private GraphView generateMilkDataGraphView(int numberOfDays) {
		if( numberOfDays < 7 )
			numberOfDays = 7;
		GraphViewData[] data = new GraphViewData[numberOfDays];
		int maxAmountOfMilk = 0;
		// Acquire milk data for milk / day
		int indexForValueArray = 0;
		
		for( int i = numberOfDays-1; i >= 0; i-- ) {
			ArrayList<BabyEvent> events = ScheduleDatabase.getAllDbActionsFromNumberOfDaysAgo(Settings.getCurrentBabyName(), 
																getString(R.string.milk_activity), i);
			int daysCombined = 0;
			for(BabyEvent event : events ) {
				daysCombined += event.getFreeValue();
				if( maxAmountOfMilk < event.getFreeValue() ) 
					maxAmountOfMilk = event.getFreeValue();
			}
			
			data[indexForValueArray] = new GraphViewData(indexForValueArray+1, daysCombined);
			
			indexForValueArray++;
		}
		
		GraphViewSeries dataSeries = new GraphViewSeries(data);

		// graph with dynamically generated horizontal and vertical labels
		GraphView graphView = new BarGraphView(
					this // context
					, getString(R.string.milk_per_day) // heading
			);
		
		// custom static labels
		graphView.addSeries(dataSeries); // data
		graphView.setManualYAxisBounds(maxAmountOfMilk, 0);
		graphView.setScrollable(true);		
		
		if( numberOfDays > 30 ) {
			graphView.setViewPort(numberOfDays-30, 30);
		}
		
		return graphView;
	}
	
}
