package com.aipxperts.ecountdown.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aipxperts.ecountdown.Fragment.EventDeatilViewpagerFragment;
import com.aipxperts.ecountdown.Fragment.EventDeatilViewpagerFutureFragment;
import com.aipxperts.ecountdown.Fragment.EventDeatilViewpagerpastFragment;
import com.aipxperts.ecountdown.Fragment.ListEventFragment;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.Widget.TextView_Bold;
import com.aipxperts.ecountdown.Widget.TextView_Regular;
import com.aipxperts.ecountdown.databinding.ListEventNewLayoutBinding;
import com.aipxperts.ecountdown.utils.Constants;
import com.aipxperts.ecountdown.utils.Pref;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;


/**
 * Created by aipxperts-ubuntu-01 on 8/6/17.
 */

public class ListEventAdapter extends BaseAdapter {

    Context context;
    ArrayList<Event> eventArrayList;
    String encodedString = "";
    String str_share = "";
    String tab;

    public ListEventAdapter(Context context, ArrayList<Event> eventArrayList,String tab) {
        this.context = context;
        this.eventArrayList = eventArrayList;
        this.tab=tab;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    private static LayoutInflater inflater = null;

    @Override
    public int getCount() {
        return eventArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final View rowView;

        final Holder holder=new Holder();
        final ListEventNewLayoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_event_new_layout, viewGroup, false);
        rowView = binding.getRoot();

        holder.fmEvent = (FrameLayout)rowView.findViewById(R.id.fm_event);
        holder.imgEventSet = (ImageView) rowView.findViewById(R.id.img_event_set);
        holder.llEvent = (LinearLayout) rowView.findViewById(R.id.ll_event);
        holder.ll1 = (LinearLayout)rowView.findViewById(R.id.ll1);
        holder.llPastFuture = (LinearLayout)rowView.findViewById(R.id.ll_past_future);
        holder.llCurrent = (LinearLayout)rowView.findViewById(R.id.ll_current);
        holder.txtEventName = (TextView_Bold) rowView.findViewById(R.id.txt_event_name);
        holder.txtEventDate = (TextView_Regular)rowView.findViewById(R.id.txt_event_date);
        holder.txtCount = (TextView_Regular)rowView.findViewById(R.id.txt_count);
        holder.txtEventCountDown = (TextView_Regular)rowView.findViewById(R.id.txt_event_count_down);
        holder.txtEventCountDown1 = (TextView_Regular)rowView.findViewById(R.id.txt_event_count_down1);

        holder.txtEventName.setText(Constants.capitalize(eventArrayList.get(i).getEvent_name()));
        /**
         * get day from date
         */
        String day = Constants.parseDateDatabaseToDisplay_day(eventArrayList.get(i).getDate());
        /**
         * get month and year from date
         */
        String month_year = Constants.parseDateDatabaseToDisplay_month(eventArrayList.get(i).getDate());
        /**
         * get suffix of day
         */
        String suffix=getDayNumberSuffix(Integer.parseInt(Constants.parseDateDatabaseToDisplay_day(eventArrayList.get(i).getDate())));
        /**
         * merge all value and display wantable string
         */
        holder.txtEventDate.setText(day+suffix+" "+month_year);

        encodedString = eventArrayList.get(i).getEvent_image();

        if(encodedString!=null)
        {
            if(!encodedString.equalsIgnoreCase("")) {

            /*if(encodedString.contains("sdcard"))
            {*/
                Glide.with(context)
                        .load(eventArrayList.get(i).getEvent_image())
                        .centerCrop()
                        .into(holder.imgEventSet);

           /* }else {
                byte[] imageByteArray = Base64.decode(encodedString, Base64.DEFAULT);

                Glide.with(context)
                        .load(imageByteArray)
                        .centerCrop()
                        .into(holder.imgEventSet);
            }*/
            }else
            {
                holder.llEvent.setBackgroundColor(Color.parseColor(eventArrayList.get(i).getCategoryColor()));
            }
        }else
        {
            holder.llEvent.setBackgroundColor(Color.parseColor(eventArrayList.get(i).getCategoryColor()));
        }
        String str_date = eventArrayList.get(i).getDate();
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;
        Date date_cur = null;
        try {
            date = (Date) formatter.parse(str_date);
            Date date1 = new Date();
            String str_cur = formatter.format(date1.getTime());
            date_cur = formatter.parse(str_cur);
            Long diff = (date_cur.getTime()) - (date.getTime());
            int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
            /**
             * here both difference less mean event is after today date
             * when both difference greater mean event is past event
             * when start differnce is greater and past differnce is less then event is currenly mean recent
             */
            if (numOfDays<0) {

                holder.llPastFuture.setVisibility(View.VISIBLE);
                holder.llCurrent.setVisibility(View.GONE);
                if(Math.abs(numOfDays)==1)
                {
                    holder.txtEventCountDown.setText(" Day After");
                }else {
                    holder.txtEventCountDown.setText(" Days After");
                }
                holder.txtCount.setText(Math.abs(numOfDays)+"");

            }else if (numOfDays > 0) {

                holder.llPastFuture.setVisibility(View.VISIBLE);
                holder.llCurrent.setVisibility(View.GONE);
                if(numOfDays==1)
                {
                    holder.txtEventCountDown.setText(" Day Before");
                }else {
                    holder.txtEventCountDown.setText(" Days Before");
                }
                holder.txtCount.setText(numOfDays+"");
            } else if (numOfDays==0) {
                holder.llPastFuture.setVisibility(View.GONE);
                holder.llCurrent.setVisibility(View.VISIBLE);
                holder.txtEventCountDown1.setText("Today");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.fmEvent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                dialog(i);

                return false;
            }
        });
        holder.fmEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tab.equalsIgnoreCase("future")) {
                    Pref.setValue(context, "detail_position", i);
                    EventDeatilViewpagerFutureFragment fragment = new EventDeatilViewpagerFutureFragment();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();
                }else if(tab.equalsIgnoreCase("past"))
                {
                    Pref.setValue(context, "detail_position", i);

                    EventDeatilViewpagerpastFragment fragment = new EventDeatilViewpagerpastFragment();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

                }else if(tab.equalsIgnoreCase("recent"))
                {
                    Pref.setValue(context, "detail_position", i);

                    EventDeatilViewpagerFragment fragment = new EventDeatilViewpagerFragment();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

                }
            }
        });
        return rowView;
    }

    /**
     * for delete event
     */
    private void dialog(final int i) {
        // custom dialog

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_dialog);
        TextView_Regular ok = (TextView_Regular) dialog.findViewById(R.id.ok);
        TextView_Regular cancel = (TextView_Regular) dialog.findViewById(R.id.cancel);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog.dismiss();
            }
        });
        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Event event = realm.where(Event.class).equalTo("event_uuid", eventArrayList.get(i).getEvent_uuid()).findFirst();
                        event.deleteFromRealm();
                        eventArrayList.remove(i);
                        ListEventFragment fragment = new ListEventFragment();
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();

                    }
                });

                dialog.dismiss();
                Toast.makeText(context, "Event deleted successfully.", Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();

    }

    private String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
    public class Holder
    {
        FrameLayout fmEvent;
        ImageView imgEventSet;
        LinearLayout llEvent,ll1,llPastFuture,llCurrent;
        TextView_Bold txtEventName;
        TextView_Regular txtEventDate,txtCount,txtEventCountDown,txtEventCountDown1;



    }
}
