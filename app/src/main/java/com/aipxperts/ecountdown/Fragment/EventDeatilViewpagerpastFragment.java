package com.aipxperts.ecountdown.Fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.Adapter.CardPagerAdapter;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.databinding.EventDetailPagerLayoutBinding;
import com.aipxperts.ecountdown.utils.Pref;
import com.aipxperts.ecountdown.utils.ShadowTransformer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmResults;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 24/7/17.
 */

public class EventDeatilViewpagerpastFragment extends Fragment {
    EventDetailPagerLayoutBinding mBinding;
    View rootView;
    Context context;
    Event[] events1;
    ArrayList<Event> eventArrayList = new ArrayList<>();
    ArrayList<Event> eventArrayList_past=new ArrayList<>();
    int numOfDays,numOfDays1;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.event_detail_pager_layout, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        preview();
        geteventsfromDb();
        Pref.setValue(context,"from_tab","past");
        eventArrayList_past.clear();
        //mFragmentCardAdapter = new CardFragmentPagerAdapter(getChildFragmentManager(), dpToPixels(2, context),eventArrayList);


        //mFragmentCardShadowTransformer = new ShadowTransformer(mBinding.viewPager, mFragmentCardAdapter);
        for(int i=0;i<eventArrayList.size();i++) {
          //  String str_end_date = eventArrayList.get(i).getEnd_date();
            String str_date = eventArrayList.get(i).getDate();
            Log.e("str_date", "" + str_date);
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");


            Date date = null,date_end=null;

            Date date_cur = null;
            try {
                date = (Date) formatter.parse(str_date);
            //    date_end = (Date) formatter.parse(str_end_date);
                //    Log.e("str_date",""+timeAgo.timeAgo(date));


                Date date1 = new Date();
                String str_cur = formatter.format(date1.getTime());

                date_cur = formatter.parse(str_cur);
                Long diff = (date_cur.getTime()) - (date.getTime());

               // Long diff1 = (date_cur.getTime()) - (date_end.getTime());

                numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
              //  numOfDays1 = (int) (diff1 / (1000 * 60 * 60 * 24));
                Log.e("numOfDaysa", "" + numOfDays);

                if (numOfDays > 0) {
                    eventArrayList_past.add(eventArrayList.get(i));

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //mCardAdapter.addCardItem(eventArrayList.get(i).getEvent_image(),eventArrayList.get(i).getEvent_name(),eventArrayList.get(i).getDate(),eventArrayList.get(i).getEvent_uuid());
        }


            mCardAdapter = new CardPagerAdapter(context,eventArrayList_past);

            for (int j = 0; j < eventArrayList_past.size(); j++) {
                mCardAdapter.addCardItem(eventArrayList_past.get(j).getEvent_image());
            }




        mCardShadowTransformer = new ShadowTransformer(mBinding.viewPager, mCardAdapter);
        mBinding.viewPager.setAdapter(mCardAdapter);
        mBinding.viewPager.setPageTransformer(false, mCardShadowTransformer);
        mBinding.viewPager.setOffscreenPageLimit(3);
        mBinding.viewPager.setCurrentItem(Pref.getValue(context,"detail_position",0));
        mCardShadowTransformer.enableScaling(true);

//        mFragmentCardShadowTransformer.enableScaling(true);

        mBinding.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                ((DashBoardActivity)context).mBinding.includeHeader.txtTitle.setText(eventArrayList_past.get(position).getEvent_name());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((DashBoardActivity)context).slidingMenu.setSlidingEnabled(false);
    }
    private void preview() {

        ((DashBoardActivity)context).mBinding.includeHeader.imgBack.setVisibility(View.VISIBLE);
        ((DashBoardActivity)context).mBinding.includeHeader.imgDrawer.setVisibility(View.GONE);
        ((DashBoardActivity)context).mBinding.includeHeader.txtDone.setVisibility(View.GONE);
        ((DashBoardActivity)context).mBinding.includeHeader.imgOptionMenu.setVisibility(View.GONE);
        ((DashBoardActivity) context).mBinding.includeHeader.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
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
    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }
}
