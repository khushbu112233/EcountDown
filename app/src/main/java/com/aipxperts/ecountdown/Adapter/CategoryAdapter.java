package com.aipxperts.ecountdown.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aipxperts.ecountdown.Fragment.ListEventFragment;
import com.aipxperts.ecountdown.Interface.OnClickTick;
import com.aipxperts.ecountdown.Model.Category;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.Widget.Edittext_Regular;
import com.aipxperts.ecountdown.databinding.ListCategoryLayoutBinding;
import com.aipxperts.ecountdown.databinding.ListEventNewLayoutBinding;
import com.aipxperts.ecountdown.utils.MyListView;

import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 1/9/17.
 */

public class CategoryAdapter extends BaseAdapter{
    Context context;
    ArrayList<Category> categories;
    LayoutInflater inflater = null;
    OnClickTick onClickTick;
    public void onClickTick(OnClickTick onClickTick) {
        this.onClickTick = onClickTick;
    }
    public CategoryAdapter(Context context, ArrayList<Category> categories){
        this.context=context;
        this.categories = categories;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return categories.size();
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
        final View rowView;


        final ListCategoryLayoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_category_layout, viewGroup, false);
        rowView = binding.getRoot();
        binding.txtCategoryName.setText(categories.get(i).getCategoryName());

        if(i==0){
            binding.ivCategory.setImageResource(R.drawable.birthday);
        } else if(i==1) {
            binding.ivCategory.setImageResource(R.drawable.birthday);
        }else if(i==2) {
            binding.ivCategory.setImageResource(R.drawable.holiday);
        }else if(i==3) {
            binding.ivCategory.setImageResource(R.drawable.birthday);
        }else if(i==4) {
            binding.ivCategory.setImageResource(R.drawable.birthday);
        }else if(i==5) {
            binding.ivCategory.setImageResource(R.drawable.holiday);
        }

        if (categories.get(i).getSelected().equalsIgnoreCase("0")) {
            binding.ivTick.setVisibility(View.INVISIBLE);
        } else {
            binding.ivTick.setVisibility(View.VISIBLE);

        }


        binding.llCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int j=0;j<categories.size();j++)
                {
                    if(categories.get(j).getCategoryId().equalsIgnoreCase(categories.get(i).getCategoryId()))
                    {
                        categories.get(j).setSelected("1");
                    }else
                    {
                        categories.get(j).setSelected("0");
                    }
                }// or realm.createObject(Person.class, id);


                notifyDataSetChanged();


                onClickTick.OnClickTick(i,categories.get(i).getCategoryId(),categories.get(i).getCategoryName(),categories.get(i).getCategoryColor());

            }
        });
        binding.llCategory.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(i>5)
                {
                    final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);

                    alertDialogBuilder.setMessage("Are you sure you want to delete this category?");
                    // set positive button: Yes message
                    alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Category category = realm.where(Category.class).equalTo("CategoryId", categories.get(i).getCategoryId()).findFirst();
                                    category.deleteFromRealm();
                                    categories.remove(i);
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
                }else
                {
                    Log.e("delete","delete");
                }

                return false;
            }
        });

        return rowView;
    }

}
