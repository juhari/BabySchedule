package fi.vincit.babyschedule.activities;

import java.util.ArrayList;

import utils.ScheduleDatabase;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import fi.vincit.babyschedule.R;

public class Settings extends PreferenceActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		ListPreference nameList = (ListPreference)findPreference("chooseList");
		ArrayList<String> names = ScheduleDatabase.getBabyNames();

		nameList.setEntries(names.toArray(new CharSequence[names.size()]));
		nameList.setEntryValues(names.toArray(new CharSequence[names.size()]));
	}

}
