package fi.vincit.babyschedule.activities;

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
	long nursingStartTimeStamp;
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);        	
    	setContentView(R.layout.nursing_activity);
    	
    	nursingStartTimeStamp = System.currentTimeMillis();
    	
    	Bundle actionNameBundle = this.getIntent().getExtras();
    	nursingLeft = actionNameBundle.getBoolean("LEFT");
    	
    	Chronometer meter = (Chronometer)findViewById(R.id.nursingChrono);
    	meter.start();
    	
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
			Chronometer meter = (Chronometer)findViewById(R.id.nursingChrono);
			meter.stop();
	    	meter.setBase(SystemClock.elapsedRealtime());
	    	meter.start();
	    	nursingLeft = !nursingLeft;
	    	setHeadlineText();
		}
		else if( v.getId() == R.id.finish_bursing_btn ) {
			finish();
		}
	}
	
}

