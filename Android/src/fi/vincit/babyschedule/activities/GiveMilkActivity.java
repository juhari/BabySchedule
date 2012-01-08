package fi.vincit.babyschedule.activities;

import java.util.Date;

import utils.ScheduleDatabase;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import fi.vincit.babyschedule.R;

public class GiveMilkActivity extends Activity 
							  implements OnClickListener {

	private EditText mEditText;
	
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);        	
    	setContentView(R.layout.give_milk_activity);
    	mEditText = (EditText)findViewById(R.id.milkEdit);
	}
	@Override
	public void onClick(View arg0) {
		
		String amountOfMilk = mEditText.getText().toString();
		if( amountOfMilk.length() > 0 ) {
			int amount = Integer.parseInt(amountOfMilk);
			Date now = new Date();
			ScheduleDatabase.insertBabyActionWithFreeVal(Settings.getCurrentBabyName(), getString(R.string.milk), now, amount);
		}
		finish();
	}
	
}
