package fi.vincit.babyschedule;

import java.util.ArrayList;
import java.util.Date;

public class BabyActivity {
	private String activityName;
	private ArrayList<Date> activities;
	
	public BabyActivity(String name, ArrayList<Date> activities) {
		this.activityName = name;
		this.activities = activities;
	}		
	
	public void addActivityNow()
	{
		activities.add(new Date());
	}
	
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
	public String getActivityName() {
		return activityName;
	}
	
	public Date getLastActivity() {
		Date lastActivity = null;
		if( !activities.isEmpty() ){
			lastActivity = activities.get(activities.size()-1); 
		}
		return lastActivity;
	}
}
