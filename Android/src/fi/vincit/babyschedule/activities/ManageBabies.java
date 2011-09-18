package fi.vincit.babyschedule.activities;

import java.util.ArrayList;

import utils.ScheduleDatabase;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import fi.vincit.babyschedule.R;

public class ManageBabies extends ListActivity 
						  implements View.OnCreateContextMenuListener {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        populateListContents();
        
        registerForContextMenu(getListView());
    }
	
	@Override
    public void onListItemClick(ListView l, View v, int position, long id) {
		v.performLongClick();
    }
	
	private void populateListContents() {
		ArrayList<String> babyNames = ScheduleDatabase.getBabyNames();
        setListAdapter(new ArrayAdapter<String>(this, 
             android.R.layout.simple_list_item_1, babyNames));
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.babymanagecontextmenu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    String babyName = (String)getListAdapter().getItem(info.position);

		Log.d("Babyschedule", "Clicked context menu on baby: " + babyName);
		//BabyEvent event = (BabyEvent)info.targetView.getTag();
		switch (item.getItemId()) {
		case R.id.delete_baby:			
			ScheduleDatabase.removeBaby(babyName);
			populateListContents();
			return true;
		case R.id.edit_baby:
			// TODO: add baby edit view
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
}
