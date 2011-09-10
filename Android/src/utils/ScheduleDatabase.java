package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import fi.vincit.babyschedule.R;

public class ScheduleDatabase {
   private static final String DATABASE_CREATE_BABY_NAMES = "create table babynames (_id integer primary key autoincrement, babyname text not null );";
   private static final String DATABASE_CREATE_BABY_SCHEDULE = "(_id integer primary key autoincrement, babyname text not null, activityname text not null, time text not null );";
   
   @SuppressWarnings("unused")
   private static final int BABY_NAME_COLUMN = 1;
   private static final int ACTIVITY_NAME_COLUMN = 2;
   private static final int TIME_COLUMN = 3;
   
   private static final String DATABASE_NAME = "BabySchedule";
   private static final int DATABASE_VERSION = 6;
   private static final String TABLE_BABY_NAMES = "babynames";
   
   
   private static Context mCtx = null;
   private static DatabaseHelper mDbHelper = null;
   private static SQLiteDatabase mDb = null;   
   
   private static boolean mOpen = false;
   
	public static void open(Context ctx) throws SQLException {		
		if( !mOpen )
		{
			mCtx = ctx;		
		    mDbHelper = new DatabaseHelper(mCtx);
	    	mDb = mDbHelper.getWritableDatabase();
	    	mOpen = true; 
		}
	}
	
	public static boolean isOpen() {
		return mOpen;
	}
	
	public static void close() {
	    mDbHelper.close();
	    mOpen = false;
	}
   
