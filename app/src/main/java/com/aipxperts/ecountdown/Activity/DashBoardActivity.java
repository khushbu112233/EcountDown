package com.aipxperts.ecountdown.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import com.aipxperts.ecountdown.Fragment.CreateNewEventFragment;
import com.aipxperts.ecountdown.Fragment.EditEventFragment;
import com.aipxperts.ecountdown.Fragment.ListEventFragment;
import com.aipxperts.ecountdown.Fragment.ProfileFragment;
import com.aipxperts.ecountdown.Fragment.SettingFragment;
import com.aipxperts.ecountdown.Fragment.SlidingMenuFragment;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.Model.UProfile;
import com.aipxperts.ecountdown.MyReceiver;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.databinding.DashboardLayoutBinding;
import com.aipxperts.ecountdown.utils.Pref;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by aipxperts-ubuntu-01 on 5/7/17.
 */

public class DashBoardActivity extends FragmentActivity {

    /**
     * Realm initialization.
     */
    public static Realm realm;
    public static RealmConfiguration config;
    public DashboardLayoutBinding mBinding;
    Event[] events1;
    Event[] events2;
    ArrayList<Event> eventArrayList = new ArrayList<>();
    ArrayList<Event> eventAlarmArrayList = new ArrayList<>();
    ArrayList<String> navDrawerItems;
    ArrayList<Integer> navDrawerItems_icons;

    private PendingIntent pendingIntent,pendingIntent1;
    IntentFilter s_intentFilter;
    MyReceiver myReceiver;
    int slide_pos_menu;
    /**
     * slide menu
     */
    public SlidingMenu slidingMenu;
    boolean is_alarm = false;

