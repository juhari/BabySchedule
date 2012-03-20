package utils;

import java.io.Serializable;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
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
	private int durationInSeconds = 0;
	private int freeValue = 0;
	
	public BabyEvent(String name, Date actionDate) {
		this.actionName = name;
		this.actionDate = actionDate;
	}		
	
	public BabyEvent(String name, Date actionDate, int duration, int freeValue) {
		this.actionName = name;
		this.actionDate = actionDate;
		this.durationInSeconds = duration;
		this.freeValue = freeValue;
	}
	
	public void setActionName(String activityName) {
		this.actionName = activityName;
	}
	
	/**
	 * 
	 * @return the date as dd.mm.yyyy
	 */
	public String getDateString() {
	    return getActionDate().getDate() + "." + (getActionDate().getMonth()+1) + "." + (getActionDate().getYear()+1900);
	}
	
	/**
	 * 
	 * @return returns time as hh.mm
	 */
	public String getTimeString() {
	    int hours = getActionDate().getHours();
	    String hoursStr = hours + "";
	    if( hours < 10) {
	        hoursStr = "0" + hoursStr;
	    }
	    int minutes = getActionDate().getMinutes();
        String minutesStr = minutes + "";
        if( minutes < 10) {
            minutesStr = "0" + minutesStr;
        }
	    return hoursStr + "." + minutesStr;
	}
	
	public String getActionName() {
		return actionName;
	}
	
	public Date getActionDate() {
		return actionDate;
	}
	
	public int getDurationInSeconds() {
		return durationInSeconds;
	}
	
	public int getFreeValue() {
		return freeValue;
	}

	public void setFreeValue(int freeValue) {
		this.freeValue = freeValue;
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
		Resources res = context.getResources();
		if( activityName.equalsIgnoreCase(res.getString(R.string.eat_activity)) ) {
			return R.drawable.eat_icon;
		}		
		else if( activityName.equalsIgnoreCase(res.getString(R.string.bath_activity)) ) {
			return R.drawable.bath_icon;
		}
		else if( activityName.equalsIgnoreCase(res.getString(R.string.milk_activity)) ) {
			return R.drawable.milk_icon;
		}
		else if( activityName.equalsIgnoreCase(res.getString(R.string.nursing)) ||
				 activityName.equalsIgnoreCase(res.getString(R.string.nurse_left_activity)) ||
				 activityName.equalsIgnoreCase(res.getString(R.string.nurse_right_activity))) {
			return R.drawable.nurse_icon;
		}
		else if( activityName.equalsIgnoreCase(res.getString(R.string.changed_diaper_activity)) ) {
			return R.drawable.icon_diaper;			
		}
		else if( activityName.equalsIgnoreCase(res.getString(R.string.made_poop_activity)) ) {
			return R.drawable.potty_icon;			
		}	
		else if( activityName.equalsIgnoreCase(res.getString(R.string.go_to_nap_activity)) ||
				 activityName.equalsIgnoreCase(res.getString(R.string.go_to_sleep_activity)) ||
				 activityName.equalsIgnoreCase(res.getString(R.string.woke_up_activity)) ||
				 activityName.equalsIgnoreCase(res.getString(R.string.woke_up_during_night_activity))) {
			return R.drawable.sleep_icon;
		}
		else {
			return R.drawable.icon_empty;
		}
	}
}
