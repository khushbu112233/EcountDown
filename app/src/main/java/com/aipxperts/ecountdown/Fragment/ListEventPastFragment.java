package com.aipxperts.ecountdown.Fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aipxperts.ecountdown.Adapter.ListEventAdapter;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.databinding.ListFragmentTabLayoutBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmResults;
import io.realm.Sort;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 25/7/17.
 */

public class ListEventPastFragment extends Fragment {

    ListFragmentTabLayoutBinding mBinding;
    View rootView;
    Context context;
    Event[] events1;
    ArrayList<Event> eventArrayList_past=new ArrayList<>();
    int numOfDays;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.list_fragment_tab_layout, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        geteventsfromDb();

        if(eventArrayList_past.size()>0)
        {
            mBinding.listEvent.setVisibility(View.VISIBLE);
            mBinding.txtMsg.setVisibility(View.GONE);
        }else
        {

            mBinding.listEvent.setVisibility(View.GONE);
            mBinding.txtMsg.setVisibility(View.VISIBLE);
            mBinding.txtMsg.setText("No more past event!");
        }
        ListEventAdapter mAdapter = new ListEventAdapter(context, eventArrayList_past,"past");
        mBinding.listEvent.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        return rootView;
    }

    public void geteventsfromDb(){
        final RealmResults<Event> event = realm.where(Event.class).findAll().sort("date", Sort.DESCENDING);
        events1=new Event[event.size()];

        eventArrayList_past.clear();
        if(event.size()>0){

            Date date = null;
            Date date_cur = null;

            for(int cat=0;cat<event.size();cat++)
            {

                events1[cat]=new Event();

                events1[cat].set_token(event.get(cat).get_token());
                events1[cat].setEvent_uuid(event.get(cat).getEvent_uuid());
                events1[cat].setEvent_name(event.get(cat).getEvent_name());
                events1[cat].setEvent_description(event.get(cat).getEvent_description());
                events1[cat].setEvent_image(event.get(cat).getEvent_image());
                events1[cat].setIs_cover(event.get(cat).getIs_cover());
                events1[cat].setDate(event.get(cat).getDate());
                events1[cat].setIsComplete(event.get(cat).getIsComplete());
                events1[cat].setCreated_date(event.get(cat).getCreated_date());
                events1[cat].setModified_date(event.get(cat).getModified_date());
                events1[cat].setOperation(event.get(cat).getOperation());
                events1[cat].setCategory(event.get(cat).getCategory());
                events1[cat].setCategoryColor(event.get(cat).getCategoryColor());
                String str_date = event.get(cat).getDate();
                DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

                try {
                    date = (Date) formatter.parse(str_date);
                    Date date1 = new Date();
                    String str_cur = formatter.format(date1.getTime());
                    date_cur = formatter.parse(str_cur);
                    Long diff = (date_cur.getTime()) - (date.getTime());
                    numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
                    if (numOfDays > 0) {

                        eventArrayList_past.add(events1[cat]);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
