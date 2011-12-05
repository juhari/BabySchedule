package fi.vincit.babyschedule.adapters;

import java.util.ArrayList;
import java.util.Date;

import utils.BabyEvent;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.MainTabWidget.StaticContext;

public class SingleEventListAdapter extends BaseAdapter {

	private ArrayList<Date> mDateList;
	private String mEventName;
	
	public SingleEventListAdapter(ArrayList<Date> dates, String eventName) {
		// TODO Auto-generated constructor stub
		Log.d("Babyschedule", "SingleActionListAdapter, dateList: " + dates);
		mDateList = dates;
		mEventName = eventName;
	}
	
	public void setDateList(ArrayList<Date> dates) {
		mDateList = dates;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mDateList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mDateList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		Date date = mDateList.get(mDateList.size() - 1 - position);
		View dateView = null;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) group.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dateView = inflater.inflate(R.layout.all_actions_list_item, null);
		} else {
			dateView = convertView;
		}
		
		TextView actionName = (TextView)dateView.findViewById(R.id.ActionName);
		actionName.setText(mEventName);	
		
		TextView dateTime = (TextView)dateView.findViewById(R.id.ActionTime);
		dateTime.setText(date.toLocaleString());	
		
		ImageView img = (ImageView)dateView.findViewById(R.id.events_list_icon);
		img.setImageDrawable(StaticContext.ctx.getResources().getDrawable(BabyEvent.getActivityIconId(mEventName)));		
		
		dateView.setTag(date);
			
		return dateView;
	}

}
