package com.aipxperts.ecountdown.Fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ClipDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Toast;

import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.Interface.OnClickTick;
import com.aipxperts.ecountdown.Model.Category;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.Widget.TextView_Bold;
import com.aipxperts.ecountdown.Widget.TextView_Regular;
import com.aipxperts.ecountdown.databinding.EditEventLayoutBinding;
import com.aipxperts.ecountdown.utils.ConnectionDetector;
import com.aipxperts.ecountdown.utils.Constants;
import com.aipxperts.ecountdown.utils.Pref;
import com.bumptech.glide.Glide;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.models.Image;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 6/7/17.
 */

public class EditEventFragment extends Fragment{

    EditEventLayoutBinding mBinding;
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
    String encodedString="";
    String  isSetAsCover="0";
    ArrayList<Event> eventArrayList=new ArrayList<>();
    String uuid;
    Calendar myCalendar;
    Event[] events1;
    Event events_edit=new Event();
    AdRequest adRequest;
    private boolean isActivityIsVisible = true;
    private InterstitialAd mInterstitialAd;
    OnClickTick onClickTick;
    ArrayList<String> categories=new ArrayList<>();
    ArrayList<String> categories_color=new ArrayList<>();
    ClipDrawable mImageDrawable;
    Dialog dialog=null;
    Dialog dialog1=null;
    String selected_color="";
    boolean isclick=false;
    ArrayList<Category> categoryArrayList = new ArrayList<>();
    Category[] category1;
    ConnectionDetector connectionDetector;
    File storeDirectory;
    String storePath = "";
    public String formattedDate;
    String cat_id;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.edit_event_layout, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        connectionDetector = new ConnectionDetector(context);
        ((DashBoardActivity)context).mBinding.includeHeader.txtDone.setEnabled(true);

        preview();
        myCalendar = Calendar.getInstance();

        getAllEventFromdb();
        if(Pref.getValue(context,"add_category","").equalsIgnoreCase(""))
        {
            AddDefaultCategory();
        }


        categoryArrayList.clear();
        GetDefaultCategory();

      /*  mInterstitialAd = new InterstitialAd(context);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
*/


