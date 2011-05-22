package fi.vincit.babyschedule.activities;

import java.util.Date;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import fi.vincit.babyschedule.BabyEvent;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.ScheduleDatabase;

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
    	
    	mListAdapter = new SingleEventListAdapter(ScheduleDatabase.getActionDatesForAction(mActionName), mActionName);
    	setListAdapter(mListAdapter);
	}
	
	@Override
    public void onStart() {
    	super.onStart();
    	mListAdapter.setDateList(ScheduleDatabase.getActionDatesForAction(mActionName));
    }
	
	@Override
    public void onClick(View v){
		if( v.getId() == R.id.eventItem ) {
			Intent i = new Intent(this, EventEditor.class);
			Bundle b = new Bundle();
			BabyEvent event = new BabyEvent(mActionName, (Date)v.getTag());
			b.putSerializable("EVENT", event);
			i.putExtras(b);
			startActivity(i);
		}
    }

}
