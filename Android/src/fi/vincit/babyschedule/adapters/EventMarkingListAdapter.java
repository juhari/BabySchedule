package fi.vincit.babyschedule.adapters;

import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.activities.Settings;
import fi.vincit.babyschedule.utils.BabyEvent;
import fi.vincit.babyschedule.utils.ConsumedTime;
import fi.vincit.babyschedule.utils.ScheduleDatabase;

public class EventMarkingListAdapter extends BaseAdapter {

	private String[] mActivityNames;
	private Context mContext;
	
	public EventMarkingListAdapter(String[] activities, Context context) {
		mActivityNames = activities;
		mContext = context;
	}
	
	public EventMarkingListAdapter(Context context) {	
		mContext = context;
	}
	
	public void setActionList(String[] actions) {
		mActivityNames = actions;
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return mActivityNames.length;
	}

	public Object getItem(int arg0) {
		return mActivityNames[arg0];
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup group) {			
		String activityName = mActivityNames[position];
		View activityView = null;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) group.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			activityView = inflater.inflate(R.layout.main_list_item, null);
		} else {
			activityView = convertView;
		}	
		
		TextView name = (TextView)activityView.findViewById(R.id.ActivityName);
		name.setText(activityName);	
		TextView timePassed = (TextView)activityView.findViewById(R.id.LastActivity);
		
		if(activityName.equalsIgnoreCase(mContext.getString(R.string.go_to_sleep))) {
			timePassed.setText(getFormattedTimeTextForGotToSleepEvent());
		} else if( activityName.equalsIgnoreCase(mContext.getString(R.string.woke_up)) ) {
			timePassed.setText(getFormattedTimeTextForWokeUpEvent());
		} else if( activityName.equalsIgnoreCase(mContext.getString(R.string.nursing)) ) {
			timePassed.setText(getFormattedTimeTextForNursingEvent());
		} else if( activityName.equalsIgnoreCase(mContext.getString(R.string.milk)) ) {
			timePassed.setText(getFormattedTimeTextForMilkEvent());
		} else {
			timePassed.setText(getFormattedTimeTextForNormalEvent(activityName));
		}
		
		ImageView img = (ImageView)activityView.findViewById(R.id.event_icon);
		img.setImageDrawable(mContext.getResources().getDrawable(BabyEvent.getActivityIconId(activityName)));
		
		activityView.setTag(activityName);
		return activityView;		
	}		
	
	private String getFormattedTimeTextForNormalEvent(String eventName) {
		Date date = ScheduleDatabase.getLastActionOfType(Settings.getCurrentBabyName(), eventName);
		if( date != null ) {	
			String time = mContext.getString(R.string.last_occurred) + " ";								
			String timeDiff = getTimeDiffFromDate(date);				
			return time + timeDiff + " " + mContext.getString(R.string.ago);
		} else {
			return mContext.getString(R.string.no_events_occurred);
		}
	}
	
	private String getFormattedTimeTextForNursingEvent() {
		Date dateleft = ScheduleDatabase.getLastActionOfType(Settings.getCurrentBabyName(), mContext.getString(R.string.milk_left));
		Date dateright = ScheduleDatabase.getLastActionOfType(Settings.getCurrentBabyName(), mContext.getString(R.string.milk_right));		
		String whichBreast = null;
		
		Date date = null;
		if( dateleft != null && dateright == null ) {
			date = dateleft;
			whichBreast = mContext.getString(R.string.from_left);
		}
		else if( dateright != null && dateleft == null) {
			date = dateright;
			whichBreast = mContext.getString(R.string.from_right);
		}
		else if( dateright != null && dateleft != null) {
			date = dateleft.after(dateright) ? dateleft : dateright;
			whichBreast = dateleft.after(dateright) ? mContext.getString(R.string.from_left) : mContext.getString(R.string.from_right);
		}

		if( date != null ) {	
			String time = mContext.getString(R.string.last_occurred) + " ";								
			String timeDiff = getTimeDiffFromDate(date);		
			int durSeconds = ScheduleDatabase.getDurationOfNursingStartedAt(Settings.getCurrentBabyName(), date);
			ConsumedTime duration = new ConsumedTime(durSeconds);
			return (time + timeDiff + " " + mContext.getString(R.string.ago) + "\n" + 
				   mContext.getString(R.string.duration)+ " " + duration.toString() + "\n" + whichBreast				   
				   );
		} else {
			return mContext.getString(R.string.no_events_occurred);
		}
	}
	
	private String getFormattedTimeTextForGotToSleepEvent() {
		Date lastWokeUp = ScheduleDatabase.getLastActionOfType(Settings.getCurrentBabyName(), mContext.getString(R.string.woke_up));
		if( lastWokeUp != null ){
			String time = Settings.getCurrentBabyName() + " " + mContext.getString(R.string.baby_has_been_awake) + "\n";
			String timeDiff = getTimeDiffFromDate(lastWokeUp);
			return time + timeDiff;
		} else {
			return Settings.getCurrentBabyName() + " " + mContext.getString(R.string.baby_is_awake_no_prev);
		}
	}
	
	private String getFormattedTimeTextForMilkEvent() {
		Date date = ScheduleDatabase.getLastActionOfType(Settings.getCurrentBabyName(), mContext.getString(R.string.milk));
		if( date != null ) {	
			String time = mContext.getString(R.string.last_occurred) + " ";								
			String timeDiff = getTimeDiffFromDate(date);		
			int amount = ScheduleDatabase.getFreeValueAttachedToEvent(Settings.getCurrentBabyName(), date);
			return (time + timeDiff + " " + mContext.getString(R.string.ago) + "\n" + 
				   mContext.getString(R.string.amount)+ " " + amount +
				   mContext.getString(R.string.ml)
				   );
		} else {
			return mContext.getString(R.string.no_events_occurred);
		}
	}
	
	private String getFormattedTimeTextForWokeUpEvent() {
		Date lastFellAsleep = ScheduleDatabase.getLastActionOfType(Settings.getCurrentBabyName(), mContext.getString(R.string.go_to_sleep));
		Date lastNap = ScheduleDatabase.getLastActionOfType(Settings.getCurrentBabyName(), mContext.getString(R.string.go_to_nap));
		if( lastNap != null && (lastFellAsleep == null || lastNap.after(lastFellAsleep)) ) {
			lastFellAsleep = lastNap;
		}
		if( lastFellAsleep != null ){
			String time = Settings.getCurrentBabyName() + " " + mContext.getString(R.string.baby_has_been_sleeping) + "\n";
			String timeDiff = getTimeDiffFromDate(lastFellAsleep);
			return time + timeDiff;
		} else {
			return Settings.getCurrentBabyName() + " " + mContext.getString(R.string.baby_is_sleeping_no_prev);
		}
	}
	
	private String getTimeDiffFromDate(Date oldDate) {
		ConsumedTime timeDiff = new ConsumedTime(new Date(), oldDate);	
		return timeDiff.toStringWithoutSeconds();
	}
}
