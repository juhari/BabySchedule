package fi.vincit.babyschedule.activities;

import java.util.ArrayList;

import utils.BabyEvent;
import utils.ScheduleDatabase;
import android.app.Activity;
import android.os.Bundle;
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
		
		GraphViewData[] data = new GraphViewData[7];
		
		// Acquire milk data for milk / day
		int indexForValueArray = 0;
		for( int i = 6; i >= 0; i-- ) {
			ArrayList<BabyEvent> events = ScheduleDatabase.getAllDbActionsFromNumberOfDaysAgo(Settings.getCurrentBabyName(), 
																getString(R.string.milk_activity), i);
			int daysCombined = 0;
			for(BabyEvent event : events ) {
				daysCombined += event.getFreeValue();
			}
			data[indexForValueArray] = new GraphViewData(indexForValueArray+1, daysCombined);
			
			indexForValueArray++;
		}
		
		
		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries(data);

		// graph with dynamically generated horizontal and vertical labels
		GraphView graphView = new BarGraphView(
					this // context
					, "Milk statistics" // heading
			);
		
		// custom static labels
		graphView.setHorizontalLabels(new String[] {"2 days ago", "yesterday", "today"});
		graphView.setVerticalLabels(new String[] {"500ml", "250ml", "0ml"});
		graphView.addSeries(exampleSeries); // data
		graphView.setManualYAxisBounds(500, 0);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.bargraphlayout);
		layout.addView(graphView);

		
	}
	
}
