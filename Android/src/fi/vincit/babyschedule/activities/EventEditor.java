package fi.vincit.babyschedule.activities;

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
    	
    	getmDatePicker().updateDate(mEvent.getActionDate().getYear() + 1900, 
    						  		mEvent.getActionDate().getMonth(), 
						  			mEvent.getActionDate().getDate());
    	
    	getmTimePicker().setCurrentHour(mEvent.getActionDate().getHours());
    	getmTimePicker().setCurrentMinute(mEvent.getActionDate().getMinutes());
    	
    	String[] activityNames = getResources().getStringArray(R.array.activity_names); 
    	int correctPosition = 0;
    	for(String name : activityNames ) {
    		if( name.equals(mEvent.getActionName()) ) {
    			break;
    		}
    		//Log.d("Babyschedule", "No match: " + name + "," + mEvent.getActionName());
    		
    		correctPosition++;
    	}
    	
    	getmSpinner().setSelection(correctPosition);    	        	
    	
    	if( isMilkEventSelected()) {
    		getmExtraInput().setText(""+mEvent.getFreeValue());
    	}
    	if( isNursingEventSelected()) {
    		getmExtraInput().setText(""+mEvent.getDurationInSeconds()/60);
    	}
    	
    	updateDescription();
	}
	
	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.saveButton ) {	
			if( saveEvent() ) {		
				String babyName = Settings.getCurrentBabyName();
				ScheduleDatabase.deleteEntryBasedOnDate(babyName, mEvent.getActionDate());
				finish();
			}
		}
		else if( v.getId() == R.id.cancelButton ) {
			finish();
		}
	}					
}
