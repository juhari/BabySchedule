package fi.vincit.babyschedule.activities;

import java.util.Date;

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
import fi.vincit.babyschedule.adapters.AllEventsListAdapter;
import fi.vincit.babyschedule.utils.BabyEvent;
import fi.vincit.babyschedule.utils.ScheduleDatabase;

public class AllEventsList extends ListActivity 
							implements View.OnCreateContextMenuListener {
	
	private AllEventsListAdapter mListAdapter;
	
	@Override
	public void onCreate(Bundle b) {
		Log.d("Babyschedule", "Single action onCreate()");
    	super.onCreate(b);
    	
    	ScheduleDatabase.open(getApplicationContext());            
    	
    	setTitle("List of all events");    
    	
    	mListAdapter = new AllEventsListAdapter(ScheduleDatabase.getAllDbActionsSortedByDate(Settings.getCurrentBabyName()));
    	setListAdapter(mListAdapter);
    	
    	registerForContextMenu(getListView());
	}
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		v.performLongClick();
    }
	
	@Override
    public void onStart() {
    	super.onStart();
    	mListAdapter.setActionsList(ScheduleDatabase.getAllDbActionsSortedByDate(Settings.getCurrentBabyName()));
    }
	
	@Override
    public void onResume() {
    	super.onResume();
    	mListAdapter.setActionsList(ScheduleDatabase.getAllDbActionsSortedByDate(Settings.getCurrentBabyName()));
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
		BabyEvent event = (BabyEvent)info.targetView.getTag();
		switch (item.getItemId()) {
		case R.id.delete_activity:			
			ScheduleDatabase.deleteEntryBasedOnDate(Settings.getCurrentBabyName(), event.getActionDate());
			if( event.getActionName().equals(getString(R.string.go_to_nap)) ||
				event.getActionName().equals(getString(R.string.go_to_nap)) ) {
				Date wokeupDate = ScheduleDatabase.getWakeUpDateFromSleepDate(Settings.getCurrentBabyName(), event.getActionDate());
				ScheduleDatabase.deleteEntryBasedOnDate(Settings.getCurrentBabyName(), wokeupDate);
			}
			mListAdapter.setActionsList(ScheduleDatabase.getAllDbActionsSortedByDate(Settings.getCurrentBabyName()));
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
