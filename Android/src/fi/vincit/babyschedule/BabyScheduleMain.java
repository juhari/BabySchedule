package fi.vincit.babyschedule;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

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
    	
    	String[] activityNames = getResources().getStringArray(R.array.activity_names);
    	mListAdapter = new MainListAdapter(ScheduleDatabase.getAllBabyActions(activityNames));
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
    	String[] activityNames = getResources().getStringArray(R.array.activity_names);
    	mListAdapter.setActionList(ScheduleDatabase.getAllBabyActions(activityNames));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainlistmenu, menu);
    	return true;
    }
    
    @Override
    public void onClick(View v){
    	BabyAction action = (BabyAction)v.getTag();
    	
    	if( v.getId() == R.id.Now ) {	    
	    	Log.d("Babyschedule", "Clicked button" + action.getActionName());	    	
	    	ScheduleDatabase.insertBabyAction("Verneri", action.getActionName());	    	
	    	mListAdapter.updateActivityTimeNow(action);    	    
    	} else if( ScheduleDatabase.getActionDatesForAction(action.getActionName()).size() > 0 ) {
    		// show list of actions for the specified type
    		Intent showSingleList = new Intent(BabyScheduleMain.this, SingleActionList.class);   
    		Bundle actionBundle = new Bundle();
    		actionBundle.putString("ACTIONNAME", action.getActionName());
    		showSingleList.putExtras(actionBundle);
    		startActivity(showSingleList);
    	}
    }
}