	private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	// Create tables
        	Log.w("Babyschedule", "Creating database!!!");
        	db.execSQL(DATABASE_CREATE_BABY_NAMES);
        };

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Babyschedule", "Upgrading database from version " + oldVersion + " to "
                    + newVersion);
            db.execSQL("DROP TABLE IF EXISTS babynames");
            db.execSQL(DATABASE_CREATE_BABY_NAMES);
            db.execSQL("ALTER TABLE babyschedule RENAME TO verneri;");
        }
    }
	   
	public static ArrayList<String> getBabyNames() {
		Log.w("Babyschedule", "getBabyNames()");
		ArrayList<String> nameList = new ArrayList<String>();
    	Cursor names = mDb.query(TABLE_BABY_NAMES, new String[] {"_id", "babyname"},  null, null, null, null, null);
    	
    	if( names.moveToFirst() ) {
	    	while( !names.isAfterLast() ) {
	    		Log.w("Babyschedule", "getBabyNames(): " + names.getString(BABY_NAME_COLUMN));
	    		nameList.add(names.getString(BABY_NAME_COLUMN));	    				
	    		names.moveToNext();
	    	}
    	}
    	return nameList;
	}
	
	public static void addNewBaby(String babyName) {
		Cursor nameCursor = mDb.query(TABLE_BABY_NAMES, new String[] {"_id", "babyname"}, 
										"babyname = '" + babyName + "'", null, null, null, null);
		if( nameCursor.getCount() == 0 ) {
			ContentValues values = new ContentValues();
			values.put("babyname", babyName);
			mDb.insert(TABLE_BABY_NAMES, null, values);
			mDb.execSQL("create table " + babyName + " " + DATABASE_CREATE_BABY_SCHEDULE);
		}
	}
	
    public static long insertBabyAction(String babyName, String actionname, Date time) {
    	Log.i("Babyschedule", "creating baby schedule to with activity name: " + actionname);
        ContentValues values = new ContentValues();
        values.put("babyname", babyName);
        values.put("activityname", actionname);
        values.put("time", time.toLocaleString());
        
        return mDb.insert(babyName, null, values);
    }	     
    
    public static Cursor fetchEntireTable(String tableName){
    	return mDb.query(tableName, new String[] {"_id", "babyname", "activityname", "time"},  null, null, null, null, null);
    }   
    
    public static void removeFromDb(String tableName, Cursor cursor) {
    	int amountDeleted = mDb.delete(tableName, ""+cursor.getPosition(), null);
    	Log.i("Babyschedule", "Deleted " + amountDeleted + " rows from db.");
    }        
    
    public static void deleteEntryBasedOnDate(String babyName, Date date) {
    	String dateString = date.toLocaleString();
    	int deleted = mDb.delete(babyName, "time = '" + dateString + "'", null);
  
    	Log.i("Babyschedule", "found " + deleted + " rows to be deleted in db.");						  
    }
    
    public static BabyEvent getEventBasedOnDate(String babyName, Date date) {
    	String dateString = date.toLocaleString();
    	BabyEvent event = null;
    	Cursor cursor = mDb.query(babyName, 
				  new String[] {"_id", "babyname", "activityname", "time"}, 
				  "time = '" + dateString + "'", 
				  null, null, null, null);
    	
    	if( cursor.moveToFirst() ) {
    		event = new BabyEvent(getRowActionName(cursor), getRowTime(cursor));
    	} 
    	
    	return event;
    }
    
    public static ArrayList<BabyEvent> getAllBabyActions(String babyName, String[] actionNames) {   
    	ArrayList<BabyEvent> activityList = new ArrayList<BabyEvent>();
    	for( String activityName : actionNames ) {
    		ArrayList<Date> currentActivityDates = getActionDatesForAction(babyName, activityName);
    		for( Date actionDate : currentActivityDates ) {
    			BabyEvent currentActivity = new BabyEvent(activityName, actionDate);
    			Log.i("Babyschedule", "Added baby activity: " + activityName + ":\n" + currentActivityDates);
    			activityList.add(currentActivity);
    		}
    	}
    	    	    	
		return activityList;    	
    }
    
    public static ArrayList<BabyEvent> getAllDbActionsSortedByDate(String babyName) {
    	Cursor everything = fetchEntireTable(babyName);
    	
    	ArrayList<BabyEvent> actions = new ArrayList<BabyEvent>();
    	if( everything.moveToFirst() ) {
	    	while( !everything.isAfterLast() ) {
	    		actions.add(new BabyEvent(getRowActionName(everything), getRowTime(everything)));
	    		everything.moveToNext();
	    	}
    	}
    	
    	Collections.sort(actions);
    	return actions;
    }
    
    public static ArrayList<Date> getActionDatesForAction(String babyName, String activityName){
    	Log.i("Babyschedule", "requesting dates for activity " + activityName);
    	Cursor cursor = mDb.query(babyName, 
    							  new String[] {"_id", "babyname", "activityname", "time"}, 
    							  "activityname = '" + activityName + "'", 
    							  null, null, null, null);
    	
    	ArrayList<Date> dateList = new ArrayList<Date>();
    	Log.i("Babyschedule", "found " + cursor.getCount() + " rows in db.");
    	
    	if( cursor.moveToFirst() ) {
	    	while( !cursor.isAfterLast() ) {
	    		dateList.add(getRowTime(cursor));
	    		cursor.moveToNext();
	    	}
    	}
    	
    	Collections.sort(dateList);
    	return dateList;
    }           
    
    public static Date getLastActionOfType(String babyName, String actionName){
    	ArrayList<Date> dates = getActionDatesForAction(babyName, actionName);
    	if( dates.size() > 0 ) {    		
    		return (Date) dates.get(dates.size()-1);
    	} else {
    		return null;
    	}
    }
            
    public static ConsumedTime getDurationOfSleepStartedAt(String babyName, Date sleepStartTime) {
    	Date wakeUpDate = getWakeUpDateFromSleepDate(babyName, sleepStartTime);
    	if( wakeUpDate == null ) {
    		return null;
    		//wakeUpDate = new Date();
    	}
    	BabyEvent sleepOrNapEvent = getEventBasedOnDate(babyName, sleepStartTime);
		assert(sleepOrNapEvent != null);
    	
		return new ConsumedTime(wakeUpDate, sleepStartTime);
    }
    
    public static Date getWakeUpDateFromSleepDate(String babyname, Date sleepStartDate) {
    	ArrayList<Date> dates = getActionDatesForAction(babyname, mCtx.getString(R.string.woke_up));
    	
    	for( Date current : dates ) {
    		if( current.after(sleepStartDate) ) {
    			return current;
    		}
    	}
    	
    	return null;
    }
    
    private static Date getRowTime(Cursor cursor) {	
    	String time = cursor.getString(TIME_COLUMN);
    	Log.i("Babyschedule", "trying to parse row time with time string: " + time);
    	Date date = null;
    	DateFormat format = DateFormat.getDateTimeInstance();
    	try {
    		date = format.parse(time);
    	}
    	catch( ParseException e)
    	{
    		// do nothing, return null
    	}
    	return date;
    }      
    
    private static String getRowActionName(Cursor cursor) {
    	return cursor.getString(ACTIVITY_NAME_COLUMN);
    }
}
