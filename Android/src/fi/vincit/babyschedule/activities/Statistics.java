package fi.vincit.babyschedule.activities;

import java.util.ArrayList;
import java.util.Date;

import utils.ConsumedTime;
import utils.ScheduleDatabase;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import fi.vincit.babyschedule.R;

public class Statistics extends Activity {
			
	@Override
	public void onCreate(Bundle b) {
		Log.d("Babyschedule", "AddActionActivity::onCreate()");
    	super.onCreate(b);    	
    	ScheduleDatabase.open(getApplicationContext());            	
    	setContentView(R.layout.statistics);
    	
    	ArrayList<Date> toSleepDates = ScheduleDatabase.getActionDatesForAction("verneri", getString(R.string.go_to_sleep));
    	ArrayList<Date> napDates = ScheduleDatabase.getActionDatesForAction("verneri", getString(R.string.go_to_nap));
    	
    	TextView view = (TextView) findViewById(R.id.sleep_stats);
    	ArrayList<ConsumedTime> sleepTimes = new ArrayList<ConsumedTime>();
    	ArrayList<ConsumedTime> napTimes = new ArrayList<ConsumedTime>();
    	for( Date sleepDate : toSleepDates ) {
    		ConsumedTime sleepTime = ScheduleDatabase.getDurationOfSleepStartedAt("verneri", sleepDate);
    		if( sleepTime != null ) {
    			sleepTimes.add(sleepTime);
    		}
    	}
    	for( Date napDate : napDates ) {
    		ConsumedTime sleepTime = ScheduleDatabase.getDurationOfSleepStartedAt("verneri", napDate);
    		if( sleepTime != null ) {
    			napTimes.add(sleepTime);
    		}
    	}
    	
    	ConsumedTime sleepAvg = ConsumedTime.getAverageOf(sleepTimes);
    	ConsumedTime napAvg = ConsumedTime.getAverageOf(napTimes);
    	
    	view.setText("Average length of night sleep:\n	" + sleepAvg.toString() + "\n\n" +
    				 "Average length of nap:\n	" + napAvg.toString());
	}
}
