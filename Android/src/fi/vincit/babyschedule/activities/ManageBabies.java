package fi.vincit.babyschedule.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.utils.ScheduleDatabase;

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
			showRenameBabyDialog(babyName);			
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	private void showRenameBabyDialog(final String oldName) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(getString(R.string.edit_baby));
        alert.setMessage(getString(R.string.edit_baby_instruction));

        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        input.setText(oldName);
        alert.setView(input);
        
        alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
          String newName = input.getText().toString();
          if( ScheduleDatabase.getBabyNames().contains(newName) ) {
              Log.w("Babyschedule", "Settings::addBaby, unable to add, already exists with the same name.");
              Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.baby_exists), 1000);
              toast.show();
              return;
          }
          ScheduleDatabase.renameBaby(oldName, newName);
          populateListContents();
          }
        });

        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
              // do nothing
          }
        });

        alert.show();
        
    }
}
