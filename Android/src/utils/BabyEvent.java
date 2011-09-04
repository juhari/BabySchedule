package utils;

import java.io.Serializable;
import java.util.Date;

public class BabyEvent implements Comparable<BabyEvent>
								   ,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7385675124548040589L;
	private String actionName;
	private Date actionDate;
	
	public BabyEvent(String name, Date actionDate) {
		this.actionName = name;
		this.actionDate = actionDate;
	}		
	
	public void setActionName(String activityName) {
		this.actionName = activityName;
	}
	
	public String getActionName() {
		return actionName;
	}
	
	public Date getActionDate() {
		return actionDate;
	}

	@Override
	public int compareTo(BabyEvent another) {
		if( getActionDate().equals(another.getActionDate())) {
			return 0;
		} else if( getActionDate().after(another.getActionDate())) {
			return 1;
		} else {
			return -1;
		}
	}
}
