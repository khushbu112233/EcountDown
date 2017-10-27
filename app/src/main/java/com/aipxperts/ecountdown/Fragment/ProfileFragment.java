package com.aipxperts.ecountdown.Fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.Model.UProfile;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.databinding.ProfileLayoutBinding;
import com.aipxperts.ecountdown.utils.Pref;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 10/7/17.
 */

public class ProfileFragment extends Fragment {
    ProfileLayoutBinding mBinding;
    View rootView;
    Context context;
    String Gender = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.profile_layout, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();

        preview();

        final RealmResults<UProfile> profile = realm.where(UProfile.class).findAll();
        if (profile.size() > 0) {
            Log.e("profile", "" + profile.get(0).getAge() + "  " + profile.get(0).getU_Gender());
            Gender = profile.get(0).getU_Gender();
            mBinding.edtAge.setText(profile.get(0).getAge());
        }
        if (Gender.equalsIgnoreCase("")) {
            mBinding.mProfileImg.setImageResource(R.mipmap.profile_female);
            mBinding.mFemale.setImageResource(R.mipmap.female_select);
            mBinding.mMale.setImageResource(R.mipmap.male_unselect);
            Gender = "Female";

            mBinding.txtFemale.setTextColor(getResources().getColor(R.color.select));
            mBinding.txtMale.setTextColor(getResources().getColor(R.color.un_Select));
        } else if (Gender.equalsIgnoreCase("Female")) {
            mBinding.mProfileImg.setImageResource(R.mipmap.profile_female);
            mBinding.mFemale.setImageResource(R.mipmap.female_select);
            mBinding.mMale.setImageResource(R.mipmap.male_unselect);
            Gender = "Female";
            mBinding.txtFemale.setTextColor(getResources().getColor(R.color.select));
            mBinding.txtMale.setTextColor(getResources().getColor(R.color.un_Select));
        } else if (Gender.equalsIgnoreCase("Male")) {
            mBinding.mProfileImg.setImageResource(R.mipmap.male_profile);
            mBinding.mFemale.setImageResource(R.mipmap.female_unselect);
            mBinding.mMale.setImageResource(R.mipmap.male_select);
            Gender = "Male";
            mBinding.txtFemale.setTextColor(getResources().getColor(R.color.un_Select));
            mBinding.txtMale.setTextColor(getResources().getColor(R.color.select));
        }
        mBinding.mFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.mProfileImg.setImageResource(R.mipmap.profile_female);
                mBinding.mFemale.setImageResource(R.mipmap.female_select);
                mBinding.mMale.setImageResource(R.mipmap.male_unselect);
                mBinding.txtFemale.setTextColor(getResources().getColor(R.color.select));
                mBinding.txtMale.setTextColor(getResources().getColor(R.color.un_Select));

                Gender = "Female";
            }
        });

        mBinding.mMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.mProfileImg.setImageResource(R.mipmap.male_profile);
                mBinding.mFemale.setImageResource(R.mipmap.female_unselect);
                mBinding.mMale.setImageResource(R.mipmap.male_select);
                Gender = "Male";
                mBinding.txtFemale.setTextColor(getResources().getColor(R.color.un_Select));
                mBinding.txtMale.setTextColor(getResources().getColor(R.color.select));
            }
        });
        mBinding.txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int err_cnt = 0;
                if (mBinding.edtAge.getText().toString().trim().equalsIgnoreCase("")) {
                    err_cnt++;
                    Toast.makeText(context, getString(R.string.age_require), Toast.LENGTH_LONG).show();
                } else if (mBinding.edtAge.getText().toString().trim().equalsIgnoreCase("0")) {
                    err_cnt++;
                    Toast.makeText(context, getString(R.string.age_greater_than_zero), Toast.LENGTH_LONG).show();
                }

                if (err_cnt == 0) {
                    if (profile.size() > 0) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<UProfile> profile = realm.where(UProfile.class).findAll();
                                profile.deleteAllFromRealm();
                            }
                        });
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                UProfile proflie = realm.createObject(UProfile.class);

                                proflie.setU_Gender(Gender);
                                proflie.setAge(mBinding.edtAge.getText().toString());
                                //    getActivity().getSupportFragmentManager().popBackStack();
                            }
                        });
                    } else {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                UProfile proflie = realm.createObject(UProfile.class);

                                proflie.setU_Gender(Gender);
                                proflie.setAge(mBinding.edtAge.getText().toString());

                            }
                        });
                    }
                    ListEventFragment fragment = new ListEventFragment();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();
                    // Pref.setValue(context, "cur_fragment", "1");
                    Toast.makeText(context, "Profile updated successful.", Toast.LENGTH_LONG).show();

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }

            }
        });
        return rootView;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

        return MoveAnimation.create(MoveAnimation.LEFT, enter, 500);

    }

    private void preview() {

      /*  ((DashBoardActivity)context).mBinding.footer.imgCalendar.setImageResource(R.mipmap.calendar_deselect);
        ((DashBoardActivity)context).mBinding.footer.imgCountDown.setImageResource(R.mipmap.countdown_deselect);
        ((DashBoardActivity)context).mBinding.footer.imgFb.setImageResource(R.mipmap.user_profile_select);
        ((DashBoardActivity)context).mBinding.footer.imgSettings.setImageResource(R.mipmap.setting_deselect);
      */
        ((DashBoardActivity) context).mBinding.includeHeader.txtTitle.setText("Profile");
        ((DashBoardActivity) context).mBinding.includeHeader.imgBack.setVisibility(View.GONE);
        ((DashBoardActivity) context).mBinding.includeHeader.imgDrawer.setVisibility(View.VISIBLE);
        ((DashBoardActivity) context).mBinding.includeHeader.txtDone.setVisibility(View.GONE);
        //  ((DashBoardActivity)context).mBinding.footer.llFooter.setVisibility(View.VISIBLE);
        ((DashBoardActivity) context).mBinding.includeHeader.imgOptionMenu.setVisibility(View.GONE);

        if (Pref.getValue(context, "add_display", "").equalsIgnoreCase("0")) {
            ((DashBoardActivity) context).mBinding.adView.setVisibility(View.GONE);

        } else {
            ((DashBoardActivity) context).mBinding.adView.setVisibility(View.VISIBLE);

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

                        if (((DashBoardActivity) context).slidingMenu.isMenuShowing()) {
                            ((DashBoardActivity) context).slidingMenu.toggle();
                        } else {
                            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

                            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                                fragmentManager.popBackStack();
                            }

                            ListEventFragment fragment = new ListEventFragment();
                            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.frame_main_container, fragment).commit();
                            //getActivity().getSupportFragmentManager().popBackStack();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
