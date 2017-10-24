package com.aipxperts.ecountdown.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.Toast;

import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.Adapter.CategoryCustomAdapter;
import com.aipxperts.ecountdown.Adapter.ColorCustomAdapter;
import com.aipxperts.ecountdown.Interface.OnClickTick;
import com.aipxperts.ecountdown.Interface.OnColorClickTick;
import com.aipxperts.ecountdown.Model.Category;
import com.aipxperts.ecountdown.Model.ColorModel;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.Widget.Edittext_Regular;
import com.aipxperts.ecountdown.Widget.TextView_Bold;
import com.aipxperts.ecountdown.databinding.OptionSelectNewBinding;
import com.aipxperts.ecountdown.utils.Pref;
import com.aipxperts.ecountdown.utils.Utils;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 5/9/17.
 */

public class CategoryFragment extends Fragment {
    OptionSelectNewBinding mBinding;
    View rootView;
    Context context;
    ArrayList<Event> eventArrayList = new ArrayList<>();
    ArrayList<Category> categoryArrayList = new ArrayList<>();
    String uuid;
    Event[] events1;
    Category[] category1;
    CategoryCustomAdapter categoryAdapter;
    OnClickTick onClickTick;
    OnColorClickTick onColorClickTick;
    ArrayList<String> categories=new ArrayList<>();
    ArrayList<String> categories_color=new ArrayList<>();
    Dialog dialog1=null;
    String selected_color="",selected_color1="";
    boolean isclick=false;
    String id1 = "",color1="";
    String cat_name;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.option_select_new, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();

        preview();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cat_name = bundle.getString("cat_name");
        }
    /*    if(Pref.getValue(context,"add_category","").equalsIgnoreCase(""))
        {
            AddDefaultCategory();
        }*/


        //categoryArrayList.clear();
        GetDefaultCategory();


        onClickTick = new OnClickTick() {
            @Override
            public void OnClickTick(int position, final String id, final String name, String color) {

                if(!id.equalsIgnoreCase("")) {
                    id1=id;
                    color1=color;
                    selected_color= color1;
                    UpdateCategory(id1);
                    Pref.setValue(context,"updated","1");
                    ((FragmentActivity)context).getSupportFragmentManager().popBackStack();

                }


            }
        };
        onColorClickTick = new OnColorClickTick() {
            @Override
            public void OnColorClickTick(int position, String color) {

                if(!color.equalsIgnoreCase("")) {
                    selected_color="";
                    selected_color= color;

                }
            }
        };
       /* mBinding.txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selected_color.equalsIgnoreCase(""))
                {
                    UpdateCategory(id1);
                     Pref.setValue(context,"updated","1");
                    ((FragmentActivity)context).getSupportFragmentManager().popBackStack();
                }
            }
        });*/
        categoryAdapter = new CategoryCustomAdapter(context,categoryArrayList,cat_name);
        mBinding.gridCategory.setAdapter(categoryAdapter);
        categoryAdapter.onClickTick(onClickTick);
        categoryAdapter.notifyDataSetChanged();
