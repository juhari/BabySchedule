package fi.vincit.babyschedule;

import java.util.ArrayList;
import java.util.Date;

public class ConsumedTime {
	
	private long days;
	private long hours;
	private long minutes;
	
	public ConsumedTime(long days, long hours, long minutes) {
		this.days = days;
		this.hours = hours;
		this.minutes = minutes;
	}
	
	public ConsumedTime(Date newer, Date oldDate) {
		long diff = newer.getTime() - oldDate.getTime();
		this.minutes = (diff/(1000*60)) % 60;
		this.hours = (diff/(1000*3600)) % 24;
		this.days = (diff/(1000*3600*24));
	}
	
	public static ConsumedTime getAverageOf(ArrayList<ConsumedTime> times) {
		ConsumedTime sum = new ConsumedTime(0,0,0);
		ConsumedTime avg = new ConsumedTime(0,0,0);
		for( ConsumedTime time : times ) {
			sum = sum.addition(time);
		}
		
		long totalMinutes = sum.minutes + sum.hours*60 + sum.days*24*60;
		totalMinutes /= times.size();
		
		avg.setMinutes(totalMinutes % 60);
		avg.setHours((totalMinutes / 60) % 24);
		avg.setDays(totalMinutes / (60*24));
		sum = null;
		return avg;
	}
	
	public String toString() {
		return this.getDays() + " days, " + this.getHours() + " hours, " + this.getMinutes() + " minutes";
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
	
	public ConsumedTime addition(ConsumedTime addee) {
		ConsumedTime time = new ConsumedTime(0,0,0);
		long totalMinutes = this.minutes + this.hours*60 + this.days*24*60;
		totalMinutes += addee.minutes + addee.hours*60 + addee.days*24*60;
		
		time.setMinutes(totalMinutes % 60);
		time.setHours((totalMinutes / 60) % 24);
		time.setDays(totalMinutes / (60*24));
		return time;				
	}
	
}
