package fi.vincit.babyschedule.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;

import fi.vincit.babyschedule.R;

public class BarStatistics extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barstatistics);
		
		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
				new GraphViewData(1, 375d)
				, new GraphViewData(2, 325d)
				, new GraphViewData(3, 500d) // another frequency
				, new GraphViewData(4, 415d)
				, new GraphViewData(5, 290d)
				, new GraphViewData(6, 360d)
				, new GraphViewData(7, 120d)
		});

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
