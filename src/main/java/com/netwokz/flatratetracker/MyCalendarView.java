package com.netwokz.flatratetracker;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;


import com.netwokz.flatratetracker.calendarview.CaldroidFragment;

import java.util.Calendar;

/**
 * Created by Steve on 10/24/13.
 */
public class MyCalendarView extends FragmentActivity {

    CaldroidFragment calFragment;

    public static MyCalendarView newInstance() {
        MyCalendarView mCalView = new MyCalendarView();
        return mCalView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_fragment);

        calFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        calFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.cal_view, calFragment);
        t.commit();
    }

}