     /*   onClickTick = new OnClickTick() {
            @Override
            public void OnClickTick(int position, final String id, final String name, String color) {

                dialog.dismiss();
                Log.e("name",""+name);
                mBinding.edtEventCategory1.setText(name);
                if(!id.equalsIgnoreCase("")) {
                    cat_id = id;
                    UpdateCategory(id);
                    selected_color= color;
                }
            }
        };*/
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
         * set details
         */
        mBinding.edtEventTitle.setText(events_edit.getEvent_name());
        mBinding.edtEventDescription.setText(events_edit.getEvent_description());
        mBinding.edtEventDate.setText(Constants.parseDateDatabaseToDisplay(events_edit.getDate()));
        mBinding.edtEventCategory1.setText(events_edit.getCategory());
        selected_color=events_edit.getCategoryColor();
        encodedString = events_edit.getEvent_image();
        storePath=events_edit.getEvent_image();
        Log.e("encodedString",""+encodedString);
        if(encodedString!=null) {
            if (!encodedString.equalsIgnoreCase("")) {
                Glide.with(context).load(encodedString).skipMemoryCache(false).into(mBinding.imgEvent);

           /* if(encodedString.startsWith("http"))
            {
                Glide.with(context).load(encodedString).skipMemoryCache(false).into(mBinding.imgEvent);
            }else {
                byte[] decodedString = Base64.decode(events_edit.getEvent_image(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                mBinding.imgEvent.setImageBitmap(decodedByte);
            }*/
                mBinding.imgCamera.setVisibility(View.GONE);
            } else {
                mBinding.imgCamera.setVisibility(View.VISIBLE);
            }
        }else {
            mBinding.imgCamera.setVisibility(View.VISIBLE);
        }
        if(events_edit.getIs_cover().equalsIgnoreCase("0"))
        {

            mBinding.SetAsCoverSwitch.setBackgroundResource(R.mipmap.switch_off);
            isSetAsCover="0";
        }else
        {
            mBinding.SetAsCoverSwitch.setBackgroundResource(R.mipmap.switch_on);
            isSetAsCover="1";
            /*Pref.setValue(context,"set_as_cover_click",events_edit.getEvent_image());
            Pref.setValue(context,"set_as_cover_des",events_edit.getEvent_description());
            Pref.setValue(context,"set_as_cover_title",events_edit.getEvent_name());
            Pref.setValue(context,"set_as_cover_date",events_edit.getDate());*/

        }
        mBinding.imgEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog_camera();
                mBinding.imgEvent.setEnabled(false);
            }
        });
        mBinding.llEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.llEventDate.setBackgroundResource(R.drawable.edit_bg_curve);
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy");
                String formattedDate = df.format(c.getTime());
                mBinding.edtEventDate.setHint(formattedDate);
                date();
            }
        });
        mBinding.edtEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.llEventDate.setBackgroundResource(R.drawable.edit_bg_curve);
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("MMM dd,yyyy");
                String formattedDate = df.format(c.getTime());
                mBinding.edtEventDate.setHint(formattedDate);
                date();
            }
        });

        mBinding.SetAsCoverSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b)
                {
                    mBinding.SetAsCoverSwitch.setBackgroundResource(R.mipmap.switch_on);
                    isSetAsCover="1";

                }else
                {
                    mBinding.SetAsCoverSwitch.setBackgroundResource(R.mipmap.switch_off);
                    isSetAsCover="0";
                }
            }
        });
        /**
         * ADD category
         */
        mBinding.edtEventCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String s = mBinding.edtEventCategory1.getText().toString();
                        CategoryFragment fragment = new CategoryFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("cat_name", s);
                        fragment.setArguments(bundle);
                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

                    }
                }, 100);


            }
        });
        /**
         * delete event
         */

        mBinding.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(0);

            }
        });
        mBinding.edtEventCategory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openDialog();
                hideKeyboard();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String s = mBinding.edtEventCategory1.getText().toString();
                        CategoryFragment fragment = new CategoryFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("cat_name", s);
                        fragment.setArguments(bundle);
                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

                    }
                }, 100);


            }
        });

        /***
         * add event
         */

        ((DashBoardActivity)context).mBinding.includeHeader.txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                int err_cnt=0;
             /*   if(encodedString.equalsIgnoreCase(""))
                {
                    err_cnt++;
                    Toast.makeText(context,getString(R.string.Event_photo_require),Toast.LENGTH_LONG).show();
                }*/
                if(mBinding.edtEventTitle.getText().toString().toString().trim().isEmpty())
                {
                    err_cnt++;
                    mBinding.edtEventTitle.setError(getString(R.string.Event_title_require));
                }else
                {
                    if(mBinding.edtEventTitle.getText().toString().length()>26)
                    {
                        err_cnt++;
                        mBinding.edtEventTitle.setError(getString(R.string.Event_title_minimum));
                    }

                }
                if (mBinding.edtEventDescription.getText().toString().length() > 0&&mBinding.edtEventDescription.getText().toString().length() > 151) {
                    err_cnt++;
                    mBinding.edtEventDescription.setError(getString(R.string.Event_description_minimum));
                }
                if (mBinding.edtEventDate.getText().toString().equalsIgnoreCase("Event date"))
                {
                    err_cnt++;
                    mBinding.edtEventDate.setError(getString(R.string.Event_date_require));
                }


                if(err_cnt==0) {
                    ((DashBoardActivity)context).mBinding.includeHeader.txtDone.setEnabled(false);
                    updateEventFromdb();
                    final RealmResults<Event> event = realm.where(Event.class).equalTo("is_cover", "1").findAll();
                    if(Pref.getValue(context,"edit_from","").equalsIgnoreCase("detail"))
                    {
                        Pref.setValue(context,"detail_even_name",mBinding.edtEventTitle.getText().toString());
                        Pref.setValue(context,"detail_even_date",Constants.parseDateToAddDatabase(mBinding.edtEventDate.getText().toString()));
                        Pref.setValue(context,"detail_even_image",encodedString);
                        Pref.setValue(context,"detail_even_des",mBinding.edtEventDescription.getText().toString());
                        //   Pref.setValue(context,"detail_even_end_date",mBinding.edtEndsDate.getText().toString());
                        Pref.setValue(context,"detail_even_uuid",Pref.getValue(context,"EditEventId",""));

                    }
                    // Toast.makeText(context,"Event updated successfully.",Toast.LENGTH_LONG).show();

                    ListEventFragment fragment = new ListEventFragment();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();
                    if(connectionDetector.isConnectingToInternet()) {
                        if (!Pref.getValue(context, "add_display", "").equalsIgnoreCase("0")) {
                            ((DashBoardActivity) context).showInterstitial();
                            ((DashBoardActivity) context).mInterstitialAd.setAdListener(new AdListener() {

                                @Override
                                public void onAdClosed() {

                                    super.onAdClosed();
                                    Log.e("ad","add closed");
                                }
                                @Override
                                public void onAdOpened() {
                                    super.onAdOpened();
                                    Log.e("ad","add opened");
                                }
                            });
                        }
                    }
                 /*   if(event.size()>0)
                    {
                        for(int i=0;i<event.size();i++) {
                            if (event.get(i).getIs_cover().equalsIgnoreCase("1")) {
                                Pref.setValue(context, "set_as_cover_click", event.get(i).getEvent_image());
                                Pref.setValue(context, "set_as_cover_des", event.get(i).getEvent_description());
                                Pref.setValue(context, "set_as_cover_title", event.get(i).getEvent_name());
                                Pref.setValue(context, "set_as_cover_date", event.get(i).getDate());
                            }else
                            {
                                Pref.setValue(context,"set_as_cover_click",event.get(0).getEvent_image());
                                Pref.setValue(context,"set_as_cover_des",event.get(0).getEvent_description());
                                Pref.setValue(context,"set_as_cover_title",event.get(0).getEvent_name());
                                Pref.setValue(context,"set_as_cover_date",event.get(0).getDate());

                            }
                        }
                    }*//*else
                    {
                        Pref.setValue(context,"set_as_cover_click",event.get(0).getEvent_image());
                        Pref.setValue(context,"set_as_cover_des",event.get(0).getEvent_description());
                        Pref.setValue(context,"set_as_cover_title",event.get(0).getEvent_name());
                        Pref.setValue(context,"set_as_cover_date",event.get(0).getDate());

                    }*/
/*
                    if (Pref.getValue(context, "add_display", "").equalsIgnoreCase("0"))
                    {
                        Toast.makeText(context,"Event updated successfully.",Toast.LENGTH_LONG).show();

                        ListEventFragment fragment = new ListEventFragment();
                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();
                    }else {

                        *//**
                     * open full screen add when edit event
                     *//*
                        if(connectionDetector.isConnectingToInternet()) {
                            ((DashBoardActivity) context).showInterstitial();
                            ((DashBoardActivity) context).mInterstitialAd.setAdListener(new AdListener() {

                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();

                                    ((DashBoardActivity) context).NotifyEvent(Pref.getValue(context, "EditEventId", ""), "Edit");
                                    Toast.makeText(context, "Event updated successfully.", Toast.LENGTH_LONG).show();
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            redirect();

                                        }
                                    }, 500);


                                }
                            });
                        }else
                        {
                            Toast.makeText(context,"Event updated successfully.",Toast.LENGTH_LONG).show();

                            ListEventFragment fragment = new ListEventFragment();
                            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

                        }
                    }*/

                    Pref.setValue(context,"updated","0");
                    ((DashBoardActivity) context).NotifyEvent(Pref.getValue(context, "EditEventId", ""), "Edit");
                    // getActivity().getSupportFragmentManager().popBackStack();
                    // Pref.setValue(context, "cur_fragment", "");
                }
            }

        });
        return rootView;
    }
    /* private void showInterstitial() {
         if (((DashBoardActivity)context).isActivityIsVisible) {
             if (((DashBoardActivity)context).mInterstitialAd.isLoaded()) {
                 ((DashBoardActivity)context).mInterstitialAd.show();
             }
         }

     }*/
    public void callParentMethod(){
        getActivity().onBackPressed();
        Log.e("back","parent");
    }
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

        return MoveAnimation.create(MoveAnimation.LEFT, enter, 500);

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
                        Event event = realm.where(Event.class).equalTo("event_uuid", events_edit.getEvent_uuid()).findFirst();
                        event.deleteFromRealm();

                        ListEventFragment fragment = new ListEventFragment();
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();

                    }
                });
               /* realm.executeTransaction(new Realm.Transaction() {
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
                        }*//*else
                                    {
                                        Pref.setValue(context,"set_as_cover_click",event.get(0).getEvent_image());
                                        Pref.setValue(context,"set_as_cover_des",event.get(0).getEvent_description());
                                        Pref.setValue(context,"set_as_cover_title",event.get(0).getEvent_name());
                                        Pref.setValue(context,"set_as_cover_date",event.get(0).getDate());

                                    }*//*
                    }
                });*/
                dialog.dismiss();
                Toast.makeText(context, "Event deleted successfully.", Toast.LENGTH_LONG).show();

                ListEventFragment fragment = new ListEventFragment();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

            }
        });
        dialog.show();

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
        categories.add("General");
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



    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void redirect()
    {

        ListEventFragment fragment = new ListEventFragment();
        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

    }
    public void onActivityGallery(Image data)
    {
        String selectedImagePath=data.path;
   /*     Uri selectedImageUri = data.getData();
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
*/

      /*  ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bitmap b = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Bitmap bmpnew=Bitmap.createScaledBitmap(b, 500, 500, false);
        //   mBinding.imgEvent.setImageBitmap(bmpnew);*/
        Glide.with(context).load(data.path).centerCrop().into(mBinding.imgEvent);
        try {
            String timeStamp = "/" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + ".jpg";

            File file = new File(storeDirectory + timeStamp);
            if (!file.exists()) {
                file.createNewFile();
            }

            Log.e("LLL", "Des : " + storeDirectory + timeStamp + "Source : " + selectedImagePath);
            copyFile(new File(selectedImagePath), new File(storeDirectory + timeStamp));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //selectedImagePath = selectedImagePath.substring(5, selectedImagePath.length());
        //  encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);
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
                matrix.postRotate(180);
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
            // mBinding.imgEvent.setImageBitmap(bmpnew);
            mBinding.imgCamera.setVisibility(View.GONE);
            storePath=fileUri.getPath();
            Glide.with(context).load(new File(fileUri.getPath())).centerCrop().into(mBinding.imgEvent);

            // file1= bitmapToFile(bmpnew);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        storePath = destFile.getAbsolutePath();
        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e("AAA","Event edit");
        if(Pref.getValue(context,"updated","").equalsIgnoreCase("1"))
        {
            if (categoryArrayList.size() > 0) {
                for (int i = 0; i < categoryArrayList.size(); i++) {

                    if(!((DashBoardActivity)context).getcategoryName().equalsIgnoreCase(""))
                    {

                        mBinding.edtEventCategory1.setText(((DashBoardActivity)context).getcategoryName());
                    }else
                    {
                        mBinding.edtEventCategory1.setText(categoryArrayList.get(i).getCategoryName());

                    }
                    if(!((DashBoardActivity)context).getcategoryColor().equalsIgnoreCase(""))
                    {
                        selected_color=((DashBoardActivity)context).getcategoryColor();
                    }else {
                        selected_color = categoryArrayList.get(i).getCategoryColor();
                    }
                }
            }
        }
        if(!Pref.getValue(context,"url","").equalsIgnoreCase(""))
        {
            encodedString=Pref.getValue(context,"url","");
            storePath=Pref.getValue(context,"url","");
            Glide.with(context).load(storePath).centerCrop().into(mBinding.imgEvent);

           /* byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            mBinding.imgEvent.setImageBitmap(decodedByte);*/
        }
        createFolder();
        ((DashBoardActivity)context).slidingMenu.setSlidingEnabled(false);
    }

    private void createFolder() {

        storeDirectory = new File("/sdcard/Android/data/" + getActivity().getPackageName() + "/");
        if (!storeDirectory.exists()) {
            storeDirectory.mkdirs();
        }
    }
    private void preview() {
        ((DashBoardActivity)context).mBinding.includeHeader.txtTitle.setText("Edit event");
        ((DashBoardActivity)context).mBinding.includeHeader.imgBack.setVisibility(View.VISIBLE);
        ((DashBoardActivity)context).mBinding.includeHeader.imgDrawer.setVisibility(View.GONE);
        ((DashBoardActivity)context).mBinding.includeHeader.txtDone.setVisibility(View.VISIBLE);
        if(Pref.getValue(context,"add_display","").equalsIgnoreCase("0"))
        {
            ((DashBoardActivity)context).mBinding.adView.setVisibility(View.GONE);

        }else
        {
            ((DashBoardActivity)context).mBinding.adView.setVisibility(View.VISIBLE);

        }
        ((DashBoardActivity)context).mBinding.includeHeader.imgOptionMenu.setVisibility(View.GONE);

        ((DashBoardActivity)context).mBinding.includeHeader.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                ((DashBoardActivity)context).setCategoryId("");
                ((DashBoardActivity)context).setCategoryName("");
                ((DashBoardActivity)context).setCategoryColor("");
                Pref.setValue(context,"updated","");

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }


    /**
     * get and update db
     */

    public void getAllEventFromdb()
    {

        final RealmResults<Event> event = realm.where(Event.class).findAll();
        Log.e("size",""+event.size());
        events1 = new Event[event.size()];
        // Event event = realm.where(Event.class).equalTo("event_uuid", Pref.getValue(context,"EditEventId","")).findFirst();
        eventArrayList.clear();
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

        for(int i = 0 ; i < eventArrayList.size(); i++)
        {

            if (eventArrayList.get(i).getEvent_uuid().equalsIgnoreCase(Pref.getValue(context, "EditEventId", ""))) {
                events_edit.set_token(eventArrayList.get(i).get_token());
                events_edit.setEvent_uuid(eventArrayList.get(i).getEvent_uuid());
                events_edit.setEvent_name(eventArrayList.get(i).getEvent_name());
                events_edit.setEvent_description(eventArrayList.get(i).getEvent_description());
                events_edit.setEvent_image(eventArrayList.get(i).getEvent_image());
                events_edit.setIs_cover(eventArrayList.get(i).getIs_cover());
                events_edit.setDate(eventArrayList.get(i).getDate());
                events_edit.setIsComplete(eventArrayList.get(i).getIsComplete());
                events_edit.setCreated_date(eventArrayList.get(i).getCreated_date());
                events_edit.setModified_date(eventArrayList.get(i).getModified_date());
                events_edit.setOperation(eventArrayList.get(i).getOperation());
                events_edit.setCategory(eventArrayList.get(i).getCategory());
                events_edit.setCategoryColor(eventArrayList.get(i).getCategoryColor());
            }
        }
    }
    public void updateEventFromdb(){
        // eventArrayList.clear();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Event> event = realm.where(Event.class).findAll();
                event.deleteAllFromRealm();
            }
        });

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if(isSetAsCover.equalsIgnoreCase("1")) {

                    for (int cat = 0; cat < eventArrayList.size(); cat++) {

                        events1[cat] = new Event();

                        if (eventArrayList.get(cat).getEvent_uuid().equalsIgnoreCase(Pref.getValue(context, "EditEventId", ""))) {

                            events1[cat].set_token(eventArrayList.get(cat).get_token());
                            events1[cat].setEvent_uuid(eventArrayList.get(cat).getEvent_uuid());
                            events1[cat].setEvent_name(mBinding.edtEventTitle.getText().toString());
                            events1[cat].setEvent_description(mBinding.edtEventDescription.getText().toString());
                            events1[cat].setEvent_image(storePath);
                            events1[cat].setIs_cover("1");
                            events1[cat].setDate(Constants.parseDateToAddDatabase(mBinding.edtEventDate.getText().toString()));
                            events1[cat].setIsComplete(eventArrayList.get(cat).getIsComplete());
                            events1[cat].setCreated_date(eventArrayList.get(cat).getCreated_date());
                            events1[cat].setModified_date(eventArrayList.get(cat).getModified_date());
                            events1[cat].setOperation(eventArrayList.get(cat).getOperation());
                            events1[cat].setCategory(mBinding.edtEventCategory1.getText().toString());
                            events1[cat].setCategoryColor(selected_color);
                            realm.insert(events1[cat]);
                        } else {

                            events1[cat].set_token(eventArrayList.get(cat).get_token());
                            events1[cat].setEvent_uuid(eventArrayList.get(cat).getEvent_uuid());
                            events1[cat].setEvent_name(eventArrayList.get(cat).getEvent_name());
                            events1[cat].setEvent_description(eventArrayList.get(cat).getEvent_description());
                            events1[cat].setEvent_image(eventArrayList.get(cat).getEvent_image());
                            events1[cat].setIs_cover("0");
                            events1[cat].setDate(eventArrayList.get(cat).getDate());
                            events1[cat].setIsComplete(eventArrayList.get(cat).getIsComplete());
                            events1[cat].setCreated_date(eventArrayList.get(cat).getCreated_date());
                            events1[cat].setModified_date(eventArrayList.get(cat).getModified_date());
                            events1[cat].setOperation(eventArrayList.get(cat).getOperation());
                            events1[cat].setCategory(eventArrayList.get(cat).getCategory());
                            events1[cat].setCategoryColor(eventArrayList.get(cat).getCategoryColor());
                            realm.insert(events1[cat]);
                        }
                    }
                }else
                {
                    for (int cat = 0; cat < eventArrayList.size(); cat++) {


                        events1[cat] = new Event();
                        if (eventArrayList.get(cat).getEvent_uuid().equalsIgnoreCase(Pref.getValue(context, "EditEventId", ""))) {
                            events1[cat].set_token(eventArrayList.get(cat).get_token());
                            events1[cat].setEvent_uuid(eventArrayList.get(cat).getEvent_uuid());
                            events1[cat].setEvent_name(mBinding.edtEventTitle.getText().toString());
                            events1[cat].setEvent_description(mBinding.edtEventDescription.getText().toString());
                            events1[cat].setEvent_image(storePath);
                            events1[cat].setIs_cover(eventArrayList.get(cat).getIs_cover());
                            events1[cat].setDate(Constants.parseDateToAddDatabase(mBinding.edtEventDate.getText().toString()));
                            events1[cat].setCreated_date(eventArrayList.get(cat).getCreated_date());
                            events1[cat].setModified_date(eventArrayList.get(cat).getModified_date());
                            events1[cat].setOperation(eventArrayList.get(cat).getOperation());
                            events1[cat].setCategory(mBinding.edtEventCategory1.getText().toString());
                            events1[cat].setCategoryColor(selected_color);
                            realm.insert(events1[cat]);
                        }else
                        {
                            events1[cat].set_token(eventArrayList.get(cat).get_token());
                            events1[cat].setEvent_uuid(eventArrayList.get(cat).getEvent_uuid());
                            events1[cat].setEvent_name(eventArrayList.get(cat).getEvent_name());
                            events1[cat].setEvent_description(eventArrayList.get(cat).getEvent_description());
                            events1[cat].setEvent_image(eventArrayList.get(cat).getEvent_image());
                            events1[cat].setIs_cover(eventArrayList.get(cat).getIs_cover());
                            events1[cat].setDate(eventArrayList.get(cat).getDate());
                            events1[cat].setIsComplete(eventArrayList.get(cat).getIsComplete());
                            events1[cat].setCreated_date(eventArrayList.get(cat).getCreated_date());
                            events1[cat].setModified_date(eventArrayList.get(cat).getModified_date());
                            events1[cat].setOperation(eventArrayList.get(cat).getOperation());
                            events1[cat].setCategory(eventArrayList.get(cat).getCategory());
                            events1[cat].setCategoryColor(eventArrayList.get(cat).getCategoryColor());
                            realm.insert(events1[cat]);
                        }
                    }
                }
            }
        });
        //eventArrayList.add(events1[cat]);
        UpdateCategory(((DashBoardActivity)context).getcategoryID());
    }

    /**
     * date related
     */
    public void date()
    {

        String str = events_edit.getDate();
        Log.e("edit_date",""+str);

        myCalendar.set(Calendar.YEAR, Integer.parseInt(str.split("/")[2]));
        myCalendar.set(Calendar.MONTH, Integer.parseInt(str.split("/")[0])-1);
        myCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(str.split("/")[1]));

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
            formattedDate = df.format(myCalendar.getTime());
            mBinding.edtEventDate.setText(formattedDate);
            mBinding.edtEventDate.setError(null);
            mBinding.llEventDate.setBackgroundResource(R.drawable.edit_bg_curve_without_focus);

        }

    };

    /**
     * image function
     */

    public  void showInputDialog_camera() {

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
        final TextView_Regular txtcamera= (TextView_Regular) mDialogRowBoardList.findViewById(R.id.txtcamera);
        final TextView_Regular txtsearch = (TextView_Regular) mDialogRowBoardList.findViewById(R.id.txtsearch);
        final TextView_Bold cancel = (TextView_Bold) mDialogRowBoardList.findViewById(R.id.cancel);

        txtgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, AlbumSelectActivity.class);
