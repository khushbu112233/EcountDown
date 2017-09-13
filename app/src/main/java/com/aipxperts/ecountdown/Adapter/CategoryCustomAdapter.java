package com.aipxperts.ecountdown.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.aipxperts.ecountdown.Interface.OnClickTick;
import com.aipxperts.ecountdown.Model.Category;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.utils.Pref;

import java.util.ArrayList;

import io.realm.Realm;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;


/**
 * Created by aipxperts-ubuntu-01 on 1/6/17.
 */

public class CategoryCustomAdapter extends BaseAdapter {


    ArrayList<Category> prgmNameList;
    Context context;
    ArrayList<String> imageId;
    private static LayoutInflater inflater = null;
    boolean isclick = false;
    OnClickTick onClickTick;
    String cat_name;

    public void onClickTick(OnClickTick onClickTick) {
        this.onClickTick = onClickTick;
    }

    int selected_position = 0;

    public CategoryCustomAdapter(Context context, ArrayList<Category> prgmNameList, String cat_name) {
        // TODO Auto-generated constructor stub
        this.prgmNameList = prgmNameList;
        this.context = context;
        this.cat_name = cat_name;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return prgmNameList.size();
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

    public class Holder {
        TextView tv;
        ImageView img;
        LinearLayout ll_category;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.category_list, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textView1);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
        holder.ll_category = (LinearLayout) rowView.findViewById(R.id.ll_category);
        holder.tv.setText(prgmNameList.get(position).getCategoryName());
        if (position == 0) {
            holder.img.setImageResource(R.mipmap.anniversary);
        } else if (position == 1) {
            holder.img.setImageResource(R.mipmap.birthday);
        } else if (position == 2) {
            holder.img.setImageResource(R.mipmap.holiday);
        } else if (position == 3) {
            holder.img.setImageResource(R.mipmap.school);
        } else if (position == 4) {
            holder.img.setImageResource(R.mipmap.life);
        } else if (position == 5) {
            holder.img.setImageResource(R.mipmap.trip);
        }else
        {
            holder.img.setImageResource(R.mipmap.event);
        }
        if (!cat_name.equalsIgnoreCase("")) {
            if (cat_name.equalsIgnoreCase(prgmNameList.get(position).getCategoryName())) {
                holder.tv.setTextColor(Color.parseColor("#ffffff"));
                // holder.img.setImageResource(R.mipmap.category_selected);
                holder.ll_category.setBackgroundColor(Color.parseColor("#07bebd"));

            } else {
                holder.tv.setTextColor(Color.parseColor("#07bebd"));
                //  holder.img.setImageResource(R.mipmap.category_unselected);
                holder.ll_category.setBackgroundColor(Color.parseColor("#ffffff"));

            }
        }
        holder.ll_category.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (position > 5) {
                    final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

                    alertDialogBuilder.setMessage("Are you sure you want to delete this category?");
                    // set positive button: Yes message
                    alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Category category = realm.where(Category.class).equalTo("CategoryId", prgmNameList.get(position).getCategoryId()).findFirst();
                                    category.deleteFromRealm();
                                    prgmNameList.remove(position);
                                    notifyDataSetChanged();

                                }
                            });
                        }
                    });
                    // set negative button: No message
                    alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // cancel the alert box and put a Toast to the user
                            dialog.cancel();

                        }
                    });
                    // set neutral button: Exit the app message

                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    // show alert
                    alertDialog.show();
                } else {
                    Log.e("delete", "delete");
                }

                return false;
            }
        });

        holder.ll_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int j = 0; j < prgmNameList.size(); j++) {
                    if (prgmNameList.get(j).getCategoryId().equalsIgnoreCase(prgmNameList.get(position).getCategoryId())) {
                        prgmNameList.get(j).setSelected("1");
                        cat_name=prgmNameList.get(position).getCategoryName();

                    } else {
                        prgmNameList.get(j).setSelected("0");
                    }
                }// or realm.createObject(Person.class, id);

                onClickTick.OnClickTick(position, prgmNameList.get(position).getCategoryId(), prgmNameList.get(position).getCategoryName(), prgmNameList.get(position).getCategoryColor());
                notifyDataSetChanged();


            }
        });


        //  if(Pref.getValue(context,"updated","").equalsIgnoreCase("1")) {

        //  }
        // }
        // }


/*
        holder.ll_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i =0;i<prgmNameList.size();i++)
                {
                    Log.e("Adapter","000 "+position );

                    if(position==i)
                    {
                        Log.e("Adapter","111 "+position  + "   "+i);
                        prgmNameList.get(i).setIsclick(true);
                        selected_position=i;
                        Pref.setValue(context,"selected_position",i);
                        Pref.setValue(context,"selected_category_name",prgmNameList.get(i).getCategoryName());
                        Pref.setValue(context,"selected_category_uuid",prgmNameList.get(i).getCategoryId());
                        final int finalI = i;

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Category category = realm.where(Category.class).equalTo("CategoryId", prgmNameList.get(finalI).getCategoryId()).findFirst();
                                category.setIsclick(true);
                            }
                        });

                        notifyDataSetChanged();
                    }else
                    {
                        final int finalI = i;
                        Log.e("Adapter","222 "+position  + "   "+i);
                       *//* realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Long tsLong = System.currentTimeMillis()/1000;
                                String ts = tsLong.toString();
                                // Add a Category
                                Category category = realm.createObject(Category.class);
                                category.setCategory_name(prgmNameList.get(finalI).getCategory_name());
                                category.set_token(prgmNameList.get(finalI).get_token());
                                category.setCategory_uuid(prgmNameList.get(finalI).getCategory_uuid());
                                category.setOperation(prgmNameList.get(finalI).getOperation());
                                category.setIsComplete(prgmNameList.get(finalI).getIsComplete());
                                category.setCreated_date(prgmNameList.get(finalI).getCreated_date());
                                category.setModified_date(prgmNameList.get(finalI).getModified_date());
                                category.setIsclick(prgmNameList.get(finalI).isclick());
                            }
                        });*//*
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Category category = realm.where(Category.class).equalTo("CategoryId", prgmNameList.get(finalI).getCategoryId()).findFirst();
                                category.setIsclick(false);
                            }
                        });
                        prgmNameList.get(i).setIsclick(false);
                        notifyDataSetChanged();
                    }


                }

            }
        });*/
        return rowView;
    }

}

