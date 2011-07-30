package fi.vincit.babyschedule;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import fi.vincit.babyschedule.activities.AllEventsList;
import fi.vincit.babyschedule.activities.EventAdder;
import fi.vincit.babyschedule.activities.EventMarkingList;

public class MainTabWidget extends TabActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ScheduleDatabase.open(getApplicationContext());
		super.onCreate(savedInstanceState);						
	    setContentView(R.layout.main);
	
	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	
	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, EventMarkingList.class);
	
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("Mark events").setIndicator("Mark Events",
	                      res.getDrawable(R.drawable.new_mark_icon))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	
	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, AllEventsList.class);
	    spec = tabHost.newTabSpec("Show events").setIndicator("Show Events",
	                      res.getDrawable(R.drawable.new_show_icon))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	
	    tabHost.setCurrentTab(0);
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
    	}
    	return super.onOptionsItemSelected(item);
    }
}
