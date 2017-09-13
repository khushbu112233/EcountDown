package com.aipxperts.ecountdown.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aipxperts.ecountdown.Fragment.EventDeatilViewpagerFragment;
import com.aipxperts.ecountdown.Fragment.EventDeatilViewpagerFutureFragment;
import com.aipxperts.ecountdown.Fragment.EventDeatilViewpagerpastFragment;
import com.aipxperts.ecountdown.Fragment.ListEventFragment;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.Widget.TextView_Regular;
import com.aipxperts.ecountdown.databinding.ListEventLayoutBinding;
import com.aipxperts.ecountdown.databinding.ListEventNewLayoutBinding;
import com.aipxperts.ecountdown.utils.Constants;
import com.aipxperts.ecountdown.utils.Pref;
import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.implments.SwipeItemAdapterMangerImpl;
import com.daimajia.swipe.implments.SwipeItemMangerImpl;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;

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
    protected Set<SwipeLayout> mShownLayouts = new HashSet<SwipeLayout>();

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


        final ListEventNewLayoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_event_new_layout, viewGroup, false);
        rowView = binding.getRoot();

        //mShownLayouts.add(binding.swipe);

        binding.txtEventName.setText(Constants.capitalize(eventArrayList.get(i).getEvent_name()));
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
        binding.txtEventDate.setText(day+suffix+" "+month_year);

        encodedString = eventArrayList.get(i).getEvent_image();


        if(!encodedString.equalsIgnoreCase("")) {
            byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

          /*  Log.e("width_height", "123" + "   " + decodedByte.getWidth() + "  " + decodedByte.getHeight());

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(decodedByte, decodedByte.getWidth(), 60, true);

            BitmapDrawable bd = new BitmapDrawable(context.getResources(), resizedBitmap);
            bd.setLevel(100);
            binding.llEvent.setBackgroundDrawable(bd);*/
            Glide.with(context)
                    .load(decodedString)
                    .asBitmap()
                    .centerCrop()
                    .into(binding.imgEventSet);
            // binding.imgEventSet.setImageBitmap(decodedByte);

        }else
        {
            binding.llEvent.setBackgroundColor(Color.parseColor(eventArrayList.get(i).getCategoryColor()));
        }//  binding.imgEventPhoto.setImageBitmap(decodedByte);
        // binding.imgEventPhoto1.setImageBitmap(decodedByte);

        String str_date = eventArrayList.get(i).getDate();
        // String str_end_date = eventArrayList.get(i).getEnd_date();
        Log.e("str_date", "" + str_date);
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");


        Date date = null,date_end=null;

        Date date_cur = null;
        try {
            date = (Date) formatter.parse(str_date);
            //  date_end = (Date) formatter.parse(str_end_date);

            Date date1 = new Date();
            String str_cur = formatter.format(date1.getTime());

            date_cur = formatter.parse(str_cur);
            Long diff = (date_cur.getTime()) - (date.getTime());
            //    Long diff1 = (date_cur.getTime()) - (date_end.getTime());

            int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
            //  int numOfDays1 = (int) (diff1 / (1000 * 60 * 60 * 24));
            Log.e("numOfDaysa", "" + numOfDays);
            /**
             * here both difference less mean event is after today date
             * when both difference greater mean event is past event
             * when start differnce is greater and past differnce is less then event is currenly mean recent
             */
            if (numOfDays<0) {

                binding.llPastFuture.setVisibility(View.VISIBLE);
                binding.llCurrent.setVisibility(View.GONE);
                if(Math.abs(numOfDays)==1)
                {
                    binding.txtEventCountDown.setText(" Day After");
                }else {
                    binding.txtEventCountDown.setText(" Days After");
                }
                binding.txtCount.setText(Math.abs(numOfDays)+"");

            }else if (numOfDays > 0) {

                binding.llPastFuture.setVisibility(View.VISIBLE);
                binding.llCurrent.setVisibility(View.GONE);
                if(numOfDays==1)
                {
                    binding.txtEventCountDown.setText(" Day Before");
                }else {
                    binding.txtEventCountDown.setText(" Days Before");
                }
                binding.txtCount.setText(numOfDays+"");
            } else if (numOfDays==0) {
                binding.llPastFuture.setVisibility(View.GONE);
                binding.llCurrent.setVisibility(View.VISIBLE);
                binding.txtEventCountDown1.setText("Today");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

     /*   if (eventArrayList.get(i).getOperation().equalsIgnoreCase("1")) { // clsoe

            binding.swipe.close();

        } else {
            //open
            binding.swipe.open();
        }*/
        binding.fmEvent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                dialog(i);

                return false;
            }
        });
        binding.fmEvent.setOnClickListener(new View.OnClickListener() {
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


        // rg.check(id);
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
                        }
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
    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView mTxtEventName;
        TextView mTxtEventDate;
        TextView mTxtEventEcountDown;
        ImageView mImageEdit;
        ImageView mImageShare;
        ImageView mImageDelete;
        ImageView mImageEventPhoto;
        ImageView mImageEventPhoto1;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            mTxtEventName = (TextView) itemView.findViewById(R.id.txt_event_name);
            mTxtEventDate = (TextView) itemView.findViewById(R.id.txt_event_date);
            mTxtEventEcountDown = (TextView) itemView.findViewById(R.id.txt_event_count_down);
            mImageEventPhoto = (ImageView) itemView.findViewById(R.id.img_event_photo);
            mImageEventPhoto1 = (ImageView) itemView.findViewById(R.id.img_event_photo1);
            mImageShare = (ImageView) itemView.findViewById(R.id.img_share);
            mImageEdit = (ImageView) itemView.findViewById(R.id.img_edit);
            mImageDelete = (ImageView) itemView.findViewById(R.id.img_delete);

        }
    }
}