    public InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    public boolean isActivityIsVisible = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.dashboard_layout);
        StatusBar();
        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.adView.loadAd(adRequest);
        mBinding.adView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Addview", "test");
            }
        });
        config = new RealmConfiguration.Builder()
                .schemaVersion(1) // Must be bumped when the schema changes
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);

        geteventsfromDb();
        preview();
        setSlidingMenu_click();
        //myReceiver = new MyReceiver();
        //registerReceiver(myReceiver, new IntentFilter("android.intent.action.TIME_TICK"));
        mInterstitialAd = new InterstitialAd(DashBoardActivity.this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);



        mBinding.includeHeader.imgDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mBinding.drawerLayout.openDrawer(Gravity.LEFT);

                slidingMenu.toggle();

            }
        });


    }
    public void showInterstitial() {
        if (isActivityIsVisible) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }

    }
    public void NotifyEvent(String str_event,String from) {

        if(from.equalsIgnoreCase("New"))
        {
            geteventsfromDb();

        }else if(from.equalsIgnoreCase("Edit"))
        {
            geteventsfromDb1(str_event);

        }
        if (eventArrayList.size() > 0) {
            if (Pref.getValue(DashBoardActivity.this, "is_notify", "").equalsIgnoreCase("1")) {
                eventAlarmArrayList.clear();

                Date date1 = new Date();
                Date date_cur = null;
                DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                String str_cur = formatter.format(date1.getTime());
                // Log.e("yes_date",""+yes_date);

                Date yes_date = null;
                Date week_date = null;

                try {
                    date_cur = formatter.parse(str_cur);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                yes_date = new Date((date_cur.getTime()) + (60 * 60 * 24));
                week_date = new Date((date_cur.getTime()) + (1000 * 60 * 60 * 24 * 7));
             /*   Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
*/
                events2 = new Event[eventArrayList.size()];
                for (int alarm = 0; alarm < eventArrayList.size(); alarm++) {
                    Date date = null;
                    try {

                        date = (Date) formatter.parse(eventArrayList.get(alarm).getDate());
                        Log.e("date111", "" + eventArrayList.get(alarm).getDate());

                        /**
                         * identify notification when notification set on the day first block access , on the day before access second bolck and on the week before aceess third block
                         */
                        if (Pref.getValue(DashBoardActivity.this, "event_day_before", 0) == 1) {
                            //   eventAlarmArrayList.clear();
                            if (date_cur.compareTo(date) <= 0) {

                                events2[alarm] = new Event();
                                events2[alarm].set_token(eventArrayList.get(alarm).get_token());
                                events2[alarm].setEvent_uuid(eventArrayList.get(alarm).getEvent_uuid());
                                events2[alarm].setEvent_name(eventArrayList.get(alarm).getEvent_name());
                                events2[alarm].setEvent_description(eventArrayList.get(alarm).getEvent_description());
                                events2[alarm].setEvent_image(eventArrayList.get(alarm).getEvent_image());
                                events2[alarm].setIs_cover(eventArrayList.get(alarm).getIs_cover());
                                events2[alarm].setDate(eventArrayList.get(alarm).getDate());
                                events2[alarm].setIsComplete(eventArrayList.get(alarm).getIsComplete());
                                events2[alarm].setCreated_date(eventArrayList.get(alarm).getCreated_date());
                                events2[alarm].setModified_date(eventArrayList.get(alarm).getModified_date());
                                events2[alarm].setOperation(eventArrayList.get(alarm).getOperation());
                                events2[alarm].setCategory(eventArrayList.get(alarm).getCategory());
                                events2[alarm].setCategoryColor(eventArrayList.get(alarm).getCategoryColor());
                                eventAlarmArrayList.add(events2[alarm]);
                            }
                        } else if (Pref.getValue(DashBoardActivity.this, "event_day_before", 0) == 2) {
                            //eventAlarmArrayList.clear();
                            if (yes_date.compareTo(date) <= 0) {

                                events2[alarm] = new Event();
                                events2[alarm].set_token(eventArrayList.get(alarm).get_token());
                                events2[alarm].setEvent_uuid(eventArrayList.get(alarm).getEvent_uuid());
                                events2[alarm].setEvent_name(eventArrayList.get(alarm).getEvent_name());
                                events2[alarm].setEvent_description(eventArrayList.get(alarm).getEvent_description());
                                events2[alarm].setEvent_image(eventArrayList.get(alarm).getEvent_image());
                                events2[alarm].setIs_cover(eventArrayList.get(alarm).getIs_cover());
                                events2[alarm].setDate(eventArrayList.get(alarm).getDate());
                                events2[alarm].setIsComplete(eventArrayList.get(alarm).getIsComplete());
                                events2[alarm].setCreated_date(eventArrayList.get(alarm).getCreated_date());
                                events2[alarm].setModified_date(eventArrayList.get(alarm).getModified_date());
                                events2[alarm].setOperation(eventArrayList.get(alarm).getOperation());
                                events2[alarm].setCategory(eventArrayList.get(alarm).getCategory());
                                events2[alarm].setCategoryColor(eventArrayList.get(alarm).getCategoryColor());
                                eventAlarmArrayList.add(events2[alarm]);
                            }
                        } else if (Pref.getValue(DashBoardActivity.this, "event_day_before", 0) == 8) {
                            //  eventAlarmArrayList.clear();
                            if (week_date.compareTo(date) <=0) {

                                events2[alarm] = new Event();

                                events2[alarm].set_token(eventArrayList.get(alarm).get_token());
                                events2[alarm].setEvent_uuid(eventArrayList.get(alarm).getEvent_uuid());
                                events2[alarm].setEvent_name(eventArrayList.get(alarm).getEvent_name());
                                events2[alarm].setEvent_description(eventArrayList.get(alarm).getEvent_description());
                                events2[alarm].setEvent_image(eventArrayList.get(alarm).getEvent_image());
                                events2[alarm].setIs_cover(eventArrayList.get(alarm).getIs_cover());
                                events2[alarm].setDate(eventArrayList.get(alarm).getDate());
                                events2[alarm].setIsComplete(eventArrayList.get(alarm).getIsComplete());
                                events2[alarm].setCreated_date(eventArrayList.get(alarm).getCreated_date());
                                events2[alarm].setModified_date(eventArrayList.get(alarm).getModified_date());
                                events2[alarm].setOperation(eventArrayList.get(alarm).getOperation());
                                events2[alarm].setCategory(eventArrayList.get(alarm).getCategory());
                                events2[alarm].setCategoryColor(eventArrayList.get(alarm).getCategoryColor());
                                eventAlarmArrayList.add(events2[alarm]);
                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }
                if (eventAlarmArrayList.size() > 0) {
                    startAlarm(Pref.getValue(DashBoardActivity.this, "event_day_before", 0));
                }
            }
        }
    }

    private void preview() {
        mBinding.includeHeader.imgOptionMenu.setVisibility(View.VISIBLE);
        if (Pref.getValue(DashBoardActivity.this, "add_display", "").equalsIgnoreCase("0")) {
            mBinding.adView.setVisibility(View.GONE);
        } else {
            mBinding.adView.setVisibility(View.VISIBLE);
        }
        //  if (!Pref.getValue(DashBoardActivity.this, "cur_fragment", "").equalsIgnoreCase("1")) {

        ListEventFragment fragment = new ListEventFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
        //  Pref.setValue(DashBoardActivity.this, "cur_fragment", "1");

        //}
    }

    /**
     * set for drawer
     */
    private void setSlidingMenu_click() {
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);

//        slidingMenu.setShadowDrawable(R.drawable.shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.sliding_menu);
        slidingMenu.setSlidingEnabled(true);

    }

    public void slidingMenuClicked(int position) {

        //callActivity(RecyclerViewExample.class);

        slide_pos_menu = position;
        if(position==0)
        {
            if (!Pref.getValue(DashBoardActivity.this, "drawer_value", "").equals("0")) {
                if (Pref.getValue(DashBoardActivity.this, "add_display", "").equalsIgnoreCase("0")) {
                    mBinding.adView.setVisibility(View.VISIBLE);
                } else {
                    mBinding.adView.setVisibility(View.GONE);
                }
                ListEventFragment fragment = new ListEventFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

            }

        }else if (position == 1) {
            if (!Pref.getValue(DashBoardActivity.this, "drawer_value", "").equals("1")) {
                if (Pref.getValue(DashBoardActivity.this, "add_display", "").equalsIgnoreCase("1")) {
                    mBinding.adView.setVisibility(View.VISIBLE);
                } else {
                    mBinding.adView.setVisibility(View.GONE);
                }
                SettingFragment fragment = new SettingFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

            }

        }else if (position == 2) {

            shareApp();


        } else if (position == 3) {

            rateApp();
        }

    }

    public void slidingMenuToggle() {
        slidingMenu.toggle();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        config = new RealmConfiguration.Builder()
                .schemaVersion(1) // Must be bumped when the schema changes
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);
        geteventsfromDb();
        mInterstitialAd = new InterstitialAd(DashBoardActivity.this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        //registerReceiver(myReceiver, new IntentFilter("android.intent.action.TIME_TICK"));
    }

    /**
     * share app
     */
    public void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String sAux = "https://play.google.com/store/apps/details?id=com.aipxperts.ecountdown \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    /*
      * Start with rating the app
      * Determine if the Play Store is installed on the device
      *
      * */
    public void rateApp() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, "com.aipxperts.ecountdown")));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    public void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);

        Log.e("3333", "" + requestCode);

        if(requestCode==200&&resultCode==RESULT_OK)
        {
            if(getCurrentFragment() instanceof  CreateNewEventFragment)
            {
                ((CreateNewEventFragment) getCurrentFragment()).onActivity(data);
            }else if(getCurrentFragment() instanceof EditEventFragment)
            {
                ((EditEventFragment) getCurrentFragment()).onActivity(data);
            }

        }else if(requestCode==201&&resultCode==RESULT_OK)
        {
            if(getCurrentFragment() instanceof  CreateNewEventFragment) {
                ((CreateNewEventFragment) getCurrentFragment()).onActivityGallery(data);
            }else if(getCurrentFragment() instanceof EditEventFragment)
            {
                ((EditEventFragment) getCurrentFragment()).onActivityGallery(data);
            }
        }
        else if (requestCode==32459&&resultCode == RESULT_OK) {

            if (!SettingFragment.bp.handleActivityResult(requestCode, resultCode, data)) {

                super.onActivityResult(requestCode, resultCode, data);
                Log.e("requestcode", "" + requestCode);
            }
        }
    }

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
    public void geteventsfromDb1(String str) {
        final RealmResults<Event> event = realm.where(Event.class).findAll();
        events1 = new Event[event.size()];
        eventArrayList.clear();
        if (event.size() > 0) {

            for (int cat = 0; cat < event.size(); cat++) {
                if (event.get(cat).getEvent_uuid().equalsIgnoreCase(str))
                {
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
    /**
     * set alarm
     */
    public void startAlarm(int interval) {

        /*myReceiver = new MyReceiver();

        registerReceiver(myReceiver, new IntentFilter("android.intent.action.TIME_SET"));
        */// 3-6-2017
        // 12-6-2017

        Calendar calendar,calendar1;


        for (int i = 0; i < eventAlarmArrayList.size(); i++) {
            String str;
            Log.e("123", "list size : " + eventAlarmArrayList.size());
          //  str = eventAlarmArrayList.get(i).getDate();
            if(interval==1)
            {
                str = getCalculatedDate(eventAlarmArrayList.get(i).getDate(), "MM/dd/yyyy", 0);
            }else {
                 str = getCalculatedDate(eventAlarmArrayList.get(i).getDate(), "MM/dd/yyyy", -(interval - 1));
            }
            String[] out = str.split("/");
            Log.e("str_alarm", "" + str);
            int min = 56;
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("MM/dd/yyyy").format(cDate);

            calendar = Calendar.getInstance();

            calendar.set(Calendar.MONTH, Integer.parseInt(out[0])-1);
            calendar.set(Calendar.YEAR, Integer.parseInt(out[2]));
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(out[1])-interval+1);
            calendar.set(Calendar.HOUR_OF_DAY,11);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.AM_PM, Calendar.AM);

               /* calendar.set(Calendar.MONTH, Integer.parseInt(out[0]) - 1);
                calendar.set(Calendar.YEAR, Integer.parseInt(out[2]));
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(out[1]) - interval + 1);
                calendar.set(Calendar.HOUR_OF_DAY, 1);
                calendar.set(Calendar.MINUTE, 2);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.AM_PM, Calendar.PM);
            */    Log.e("str_alarm1", "" + Integer.parseInt(out[0]) + "   " + Integer.parseInt(out[1]) + "   " + Integer.parseInt(out[2]));


            long when = calendar.getTimeInMillis();

            Log.e("when", "" + when+"  "+System.currentTimeMillis());
            // notification time
            Intent myIntent = new Intent(DashBoardActivity.this, MyReceiver.class);
            myIntent.putExtra("Event_name", eventAlarmArrayList.get(i).getEvent_name());
            myIntent.putExtra("Event_des", eventAlarmArrayList.get(i).getEvent_description());
            // myIntent.putExtra("Event_image", eventArrayList.get(i).getEvent_image());
            //sendBroadcast(myIntent);


            pendingIntent = PendingIntent.getBroadcast(DashBoardActivity.this, i, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            // pendingIntent.cancel();

            // this.registerReceiver(myReceiver, new IntentFilter("android.intent.action.TIME_TICK"));
          /*  calendar1 = Calendar.getInstance();

            calendar1.set(Calendar.MONTH, Integer.parseInt(out[0]) - 1);
            calendar1.set(Calendar.YEAR, Integer.parseInt(out[2]));
            calendar1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(out[1]) - interval+1);
            calendar1.set(Calendar.HOUR_OF_DAY, 12);
            calendar1.set(Calendar.MINUTE, 46);
            calendar1.set(Calendar.SECOND, 0);
            calendar1.set(Calendar.AM_PM, Calendar.PM);

            Log.e("str_alarm1", "" + Integer.parseInt(out[0]) + "   " + Integer.parseInt(out[1]) + "   " + Integer.parseInt(out[2]));


            long when1 = calendar1.getTimeInMillis();

            Log.e("when", "" + when1);
            // notification time
            Intent myIntent1 = new Intent(DashBoardActivity.this, MyReceiver.class);
            myIntent1.putExtra("Event_name", eventAlarmArrayList.get(i).getEvent_name());
            myIntent1.putExtra("Event_des", eventAlarmArrayList.get(i).getEvent_description());
            // myIntent.putExtra("Event_image", eventArrayList.get(i).getEvent_image());
            //  sendBroadcast(myIntent);


            pendingIntent1 = PendingIntent.getBroadcast(DashBoardActivity.this, i, myIntent1, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager1.setExact(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);
*/

        }
    }
    public static String getCalculatedDate(String date, String dateFormat, int days) {
        Calendar cal = Calendar.getInstance();
        String str="";
        SimpleDateFormat s = new SimpleDateFormat(dateFormat);
        cal.add(Calendar.DAY_OF_YEAR, days);
        try {
            str=s.format(new Date(s.parse(date).getTime()));
            Log.e("str",""+str);
            return s.format(new Date(s.parse(date).getTime()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            Log.e("TAG", "Error in Parsing Date : " + e.getMessage());
        }
        return str;
    }
    @Override
    protected void onPause() {
        super.onPause();
        //  unregisterReceiver(myReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  unregisterMyreceiver();
    }

    public Fragment getCurrentFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_main_container);
        //    Log.e("currentFragment",""+currentFragment);
        return currentFragment;

    }

}
