package fi.vincit.babyschedule;

import java.util.ArrayList;
import java.util.Date;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import fi.vincit.babyschedule.activities.AllEventsList;
import fi.vincit.babyschedule.activities.EventAdder;
import fi.vincit.babyschedule.activities.SingleEventList;

public class BabyScheduleMain extends ListActivity
							  implements View.OnClickListener {		
	
	private MainListAdapter mListAdapter;
	@SuppressWarnings("unused")
	private CountDownTimer mListUpdatetimer;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("Babyschedule", "Main view onCreate()");
    	super.onCreate(savedInstanceState);
    	
    	ScheduleDatabase.open(getApplicationContext());
    	
    	mListAdapter = new MainListAdapter();    	
    	
    	updateMainListAdapter();    	    	
    	setListAdapter(mListAdapter);
    	
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
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainlistmenu, menu);
    	return true;
    }
    
    @Override
    public void onClick(View v){
    	BabyEvent action = (BabyEvent)v.getTag();
    	    	
    	if( v.getId() == R.id.Now ) {	    
	    	Log.d("Babyschedule", "Clicked button" + action.getActionName());	    	
	    	ScheduleDatabase.insertBabyAction("Verneri", action.getActionName(), new Date());	    	
	    	mListAdapter.updateActivityTimeNow(action);    	    
    	} else if( ScheduleDatabase.getActionDatesForAction(action.getActionName()).size() > 0 ) {
    		// show list of actions for the specified type
    		Intent showSingleList = new Intent(BabyScheduleMain.this, SingleEventList.class);   
    		Bundle actionBundle = new Bundle();
    		actionBundle.putString("ACTIONNAME", action.getActionName());
    		showSingleList.putExtras(actionBundle);
    		startActivity(showSingleList);
    	}
    	
    	updateMainListAdapter();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if( item.getItemId() == R.id.add_activity ) {
	    	Intent showAddAction = new Intent(BabyScheduleMain.this, EventAdder.class);
	    	startActivity(showAddAction);
    	} else if( item.getItemId() == R.id.all_events ) {
    		Intent showEventsList = new Intent(BabyScheduleMain.this, AllEventsList.class);
    		startActivity(showEventsList);
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    public boolean isCurrentlyAsleep() {
    	ArrayList<Date> toSleepDates = ScheduleDatabase.getActionDatesForAction(getResources().getString(R.string.go_to_sleep));
    	ArrayList<Date> wakeUpDates = ScheduleDatabase.getActionDatesForAction(getResources().getString(R.string.woke_up));
    	
    	if( !toSleepDates.isEmpty() && !wakeUpDates.isEmpty() ) {
    		Date latestToSleep = toSleepDates.get(toSleepDates.size()-1);
    		Date latestWakeUp = wakeUpDates.get(wakeUpDates.size()-1);    	
    		return latestToSleep.after(latestWakeUp);
    	}
    	else {
    		return toSleepDates.size() > wakeUpDates.size();
    	}
    }
    
    private void updateMainListAdapter() {
    	String[] activityNames;
    	if( isCurrentlyAsleep() ) {
    		setTitle(getResources().getString(R.string.app_name) + ": Baby is now asleep");
    		activityNames = getResources().getStringArray(R.array.sleep_activities); 
    	} else {
    		setTitle(getResources().getString(R.string.app_name) + ": Baby is now awake");
    		activityNames = getResources().getStringArray(R.array.awake_activities);
    	}    	
    	
    	mListAdapter.setActionList(ScheduleDatabase.getAllBabyActions(activityNames));    	    	
    }
}