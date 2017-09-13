package com.aipxperts.ecountdown.Fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.Adapter.CategoryAdapter;
import com.aipxperts.ecountdown.Adapter.ColorAdapter;
import com.aipxperts.ecountdown.Interface.OnClickTick;
import com.aipxperts.ecountdown.Model.Category;
import com.aipxperts.ecountdown.Model.ColorModel;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.Widget.Edittext_Regular;
import com.aipxperts.ecountdown.Widget.TextView_Bold;
import com.aipxperts.ecountdown.Widget.TextView_Regular;
import com.aipxperts.ecountdown.databinding.CreateNewEventLayoutBinding;
import com.aipxperts.ecountdown.utils.Constants;
import com.aipxperts.ecountdown.utils.MyListView;
import com.aipxperts.ecountdown.utils.Pref;
import com.aipxperts.ecountdown.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;
import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 6/7/17.
 */

public class CreateNewEventFragment extends Fragment {

    CreateNewEventLayoutBinding mBinding;
    View rootView;
    Context context;
    public Dialog mDialogRowBoardList;
    private Window mWindow;
    private WindowManager.LayoutParams mLayoutParams;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MY_REQUEST_CODE = 100;
    public static Uri fileUri;
    int REQUEST_CAMERA = 200;
    int SELECT_FILE = 201;
    private static final String IMAGE_DIRECTORY_NAME = "Camera";
    Bitmap rotatedBitmap;
    String encodedString = "";
    String isSetAsCover = "0";
    ArrayList<Event> eventArrayList = new ArrayList<>();
    ArrayList<Category> categoryArrayList = new ArrayList<>();
    String uuid;
    Calendar myCalendar;
    Event[] events1;
    Category[] category1;
    CategoryAdapter categoryAdapter;
    AdRequest adRequest;
    private boolean isActivityIsVisible = true;
    private InterstitialAd mInterstitialAd;
    ArrayList<String> categories=new ArrayList<>();
    ArrayList<String> categories_color=new ArrayList<>();
    ClipDrawable mImageDrawable;
    Dialog dialog=null;
    Dialog dialog1=null;
    String selected_color="";
    boolean isclick=false;
    int colorPosition = 0;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.create_new_event_layout, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();

        preview();
        mBinding.txtDone.setEnabled(true);
        geteventsfromDb();


        // mBinding.edtEventCategory1.setText("Category");


        if(Pref.getValue(context,"add_category","").equalsIgnoreCase(""))
        {
            AddDefaultCategory();
        }


        categoryArrayList.clear();
        GetDefaultCategory();
        mInterstitialAd = new InterstitialAd(context);
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        if (categoryArrayList.size() > 0) {
            for (int i = 0; i < categoryArrayList.size(); i++) {
                if (categoryArrayList.get(i).getSelected().equalsIgnoreCase("1")) {
                    mBinding.edtEventCategory1.setText(categoryArrayList.get(i).getCategoryName());
                    selected_color = categoryArrayList.get(i).getCategoryColor();
                }
            }
        }

