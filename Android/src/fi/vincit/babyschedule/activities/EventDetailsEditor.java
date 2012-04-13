package fi.vincit.babyschedule.activities;

import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
	private TextView mExtraInputDescription;	
	private EditText mExtraInput;
	private Dialog mAddWakeUpDialog;	

    private Date mWakeUpDate;
    private Date mOriginalWakeupDate;		

    static final int WAKE_UP_DIALOG_ID = 0;
	
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
    	    	
    	Date now = new Date();
    	mDatePicker.init(now.getYear() + 1900, now.getMonth(), now.getDate(), this);
    	mTimePicker.setOnTimeChangedListener(this);
    	
    	setTitle(R.string.add_activity);
    	
    	initializeWakeUpDialog();
    	
    	updateDescription();
    	updateOptionalFieldVisibilities();
	}  

	private void initializeWakeUpDialog() {
	    mAddWakeUpDialog = new Dialog(this);
        mAddWakeUpDialog.setContentView(R.layout.datetimepicker);
        mAddWakeUpDialog.setTitle(getString(R.string.add_wakeup));
        
        mWakeUpDate = null;
        
        Button okButton = (Button)mAddWakeUpDialog.findViewById(R.id.wakeupOkButton);
        Button cancelButton = (Button)mAddWakeUpDialog.findViewById(R.id.wakeupCancelButton);                
        
        TimePicker wuTimePicker = (TimePicker)mAddWakeUpDialog.findViewById(R.id.wakeupTimePick);        
        wuTimePicker.setIs24HourView(true);
        
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.saveButton ) {		
			finish();
		}
		else if( v.getId() == R.id.cancelButton ) {
			finish();
		}
		else if( v.getId() == R.id.addWakeUpButton ) {
		    showDialog(WAKE_UP_DIALOG_ID);		    
		}
		else if( v.getId() == R.id.wakeupOkButton ) {
		    wakeUpDateSet();
		    mAddWakeUpDialog.dismiss();
		}
		else if( v.getId() == R.id.wakeupCancelButton ) {
            mAddWakeUpDialog.dismiss();
        }
	}
	
	private void wakeUpDateSet() {
	    mWakeUpDate = getWakeUpDateTimeFromSpinners();
	    setWakeupDescription();
	}
	
	protected void setWakeupDescription() {
	    TextView wakeUpDateText = (TextView)findViewById(R.id.wokeUpText);
        wakeUpDateText.setText(getString(R.string.editor_time) + " " + mWakeUpDate.toLocaleString());
	}
	
	protected void updateWakeupSpinnerValues() {
	    DatePicker wuDatePicker = (DatePicker)mAddWakeUpDialog.findViewById(R.id.wakeupDatePick);
        TimePicker wuTimePicker = (TimePicker)mAddWakeUpDialog.findViewById(R.id.wakeupTimePick);        
        
        wuDatePicker.updateDate(mWakeUpDate.getYear() + 1900, 
                mWakeUpDate.getMonth(), 
                mWakeUpDate.getDate());

        wuTimePicker.setCurrentHour(mWakeUpDate.getHours());
        wuTimePicker.setCurrentMinute(mWakeUpDate.getMinutes());
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    switch(id) {
	    case WAKE_UP_DIALOG_ID:
	        dialog = mAddWakeUpDialog;
	        break;
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	protected void updateDescription() {
		Date dateTime = getDateTimeFromSpinners();
		
		TextView date = (TextView)findViewById(R.id.eventDateTimeText);
		date.setText(getString(R.string.editor_time) + " " + 						  
						   dateTime.toLocaleString());
	}
	
	protected void updateOptionalFieldVisibilities() {
	    LinearLayout extraInputLo = (LinearLayout)findViewById(R.id.extraInputLayout);
	    LinearLayout wokeUpLo = (LinearLayout)findViewById(R.id.addWakeUpLayout);
		if( isMilkEventSelected() ) {
			mExtraInputDescription.setText(R.string.milk_amount_instruction);
			
			extraInputLo.setVisibility(View.VISIBLE);

		}
		else if( isNursingEventSelected() ) {
			mExtraInputDescription.setText(R.string.nursing_instruction);
			extraInputLo.setVisibility(View.VISIBLE);

		}
		else {
		    extraInputLo.setVisibility(View.GONE);
		}
		
		if( isSleepEventSelected() ) {
		    wokeUpLo.setVisibility(View.VISIBLE);
		}
		else {
		    wokeUpLo.setVisibility(View.GONE);
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
	
	protected void showInvalidWakeupTimeMessage() {
	    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.invalid_wakeup), 1000);
        toast.show();
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
		else if( isSleepEventSelected() && mWakeUpDate != null ) {
            if( mWakeUpDate.after(dateTime) ) {
                ScheduleDatabase.insertBabyAction(babyName, actionName, dateTime);
                if( mOriginalWakeupDate == null || !mWakeUpDate.equals(mOriginalWakeupDate) ) {
                    ScheduleDatabase.insertBabyAction(babyName, getString(R.string.woke_up_activity), mWakeUpDate);
                }
            }
            else {
                showInvalidWakeupTimeMessage();
                return false;
            }
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
	
	protected boolean isSleepEventSelected() {
	    String spinnerValue = (String)mSpinner.getSelectedItem();
	    return spinnerValue.equals(getString(R.string.go_to_nap_activity)) ||
	           spinnerValue.equals(getString(R.string.go_to_sleep_activity)); 
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
	
	protected Date getWakeUpDateTimeFromSpinners() {	    
	    DatePicker wuDatePicker = (DatePicker)mAddWakeUpDialog.findViewById(R.id.wakeupDatePick);
        TimePicker wuTimePicker = (TimePicker)mAddWakeUpDialog.findViewById(R.id.wakeupTimePick);        
	    
        Date dateTime = new Date();
        dateTime.setYear(wuDatePicker.getYear()-1900);
        dateTime.setMonth(wuDatePicker.getMonth());
        dateTime.setDate(wuDatePicker.getDayOfMonth());
        dateTime.setHours(wuTimePicker.getCurrentHour());
        dateTime.setMinutes(wuTimePicker.getCurrentMinute());
        dateTime.setSeconds(0);
        return dateTime;
    }

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		updateDescription();	
		updateOptionalFieldVisibilities();
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
	
	protected TextView getmExtraInputDescription() {
		return mExtraInputDescription;
	}

	protected EditText getmExtraInput() {
		return mExtraInput;
	}
	
	public Dialog getmAddWakeUpDialog() {
        return mAddWakeUpDialog;
    }

    public void setmAddWakeUpDialog(Dialog mAddWakeUpDialog) {
        this.mAddWakeUpDialog = mAddWakeUpDialog;
    }

    public Date getmWakeUpDate() {
        return mWakeUpDate;
    }

    public void setmWakeUpDate(Date mWakeUpDate) {
        this.mWakeUpDate = mWakeUpDate;
    }
    
    public Date getmOriginalWakeupDate() {
        return mOriginalWakeupDate;
    }

    public void setmOriginalWakeupDate(Date mOriginalWakeupDate) {
        this.mOriginalWakeupDate = mOriginalWakeupDate;
    }

}
