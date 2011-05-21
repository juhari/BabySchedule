package fi.vincit.babyschedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class BabyEvent implements Comparable<BabyEvent>
								   ,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7385675124548040589L;
	private String actionName;
	private ArrayList<Date> actions;
	
	public BabyEvent(String name, ArrayList<Date> activities) {
		this.actionName = name;
		this.actions = activities;
	}		
	
	public BabyEvent(String name, Date actionDate) {
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
	public int compareTo(BabyEvent another) {
		if( getLastAction().equals(another.getLastAction())) {
			return 0;
		} else if( getLastAction().after(another.getLastAction())) {
			return -1;
		} else {
			return 1;
		}
	}
}