        mBinding.edtEventTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(mBinding.edtEventTitle.hasFocus())
                {
                    mBinding.edtEventTitle.setBackgroundResource(R.drawable.edit_bg_curve);
                }else
                {
                    mBinding.edtEventTitle.setBackgroundResource(R.drawable.edit_bg_curve_without_focus);
                }
            }
        });
        mBinding.edtEventDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(mBinding.edtEventDescription.hasFocus())
                {
                    mBinding.edtEventDescription.setBackgroundResource(R.drawable.edit_bg_curve1);
                }else
                {
                    mBinding.edtEventDescription.setBackgroundResource(R.drawable.edit_bg_curve_without_focus1);
                }
            }
        });
        /**
         * ADD category
         */


        mBinding.edtEventCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = mBinding.edtEventCategory1.getText().toString();

                CategoryFragment fragment = new CategoryFragment();

                Bundle bundle = new Bundle();
                bundle.putString("cat_name", s);
                fragment.setArguments(bundle);
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();


            }
        });
        mBinding.edtEventCategory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = mBinding.edtEventCategory1.getText().toString();

                CategoryFragment fragment = new CategoryFragment();

                Bundle bundle = new Bundle();
                bundle.putString("cat_name", s);
                fragment.setArguments(bundle);
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

            }
        });

        /***
         * add event
         */
        ((DashBoardActivity)context).mBinding.includeHeader.txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mBinding.txtDone.setEnabled(false);
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                int err_cnt = 0;
             /*   if (encodedString.equalsIgnoreCase("")) {
                    err_cnt++;
                    Toast.makeText(context, getString(R.string.Event_photo_require), Toast.LENGTH_LONG).show();
                }*/
                if (mBinding.edtEventTitle.getText().toString().toString().trim().isEmpty()) {
                    err_cnt++;
                    mBinding.edtEventTitle.setError(getString(R.string.Event_title_require));
                } else {
                    if (mBinding.edtEventTitle.getText().toString().length() > 26) {
                        err_cnt++;
                        mBinding.edtEventTitle.setError(getString(R.string.Event_title_minimum));
                    }

                }
                if (mBinding.edtEventDescription.getText().toString().length() > 0&&mBinding.edtEventDescription.getText().toString().length() > 151) {
                    err_cnt++;
                    mBinding.edtEventDescription.setError(getString(R.string.Event_description_minimum));
                }

                if (mBinding.edtEventDate.getText().toString().isEmpty()) {
                    err_cnt++;
                    mBinding.edtEventDate.setError(getString(R.string.Event_date_require));
                }


                if (err_cnt == 0) {
                    if (eventArrayList.size() > 0) {

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Long tsLong = System.currentTimeMillis() / 1000;
                                String ts = tsLong.toString();
                                // Add a Category
                                Event event = realm.createObject(Event.class);

                                event.set_token(Pref.getValue(context, Constants.Token_tag, ""));
                                uuid = UUID.randomUUID().toString();
                                event.setEvent_uuid(uuid);

                                event.setEvent_name(mBinding.edtEventTitle.getText().toString());
                                event.setEvent_description(mBinding.edtEventDescription.getText().toString());
                                event.setEvent_image(encodedString);
                                event.setIs_cover(isSetAsCover);
                                event.setCategory(mBinding.edtEventCategory1.getText().toString());
                                event.setCategoryColor(selected_color);
                                event.setDate(Constants.parseDateToAddDatabase(mBinding.edtEventDate.getText().toString()));
                                // event.setEnd_date(Constants.parseDateToAddDatabase(mBinding.edtEndsDate.getText().toString()));
                                event.setIsComplete("0");
                                event.setCreated_date(ts);
                                event.setModified_date(ts);
                                event.setOperation("1");
                            }
                        });
                        if (isSetAsCover.equalsIgnoreCase("1")) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    final RealmResults<Event> event = realm.where(Event.class).findAll();
                                    for (int i = 0; i < event.size(); i++) {
                                        if (uuid.equalsIgnoreCase(event.get(i).getEvent_uuid())) {
                                            event.get(i).setIs_cover("1");
                                        } else {
                                            event.get(i).setIs_cover("0");
                                        }
                                    }
                                }
                            });
                        }
                    } else if (eventArrayList.size() == 0) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Long tsLong = System.currentTimeMillis() / 1000;
                                String ts = tsLong.toString();
                                // Add a Category
                                Event event = realm.createObject(Event.class);

                                event.set_token(Pref.getValue(context, Constants.Token_tag, ""));
                                event.setEvent_uuid(UUID.randomUUID().toString());
                                event.setEvent_name(mBinding.edtEventTitle.getText().toString());
                                event.setEvent_description(mBinding.edtEventDescription.getText().toString());
                                event.setEvent_image(encodedString);
                                event.setIs_cover("1");
                                event.setDate(Constants.parseDateToAddDatabase(mBinding.edtEventDate.getText().toString()));
                                // event.setEnd_date(Constants.parseDateToAddDatabase(mBinding.edtEndsDate.getText().toString()));
                                event.setIsComplete("0");
                                event.setCategory(mBinding.edtEventCategory1.getText().toString());
                                event.setCategoryColor(selected_color);
                                event.setCreated_date(ts);
                                event.setModified_date(ts);
                                event.setOperation("1");
                            }
                        });
                    }

                    final RealmResults<Event> event = realm.where(Event.class).equalTo("is_cover", "1").findAll();
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
                    } /*else {
                        Pref.setValue(context, "set_as_cover_click", event.get(0).getEvent_image());
                        Pref.setValue(context, "set_as_cover_des", event.get(0).getEvent_description());
                        Pref.setValue(context, "set_as_cover_title", event.get(0).getEvent_name());
                        Pref.setValue(context, "set_as_cover_date", event.get(0).getDate());

                    }*/
                    Pref.setValue(context, "from_tab", "recent");

                    if (Pref.getValue(context, "add_display", "").equalsIgnoreCase("0"))
                    {
                        ListEventFragment fragment = new ListEventFragment();
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

                        Toast.makeText(context, "Event added successfully.", Toast.LENGTH_LONG).show();

                    }else {
                        /**
                         * open full screen add when create new event
                         */
                        showInterstitial();
                        mInterstitialAd.setAdListener(new AdListener() {


                            @Override
                            public void onAdClosed() {
                                super.onAdClosed();

                                Toast.makeText(context, "Event added successfully.", Toast.LENGTH_LONG).show();
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Do something after 5s = 5000ms
                                        redirect();

                                    }
                                }, 500);
                            }
                        });
                    }
                    Pref.setValue(context,"updated","0");
                    ((DashBoardActivity)context).NotifyEvent(uuid,"New");
                    //Pref.setValue(context, "cur_fragment", "");
                }
            }
        });



        mBinding.imgEvent.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        showInputDialog_camera();
                    }
                });
        mBinding.llEvenDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.llEvenDate.setBackgroundResource(R.drawable.edit_bg_curve);
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy");
                String formattedDate = df.format(c.getTime());
                // mBinding.edtEventDate.setHint(formattedDate);
                date();
            }
        });
        mBinding.edtEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.llEvenDate.setBackgroundResource(R.drawable.edit_bg_curve);
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy");
                String formattedDate = df.format(c.getTime());
                // mBinding.edtEventDate.setHint(formattedDate);
                date();
            }
        });
        mBinding.SetAsCoverSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    mBinding.SetAsCoverSwitch.setBackgroundResource(R.mipmap.switch_on);
                    isSetAsCover = "1";

                } else {
                    mBinding.SetAsCoverSwitch.setBackgroundResource(R.mipmap.switch_off);
                    isSetAsCover = "0";
                }
            }
        });


        return rootView;
    }



    /*
        public void openDialog() {
            dialog=new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.option_select_new);
           // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            MyListView ll_category_list = (MyListView)dialog.findViewById(R.id.ll_category_list);
            ImageView iv_category = (ImageView)dialog.findViewById(R.id.iv_category);
            LinearLayout ll_add_new_category =(LinearLayout)dialog.findViewById(R.id.ll_add_new_category);
            categoryAdapter = new CategoryAdapter(context,categoryArrayList);
            ll_category_list.setAdapter(categoryAdapter);
            categoryAdapter.onClickTick(onClickTick);
            categoryAdapter.notifyDataSetChanged();

            ll_category_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("selected category",""+categories.get(position));
                    dialog.dismiss();
                }
            });
            ll_add_new_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                   // openDialog_for_add();
                }
            });
            dialog.show();
        }*/

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
        categories_color.add("#92bfde");
        categories_color.add("#448bc9");
        categories_color.add("#ef9b43");
        categories_color.add("#b96972");
        categories_color.add("#bbc1d7");
        categories_color.add("#81569d");
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
                    if(finalI==0) {
                        event_category.setSelected("1");
                    }else
                    {
                        event_category.setSelected("0");
                    }
                }
            });
        }
    }
    public void UpdateCategory() {

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

                    category1[cat].setCategoryId(categoryArrayList.get(cat).getCategoryId());
                    category1[cat].setCategoryName(categoryArrayList.get(cat).getCategoryName());
                    category1[cat].setCategoryColor(categoryArrayList.get(cat).getCategoryColor());
                    category1[cat].setIsNew(categoryArrayList.get(cat).getIsNew());
                    category1[cat].setSelected("0");
                    realm.insert(category1[cat]);

                }

            }
        });

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        Pref.setValue(context,"updated","0");
                        ((FragmentActivity)context).getSupportFragmentManager().popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
    public void redirect()
    {
        ListEventFragment fragment = new ListEventFragment();
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

        // getActivity().getSupportFragmentManager().popBackStack();
    }
    private void showInterstitial() {
        if (isActivityIsVisible) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }

    }

    private void preview() {
      /*  ((DashBoardActivity) context).mBinding.footer.imgCalendar.setImageResource(R.mipmap.calendar_deselect);
        ((DashBoardActivity) context).mBinding.footer.imgCountDown.setImageResource(R.mipmap.countdown_deselect);
        ((DashBoardActivity) context).mBinding.footer.imgFb.setImageResource(R.mipmap.user_profile);
        ((DashBoardActivity) context).mBinding.footer.imgSettings.setImageResource(R.mipmap.setting_deselect);
    */
        ((DashBoardActivity) context).mBinding.includeHeader.txtTitle.setText("Create new event");
        ((DashBoardActivity) context).mBinding.includeHeader.imgBack.setVisibility(View.VISIBLE);
        ((DashBoardActivity) context).mBinding.includeHeader.imgDrawer.setVisibility(View.GONE);
        ((DashBoardActivity) context).mBinding.includeHeader.txtDone.setVisibility(View.VISIBLE);
        ((DashBoardActivity) context).mBinding.includeHeader.imgOptionMenu.setVisibility(View.GONE);

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

    /**
     * date related
     */
    public void date() {

        myCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, date,
                myCalendar.get(Calendar.YEAR), myCalendar
                .get(Calendar.MONTH), myCalendar
                .get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

        datePickerDialog.setCanceledOnTouchOutside(false);

        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mBinding.edtEventDate.setEnabled(true);
            }
        });


    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
// TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mBinding.edtEventDate.setEnabled(true);
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
            String formattedDate = df.format(myCalendar.getTime());
            mBinding.edtEventDate.setText(formattedDate);
            mBinding.edtEventDate.setError(null);
            mBinding.llEvenDate.setBackgroundResource(R.drawable.edit_bg_curve_without_focus);
        }

    };

    @Override
    public void onResume() {
        super.onResume();

        // GetDefaultCategory();
        if(Pref.getValue(context,"updated","").equalsIgnoreCase("1")) {
            GetDefaultCategory1();
            if (categoryArrayList.size() > 0) {
                for (int i = 0; i < categoryArrayList.size(); i++) {
                    if (categoryArrayList.get(i).getSelected().equalsIgnoreCase("1")) {
                        mBinding.edtEventCategory1.setText(categoryArrayList.get(i).getCategoryName());
                        selected_color = categoryArrayList.get(i).getCategoryColor();
                    }
                }
            }
        }else
        {
            GetDefaultCategory();
            if (categoryArrayList.size() > 0) {
                for (int i = 0; i < categoryArrayList.size(); i++) {
                    if (categoryArrayList.get(i).getSelected().equalsIgnoreCase("1")) {
                        mBinding.edtEventCategory1.setText(categoryArrayList.get(i).getCategoryName());
                        selected_color = categoryArrayList.get(i).getCategoryColor();
                    }
                }
            }

        }

        ((DashBoardActivity)context).slidingMenu.setSlidingEnabled(false);
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
                if(cat==0)
                {
                    category1[cat].setSelected("1");
                }else
                {
                    category1[cat].setSelected("0");
                }


                categoryArrayList.add(category1[cat]);
            }
        }
    }
    public void GetDefaultCategory1() {
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
    /**
     * image function
     */

    public void showInputDialog_camera() {

        mDialogRowBoardList = new Dialog(context);
        mDialogRowBoardList.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogRowBoardList.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialogRowBoardList.setContentView(R.layout.photo_dialog_layout);
        mDialogRowBoardList.setCancelable(false);
        mWindow = mDialogRowBoardList.getWindow();
        mLayoutParams = mWindow.getAttributes();
        mLayoutParams.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(mLayoutParams);
        final TextView_Regular txtgallery = (TextView_Regular) mDialogRowBoardList.findViewById(R.id.txtgallery);
        final TextView_Regular txtcamera = (TextView_Regular) mDialogRowBoardList.findViewById(R.id.txtcamera);
        final TextView_Bold cancel = (TextView_Bold) mDialogRowBoardList.findViewById(R.id.cancel);

        txtgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        getActivity().startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                SELECT_FILE);
                        mDialogRowBoardList.dismiss();
                    }
                } else {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    getActivity().startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                    mDialogRowBoardList.dismiss();
                }
            }
        });

        txtcamera.setOnClickListener(new View.OnClickListener() {

                                         @Override
                                         public void onClick(View v) {


                                             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                 if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                                                         != PackageManager.PERMISSION_GRANTED) {
                                                     getActivity().requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                             MY_REQUEST_CODE);
                                                 } else {
                                                     Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                     fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                                     Log.v("fileUri", fileUri + "--");
                                                     intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                                     // start the image capture Intent
                                                     getActivity().startActivityForResult(intent, REQUEST_CAMERA);
                                                     mDialogRowBoardList.dismiss();
                                                 }
                                             } else {
                                                 Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                 fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                                 Log.v("fileUri", fileUri + "--");
                                                 intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                                 // start the image capture Intent
                                                 getActivity().startActivityForResult(intent, REQUEST_CAMERA);
                                                 mDialogRowBoardList.dismiss();
                                             }


                                         }
                                     }
        );
        cancel.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View v) {

                                          mDialogRowBoardList.dismiss();
                                      }
                                  }

        );


        mDialogRowBoardList.show();
    }


    public void onActivityGallery(Intent data)
    {
        String selectedImagePath="";
        Uri selectedImageUri = data.getData();
        if(selectedImageUri.toString().startsWith("content")) {
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = ((FragmentActivity)context).managedQuery(selectedImageUri, projection, null, null,
                    null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            selectedImagePath = cursor.getString(column_index);
            Bitmap bm;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(selectedImagePath, options);
            final int REQUIRED_SIZE = 200;
            int scale = 1;
            while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                    && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(selectedImagePath, options);

            rotatedBitmap=bm;

            if (bm.getWidth() > bm.getHeight()) {
                Matrix matrix = new Matrix();
                //matrix.postRotate(90);
                rotatedBitmap = Bitmap.createBitmap(bm, 0, 0,bm.getWidth(),bm.getHeight(), matrix, true);
            }
        }
        else {

            selectedImagePath=data.getData().toString();
            Log.e("rotatedBitmap","---------------"+selectedImagePath.substring(5,selectedImagePath.length()));
            File imgFile = new  File(selectedImagePath.substring(5,selectedImagePath.length()));
            Log.e("rotatedBitmap","---------------"+imgFile+"--"+imgFile.exists());
            if(imgFile.exists()) {
                rotatedBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                Log.e("rotatedBitmap",rotatedBitmap+"---");
            }
        }


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bitmap b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Bitmap bmpnew=Bitmap.createScaledBitmap(b, 500, 500, false);
        //   mBinding.imgEvent.setImageBitmap(bmpnew);
        Glide.with(context).load(selectedImagePath).centerCrop().into(mBinding.imgEvent);


        encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        mBinding.imgCamera.setVisibility(View.GONE);

    }
    public void onActivity(Intent data)
    {
        Bitmap thumbnail=null;
        Bitmap rotatedBitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            if (bitmap.getWidth() > bitmap.getHeight()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(), matrix, true);
            }
            else {
                rotatedBitmap=bitmap;
            }

            // rotatedBitmap=bitmap;
            Log.v("rotatedBitmap",rotatedBitmap+"--");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Bitmap b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Bitmap bmpnew=Bitmap.createScaledBitmap(b, 500, 500, false);

            encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);

            //  mBinding.imgEvent.setImageBitmap(bmpnew);
            mBinding.imgCamera.setVisibility(View.GONE);
            Glide.with(context).load(new File(fileUri.getPath())).centerCrop().into(mBinding.imgEvent);

            // file1= bitmapToFile(bmpnew);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    /**
     * get data from data base
     */
    public void geteventsfromDb() {
        final RealmResults<Event> event = realm.where(Event.class).findAll();
        events1 = new Event[event.size()];
        eventArrayList.clear();
        if (event.size() > 0) {

            for (int cat = 0; cat < event.size(); cat++) {

                events1[cat] = new Event();

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
}