//set limit on number of images that can be selected, default is 10
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, Constants.DEFAULT_LIMIT);
                getActivity().startActivityForResult(intent, SELECT_FILE);
               /* Intent intent = new Intent(context,GalleryActivity.class);
                startActivity(intent);*/
                mDialogRowBoardList.dismiss();

                mBinding.imgEvent.setEnabled(true);
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

                                             mBinding.imgEvent.setEnabled(true);

                                         }
                                     }
        );

        txtsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(connectionDetector.isConnectingToInternet())
                {
                    GoogleSearchFragment fragment = new GoogleSearchFragment();
                    Bundle bundle = new Bundle();
                    if(mBinding.edtEventTitle.getText().toString().trim().length()>0)
                    {
                        bundle.putString("Event_name", mBinding.edtEventTitle.getText().toString());
                    }else {
                        bundle.putString("Event_name", mBinding.edtEventCategory1.getText().toString());
                    }                    fragment.setArguments(bundle);
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

                }else
                {
                    Toast.makeText(context,"No Internet Connection !",Toast.LENGTH_SHORT).show();
                }
                mDialogRowBoardList.dismiss();
                mBinding.imgEvent.setEnabled(true);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View v) {

                                          mDialogRowBoardList.dismiss();
                                          mBinding.imgEvent.setEnabled(true);
                                      }
                                  }

        );


        mDialogRowBoardList.show();
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
        }  else {
            return null;
        }

        return mediaFile;
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
                        ((DashBoardActivity)context).setCategoryId("");
                        ((DashBoardActivity)context).setCategoryName("");
                        ((DashBoardActivity)context).setCategoryColor("");
                        Pref.setValue(context,"updated","");
                        ((FragmentActivity)context).getSupportFragmentManager().popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
