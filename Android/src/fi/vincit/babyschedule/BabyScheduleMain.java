package fi.vincit.babyschedule;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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
    	mListAdapter = new MainListAdapter(getApplicationContext(), 
    									   ScheduleDatabase.getAllBabyActivities(activityNames));
    	setListAdapter(mListAdapter);
    	
    	this.mListUpdatetimer = new CountDownTimer(10000, 10000) {
			
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
    public void onClick(View v){
    	Button nowButton = (Button)v;
    	BabyActivity activity = (BabyActivity)nowButton.getTag();
    	
    	Log.d("Babyschedule", "Clicked button" + activity.getActivityName());
    	
    	ScheduleDatabase.insertBabyActivity(activity.getActivityName());
    	
    	mListAdapter.updateActivityTimeNow(activity);    	    
    }
}