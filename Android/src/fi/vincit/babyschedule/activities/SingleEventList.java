package fi.vincit.babyschedule.activities;

import java.util.Date;

import fi.vincit.babyschedule.ScheduleDatabase;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SingleEventList extends ListActivity 
						      implements View.OnClickListener {

	private SingleEventListAdapter mListAdapter;
	private String mActionName;
	
	@Override
	public void onCreate(Bundle b) {
		Log.d("Babyschedule", "Single action onCreate()");
    	super.onCreate(b);
    	
    	ScheduleDatabase.open(getApplicationContext());        
    	
    	Bundle actionNameBundle = this.getIntent().getExtras();
    	mActionName = actionNameBundle.getString("ACTIONNAME");
    	
    	setTitle("List of " + mActionName + " events");
    	
    	Log.d("Babyschedule", "Single action onCreate(): action name: " + mActionName);
    	
    	mListAdapter = new SingleEventListAdapter(ScheduleDatabase.getActionDatesForAction(mActionName));
    	setListAdapter(mListAdapter);
	}
	
	@Override
    public void onClick(View v){
    	ScheduleDatabase.deleteEntryBasedOnDate((Date)v.getTag());
    	mListAdapter.setDateList(ScheduleDatabase.getActionDatesForAction(mActionName));
    }

}
