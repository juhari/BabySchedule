package fi.vincit.babyschedule.activities;

import java.util.Date;

import android.view.View;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.ScheduleDatabase;

public class EventAdder extends EventDetailsEditor {

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.saveButton ) {
			Date dateTime = getDateTimeFromSpinners();
			ScheduleDatabase.insertBabyAction("Verneri", (String)getmSpinner().getSelectedItem(), dateTime);			
			finish();
		}
		else if( v.getId() == R.id.cancelButton ) {
			finish();
		}
	}
	
}
