package fi.vincit.babyschedule.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import fi.vincit.babyschedule.BabyEvent;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.ScheduleDatabase;
import fi.vincit.babyschedule.adapters.AllEventsListAdapter;

public class AllEventsList extends ListActivity 
							implements View.OnClickListener {
	
	private AllEventsListAdapter mListAdapter;
	
	@Override
	public void onCreate(Bundle b) {
		Log.d("Babyschedule", "Single action onCreate()");
    	super.onCreate(b);
    	
    	ScheduleDatabase.open(getApplicationContext());            
    	
    	setTitle("List of all events");    
    	
    	mListAdapter = new AllEventsListAdapter(ScheduleDatabase.getAllDbActionsSortedByDate());
    	setListAdapter(mListAdapter);
	}
	
	@Override
    public void onClick(View v){
		if( v.getId() == R.id.eventItem ) {
			Intent i = new Intent(this, EventEditor.class);
			Bundle b = new Bundle();
			b.putSerializable("EVENT", (BabyEvent)v.getTag());
			i.putExtras(b);
			startActivity(i);
		}
    }
	
	@Override
    public void onStart() {
    	super.onStart();
    	mListAdapter.setActionsList(ScheduleDatabase.getAllDbActionsSortedByDate());
    }
}
