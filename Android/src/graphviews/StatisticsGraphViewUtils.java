package graphviews;

import android.content.Context;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;

import fi.vincit.babyschedule.R;

public class StatisticsGraphViewUtils {
	
	
	public static GraphView createGraphViewFromData(Context context, GraphViewData[] data, double maxYValue, int numberOfDays, String title) {
		if( numberOfDays == 0 || data.length == 0) {
			return new BarGraphView(context, context.getString(R.string.nodata));
		}
		GraphViewSeries dataSeries = new GraphViewSeries(data);

		// graph with dynamically generated horizontal and vertical labels
		GraphView graphView = new BarGraphView(context, title);
		
		// custom static labels
		graphView.addSeries(dataSeries); // data
		graphView.setManualYAxisBounds(maxYValue, 0);
		graphView.setScrollable(true);		
		
		if( numberOfDays > 30 ) {
			graphView.setViewPort(numberOfDays-30, 30);
			graphView.setScrollable(true);
		}
		
		return graphView;
	}
}
