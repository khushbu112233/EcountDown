package com.aipxperts.ecountdown.Fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.Adapter.NavDrawerListAdapter;
import com.aipxperts.ecountdown.Model.UProfile;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.databinding.SlidingMenuListBinding;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import java.util.ArrayList;

import io.realm.RealmResults;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 17/7/17.
 */

public class SlidingMenuFragment extends Fragment {
    SlidingMenuListBinding mBinding;
    View rootView;
    Context context;
    ArrayList<String> navDrawerItems;
    ArrayList<Integer> navDrawerItems_icons;
    String Gender="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.sliding_menu_list, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        setprofile();
        setdrawer();

        mBinding.relProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment fragment = new ProfileFragment();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();
                ((DashBoardActivity)context).slidingMenu.toggle();
            }
        });
        ((DashBoardActivity)context).slidingMenu.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                setprofile();
            }
        });
        return rootView;
    }
    public void setdrawer()
    {


        navDrawerItems = new ArrayList<String>();
        navDrawerItems.add("Events");
        navDrawerItems.add("Settings");
        //   navDrawerItems.add("Remove ads");
        navDrawerItems.add("Share App");
        navDrawerItems.add("Rate us");
       // navDrawerItems.add("Shared Event");
        navDrawerItems_icons = new ArrayList<Integer>();
        navDrawerItems_icons.add(R.mipmap.home);
        navDrawerItems_icons.add(R.mipmap.settings);
        //  navDrawerItems_icons.add(R.mipmap.remove_adds);
        navDrawerItems_icons.add(R.mipmap.share_app);
        navDrawerItems_icons.add(R.mipmap.rate_us);
       // navDrawerItems_icons.add(R.mipmap.share_event_icon);
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(context,navDrawerItems,navDrawerItems_icons);
        mBinding.listSlidermenu.setAdapter(adapter);
        //  mBinding.listSlidermenu.setOnItemClickListener(new DashBoardActivity.SlideMenuClickListener());

    }
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

        return MoveAnimation.create(MoveAnimation.LEFT, enter, 500);

    }
    public void setprofile()
    {
        final RealmResults<UProfile> profile = realm.where(UProfile.class).findAll();
        if(profile.size()>0) {
            Log.e("profile", "" + profile.get(0).getAge() + "  " + profile.get(0).getU_Gender());
            Gender =profile.get(0).getU_Gender() ;
        }
        if(Gender.equalsIgnoreCase("Female"))
        {
            mBinding.mProfileImg.setImageResource(R.mipmap.profile_female);
        }else if(Gender.equalsIgnoreCase("Male"))
        {
            mBinding.mProfileImg.setImageResource(R.mipmap.male_profile);
        }
    }
}
