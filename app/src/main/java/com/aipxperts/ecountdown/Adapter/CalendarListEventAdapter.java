package com.aipxperts.ecountdown.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.databinding.CalListEventLayoutBinding;
import com.aipxperts.ecountdown.utils.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by aipxperts-ubuntu-01 on 8/6/17.
 */

public class CalendarListEventAdapter extends BaseAdapter {

    Context context;
    LinearLayout ll1;
    TextView txt_date,txt_business_trip,txt_days_until;
    ArrayList<Event> eventArrayList;
    ImageView img_edit,img_delete;

    String encodedString="";
    public CalendarListEventAdapter(Context context,ArrayList<Event> eventArrayList)
    {
        this.context=context;
        this.eventArrayList=eventArrayList;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    private static LayoutInflater inflater=null;
    @Override
    public int getCount() {
        return eventArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View rowView;


        CalListEventLayoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.cal_list_event_layout, viewGroup, false);
        rowView = binding.getRoot();

        binding.txtEventName.setText(Constants.capitalize(eventArrayList.get(i).getEvent_name()));
        binding.txtEventDate.setText(Constants.parseDateDatabaseToDisplay1(eventArrayList.get(i).getDate()));
        encodedString = eventArrayList.get(i).getEvent_image();

        byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
       // binding.imgEventPhoto.setImageBitmap(decodedByte);

        Log.e("width_height","123"+"   "+decodedByte.getWidth()+"  "+decodedByte.getHeight());

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(decodedByte,decodedByte.getWidth()/2,60,false);

        BitmapDrawable bd = new BitmapDrawable(context.getResources(), resizedBitmap);
        binding.llEvent.setBackgroundDrawable(bd);



        String str_date=eventArrayList.get(i).getDate();
        Log.e("str_date",""+str_date);
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;
        Date date_cur = null;
        try {
            date = (Date)formatter.parse(str_date);
            Long tsLong = System.currentTimeMillis();
            String ts = tsLong.toString();

            Date date1 = new Date();
            String str_cur = formatter.format(date1.getTime());

            date_cur = formatter.parse(str_cur);
            Long diff =(date_cur.getTime())-(date.getTime());
          //  Long diff = Long.parseLong(ts)-(date.getTime());
            int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
            Log.e("numOfDays",""+numOfDays);
            if (numOfDays < 0) {

                binding.llPastFuture.setVisibility(View.VISIBLE);
                binding.llCurrent.setVisibility(View.GONE);
                binding.txtEventCountDown.setText(" Days After");
                binding.txtCount.setText(Math.abs(numOfDays));

            } else if (numOfDays > 0) {

                binding.llPastFuture.setVisibility(View.VISIBLE);
                binding.llCurrent.setVisibility(View.GONE);
                binding.txtEventCountDown.setText(" Days Before");
                binding.txtCount.setText(numOfDays+"");
            } else if (numOfDays == 0) {
                binding.llPastFuture.setVisibility(View.GONE);
                binding.llCurrent.setVisibility(View.VISIBLE);
                binding.txtEventCountDown1.setText("Today");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rowView;
    }
}
