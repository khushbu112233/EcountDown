package com.aipxperts.ecountdown.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.aipxperts.ecountdown.Adapter.ListEventAdapter;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.databinding.ListFragmentTabLayoutBinding;
import com.aipxperts.ecountdown.utils.Pref;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 25/7/17.
 */

public class ListEventFutureFragment extends Fragment {

    ListFragmentTabLayoutBinding mBinding;
    View rootView;
    Context context;
    Event[] events1;
    ArrayList<Event> eventArrayList=new ArrayList<>();
    ArrayList<Event> eventArrayList_recent=new ArrayList<>();
    ArrayList<Event> eventArrayList_future=new ArrayList<>();
    ArrayList<Event> eventArrayList_past=new ArrayList<>();
    int numOfDays,numOfDays1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.list_fragment_tab_layout, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        geteventsfromDb();
        eventArrayList_future.clear();
        eventArrayList_past.clear();
        eventArrayList_recent.clear();
       /* // Layout Managers:
        mBinding.listEvent.setLayoutManager(new LinearLayoutManager(context));
*/
        // Creating Adapter object
        for (int i=0;i<eventArrayList.size();i++)
        {
           // String str_end_date = eventArrayList.get(i).getEnd_date();
            String str_date = eventArrayList.get(i).getDate();
            Log.e("str_date", "" + str_date);
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");


            Date date = null,date_end=null;

            Date date_cur = null;
            try {
                date = (Date) formatter.parse(str_date);
              //  date_end = (Date) formatter.parse(str_end_date);
                //    Log.e("str_date",""+timeAgo.timeAgo(date));


                Date date1 = new Date();
                String str_cur = formatter.format(date1.getTime());

                date_cur = formatter.parse(str_cur);
                Long diff = (date_cur.getTime()) - (date.getTime());

            //    Long diff1 = (date_cur.getTime()) - (date_end.getTime());

                numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
              //  numOfDays1 = (int) (diff1 / (1000 * 60 * 60 * 24));
                           Log.e("numOfDaysa", "" + numOfDays);

                if (numOfDays<0) {
                    eventArrayList_future.add(eventArrayList.get(i));


                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        if(eventArrayList_future.size()>0)
        {
            mBinding.listEvent.setVisibility(View.VISIBLE);
            mBinding.txtMsg.setVisibility(View.GONE);
        }else
        {

            mBinding.listEvent.setVisibility(View.GONE);
            mBinding.txtMsg.setVisibility(View.VISIBLE);
            mBinding.txtMsg.setText("No more future event!");
        }

        ListEventAdapter mAdapter = new ListEventAdapter(context, eventArrayList_future,"future");
        mBinding.listEvent.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        Log.e("eventArrayList_current","size"+eventArrayList_future.size());
       /* mBinding.listEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    for (int j = 0; j < eventArrayList_future.size(); j++) {
                        if (i == j) {
                        *//*
                        Pref.setValue(context,"detail_even_name",eventArrayList.get(j).getEvent_name());
                        Pref.setValue(context,"detail_even_date",eventArrayList.get(j).getDate());
                        Pref.setValue(context,"detail_even_image",eventArrayList.get(j).getEvent_image());
                        Pref.setValue(context,"detail_even_des",eventArrayList.get(j).getEvent_description());
                        Pref.setValue(context,"detail_even_end_date",eventArrayList.get(j).getEnd_date());
                        Pref.setValue(context,"detail_even_uuid",eventArrayList.get(j).getEvent_uuid());*//*

                            Pref.setValue(context, "detail_position", j);
                            EventDeatilViewpagerFutureFragment fragment = new EventDeatilViewpagerFutureFragment();
                            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();
                        }
                    }

            }
        });*/
       /* mBinding.listEvent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

                alertDialogBuilder.setMessage("Are you sure you want to delete this event?");
                // set positive button: Yes message
                alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int item) {


                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Event event = realm.where(Event.class).equalTo("event_uuid", eventArrayList_future.get(item).getEvent_uuid()).findFirst();
                                event.deleteFromRealm();
                                eventArrayList_future.remove(item);

                                ListEventFragment fragment = new ListEventFragment();
                                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();

                            }
                        });
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<Event> event = realm.where(Event.class).findAll();
                                if (event.size() > 0) {
                                    for (int i = 0; i < event.size(); i++) {
                                        if (event.get(i).getIs_cover().equalsIgnoreCase("1")) {
                                            Pref.setValue(context, "set_as_cover_click", event.get(i).getEvent_image());
                                            Pref.setValue(context, "set_as_cover_des", event.get(i).getEvent_description());
                                            Pref.setValue(context, "set_as_cover_title", event.get(i).getEvent_name());
                                            Pref.setValue(context, "set_as_cover_date", event.get(i).getDate());
                                        } else {
                                            Pref.setValue(context, "set_as_cover_click", event.get(0).getEvent_image());
                                            Pref.setValue(context, "set_as_cover_des", event.get(0).getEvent_description());
                                            Pref.setValue(context, "set_as_cover_title", event.get(0).getEvent_name());
                                            Pref.setValue(context, "set_as_cover_date", event.get(0).getDate());

                                        }
                                    }
                                }*//*else
                                    {
                                        Pref.setValue(context,"set_as_cover_click",event.get(0).getEvent_image());
                                        Pref.setValue(context,"set_as_cover_des",event.get(0).getEvent_description());
                                        Pref.setValue(context,"set_as_cover_title",event.get(0).getEvent_name());
                                        Pref.setValue(context,"set_as_cover_date",event.get(0).getDate());

                                    }*//*
                            }
                        });
                        Toast.makeText(context, "Event deleted successfully.", Toast.LENGTH_LONG).show();

                        ListEventFragment fragment = new ListEventFragment();
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();
                    }
                });
                // set negative button: No message
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();

                    }
                });
                // set neutral button: Exit the app message

                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                // show alert
                alertDialog.show();
                return false;
            }
        });*/
        return rootView;
    }

    public void geteventsfromDb(){
        final RealmResults<Event> event = realm.where(Event.class).findAll().sort("date", Sort.ASCENDING);
        events1=new Event[event.size()];
        eventArrayList.clear();
        if(event.size()>0){

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
                eventArrayList.add(events1[cat]);
            }
        }

    }
   public class StringDateComparator implements Comparator<String>
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        public int compare(String lhs, String rhs)
        {
            try {
                return dateFormat.parse(lhs).compareTo(dateFormat.parse(rhs));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        return 1;
        }
    }

}
