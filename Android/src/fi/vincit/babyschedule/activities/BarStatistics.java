package fi.vincit.babyschedule.activities;

import android.app.Activity;
import android.os.Bundle;
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
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
