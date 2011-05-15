package fi.vincit.babyschedule;

import java.util.Date;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AllActionsList extends ListActivity 
							implements View.OnClickListener {
	
	private AllActionsListAdapter mListAdapter;
	
	@Override
	public void onCreate(Bundle b) {
		Log.d("Babyschedule", "Single action onCreate()");
    	super.onCreate(b);
    	
    	ScheduleDatabase.open(getApplicationContext());            
    	
    	setTitle("List of all events");    
    	
    	mListAdapter = new AllActionsListAdapter(ScheduleDatabase.getAllDbActions(getResources().getStringArray(R.array.activity_names)));
    	setListAdapter(mListAdapter);
	}
	
	@Override
    public void onClick(View v){
    	ScheduleDatabase.deleteEntryBasedOnDate((Date)v.getTag());
    	mListAdapter.setActionsList(ScheduleDatabase.getAllDbActions(getResources().getStringArray(R.array.activity_names)));
    }
}
