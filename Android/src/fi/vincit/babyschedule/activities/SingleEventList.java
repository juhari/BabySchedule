package fi.vincit.babyschedule.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import utils.BabyEvent;
import utils.ScheduleDatabase;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.adapters.SingleEventListAdapter;

public class SingleEventList extends ListActivity 
						      implements View.OnCreateContextMenuListener {

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
    	
    	mListAdapter = new SingleEventListAdapter(getMyActionDates(), mActionName);
    	setListAdapter(mListAdapter);
    	
    	registerForContextMenu(getListView());
	}
	
	@Override
    public void onStart() {
    	super.onStart();
    	mListAdapter.setDateList(getMyActionDates());
    }
	
	private ArrayList<Date> getMyActionDates() {
		if( mActionName == getString(R.string.go_to_sleep) ){
    		ArrayList<Date> dates = ScheduleDatabase.getActionDatesForAction("verneri", mActionName);
    		dates.addAll(ScheduleDatabase.getActionDatesForAction("verneri", getString(R.string.go_to_nap)));
    		Collections.sort(dates);
    		return dates;
    	} else {
    		return ScheduleDatabase.getActionDatesForAction("verneri", mActionName);
    	}
	}
	
    public void onListItemClick(ListView l, View v, int position, long id) {
    	v.performLongClick();
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listcontextmenu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		BabyEvent event = new BabyEvent(mActionName, (Date)info.targetView.getTag());
		switch (item.getItemId()) {
		case R.id.delete_activity:			
			ScheduleDatabase.deleteEntryBasedOnDate("verneri", event.getActionDate());
			mListAdapter.setDateList(ScheduleDatabase.getActionDatesForAction("verneri", mActionName));
			return true;
		case R.id.edit_activity:
			openEditViewForEvent(event);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	private void openEditViewForEvent(BabyEvent event) {
		Intent i = new Intent(this, EventEditor.class);
		Bundle b = new Bundle();
		b.putSerializable("EVENT", event);
		i.putExtras(b);
		startActivity(i);
	}

}
