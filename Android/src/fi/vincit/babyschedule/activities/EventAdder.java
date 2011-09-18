package fi.vincit.babyschedule.activities;

import java.util.Date;

import utils.ScheduleDatabase;

import android.view.View;
import fi.vincit.babyschedule.R;

public class EventAdder extends EventDetailsEditor {

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.saveButton ) {
			Date dateTime = getDateTimeFromSpinners();
			ScheduleDatabase.insertBabyAction(Settings.getCurrentBabyName(), (String)getmSpinner().getSelectedItem(), dateTime);			
			finish();
		}
		else if( v.getId() == R.id.cancelButton ) {
			finish();
		}
	}
	
}
