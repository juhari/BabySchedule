package fi.vincit.babyschedule.activities;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.utils.ScheduleDatabase;

public class EventDetailsEditor extends Activity
							   implements OnClickListener
							   			  , DatePicker.OnDateChangedListener
							   			  , TimePicker.OnTimeChangedListener
							   			  , OnItemSelectedListener {

	private DatePicker mDatePicker;
	private TimePicker mTimePicker;
	private Spinner mSpinner;	
	private TextView mDescription;
	private TextView mExtraInputDescription;	
	private EditText mExtraInput;
	
	@Override
	public void onCreate(Bundle b) {
		Log.d("Babyschedule", "AddActionActivity::onCreate()");
    	super.onCreate(b);
    	
    	ScheduleDatabase.open(getApplicationContext());        
    	
    	setContentView(R.layout.add_activity);
    	
    	mExtraInputDescription = (TextView) findViewById(R.id.extraInputDescription);
    	mExtraInput = (EditText) findViewById(R.id.extraInput);
    	
    	mSpinner = (Spinner) findViewById(R.id.actionsSpinner);
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
    																		 R.array.activity_names, 
    																		 android.R.layout.simple_spinner_item);
    	
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	mSpinner.setAdapter(adapter);   
    	mSpinner.setOnItemSelectedListener(this);
    	mDatePicker = (DatePicker)findViewById(R.id.datePick);
    	mTimePicker = (TimePicker)findViewById(R.id.timePick);
    	mTimePicker.setIs24HourView(true);
    	
    	mDescription = (TextView)findViewById(R.id.descriptionText);
    	    	
    	Date now = new Date();
    	mDatePicker.init(now.getYear() + 1900, now.getMonth(), now.getDate(), this);
    	mTimePicker.setOnTimeChangedListener(this);
    	
    	setTitle(R.string.add_activity);
    	
    	updateDescription();
	}
	
	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.saveButton ) {		
			finish();
		}
		else if( v.getId() == R.id.cancelButton ) {
			finish();
		}
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		updateDescription();		
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		updateDescription();		
	}
	
	protected void updateDescription() {
		Date dateTime = getDateTimeFromSpinners();
		
		mDescription.setText("Add event: " + 
						   (String)mSpinner.getSelectedItem() + 
						   ", " + 
						   dateTime.toLocaleString());
		updateExtraInputField();
	}
	
	protected void updateExtraInputField() {
		if( isMilkEventSelected() ) {
			mExtraInputDescription.setText(R.string.milk_amount_instruction);
			mExtraInputDescription.setVisibility(View.VISIBLE);
			mExtraInput.setVisibility(View.VISIBLE);
		}
		else if( isNursingEventSelected() ) {
			mExtraInputDescription.setText(R.string.nursing_instruction);
			mExtraInputDescription.setVisibility(View.VISIBLE);
			mExtraInput.setVisibility(View.VISIBLE);
		}
		else {
			mExtraInputDescription.setVisibility(View.INVISIBLE);
			mExtraInput.setVisibility(View.INVISIBLE);
		}
	}
	
	protected int parseAndConfirmExtraValueGiven() {
		int value = 0;
		try {
			value = Integer.parseInt(getmExtraInput().getText().toString());
		} catch( Exception e) { }// nothing needs to be done, the exception is handled when amount is 0 
		if( value == 0 ) {
			showForgotMessage();
		}
		return value;
	}
	
	protected void showForgotMessage() {
		if( isMilkEventSelected() ) {
			Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.forgot_milk_amount), 1000);
			toast.show();
		}
		else {
			Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.forgot_nursing_time), 1000);
			toast.show();
		}
	}
	
	/**
	 * Saves the currently edited/added event to db. 
	 * @return true if save was successfull
	 */
	protected boolean saveEvent() {
		Date dateTime = getDateTimeFromSpinners();
		String actionName = (String)getmSpinner().getSelectedItem();
		String babyName = Settings.getCurrentBabyName();
		if( isMilkEventSelected() ) {
			int amount = parseAndConfirmExtraValueGiven();
			if( amount == 0 ) {
				return false;
			}
			ScheduleDatabase.insertBabyActionWithFreeVal(babyName, actionName, dateTime, amount);
		}
		else if( isNursingEventSelected() ) {
			int seconds = parseAndConfirmExtraValueGiven();
			if( seconds == 0 ) {
				return false;
			}
			ScheduleDatabase.insertBabyActionWithDuration(babyName, actionName, dateTime, seconds*60);
		}
		else {
			ScheduleDatabase.insertBabyAction(babyName, actionName, dateTime);
		}
		return true;
	}
	
	protected boolean isMilkEventSelected() {
		String spinnerValue = (String)mSpinner.getSelectedItem();
		return spinnerValue.equals(getString(R.string.milk_activity));
	}
	
	protected boolean isNursingEventSelected() {
		String spinnerValue = (String)mSpinner.getSelectedItem();
		return spinnerValue.equals(getString(R.string.nurse_left_activity)) ||
		 	   spinnerValue.equals(getString(R.string.nurse_right_activity));
	}
	
	protected Date getDateTimeFromSpinners() {
		Date dateTime = new Date();
		dateTime.setYear(mDatePicker.getYear()-1900);
		dateTime.setMonth(mDatePicker.getMonth());
		dateTime.setDate(mDatePicker.getDayOfMonth());
		dateTime.setHours(mTimePicker.getCurrentHour());
		dateTime.setMinutes(mTimePicker.getCurrentMinute());
		dateTime.setSeconds(0);
		return dateTime;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		updateDescription();		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		updateDescription();		
	}
	
	protected DatePicker getmDatePicker() {
		return mDatePicker;
	}

	protected void setmDatePicker(DatePicker mDatePicker) {
		this.mDatePicker = mDatePicker;
	}

	protected TimePicker getmTimePicker() {
		return mTimePicker;
	}

	protected void setmTimePicker(TimePicker mTimePicker) {
		this.mTimePicker = mTimePicker;
	}

	protected Spinner getmSpinner() {
		return mSpinner;
	}

	protected void setmSpinner(Spinner mSpinner) {
		this.mSpinner = mSpinner;
	}

	protected TextView getmDescription() {
		return mDescription;
	}

	protected void setmDescription(TextView mDescription) {
		this.mDescription = mDescription;
	}
	
	protected TextView getmExtraInputDescription() {
		return mExtraInputDescription;
	}

	protected EditText getmExtraInput() {
		return mExtraInput;
	}

}
