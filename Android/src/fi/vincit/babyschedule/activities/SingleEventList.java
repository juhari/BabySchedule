package fi.vincit.babyschedule.activities;

import java.util.ArrayList;
import java.util.Collections;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.adapters.AllEventsListAdapter;
import fi.vincit.babyschedule.utils.BabyEvent;
import fi.vincit.babyschedule.utils.ScheduleDatabase;

public class SingleEventList extends ListActivity 
						      implements View.OnCreateContextMenuListener {

	private AllEventsListAdapter mListAdapter;
	private String mActionName;
	
	@Override
	public void onCreate(Bundle b) {
		Log.d("Babyschedule", "Single action onCreate()");
    	super.onCreate(b);    	    	
    	
    	ScheduleDatabase.open(getApplicationContext());        
    	
    	Bundle actionNameBundle = this.getIntent().getExtras();
    	mActionName = actionNameBundle.getString("ACTIONNAME");
    	
    	setTitle(mActionName);
    	
    	Log.d("Babyschedule", "Single action onCreate(): action name: " + mActionName);
    	
    	mListAdapter = new AllEventsListAdapter(getEventListForAction());
    	setListAdapter(mListAdapter);
    	
    	registerForContextMenu(getListView());
	}
	
	private ArrayList<BabyEvent> getEventListForAction() {
		ArrayList<BabyEvent> events = new ArrayList<BabyEvent>();
		if( mActionName.equalsIgnoreCase(getString(R.string.nursing_activity)) ||
			mActionName.equalsIgnoreCase(getString(R.string.nursing)) ||
			mActionName.equalsIgnoreCase(getString(R.string.nurse_left_activity)) ||
			mActionName.equalsIgnoreCase(getString(R.string.nurse_right_activity)) ) {
			events.addAll(ScheduleDatabase.getAllDbActionsByActionName(Settings.getCurrentBabyName(), getString(R.string.nurse_left_activity)));
			events.addAll(ScheduleDatabase.getAllDbActionsByActionName(Settings.getCurrentBabyName(), getString(R.string.nurse_right_activity)));
			Collections.sort(events);
			return events;
		}
		else if( mActionName.equalsIgnoreCase(getString(R.string.go_to_nap_activity)) ||
				 mActionName.equalsIgnoreCase(getString(R.string.go_to_sleep_activity)) ||
				 mActionName.equalsIgnoreCase(getString(R.string.woke_up_activity)) ||
				 mActionName.equalsIgnoreCase(getString(R.string.woke_up_during_night_activity)) ) {
			events.addAll(ScheduleDatabase.getAllDbActionsByActionName(Settings.getCurrentBabyName(), getString(R.string.go_to_nap_activity)));
			events.addAll(ScheduleDatabase.getAllDbActionsByActionName(Settings.getCurrentBabyName(), getString(R.string.go_to_sleep_activity)));
			events.addAll(ScheduleDatabase.getAllDbActionsByActionName(Settings.getCurrentBabyName(), getString(R.string.woke_up_activity)));
			events.addAll(ScheduleDatabase.getAllDbActionsByActionName(Settings.getCurrentBabyName(), getString(R.string.woke_up_during_night_activity)));
			Collections.sort(events);
			return events;
		}
		else {
			return ScheduleDatabase.getAllDbActionsByActionName(Settings.getCurrentBabyName(), mActionName);
		}
	}
	
	@Override
    public void onStart() {
    	super.onStart();
    	mListAdapter.setActionsList(getEventListForAction());
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
		BabyEvent event = (BabyEvent)info.targetView.getTag();
		switch (item.getItemId()) {
		case R.id.delete_activity:			
			ScheduleDatabase.deleteEntryBasedOnDate(Settings.getCurrentBabyName(), event.getActionDate());
			mListAdapter.setActionsList(getEventListForAction());
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
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainlistmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId() == R.id.add_activity ) {
            Intent showAddAction = new Intent(this, EventAdder.class);
            startActivity(showAddAction);
        } else if( item.getItemId() == R.id.show_statistics ) {
            Intent showAddAction = new Intent(this, BarStatistics.class);
            startActivity(showAddAction);
        } else if( item.getItemId() == R.id.show_settings ) {
            Intent showSettingsAction = new Intent(this, Settings.class);
            startActivity(showSettingsAction);
        }
        return super.onOptionsItemSelected(item);
    }

}