/*
        mBinding.gridCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("selected category",""+categories.get(position));
                ((FragmentActivity)context).getSupportFragmentManager().popBackStack();
            }
        });*/
        ((DashBoardActivity) context).mBinding.includeHeader.imgOptionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openDialog_for_add();
            }
        });
        return rootView;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

        return MoveAnimation.create(MoveAnimation.LEFT, enter, 500);

    }
    public void UpdateCategory(final String id) {
        if (!id.equalsIgnoreCase(""))
        {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Category> category = realm.where(Category.class).findAll();
                    category.deleteAllFromRealm();
                }
            });


            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    for (int cat = 0; cat < categoryArrayList.size(); cat++) {

                        category1[cat] = new Category();

                        if (categoryArrayList.get(cat).getCategoryId().equalsIgnoreCase(id)) {

                            category1[cat].setCategoryId(categoryArrayList.get(cat).getCategoryId());
                            category1[cat].setCategoryName(categoryArrayList.get(cat).getCategoryName());
                            category1[cat].setCategoryColor(categoryArrayList.get(cat).getCategoryColor());
                            category1[cat].setIsNew(categoryArrayList.get(cat).getIsNew());
                            category1[cat].setSelected("1");
                            realm.insert(category1[cat]);
                        } else {

                            category1[cat].setCategoryId(categoryArrayList.get(cat).getCategoryId());
                            category1[cat].setCategoryName(categoryArrayList.get(cat).getCategoryName());
                            category1[cat].setCategoryColor(categoryArrayList.get(cat).getCategoryColor());
                            category1[cat].setIsNew(categoryArrayList.get(cat).getIsNew());
                            category1[cat].setSelected("0");
                            realm.insert(category1[cat]);
                        }
                    }

                }
            });
        }
    }
    public void openDialog_for_add() {
        ArrayList<ColorModel> colorList = new ArrayList<>();
        colorList = Utils.colorList();

        final ArrayList<String> colorName = new ArrayList<>();
        for (int i = 0; i < (colorList.size()); i++) {

            colorName.add(colorList.get(i).color);

        }
        dialog1=new Dialog(context,R.style.PauseDialog);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.add_new_category);
        // dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final Edittext_Regular edt_event_category =(Edittext_Regular)dialog1.findViewById(R.id.edt_event_category);
        GridView gridColor = (GridView)dialog1.findViewById(R.id.gridColor);
        TextView_Bold txt_done = (TextView_Bold) dialog1.findViewById(R.id.txt_done);
        ColorCustomAdapter adapter=new ColorCustomAdapter(context,colorList);
        adapter.onColorClickTick(onColorClickTick);
        gridColor.setAdapter(adapter);

        txt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count=0;
                if(edt_event_category.getText().toString().equalsIgnoreCase(""))
                {
                    count++;
                    Toast.makeText(context,"Please add category name.",Toast.LENGTH_SHORT).show();
                }
                if(selected_color.equalsIgnoreCase("")) {
                    count++;
                    Toast.makeText(context,"Please select color.",Toast.LENGTH_SHORT).show();

                }
                if(count==0) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Long tsLong = System.currentTimeMillis() / 1000;
                            String ts = tsLong.toString();
                            // Add a Category
                            Category event_category = realm.createObject(Category.class);
                            String uuid = UUID.randomUUID().toString();
                            event_category.setCategoryId(uuid);
                            event_category.setCategoryName(edt_event_category.getText().toString());
                            event_category.setCategoryColor(selected_color);
                            event_category.setIsNew("1");
                            event_category.setSelected("0");
                            selected_color="";

                        }
                    });

                    dialog1.dismiss();
                    categoryArrayList.clear();
                    GetDefaultCategory();
                    categoryAdapter.notifyDataSetChanged();
                }
            }
        });


        dialog1.show();
    }


    public void GetDefaultCategory() {
        final RealmResults<Category> category = realm.where(Category.class).findAll();
        category1 = new Category[category.size()];
        categoryArrayList.clear();
        if (category.size() > 0) {

            for (int cat = 0; cat < category.size(); cat++) {

                category1[cat] = new Category();

                category1[cat].setCategoryId(category.get(cat).getCategoryId());
                category1[cat].setCategoryName(category.get(cat).getCategoryName());
                category1[cat].setCategoryColor(category.get(cat).getCategoryColor());
                category1[cat].setIsNew(category.get(cat).getIsNew());
                category1[cat].setSelected(category.get(cat).getSelected());

                categoryArrayList.add(category1[cat]);
            }
        }
    }

    public void AddDefaultCategory() {
        Pref.setValue(context,"add_category","1");
        categories.clear();
        categories_color.clear();
        categories.add("Anniversary");
        categories.add("Birthday");
        categories.add("Holiday");
        categories.add("School");
        categories.add("Life");
        categories.add("Trip");
        categories.add("Share");
        categories_color.add("#92bfde");
        categories_color.add("#448bc9");
        categories_color.add("#ef9b43");
        categories_color.add("#b96972");
        categories_color.add("#bbc1d7");
        categories_color.add("#81569d");
        categories_color.add("#911414");

        for(int i=0;i<categories.size();i++) {
            final int finalI = i;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts = tsLong.toString();
                    // Add a Category
                    Category event_category = realm.createObject(Category.class);
                    uuid = UUID.randomUUID().toString();
                    event_category.setCategoryId(uuid);
                    event_category.setCategoryName(categories.get(finalI));
                    event_category.setCategoryColor(categories_color.get(finalI));
                    event_category.setIsNew("0");
                    event_category.setSelected("0");

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        preview();

    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void preview() {
      /*  ((DashBoardActivity) context).mBinding.footer.imgCalendar.setImageResource(R.mipmap.calendar_deselect);
        ((DashBoardActivity) context).mBinding.footer.imgCountDown.setImageResource(R.mipmap.countdown_deselect);
        ((DashBoardActivity) context).mBinding.footer.imgFb.setImageResource(R.mipmap.user_profile);
        ((DashBoardActivity) context).mBinding.footer.imgSettings.setImageResource(R.mipmap.setting_deselect);
    */    ((DashBoardActivity) context).mBinding.includeHeader.txtTitle.setText("Category");
        ((DashBoardActivity) context).mBinding.includeHeader.imgBack.setVisibility(View.VISIBLE);
        ((DashBoardActivity) context).mBinding.includeHeader.imgDrawer.setVisibility(View.GONE);
        ((DashBoardActivity) context).mBinding.includeHeader.txtDone.setVisibility(View.GONE);
        ((DashBoardActivity) context).mBinding.includeHeader.imgOptionMenu.setVisibility(View.VISIBLE);

        if (Pref.getValue(context, "add_display", "").equalsIgnoreCase("0")) {
            ((DashBoardActivity) context).mBinding.adView.setVisibility(View.GONE);

        } else {
            ((DashBoardActivity) context).mBinding.adView.setVisibility(View.VISIBLE);

        }
        ((DashBoardActivity) context).mBinding.includeHeader.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                getActivity().getSupportFragmentManager().popBackStack();

            }
        });

    }
}
