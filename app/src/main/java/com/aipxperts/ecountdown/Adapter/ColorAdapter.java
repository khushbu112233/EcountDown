package com.aipxperts.ecountdown.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.databinding.RowColorListBinding;

import java.util.ArrayList;

/**
 * Created by aipxperts on 9/12/16.
 */

//brach testing
public class ColorAdapter extends BaseAdapter {
    Context context;
    private ArrayList<String> datalist = new ArrayList<>();

    public ColorAdapter(Context context, ArrayList<String> datalist) {
        this.context = context;
        this.datalist = datalist;

    }

    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        RowColorListBinding binding;

        if (convertView == null) {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.row_color_list, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (RowColorListBinding) convertView.getTag();
        }
        if(position==0)
        {
            binding.txtcolor.setText(datalist.get(position));
            binding.txtcolor.setBackgroundColor(Color.parseColor("#ffffff"));
        }else {
            binding.txtcolor.setBackgroundColor(Color.parseColor(datalist.get(position)));
            binding.txtcolor.setText("");
        }
        return convertView;

    }


}
