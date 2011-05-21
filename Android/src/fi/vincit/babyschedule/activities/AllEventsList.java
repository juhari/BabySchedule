package fi.vincit.babyschedule.activities;

import java.util.Date;

import fi.vincit.babyschedule.ScheduleDatabase;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
    	ScheduleDatabase.deleteEntryBasedOnDate((Date)v.getTag());
    	mListAdapter.setActionsList(ScheduleDatabase.getAllDbActionsSortedByDate());
    }
}
