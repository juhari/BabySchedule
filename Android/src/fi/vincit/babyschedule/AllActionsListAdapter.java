package fi.vincit.babyschedule;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AllActionsListAdapter extends BaseAdapter {

	private ArrayList<BabyAction> mActionList;
	
	public AllActionsListAdapter(ArrayList<BabyAction> actions) {		
		mActionList = actions;
	}
	
	public void setActionsList(ArrayList<BabyAction> actions) {
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
		BabyAction action = mActionList.get(position);
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
		actionTime.setText("Marked at: " + action.getLastAction().toLocaleString());
		
		// Remember the activity for each button so that we can refer to it when the button is clicked
		Button deleteButton = (Button)actionView.findViewById(R.id.Delete);
		deleteButton.setTag(action.getLastAction());		
		
		return actionView;
	}

}
