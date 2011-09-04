package utils;

import java.util.Date;

import fi.vincit.babyschedule.MainTabWidget;
import fi.vincit.babyschedule.R;

public class SleepEvent extends BabyEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -544253565319856738L;
	private ConsumedTime sleepDuration = null;
	private boolean isSleep = false;

	public SleepEvent(String name, Date actionDate) {
		super(name, actionDate);
		String sleepString = MainTabWidget.StaticResources.res.getString(R.string.go_to_sleep);
		String napString = MainTabWidget.StaticResources.res.getString(R.string.go_to_nap);
		assert(name.equals(sleepString) || name.equals(napString));
			
		if( name.equals(sleepString) ) {
			setSleep(true);
		}
		
		setSleepDuration(ScheduleDatabase.getDurationOfSleepStartedAt(actionDate));
	}

	private void setSleepDuration(ConsumedTime sleepDuration) {
		this.sleepDuration = sleepDuration;
	}

	public ConsumedTime getSleepDuration() {
		return sleepDuration;
	}

	private void setSleep(boolean isSleep) {
		this.isSleep = isSleep;
	}

	public boolean isSleep() {
		return isSleep;
	}

}
