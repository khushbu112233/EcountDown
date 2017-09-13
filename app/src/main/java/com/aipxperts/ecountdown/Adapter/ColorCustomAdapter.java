package com.aipxperts.ecountdown.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aipxperts.ecountdown.Interface.OnClickTick;
import com.aipxperts.ecountdown.Interface.OnColorClickTick;
import com.aipxperts.ecountdown.Model.Category;
import com.aipxperts.ecountdown.Model.ColorModel;
import com.aipxperts.ecountdown.R;

import java.util.ArrayList;

import io.realm.Realm;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;


/**
 * Created by aipxperts-ubuntu-01 on 1/6/17.
 */

public class ColorCustomAdapter extends BaseAdapter {


    ArrayList<ColorModel> datalist;
    Context context;
    private static LayoutInflater inflater=null;
    OnColorClickTick onColorClickTick;
    boolean isClick=false;
    public void onColorClickTick(OnColorClickTick onColorClickTick) {
        this.onColorClickTick = onColorClickTick;
    }
    public ColorCustomAdapter(Context context, ArrayList<ColorModel> datalist) {
        // TODO Auto-generated constructor stub
        this.datalist = datalist;
        this.context=context;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        ImageView img_color,img_tick;
        FrameLayout fm_color;
        LinearLayout ll_color;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.color_list_layout, null);
        holder.img_color=(ImageView) rowView.findViewById(R.id.img_color);
        holder.img_tick = (ImageView)rowView.findViewById(R.id.img_tick);
        holder.fm_color = (FrameLayout) rowView.findViewById(R.id.fm_color);
        holder.ll_color= (LinearLayout) rowView.findViewById(R.id.ll_color);

        holder.img_color.setBackgroundResource(R.drawable.circle_group_shape);
        GradientDrawable drawable = (GradientDrawable) holder.img_color.getBackground();

        drawable.setColor(Color.parseColor(datalist.get(position).getColor()));

        if(datalist.get(position).isClick())
        {
            holder.img_tick.setVisibility(View.VISIBLE);
        }else
        {
            holder.img_tick.setVisibility(View.GONE);
        }
        holder.ll_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < datalist.size() ; i++ )
                {
                    if(datalist.get(i).getColor().equalsIgnoreCase(datalist.get(position).getColor()))
                    {
                        datalist.get(i).setClick(true);
                    }else
                    {
                        datalist.get(i).setClick(false);

                    }
                }

                onColorClickTick.OnColorClickTick(position,datalist.get(position).getColor());
                notifyDataSetChanged();
            }
        });

        return rowView;
    }
}

