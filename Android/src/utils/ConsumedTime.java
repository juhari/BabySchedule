package utils;

import java.util.ArrayList;
import java.util.Date;

import fi.vincit.babyschedule.MainTabWidget;
import fi.vincit.babyschedule.R;

public class ConsumedTime {
	
	private long days = 0;
	private long hours = 0;
	private long minutes = 0;
	private long seconds = 0;	

	private static String dayString = MainTabWidget.StaticResources.res.getString(R.string.days);
	private static String hourString = MainTabWidget.StaticResources.res.getString(R.string.hours);
	private static String minuteString = MainTabWidget.StaticResources.res.getString(R.string.minutes);
	private static String secondString = MainTabWidget.StaticResources.res.getString(R.string.seconds);
	
	public ConsumedTime() {
	}
	
	public ConsumedTime(long days, long hours, long minutes) {
		this.days = days;
		this.hours = hours;
		this.minutes = minutes;
	}
	
	public ConsumedTime(long days, long hours, long minutes, long seconds) {
		this.days = days;
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
	}
	
	public ConsumedTime(long seconds) {
		this.seconds = seconds % 60;
		this.minutes = (seconds/60) % 60;
		this.hours = (seconds/3600) % 24;
		this.days = (seconds/3600*24);
	}
	
	public ConsumedTime(Date newer, Date oldDate) {
		long diff = newer.getTime() - oldDate.getTime();
		this.seconds = (diff/1000) % 60;
		this.minutes = (diff/(1000*60)) % 60;
		this.hours = (diff/(1000*3600)) % 24;
		this.days = (diff/(1000*3600*24));
	}
	
	public static ConsumedTime getAverageOf(ArrayList<ConsumedTime> times) {
		ConsumedTime sum = new ConsumedTime();
		ConsumedTime avg = new ConsumedTime();
		for( ConsumedTime time : times ) {
			sum = sum.addition(time);
		}
		
		long totalSeconds = sum.minutes*60 + sum.hours*60*60 + sum.days*24*60*60;
		totalSeconds /= times.size();
		
		avg.setSeconds(totalSeconds % 60);
		avg.setMinutes((totalSeconds / 60) % 60);
		avg.setHours((totalSeconds / 3600) % 24);
		avg.setDays(totalSeconds / (3600*24));
		sum = null;
		return avg;
	}
	
	public String toStringWithoutSeconds() {
		if( this.getDays() > 0 ) {
			return this.getDays() + dayString + " " + this.getHours() + hourString + " " + 
				   this.getMinutes() + minuteString;
		} else if( this.getHours() > 0 ) {
			return this.getHours() + hourString + " " + 
			   	   this.getMinutes() + minuteString;
		} else {
			return this.getMinutes() + minuteString;
		}
	}
	
	public String toString() {
		if( this.getDays() > 0 ) {
			return this.getDays() + dayString + " " + this.getHours() + hourString + " " + 
				   this.getMinutes() + minuteString + " " + this.getSeconds() + secondString;
		} else if( this.getHours() > 0 ) {
			return this.getHours() + hourString + " " + 
			   	   this.getMinutes() + minuteString + " " + this.getSeconds() + secondString;
		} else if( this.getMinutes() > 0 ) {
			return this.getMinutes() + minuteString + " " + this.getSeconds() + secondString;
		} else {
			return this.getSeconds() + secondString;
		}
	}
	
	public double getHoursDecimals() {
		double hourDecimals = getHours();
		hourDecimals += (double)getMinutes()/60.0;
		return hourDecimals;
	}
	
	public long getDays() {
		return days;
	}
	public void setDays(long days) {
		this.days = days;
	}
	public long getHours() {
		return hours;
	}			
	public void setHours(long hours) {
		this.hours = hours;
	}
	public long getMinutes() {
		return minutes;
	}
	public void setMinutes(long minutes) {
		this.minutes = minutes;
	}
	
	public long getSeconds() {
		return seconds;
	}

	public void setSeconds(long seconds) {
		this.seconds = seconds;
	}
	
	public ConsumedTime addition(ConsumedTime addee) {
		ConsumedTime time = new ConsumedTime();
		long totalSeconds = this.seconds + this.minutes*60 + this.hours*3600 + this.days*24*3600;
		totalSeconds += addee.seconds + addee.minutes*60 + addee.hours*3600 + addee.days*24*3600;
		
		time.setSeconds(totalSeconds % 60);
		time.setMinutes((totalSeconds / 60) % 60);
		time.setHours((totalSeconds / 3600) % 24);
		time.setDays(totalSeconds / (3600*24));
		return time;				
	}
	
}
