package fi.vincit.babyschedule.activities;

import java.util.Date;

import utils.BabyEvent;
import utils.ScheduleDatabase;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import fi.vincit.babyschedule.R;

public class EventEditor extends EventDetailsEditor {

	private BabyEvent mEvent;
	
	@Override
	public void onCreate(Bundle b) {
		Log.d("Babyschedule", "EventEditor::onCreate()");
    	super.onCreate(b);    	
    	setTitle(R.string.edit_event);
    	
    	// acquire required information from bundle and set it to the spinner, datepick and timepick
    	Bundle eventBundle = this.getIntent().getExtras();
    	mEvent = (BabyEvent)eventBundle.getSerializable("EVENT");
    	
    	Log.d("Babyschedule", "Editing event: " + mEvent.getActionName());
    	
    	getmDatePicker().updateDate(mEvent.getLastAction().getYear() + 1900, 
    						  		mEvent.getLastAction().getMonth(), 
						  			mEvent.getLastAction().getDate());
    	
    	getmTimePicker().setCurrentHour(mEvent.getLastAction().getHours());
    	getmTimePicker().setCurrentMinute(mEvent.getLastAction().getMinutes());
    	
    	String[] activityNames = getResources().getStringArray(R.array.activity_names); 
    	int correctPosition = 0;
    	for(String name : activityNames ) {
    		if( name.equals(mEvent.getActionName()) ) {
    			break;
    		}
    		Log.d("Babyschedule", "No match: " + name + "," + mEvent.getActionName());
    		
    		correctPosition++;
    	}
    	
    	getmSpinner().setSelection(correctPosition);    	    
    	
    	updateDescription();
	}
	
	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.saveButton ) {			
			Date dateTime = getDateTimeFromSpinners();
			ScheduleDatabase.insertBabyAction("Verneri", (String)getmSpinner().getSelectedItem(), dateTime);
			ScheduleDatabase.deleteEntryBasedOnDate(mEvent.getLastAction());
			finish();
		}
		else if( v.getId() == R.id.cancelButton ) {
			finish();
		}
	}
	
}
