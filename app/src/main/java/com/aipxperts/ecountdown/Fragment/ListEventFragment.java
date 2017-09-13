package com.aipxperts.ecountdown.Fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.Adapter.EventPagerAdapter;
import com.aipxperts.ecountdown.Adapter.ListEventAdapter;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.databinding.FragmentDashboardBinding;
import com.aipxperts.ecountdown.utils.Pref;
import com.daimajia.swipe.util.Attributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmResults;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 5/7/17.
 */

public class ListEventFragment extends Fragment {

    FragmentDashboardBinding mBinding;
    View rootView;
    Context context;
    Event[] events1;
    ArrayList<Event> eventArrayList=new ArrayList<>();
    int numOfDays;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_dashboard, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        preview();
        geteventsfromDb();

        final EventPagerAdapter adapter = new EventPagerAdapter
                (getChildFragmentManager(), 3);
        mBinding.viewPager1.setAdapter(adapter);

        if(Pref.getValue(context,"from_tab","").equalsIgnoreCase("recent"))
        {
            mBinding.viewPager1.setCurrentItem(0);

        }else if(Pref.getValue(context,"from_tab","").equalsIgnoreCase("past"))
        {
            mBinding.viewPager1.setCurrentItem(2);
        }else if(Pref.getValue(context,"from_tab","").equalsIgnoreCase("future"))
        {
            mBinding.viewPager1.setCurrentItem(1);
        }
        ((DashBoardActivity)context).mBinding.includeHeader.imgOptionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewEventFragment fragment = new CreateNewEventFragment();
                //getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left).addToBackStack(null).commit();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

            }
        });
        mBinding.viewPager1.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("position",""+position);
                if(position==0)
                {
                    mBinding.txtRecent.setTextColor(getResources().getColor(R.color.white));
                    mBinding.txtFuture.setTextColor(getResources().getColor(R.color.un_select_header));
                    mBinding.txtPast.setTextColor(getResources().getColor(R.color.un_select_header));

                }else if(position==1)
                {
                    mBinding.txtRecent.setTextColor(getResources().getColor(R.color.un_select_header));
                    mBinding.txtFuture.setTextColor(getResources().getColor(R.color.white));
                    mBinding.txtPast.setTextColor(getResources().getColor(R.color.un_select_header));

                }else if(position==2)
                {
                    mBinding.txtRecent.setTextColor(getResources().getColor(R.color.un_select_header));
                    mBinding.txtFuture.setTextColor(getResources().getColor(R.color.un_select_header));
                    mBinding.txtPast.setTextColor(getResources().getColor(R.color.white));

                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBinding.txtRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.viewPager1.setCurrentItem(0);
                /*ListEventRecentFragment fragment = new ListEventRecentFragment();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
*/

            }
        });
        mBinding.txtFuture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.viewPager1.setCurrentItem(1);
                /*
                ListEventFutureFragment fragment = new ListEventFutureFragment();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();*/

            }
        });
        mBinding.txtPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBinding.viewPager1.setCurrentItem(2);
                /*  ListEventPastFragment fragment = new ListEventPastFragment();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
*/

            }
        });


        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((DashBoardActivity)context).slidingMenu.setSlidingEnabled(true);

    }
    private void preview() {

        ((DashBoardActivity)context).mBinding.includeHeader.txtTitle.setText("Events");
        ((DashBoardActivity)context).mBinding.includeHeader.imgBack.setVisibility(View.GONE);
        ((DashBoardActivity)context).mBinding.includeHeader.imgDrawer.setVisibility(View.VISIBLE);
        ((DashBoardActivity)context).mBinding.includeHeader.txtDone.setVisibility(View.GONE);
        ((DashBoardActivity)context).mBinding.includeHeader.imgOptionMenu.setVisibility(View.VISIBLE);
        ((DashBoardActivity)context).mBinding.includeHeader.imgOptionMenu.setImageResource(R.mipmap.plus);

        if(Pref.getValue(context,"add_display","").equalsIgnoreCase("0"))
        {
            ((DashBoardActivity)context).mBinding.adView.setVisibility(View.GONE);

        }else
        {
            ((DashBoardActivity)context).mBinding.adView.setVisibility(View.VISIBLE);

        }
    }

    public void geteventsfromDb(){
        final RealmResults<Event> event = realm.where(Event.class).findAll();
        events1=new Event[event.size()];
        eventArrayList.clear();
        if(event.size()>0){

            for(int cat=0;cat<event.size();cat++)
            {

                events1[cat]=new Event();

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
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        if(((DashBoardActivity)context).slidingMenu.isMenuShowing()){
                            ((DashBoardActivity)context).slidingMenu.toggle();
                        }
                        else{
                            //  getActivity().getSupportFragmentManager().popBackStack();
                            getActivity().finish();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
