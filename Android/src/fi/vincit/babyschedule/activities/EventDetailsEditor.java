package fi.vincit.babyschedule.activities;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.ScheduleDatabase;

public class EventDetailsEditor extends Activity
							   implements OnClickListener
							   			  , DatePicker.OnDateChangedListener
							   			  , TimePicker.OnTimeChangedListener
							   			  , OnItemSelectedListener {

	private DatePicker mDatePicker;
	private TimePicker mTimePicker;
	private Spinner mSpinner;
	private TextView mDescription;
	
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
    	mSpinner.setOnItemSelectedListener(this);
    	mDatePicker = (DatePicker)findViewById(R.id.datePick);
    	mTimePicker = (TimePicker)findViewById(R.id.timePick);
    	mTimePicker.setIs24HourView(true);
    	
    	mDescription = (TextView)findViewById(R.id.descriptionText);
    	    	
    	Date now = new Date();
    	mDatePicker.init(now.getYear() + 1900, now.getMonth(), now.getDate(), this);
    	mTimePicker.setOnTimeChangedListener(this);
    	
    	setTitle(R.string.add_activity);
    	
    	updateDescription();
	}
	
	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.saveButton ) {
			Date dateTime = getDateTimeFromSpinners();
			ScheduleDatabase.insertBabyAction("Verneri", (String)mSpinner.getSelectedItem(), dateTime);			
			finish();
		}
		else if( v.getId() == R.id.cancelButton ) {
			finish();
		}
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		updateDescription();		
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		updateDescription();		
	}
	
	public void updateDescription() {
		Date dateTime = getDateTimeFromSpinners();
		
		mDescription.setText("Add event: " + 
						   (String)mSpinner.getSelectedItem() + 
						   ", " + 
						   dateTime.toLocaleString());
	}
	
	public Date getDateTimeFromSpinners() {
		Date dateTime = new Date();
		dateTime.setYear(mDatePicker.getYear()-1900);
		dateTime.setMonth(mDatePicker.getMonth());
		dateTime.setDate(mDatePicker.getDayOfMonth());
		dateTime.setHours(mTimePicker.getCurrentHour());
		dateTime.setMinutes(mTimePicker.getCurrentMinute());
		dateTime.setSeconds(0);
		return dateTime;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		updateDescription();		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		updateDescription();		
	}
}
