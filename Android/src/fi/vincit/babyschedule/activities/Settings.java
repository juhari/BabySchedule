package fi.vincit.babyschedule.activities;

import java.util.ArrayList;

import utils.ScheduleDatabase;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import fi.vincit.babyschedule.MainTabWidget.StaticContext;
import fi.vincit.babyschedule.R;

public class Settings extends PreferenceActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		updateBabyNameList();
		initAddBabyPreference();
		initManageBabiesPreference();
	}
	
	public static String getCurrentBabyName() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(StaticContext.ctx);
    	String name = pref.getString("chooseList", "verneri");
    	//Log.d("Babyschedule", "Name of current baby: " + name);
    	ArrayList<String> names = ScheduleDatabase.getBabyNames();    	    	
    	
    	if( names.isEmpty() ) {
    		return name;
    	}
    	
    	// check for obsolete preference values
    	if( !names.contains(name) ) {
    		SharedPreferences.Editor editor = pref.edit();
			editor.putString("chooseList", names.get(0));
			editor.commit();
    		return names.get(0);
    	}
    	
    	return name;
	}
	
	@Override
    public void onStart() {
    	super.onStart();
    	updateBabyNameList();
    }
	
	@Override
    public void onResume() {
    	super.onResume();
    	updateBabyNameList();
    }
	
	private void initAddBabyPreference() {
		// Get the custom preference
        Preference customPref = (Preference) findPreference("addBaby");
        customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {
                        getBabyNameFromAlertDialog();                                                        
                        return true;
                    }

            });
	}
	
	private void initManageBabiesPreference() {
		// Get the custom preference
        Preference customPref = (Preference) findPreference("manageBabies");
        customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {
            	        showManageBabiesActivity();                                      
                        return true;
                    }

            });
	}
	
	private void showManageBabiesActivity() {
		Intent showBabyList = new Intent(this, ManageBabies.class);
		startActivity(showBabyList);        
	}
	
	private void updateBabyNameList() {
		ListPreference nameList = (ListPreference)findPreference("chooseList");
		ArrayList<String> names = ScheduleDatabase.getBabyNames();

		nameList.setEntries(names.toArray(new CharSequence[names.size()]));
		nameList.setEntryValues(names.toArray(new CharSequence[names.size()]));
	}
	
	private void getBabyNameFromAlertDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getString(R.string.add_baby));
		alert.setMessage(getString(R.string.add_baby_instruction));

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);
		
		alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String babyName = input.getText().toString();
		  if( ScheduleDatabase.getBabyNames().contains(babyName) ) {
			  Log.w("Babyschedule", "Settings::addBaby, unable to add, already exists with the same name.");
			  Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.baby_exists), 1000);
			  toast.show();
			  return;
		  }
		  ScheduleDatabase.addNewBaby(babyName);
	      updateBabyNameList();	  
		  }
		});

		alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
			  // do nothing
		  }
		});

		alert.show();
		
	}

}
