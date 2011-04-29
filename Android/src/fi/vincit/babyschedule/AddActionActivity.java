package fi.vincit.babyschedule;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

public class AddActionActivity extends Activity
							   implements OnClickListener {

	private DatePicker mDatePicker;
	private TimePicker mTimePicker;
	private Spinner mSpinner;
	
	@Override
	public void onCreate(Bundle b) {
		Log.d("Babyschedule", "AddActionActivity::onCreate()");
    	super.onCreate(b);
    	
    	ScheduleDatabase.open(getApplicationContext());        
    	
    	setContentView(R.layout.add_activity);
    	
    	mSpinner = (Spinner) findViewById(R.id.actionsSpinner);
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
    																		 R.array.activity_names, 
    																		 android.R.layout.simple_spinner_item);
    	
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	mSpinner.setAdapter(adapter);
    	
    	mDatePicker = (DatePicker)findViewById(R.id.datePick);
    	mTimePicker = (TimePicker)findViewById(R.id.timePick);
    	
    	setTitle(R.string.add_activity);
	}
	
	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.addButton ) {
			Date dateTime = new Date();
			dateTime.setYear(mDatePicker.getYear()-1900);
			dateTime.setMonth(mDatePicker.getMonth());
			dateTime.setDate(mDatePicker.getDayOfMonth());
			dateTime.setHours(mTimePicker.getCurrentHour());
			dateTime.setMinutes(mTimePicker.getCurrentMinute());
			dateTime.setSeconds(0);
			ScheduleDatabase.insertBabyAction("Verneri", (String)mSpinner.getSelectedItem(), dateTime);			
			finish();
		}
	}
}
