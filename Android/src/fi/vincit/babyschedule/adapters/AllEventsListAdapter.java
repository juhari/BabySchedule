package fi.vincit.babyschedule.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fi.vincit.babyschedule.BabyEvent;
import fi.vincit.babyschedule.R;

public class AllEventsListAdapter extends BaseAdapter {

	private ArrayList<BabyEvent> mActionList;
	
	public AllEventsListAdapter(ArrayList<BabyEvent> actions) {		
		mActionList = actions;
	}
	
	public void setActionsList(ArrayList<BabyEvent> actions) {
		mActionList = actions;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mActionList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mActionList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		BabyEvent action = mActionList.get(mActionList.size() - 1 - position);
		View actionView = null;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) group.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			actionView = inflater.inflate(R.layout.all_actions_list_item, null);
		} else {
			actionView = convertView;
		}
		
		TextView actionName = (TextView)actionView.findViewById(R.id.ActionName);
		actionName.setText(action.getActionName());	
		
		TextView actionTime = (TextView)actionView.findViewById(R.id.ActionTime);
		actionTime.setText(action.getLastAction().toLocaleString());
		
		actionView.setTag(action);
		return actionView;
	}

}
