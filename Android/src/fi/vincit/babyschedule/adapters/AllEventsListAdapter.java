package fi.vincit.babyschedule.adapters;

import java.util.ArrayList;

import utils.BabyEvent;
import utils.ConsumedTime;
import utils.ScheduleDatabase;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fi.vincit.babyschedule.MainTabWidget;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.MainTabWidget.StaticContext;
import fi.vincit.babyschedule.activities.Settings;

public class AllEventsListAdapter extends BaseAdapter {

	private ArrayList<BabyEvent> mActionList;
	private String mSleepString;
	private String mNapString;
	private String mWokeUpString;
	
	public AllEventsListAdapter(ArrayList<BabyEvent> actions) {		
		mActionList = actions;
		removeWokeUps();
		
		mNapString = MainTabWidget.StaticResources.res.getString(R.string.go_to_nap);
		mSleepString = MainTabWidget.StaticResources.res.getString(R.string.go_to_sleep);
		mWokeUpString = MainTabWidget.StaticResources.res.getString(R.string.woke_up);
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
		} else {			
			actionTime.setText(action.getActionDate().toLocaleString());
		}
		
		ImageView img = (ImageView)actionView.findViewById(R.id.events_list_icon);
		img.setImageDrawable(StaticContext.ctx.getResources().getDrawable(BabyEvent.getActivityIconId(action.getActionName())));		
		
		actionView.setTag(action);
		return actionView;
	}
	
	private String getTimeTextForSleepOrNap(BabyEvent event) {
		ConsumedTime duration = ScheduleDatabase.getDurationOfSleepStartedAt(Settings.getCurrentBabyName(), event.getActionDate());
		if( duration != null ) {
			return (event.getActionDate().toLocaleString() + "\nDuration: " + duration.toString());
		} else {
			return (event.getActionDate().toLocaleString());
		}
	}

}
