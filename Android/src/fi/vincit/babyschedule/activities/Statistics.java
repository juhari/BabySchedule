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
    	
    	ArrayList<Date> toSleepDates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), getString(R.string.go_to_sleep));
    	ArrayList<Date> napDates = ScheduleDatabase.getActionDatesForAction(Settings.getCurrentBabyName(), getString(R.string.go_to_nap));
    	
    	TextView view = (TextView) findViewById(R.id.sleep_stats);
    	ArrayList<ConsumedTime> sleepTimes = new ArrayList<ConsumedTime>();
    	ArrayList<ConsumedTime> napTimes = new ArrayList<ConsumedTime>();
    	for( Date sleepDate : toSleepDates ) {
    		ConsumedTime sleepTime = ScheduleDatabase.getDurationOfSleepStartedAt(Settings.getCurrentBabyName(), sleepDate);
    		if( sleepTime != null ) {
    			sleepTimes.add(sleepTime);
    		}
    	}
    	for( Date napDate : napDates ) {
    		ConsumedTime sleepTime = ScheduleDatabase.getDurationOfSleepStartedAt(Settings.getCurrentBabyName(), napDate);
    		if( sleepTime != null ) {
    			napTimes.add(sleepTime);
    		}
    	}
    	
    	ConsumedTime sleepAvg = null;
    	ConsumedTime napAvg = null;
    	if( sleepTimes.size() > 0 ) {
    		sleepAvg = ConsumedTime.getAverageOf(sleepTimes);
    	} else {
    		sleepAvg = new ConsumedTime();
    	}
    	
    	if( napTimes.size() > 0 ) {
    		napAvg = ConsumedTime.getAverageOf(napTimes);
    	} else {
    		napAvg = new ConsumedTime();
    	}
    	
    	view.setText("Average length of night sleep:\n	" + sleepAvg.toString() + "\n\n" +
    				 "Average length of nap:\n	" + napAvg.toString());
	}
}
