package com.aipxperts.ecountdown.Adapter;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.aipxperts.ecountdown.Fragment.ListEventFutureFragment;
import com.aipxperts.ecountdown.Fragment.ListEventPastFragment;
import com.aipxperts.ecountdown.Fragment.ListEventRecentFragment;

/**
 * Created by aipxperts-ubuntu-01 on 25/7/17.
 */

public class EventPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public EventPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ListEventRecentFragment tab1 = new ListEventRecentFragment();
                return tab1;
            case 1:
                ListEventFutureFragment tab2 = new ListEventFutureFragment();
                return tab2;
            case 2:
                ListEventPastFragment tab3 = new ListEventPastFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
        //do nothing here! no call to super.restoreState(arg0, arg1);
    }
}
