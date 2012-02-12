package fi.vincit.babyschedule.activities;

import java.util.ArrayList;
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
import fi.vincit.babyschedule.adapters.AllEventsListAdapter;

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
    	
    	setTitle("List of " + mActionName + " events");
    	
    	Log.d("Babyschedule", "Single action onCreate(): action name: " + mActionName);
    	
    	ArrayList<BabyEvent> myEvents = ScheduleDatabase.getAllDbActionsByActionName(Settings.getCurrentBabyName(), mActionName);
    	mListAdapter = new AllEventsListAdapter(myEvents);
    	setListAdapter(mListAdapter);
    	
    	registerForContextMenu(getListView());
	}
	
	@Override
    public void onStart() {
    	super.onStart();
    	ArrayList<BabyEvent> myEvents = ScheduleDatabase.getAllDbActionsByActionName(Settings.getCurrentBabyName(), mActionName);
    	mListAdapter.setActionsList(myEvents);
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
			ScheduleDatabase.deleteEntryBasedOnDate(Settings.getCurrentBabyName(), event.getActionDate());
			ArrayList<BabyEvent> myEvents = ScheduleDatabase.getAllDbActionsByActionName(Settings.getCurrentBabyName(), mActionName);
			mListAdapter.setActionsList(myEvents);
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
