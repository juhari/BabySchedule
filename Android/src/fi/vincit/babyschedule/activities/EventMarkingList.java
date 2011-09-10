package fi.vincit.babyschedule.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import utils.ScheduleDatabase;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.adapters.EventMarkingListAdapter;

public class EventMarkingList extends ListActivity
							  implements View.OnCreateContextMenuListener
							  			,View.OnLongClickListener {		
	
	private EventMarkingListAdapter mListAdapter;
	@SuppressWarnings("unused")
	private CountDownTimer mListUpdatetimer;
	private String mSelectedEventName = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("Babyschedule", "Main view onCreate()");
    	super.onCreate(savedInstanceState);    	    	
    	
    	mListAdapter = new EventMarkingListAdapter(getBaseContext());    	
    	
    	updateMainListAdapter();    	    	
    	setListAdapter(mListAdapter);
    	
    	registerForContextMenu(getListView());
    	
    	this.mListUpdatetimer = new CountDownTimer(60000, 60000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFinish() {
				mListAdapter.notifyDataSetChanged();
				start();
			}
		}.start();
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	updateMainListAdapter();
    }

    @Override
    public void onResume() {
    	super.onResume();
    	updateMainListAdapter();
    }
    
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {    	
    	super.onCreateContextMenu(menu, v, menuInfo);    	
    	
		MenuInflater inflater = getMenuInflater();
		if( mSelectedEventName.equalsIgnoreCase(getString(R.string.go_to_sleep)) ) {
			inflater.inflate(R.menu.marksleepeventscontext, menu);
		} else {
			inflater.inflate(R.menu.markeventscontext, menu);
		}		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		String actionName = (String)info.targetView.getTag();	  
		
		switch (item.getItemId()) {
			case R.id.mark_nightsleep_activity:
			{
				eventMarkedNow(getString(R.string.go_to_sleep));	    	
				return true;
			}
			case R.id.mark_nap_activity:
			{
				eventMarkedNow(getString(R.string.go_to_nap));	    	
				return true;
			}
			case R.id.mark_activity:
			{				  
		    	eventMarkedNow(actionName);
		    	return true;
			}
			case R.id.show_these_events:
			{    
		    	showSelectedEventList(actionName);
		    	return true;
			}
			default:
				return super.onContextItemSelected(item);
			}
	}
	
	private void eventMarkedNow(String eventName) {
		Log.d("Babyschedule", "Marked event" + eventName);	    	
    	ScheduleDatabase.insertBabyAction("Verneri", eventName, new Date());	    	
    	mListAdapter.notifyDataSetChanged();     
    	
    	updateMainListAdapter();
	}
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	mSelectedEventName = (String)v.getTag();
    	Log.d("Babyschedule", "Clicked button" + mSelectedEventName);
    	v.performLongClick();
    }
    
	@Override
	public boolean onLongClick(View v) {
		// consume long clicks, so that we'll just get the clicks to list items
		// For some reason, the v.performLongClick in onListItemClick will still
		// bring up the context menu
		return true;
	}
    
    private void showSelectedEventList(String actionName) {
    	if( ScheduleDatabase.getActionDatesForAction("verneri", actionName).size() > 0 ) {
    		// show list of actions for the specified type
    		Intent showSingleList = new Intent(EventMarkingList.this, SingleEventList.class);   
    		Bundle actionBundle = new Bundle();
    		actionBundle.putString("ACTIONNAME", actionName);
    		showSingleList.putExtras(actionBundle);
    		startActivity(showSingleList);
    	}
    }

    public boolean isCurrentlyAsleep() {
    	ArrayList<Date> toSleepDates = ScheduleDatabase.getActionDatesForAction("verneri", getResources().getString(R.string.go_to_sleep));
    	toSleepDates.addAll(ScheduleDatabase.getActionDatesForAction("verneri", getResources().getString(R.string.go_to_nap)));
    	ArrayList<Date> wakeUpDates = ScheduleDatabase.getActionDatesForAction("verneri", getResources().getString(R.string.woke_up));
    	
    	Collections.sort(toSleepDates);
    	
    	if( toSleepDates.isEmpty() || wakeUpDates.isEmpty() ) {
    		return toSleepDates.size() > wakeUpDates.size();    		
    	}
    	else {
    		Date latestToSleep = toSleepDates.get(toSleepDates.size()-1);
    		Date latestWakeUp = wakeUpDates.get(wakeUpDates.size()-1);    	
    		return latestToSleep.after(latestWakeUp);
    	}
    }
    
    private void updateMainListAdapter() {
    	String[] activityNames;
    	if( isCurrentlyAsleep() ) {
    		// setting the title is disabled, as it is not visible within tabbed widgets
    		//setTitle(getResources().getString(R.string.app_name) + ": Baby is now asleep");
    		activityNames = getResources().getStringArray(R.array.sleep_activities); 
    	} else {
    		//setTitle(getResources().getString(R.string.app_name) + ": Baby is now awake");
    		activityNames = getResources().getStringArray(R.array.awake_activities);
    	}    	
    	
    	mListAdapter.setActionList(activityNames);    	    	
    }
}