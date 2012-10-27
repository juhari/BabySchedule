package fi.vincit.babyschedule.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import fi.vincit.babyschedule.R;
import fi.vincit.babyschedule.graphviews.SleepRangeGraphView;

/**
 * Created with IntelliJ IDEA.
 * User: JuJoVeTu
 * Date: 13.10.2012
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
public class BarTestActivity extends Activity {
    private LinearLayout mGraphLayout;
    private SleepRangeGraphView mSleepRangeView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barstatistics);

        mGraphLayout = (LinearLayout) findViewById(R.id.bargraphlayout);

        mSleepRangeView = new SleepRangeGraphView(this);

        Button previous = (Button)findViewById(R.id.previousItemsButton);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSleepRangeView.loadPreviousItems();
            }
        });

        Button next = (Button)findViewById(R.id.nextItemsButton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSleepRangeView.loadNextItems();
            }
        });

        mGraphLayout.addView(mSleepRangeView);
    }


}
