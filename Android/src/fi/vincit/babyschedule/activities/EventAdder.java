package fi.vincit.babyschedule.activities;

import android.view.View;
import fi.vincit.babyschedule.R;

public class EventAdder extends EventDetailsEditor {

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.saveButton ) {
			if( saveEvent() ) {			
				finish();
			}
		}
		else {
            super.onClick(v);
        }
	}
	
}
