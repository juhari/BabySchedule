package fi.vincit.babyschedule;

import java.util.ArrayList;
import java.util.Date;

public class BabyAction implements Comparable<BabyAction>{
	private String actionName;
	private ArrayList<Date> actions;
	
	public BabyAction(String name, ArrayList<Date> activities) {
		this.actionName = name;
		this.actions = activities;
	}		
	
	public BabyAction(String name, Date actionDate) {
		this.actionName = name;
		this.actions = new ArrayList<Date>();
		this.actions.add(actionDate);
	}		
	
	public void addActionNow()
	{
		actions.add(new Date());
	}
	
	public void setActionName(String activityName) {
		this.actionName = activityName;
	}
	
	public String getActionName() {
		return actionName;
	}
	
	public Date getLastAction() {
		Date lastActivity = null;
		if( !actions.isEmpty() ){
			lastActivity = actions.get(actions.size()-1); 
		}
		return lastActivity;
	}

	@Override
	public int compareTo(BabyAction another) {
		if( getLastAction().equals(another.getLastAction())) {
			return 0;
		} else if( getLastAction().after(another.getLastAction())) {
			return -1;
		} else {
			return 1;
		}
	}
}
