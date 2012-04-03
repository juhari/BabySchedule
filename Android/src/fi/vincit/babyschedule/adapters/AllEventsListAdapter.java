package fi.vincit.babyschedule.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fi.vincit.babyschedule.MainTabWidget;
import fi.vincit.babyschedule.MainTabWidget.StaticContext;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.activities.Settings;
import fi.vincit.babyschedule.utils.BabyEvent;
import fi.vincit.babyschedule.utils.ConsumedTime;
import fi.vincit.babyschedule.utils.ScheduleDatabase;

public class AllEventsListAdapter extends BaseAdapter {

	private ArrayList<BabyEvent> mActionList;
	private String mSleepString;
	private String mNapString;
	private String mWokeUpString;
	private String mNursingLeftString;
	private String mNursingRightString;
	private String mMilkString;
	private Resources mRes;
	
	public AllEventsListAdapter(ArrayList<BabyEvent> actions) {		
		mActionList = actions;
		removeWokeUps();
		
		mRes = MainTabWidget.StaticResources.res;
		mNapString = mRes.getString(R.string.go_to_nap);
		mSleepString = mRes.getString(R.string.go_to_sleep);
		mWokeUpString = mRes.getString(R.string.woke_up);
		mNursingLeftString = mRes.getString(R.string.milk_left);
		mNursingRightString = mRes.getString(R.string.milk_right);
		mMilkString = mRes.getString(R.string.milk);		
	}
	
	public void setActionsList(ArrayList<BabyEvent> actions) {
		mActionList = actions;
		removeWokeUps();
		notifyDataSetChanged();
	}
	
	private void removeWokeUps() {
		for( int i = 0; i < mActionList.size(); i++ ) {
			if(mActionList.get(i).getActionName().equals(mWokeUpString) ) {
				mActionList.remove(i);
			}
		}
	}
	
	@Override
	public int getCount() {
		return mActionList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mActionList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		BabyEvent action = mActionList.get(mActionList.size() - 1 - position);
		View actionView = null;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) group.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			actionView = inflater.inflate(R.layout.all_actions_list_item, null);
		} else {
			actionView = convertView;
		}
		
		TextView actionName = (TextView)actionView.findViewById(R.id.ActionName);
		actionName.setText(action.getActionName());	
		
		TextView actionTime = (TextView)actionView.findViewById(R.id.ActionTime);
		if( action.getActionName().equals(mNapString) || action.getActionName().equals(mSleepString) ) {
			actionTime.setText(getTimeTextForSleepOrNap(action));
		} 
		else if( action.getActionName().equals(mNursingLeftString) || action.getActionName().equals(mNursingRightString)) {
			actionTime.setText(getTimeTextForNursing(action));
		}
		else if( action.getActionName().equals(mMilkString) ) {
			actionTime.setText(getTimeTextForMilk(action));
		}
		else {			
			actionTime.setText(getTimeAndDateString(action));
		}
		
		ImageView img = (ImageView)actionView.findViewById(R.id.events_list_icon);
		img.setImageDrawable(StaticContext.ctx.getResources().getDrawable(BabyEvent.getActivityIconId(action.getActionName())));		
		
		actionView.setTag(action);
		return actionView;
	}
	
	private String getTimeAndDateString(BabyEvent event) {
	    return mRes.getString(R.string.list_date) + " " + event.getDateString() + "\n" +
	           mRes.getString(R.string.list_time) + " " + event.getTimeString();
	}
	
	private String getTimeTextForMilk(BabyEvent event) {
		int amount = event.getFreeValue();
		String retVal =  (getTimeAndDateString(event) + "\n" + 
				mRes.getString(R.string.amount) + 
				" " + amount + mRes.getString(R.string.ml)); 
		return retVal;
	}
	
	private String getTimeTextForNursing(BabyEvent event) {
		ConsumedTime duration = new ConsumedTime(event.getDurationInSeconds());
		return (getTimeAndDateString(event) + "\n" +  
				mRes.getString(R.string.duration) + 
				" " + duration.toString());
	}
	
	private String getTimeTextForSleepOrNap(BabyEvent event) {
		ConsumedTime duration = ScheduleDatabase.getDurationOfSleepStartedAt(Settings.getCurrentBabyName(), event.getActionDate());
		if( duration != null ) {
			return (getTimeAndDateString(event) + "\n" +  
					mRes.getString(R.string.duration) + 
					duration.toStringWithoutSeconds());
		} else {
			return getTimeAndDateString(event);
		}
	}

}
