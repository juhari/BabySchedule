package fi.vincit.babyschedule.activities;

import java.util.ArrayList;

import utils.ScheduleDatabase;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ManageBabies extends ListActivity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(android.R.layout.si);
        ArrayList<String> babyNames = ScheduleDatabase.getBabyNames();
        setListAdapter(new ArrayAdapter<String>(this, 
             android.R.layout.simple_list_item_1, babyNames));
    }
}
