package fi.vincit.babyschedule;

import java.util.ArrayList;

import utils.ScheduleDatabase;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import fi.vincit.babyschedule.activities.AllEventsList;
import fi.vincit.babyschedule.activities.EventAdder;
import fi.vincit.babyschedule.activities.EventMarkingList;
import fi.vincit.babyschedule.activities.Settings;
import fi.vincit.babyschedule.activities.Statistics;

public class MainTabWidget extends TabActivity {
	
	public static class StaticResources {
	    public static Resources res;
	}
		
	public static class StaticContext {
		public static Context ctx;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ScheduleDatabase.open(getApplicationContext());
		ArrayList<String> names = ScheduleDatabase.getBabyNames();
		super.onCreate(savedInstanceState);						
	    setContentView(R.layout.main);	   	    
	    
	    // Show a dialog to add baby, if none exist
    	if( names.isEmpty() ) {
    		// Set an EditText view to get user input 
    		final EditText input = new EditText(this);
    		
    		new AlertDialog.Builder(MainTabWidget.this)
    	    .setTitle(getString(R.string.no_babies))
    	    .setMessage(getString(R.string.no_babies))
    	    .setView(input)
    	    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int whichButton) {
    	            Editable value = input.getText(); 
    	            ScheduleDatabase.addNewBaby(value.toString());
    	            createTabs();
    	        }
    	    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int whichButton) {
    	            // exit the application
    	        	System.exit(0);
    	        }
    	    }).show();
    	}
    	else {
    		createTabs();
    	}	    	   
	}
	
	private void createTabs() {
		Resources res = getResources(); // Resource object to get Drawables
	    StaticResources.res = res;
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab
	    
	    StaticContext.ctx = this.getApplicationContext();
	    
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
	    
	 // Change background
	    for(int i=0; i< getTabWidget().getChildCount(); i++) {
	    	getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_style);
	    	final TextView tv = (TextView) getTabWidget().getChildAt(i).findViewById(android.R.id.title);        
	    	tv.setTextColor(this.getResources().getColorStateList(R.color.tab_selected_text_color));

	    }
	    
	    tabHost.setCurrentTab(0);	
	    
	    setTitle("Babyschedule: " + Settings.getCurrentBabyName());
	}

	@Override
    public void onResume() {
		super.onResume();
		if( !ScheduleDatabase.getBabyNames().isEmpty() )
			setTitle("Babyschedule: " + Settings.getCurrentBabyName());
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
    		Intent showAddAction = new Intent(this, Statistics.class);
	    	startActivity(showAddAction);
    	} else if( item.getItemId() == R.id.show_settings ) {
    		Intent showSettingsAction = new Intent(this, Settings.class);
    		startActivity(showSettingsAction);
    	}
    	return super.onOptionsItemSelected(item);
    }
}
