package fi.vincit.babyschedule.chartengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PeriodBarChartData {

    public class PeriodBarChartDataItem {
        public float startTime;
        public float endTime;
    }


    Map<Integer, List<PeriodBarChartDataItem>> mDataItems = new HashMap<Integer, List<PeriodBarChartDataItem>>();


    public int getSize() {
        return mDataItems.size();
    }

    public void addItem(int xIndex, float startTime, float endTime) {
        if( mDataItems.get(xIndex) == null ) {
            mDataItems.put(xIndex, new ArrayList<PeriodBarChartDataItem>());
        }
        List<PeriodBarChartDataItem> itemsListForX = mDataItems.get(xIndex);
        PeriodBarChartDataItem item = new PeriodBarChartDataItem();
        item.startTime = startTime;
        item.endTime = endTime;
        itemsListForX.add(item);
    }

    public List<PeriodBarChartDataItem> getItemsAt(int xIndex) {
        return mDataItems.get(xIndex);
    }
}