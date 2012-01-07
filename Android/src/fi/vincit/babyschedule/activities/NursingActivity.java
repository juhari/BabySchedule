package fi.vincit.babyschedule.activities;

import java.util.Date;

import utils.ScheduleDatabase;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.TextView;
import fi.vincit.babyschedule.R;

public class NursingActivity extends Activity 
							 implements OnClickListener {

	private boolean nursingLeft = true;
	long nursingLeftStartTimeStamp = 0;
	long nursingRightStartTimeStamp = 0;
	private Chronometer leftMeter;
	private Chronometer rightMeter;
	private long leftMeterMilliseconds = 0;
	private long rightMeterMilliseconds = 0;
	
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);        	
    	setContentView(R.layout.nursing_activity);    
    	
    	Bundle actionNameBundle = this.getIntent().getExtras();
    	nursingLeft = actionNameBundle.getBoolean("LEFT");
    	
    	leftMeter = (Chronometer)findViewById(R.id.nursingLeftChrono);
    	rightMeter = (Chronometer)findViewById(R.id.nursingRightChrono);
    	
    	if( nursingLeft ) {
    		nursingLeftStartTimeStamp = System.currentTimeMillis();
    		leftMeter.start();
    	}
    	else {
    		nursingRightStartTimeStamp = System.currentTimeMillis();
    		rightMeter.start();
    	}
    	
    	setHeadlineText();
	}

	private void setHeadlineText() {
		TextView headline = (TextView)findViewById(R.id.nursing_which);
    	if( nursingLeft ) {    		
    		headline.setText(getString(R.string.nursing_left));
    	}
    	else {
    		headline.setText(getString(R.string.nursing_right));
    	}
	}
	
	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.switch_breast_btn ) {		
			if( nursingLeft ) {
				leftMeterMilliseconds = SystemClock.elapsedRealtime() - leftMeter.getBase();
				leftMeter.stop();
				rightMeter.setBase(SystemClock.elapsedRealtime() - rightMeterMilliseconds);
				rightMeter.start();	
				if( nursingRightStartTimeStamp == 0 ) {
					rightMeter.setBase(SystemClock.elapsedRealtime());
					nursingRightStartTimeStamp = System.currentTimeMillis();
				}
			}
			else {
				rightMeterMilliseconds = SystemClock.elapsedRealtime() - rightMeter.getBase();
				rightMeter.stop();
				leftMeter.setBase(SystemClock.elapsedRealtime() - leftMeterMilliseconds);
				leftMeter.start();
				if( nursingLeftStartTimeStamp == 0 ) {
					leftMeter.setBase(SystemClock.elapsedRealtime());
					nursingLeftStartTimeStamp = System.currentTimeMillis();
				}
			}
	    	nursingLeft = !nursingLeft;
	    	setHeadlineText();
		}
		else if( v.getId() == R.id.finish_bursing_btn ) {
			if( nursingLeft ) {
				leftMeterMilliseconds = SystemClock.elapsedRealtime() - leftMeter.getBase();
			}
			else {
				rightMeterMilliseconds = SystemClock.elapsedRealtime() - rightMeter.getBase();
			}
			
			if( leftMeterMilliseconds > 0 ) {
				Date startDate = new Date(nursingLeftStartTimeStamp);
				int durationInSeconds = (int) (leftMeterMilliseconds/1000);
				ScheduleDatabase.insertBabyAction(Settings.getCurrentBabyName(), getString(R.string.milk_left), startDate, durationInSeconds);
			}
			if( rightMeterMilliseconds > 0 ) {
				Date startDate = new Date(nursingRightStartTimeStamp);
				int durationInSeconds = (int) (rightMeterMilliseconds/1000);
				ScheduleDatabase.insertBabyAction(Settings.getCurrentBabyName(), getString(R.string.milk_right), startDate, durationInSeconds);
			}
			
			finish();
		}
	}
	
}

