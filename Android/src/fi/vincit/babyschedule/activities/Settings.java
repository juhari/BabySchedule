package fi.vincit.babyschedule.activities;

import java.util.ArrayList;

import utils.ScheduleDatabase;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.EditText;
import fi.vincit.babyschedule.R;

public class Settings extends PreferenceActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		updateBabyNameList();
		
		// Get the custom preference
        Preference customPref = (Preference) findPreference("addBaby");
        customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {
                            getBabyNameFromAlertDialog();                                                        
                            return true;
                    }

            });
	}
	
	public void updateBabyNameList() {
		ListPreference nameList = (ListPreference)findPreference("chooseList");
		ArrayList<String> names = ScheduleDatabase.getBabyNames();

		nameList.setEntries(names.toArray(new CharSequence[names.size()]));
		nameList.setEntryValues(names.toArray(new CharSequence[names.size()]));
	}
	
	public void getBabyNameFromAlertDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getString(R.string.add_baby));
		alert.setMessage(getString(R.string.add_baby_instruction));

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String babyName = input.getText().toString();
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
