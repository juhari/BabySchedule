package utils;

import java.io.Serializable;
import java.util.Date;

import android.content.Context;
import fi.vincit.babyschedule.MainTabWidget.StaticContext;
import fi.vincit.babyschedule.R;

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
	
	public static int getActivityIconId(String activityName) {
		Context context = StaticContext.ctx;
		String[] aNames = context.getResources().getStringArray(R.array.activity_names);
		if( activityName.equalsIgnoreCase(aNames[0]) ) {
			return R.drawable.eat_icon;
		}
		else if( activityName.equalsIgnoreCase(aNames[4]) ) {
			return R.drawable.bath_icon;
		}
		else if( activityName.equalsIgnoreCase(aNames[5]) ||
				 activityName.equalsIgnoreCase(aNames[6])) {
			return R.drawable.milk_icon;
		}
		else if( activityName.equalsIgnoreCase(aNames[8]) ) {
			return R.drawable.icon_diaper;			
		}
		else if( activityName.equalsIgnoreCase(aNames[9]) ) {
			return R.drawable.potty_icon;			
		}	
		else if( activityName.equalsIgnoreCase(aNames[1]) ||
				 activityName.equalsIgnoreCase(aNames[2]) ||
				 activityName.equalsIgnoreCase(aNames[3]) ||
				 activityName.equalsIgnoreCase(aNames[7])) {
			return R.drawable.sleep_icon;
		}
		else {
			return R.drawable.icon_empty;
		}
	}
}
