package fi.vincit.babyschedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ScheduleDatabase {
   
   private static final String DATABASE_CREATE_BABY_SCHEDULE = "create table babyschedule (_id integer primary key autoincrement, activityname text not null, time text not null );";
   
   
   private static final String DATABASE_NAME = "BabySchedule";
   private static final int DATABASE_VERSION = 2;
   private static final String TABLE_BABY_SCHEDULE = "babyschedule";
   
   
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
        	db.execSQL(DATABASE_CREATE_BABY_SCHEDULE);
        };

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("Babyschedule", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS babyschedule");
            onCreate(db);
        }
    }
	   
    public static long insertBabyActivity(String name) {
    	Log.i("Babyschedule", "creating baby schedule to with activity name: " + name);
        ContentValues values = new ContentValues();
        values.put("activityname", name);
        Date now = new Date();
        values.put("time", now.toLocaleString());
        
        return mDb.insert(TABLE_BABY_SCHEDULE, null, values);
    }	     
    
    public static Cursor fetchEntireTable(){
    	return mDb.query(TABLE_BABY_SCHEDULE, new String[] {"_id", "activityname", "time"},  null, null, null, null, null);
    }   
    
    public static void removeFromDb(Cursor cursor) {
    	int amountDeleted = mDb.delete(TABLE_BABY_SCHEDULE, ""+cursor.getPosition(), null);
    	Log.i("Babyschedule", "Deleted " + amountDeleted + " rows from db.");
    }        
    
    public static ArrayList<BabyActivity> getAllBabyActivities(String[] activityNames) {   
    	ArrayList<BabyActivity> activityList = new ArrayList<BabyActivity>();
    	for( String activityName : activityNames ) {
    		ArrayList<Date> currentActivityDates = getActivityDatesForActivity(activityName);
    		BabyActivity currentActivity = new BabyActivity(activityName, currentActivityDates);
    		Log.i("Babyschedule", "Added baby activity: " + activityName + ":\n" + currentActivityDates);
    		activityList.add(currentActivity);
    	}
    	    	    	
		return activityList;    	
    }
    
    private static ArrayList<Date> getActivityDatesForActivity(String activityName){
    	Log.i("Babyschedule", "requesting dates for activity " + activityName);
    	Cursor cursor = mDb.query(TABLE_BABY_SCHEDULE, 
    							  new String[] {"_id", "activityname", "time"}, 
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
    
    private static Date getRowTime(Cursor cursor) {	
    	String time = cursor.getString(2);
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
}
