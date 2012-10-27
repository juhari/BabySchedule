package fi.vincit.babyschedule.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.graphviews.MilkGraphView;
import fi.vincit.babyschedule.graphviews.NursingGraphView;
import fi.vincit.babyschedule.graphviews.SleepGraphView;
import fi.vincit.babyschedule.graphviews.SleepRangeGraphView;

public class BarStatistics extends Activity implements OnItemSelectedListener {
	
	private Spinner mStatsChooser;
	private LinearLayout mGraphLayout;	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barstatistics);					
		
		mStatsChooser = (Spinner) findViewById(R.id.stats_chooser);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
				 R.array.statistics_types, 
				 android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mStatsChooser.setAdapter(adapter);   
		mStatsChooser.setOnItemSelectedListener(this);

		mGraphLayout = (LinearLayout) findViewById(R.id.bargraphlayout);	
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {		
		String item = (String)mStatsChooser.getSelectedItem();
		if( item.equalsIgnoreCase(getString(R.string.milk_stats_str)) ) {
			mGraphLayout.removeAllViews();
			mGraphLayout.addView(new MilkGraphView(this));
		}
		else if( item.equalsIgnoreCase(getString(R.string.sleep_stats_str)) ) {
			mGraphLayout.removeAllViews();
			mGraphLayout.addView(new SleepGraphView(this));
		}
		else if( item.equalsIgnoreCase(getString(R.string.nursing_stats_str)) ) {
			mGraphLayout.removeAllViews();
			mGraphLayout.addView(new NursingGraphView(this));
		}
        else if( item.equalsIgnoreCase(getString(R.string.sleep_range_stats_str)) ) {
            mGraphLayout.removeAllViews();
            mGraphLayout.addView(new SleepRangeGraphView(this));
        }
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.statsmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if( item.getItemId() == R.id.add_activity ) {
            Intent showAddAction = new Intent(this, EventAdder.class);
            startActivity(showAddAction);
        } else if( item.getItemId() == R.id.show_settings ) {
            Intent showSettingsAction = new Intent(this, Settings.class);
            startActivity(showSettingsAction);
        }
        return super.onOptionsItemSelected(item);
    }
	
}
