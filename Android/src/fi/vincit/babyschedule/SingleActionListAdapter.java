package fi.vincit.babyschedule;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class SingleActionListAdapter extends BaseAdapter {

	private ArrayList<Date> mDateList;
	
	public SingleActionListAdapter(ArrayList<Date> dates) {
		// TODO Auto-generated constructor stub
		Log.d("Babyschedule", "SingleActionListAdapter, dateList: " + dates);
		mDateList = dates;
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
		Date date = mDateList.get(position);
		View dateView = null;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) group.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dateView = inflater.inflate(R.layout.single_action_list_item, null);
		} else {
			dateView = convertView;
		}
		
		TextView dateTime = (TextView)dateView.findViewById(R.id.ActionTime);
		dateTime.setText(date.toLocaleString());	
		
		// Remember the activity for each button so that we can refer to it when the button is clicked
		Button deleteButton = (Button)dateView.findViewById(R.id.Delete);
		deleteButton.setTag(date);		
		return dateView;
	}

}
