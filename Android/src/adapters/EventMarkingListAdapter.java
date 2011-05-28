package adapters;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import fi.vincit.babyschedule.BabyEvent;
import fi.vincit.babyschedule.R;

public class EventMarkingListAdapter extends BaseAdapter {

	private ArrayList<BabyEvent> mActivities;
	
	public EventMarkingListAdapter(ArrayList<BabyEvent> activities) {
		mActivities = activities;
	}
	
	public EventMarkingListAdapter() {	
	}
	
	public void setActionList(ArrayList<BabyEvent> actions) {
		mActivities = actions;
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return mActivities.size();
	}

	public Object getItem(int arg0) {
		return mActivities.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup group) {			
		BabyEvent activity = mActivities.get(position);
		View activityView = null;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) group.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			activityView = inflater.inflate(R.layout.main_list_item, null);
		} else {
			activityView = convertView;
		}
		
		TextView name = (TextView)activityView.findViewById(R.id.ActivityName);
		name.setText(activity.getActionName());	
		
		TextView timePassed = (TextView)activityView.findViewById(R.id.LastActivity);
		Log.d("Babyschedule", "ListAdapter, getting activity time.");
		Date date = activity.getLastAction();
		Log.d("Babyschedule", "ListAdapter, got time: " + date);
		if( date != null ) {	
			String time = "Last occurred at: " + date.toLocaleString();
			
			Date current = new Date();
			long diff = current.getTime() - date.getTime();
			long diffInMinutes = (diff/(1000*60)) % 60;
			long diffInHours = (diff/(1000*3600)) % 24;
			long diffInDays = (diff/(1000*3600*24));
			
			String timeDiff = "\n" + diffInDays + " days, " + diffInHours + " hours, " + diffInMinutes +
							  " minutes ago";
			
			timePassed.setText(time + timeDiff);			
		} else {
			timePassed.setText("No \"" + activity.getActionName() + "\" events occurred");
		}
		
		// Remember the activity for each button so that we can refer to it when the button is clicked
		Button nowButton = (Button)activityView.findViewById(R.id.Now);
		nowButton.setText("Mark " + activity.getActionName() + "!");
		
		name.setTag(activity);
		nowButton.setTag(activity);		
		timePassed.setTag(activity);
		return activityView;
	}
	
	public void updateActivityTimeNow(BabyEvent activity)
	{
		activity.addActionNow();
		notifyDataSetChanged();
	}

}
