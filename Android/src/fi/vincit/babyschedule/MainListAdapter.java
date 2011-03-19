package fi.vincit.babyschedule;

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

public class MainListAdapter extends BaseAdapter {

	private final ArrayList<BabyActivity> mActivities;
	
	public MainListAdapter(Context c, ArrayList<BabyActivity> activities) {
		mActivities = activities;
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
		BabyActivity activity = mActivities.get(position);
		View activityView = null;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) group.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			activityView = inflater.inflate(R.layout.main_list_item, null);
		} else {
			activityView = convertView;
		}
		
		TextView name = (TextView)activityView.findViewById(R.id.ActivityName);
		name.setText(activity.getActivityName());	
		
		TextView timePassed = (TextView)activityView.findViewById(R.id.LastActivity);
		Log.d("Babyschedule", "ListAdapter, getting activity time.");
		Date date = activity.getLastActivity();
		Log.d("Babyschedule", "ListAdapter, got time: " + date);
		if( date != null ) {	
			String time = "Last occurred at: " + date.toLocaleString();
			
			Date current = new Date();
			long diff = current.getTime() - date.getTime();
			long diffInDays = diff/(1000*3600*24);
			long diffInHours = (diff - diffInDays*(1000*3600*24))/(1000*3600);
			long diffInMinutes = (diff - diffInHours*(1000*3600))/(1000*60);
			long diffInSeconds = (diff - diffInMinutes*(1000*60))/(1000);
			
			String timeDiff = "\n" + diffInDays + " days, " + diffInHours + " hours, " + diffInMinutes +
							  " minutes and " + diffInSeconds + " seconds ago";
			
			timePassed.setText(time + timeDiff);
		} else {
			timePassed.setText("No \"" + activity.getActivityName() + "\" events occurred");
		}
		
		// Remember the activity for each button so that we can refer to it when the button is clicked
		Button nowButton = (Button)activityView.findViewById(R.id.Now);
		nowButton.setTag(activity);
		
		return activityView;
	}
	
	public void updateActivityTimeNow(BabyActivity activity)
	{
		activity.addActivityNow();
		notifyDataSetChanged();
	}

}