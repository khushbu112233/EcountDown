package com.aipxperts.ecountdown.Adapter;

/**
 * Created by aipxperts on 3/2/16.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.Widget.TextView_Regular;

import java.util.ArrayList;

public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> navDrawerItems;
    private ArrayList<Integer> navDrawerItems_icons;

    public NavDrawerListAdapter(Context context, ArrayList<String> navDrawerItems , ArrayList<Integer> navDrawerItems_icons){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        this.navDrawerItems_icons=navDrawerItems_icons;

    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list, null);

        }

        TextView_Regular txtTitle = (TextView_Regular) convertView.findViewById(R.id.title);
        ImageView img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
        txtTitle.setText(navDrawerItems.get(position));
        img_icon.setImageResource(navDrawerItems_icons.get(position));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((DashBoardActivity) context).slidingMenuToggle();
                ((DashBoardActivity) context).slidingMenuClicked(position);
            }
        });
        return convertView;
    }

}