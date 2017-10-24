package com.aipxperts.ecountdown.Fragment;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.Widget.TextView_Regular;
import com.aipxperts.ecountdown.databinding.SettingLayoutBinding;
import com.aipxperts.ecountdown.utils.Pref;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.labo.kaji.fragmentanimations.MoveAnimation;

import java.util.ArrayList;

import io.realm.RealmResults;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 12/7/17.
 */

public class SettingFragment extends Fragment  implements BillingProcessor.IBillingHandler{

    SettingLayoutBinding mBinding;
    View rootView;
    Context context;
    String isnotify = "1";
    int id = 0;
    String text="";
    private PendingIntent pendingIntent;
    Event[] events1;
    String isUpDown="1";
    String isRemoveAds="0";
    String isRestoreAds="0";
    ArrayList<Event> eventArrayList = new ArrayList<>();

    public static BillingProcessor bp;
    public static boolean isPurchase = false;
    public  String KEY_REMOVE_ADDS ="com.aipxperts.ecountdown.removeads";
    //public  String KEY_REMOVE_ADDS ="android.test.purchased";

    int is_restore_purchase_click=0;
    int is_remove_click=0;
    int is_restore_click=0;
    int is_radiobutton=0;
    Dialog dialog=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.setting_layout, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        preview();
        //Pref.setValue(context, "is_notify", isnotify);

        bp = new BillingProcessor(getActivity(), null, this);
        bp.initialize();
        isPurchase = bp.isPurchased(KEY_REMOVE_ADDS);
        if(isPurchase) {

            Pref.setValue(context,"add_display","0");

        }else{
            Pref.setValue(context,"add_display","1");
        }

        mBinding.llRemove.setEnabled(true);
        mBinding.llRestore.setEnabled(true);
        if(!Pref.getValue(context,"isUpdown","").equalsIgnoreCase("")) {
            if(Pref.getValue(context,"isUpdown","").equalsIgnoreCase("1"))
            {

                mBinding.llRemove.setVisibility(View.VISIBLE);
                mBinding.llRestore.setVisibility(View.VISIBLE);
                mBinding.vRemove.setVisibility(View.VISIBLE);
                mBinding.vRestore.setVisibility(View.VISIBLE);
                isUpDown="1";
                mBinding.imgUpDown.setImageResource(R.mipmap.up_arrow);
                Pref.setValue(context,"isUpdown",isUpDown);
            }else  if(Pref.getValue(context,"isUpdown","").equalsIgnoreCase("0"))
            {

                mBinding.llRemove.setVisibility(View.GONE);
                mBinding.llRestore.setVisibility(View.GONE);
                mBinding.vRemove.setVisibility(View.GONE);
                mBinding.vRestore.setVisibility(View.GONE);
                isUpDown="0";
                mBinding.imgUpDown.setImageResource(R.mipmap.drop_down);
                Pref.setValue(context,"isUpdown",isUpDown);
            }

        }else
        {
            mBinding.llRemove.setVisibility(View.VISIBLE);
            mBinding.llRestore.setVisibility(View.VISIBLE);
            mBinding.vRemove.setVisibility(View.VISIBLE);
            mBinding.vRestore.setVisibility(View.VISIBLE);
            isUpDown="1";
            mBinding.imgUpDown.setImageResource(R.mipmap.up_arrow);
            Pref.setValue(context,"isUpdown",isUpDown);
        }
       /* if(!Pref.getValue(context, "is_remove_ads","").equalsIgnoreCase(""))
        {
            if(Pref.getValue(context, "is_remove_ads","").equalsIgnoreCase("1")) {
                mBinding.removeadSwitch.setClickable(false);
                mBinding.removeadSwitch.setBackgroundResource(R.mipmap.switch_on);
            }
        }
        if(!Pref.getValue(context, "is_restore_ads","").equalsIgnoreCase(""))
        {
            if(Pref.getValue(context, "is_restore_ads","").equalsIgnoreCase("1")) {
                mBinding.restoreSwitch.setClickable(false);
                mBinding.restoreSwitch.setBackgroundResource(R.mipmap.switch_on);
            }
        }*/
        if (Pref.getValue(context, "is_notify", "").equalsIgnoreCase("1")) {
            isnotify = "1";
            Pref.setValue(context, "event_day_before", 1);
            Pref.setValue(context, "is_notify", isnotify);
            mBinding.holidaySwitch.setBackgroundResource(R.mipmap.switch_on);
        } else if(Pref.getValue(context, "is_notify", "").equalsIgnoreCase("0")) {
            mBinding.holidaySwitch.setBackgroundResource(R.mipmap.switch_off);
            isnotify = "0";
            Pref.setValue(context, "is_notify", isnotify);
        }
        geteventsfromDb();

        /**
         * event notification switch
         */
        mBinding.holidaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Pref.setValue(context,"switch","1");


                if (Pref.getValue(context, "is_notify", "").equalsIgnoreCase("0")) {

                    dialog();
                    mBinding.holidaySwitch.setEnabled(false);
                } else {
                    mBinding.holidaySwitch.setBackgroundResource(R.mipmap.switch_off);
                    isnotify = "0";
                    is_radiobutton=0;
                    Pref.setValue(context, "is_notify", isnotify);

                    Pref.setValue(context, "event_day_before", 0);
                }
            }
        });
        mBinding.txtShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp();
            }
        });
        mBinding.txtContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL,new String[] {"aipxperts@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Support");
                intent.putExtra(Intent.EXTRA_TEXT, "");

                startActivity(Intent.createChooser(intent, "Contact Support"));
            }
        });
        /**
         * open or close Advertisement
         */
        mBinding.llUpDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isUpDown.equalsIgnoreCase("1"))
                {

                    mBinding.llRemove.setVisibility(View.GONE);
                    mBinding.llRestore.setVisibility(View.GONE);
                    mBinding.vRemove.setVisibility(View.GONE);
                    mBinding.vRestore.setVisibility(View.GONE);
                    mBinding.imgUpDown.setImageResource(R.mipmap.drop_down);
                    Pref.setValue(context,"isUpdown","0");
                    isUpDown="0";
                }else if(isUpDown.equalsIgnoreCase("0"))
                {
                    mBinding.llRemove.setVisibility(View.VISIBLE);
                    mBinding.llRestore.setVisibility(View.VISIBLE);
                    mBinding.vRemove.setVisibility(View.VISIBLE);
                    mBinding.vRestore.setVisibility(View.VISIBLE);
                    mBinding.imgUpDown.setImageResource(R.mipmap.up_arrow);
                    isUpDown="1";
                    Pref.setValue(context,"isUpdown","1");
                }
            }
        });
        /**
         *remove ads
         */
        mBinding.llRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_remove_click=1;
                mBinding.llRemove.setEnabled(false);
                if(bp.isPurchased(KEY_REMOVE_ADDS)) {
                    opendialog((getResources().getString(R.string.app_name)), "All Ads already removed!", true, true,"remove");
                }else
                {
                    opendialog((getResources().getString(R.string.app_name)), "Do you want to remove all Ads?", true, true,"remove");
                }
            }
        });
        mBinding.llRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_restore_click=1;

                mBinding.llRestore.setEnabled(false);

                Log.e("purchase_product", bp.getPurchaseListingDetails(KEY_REMOVE_ADDS) + "---" + bp.getPurchaseTransactionDetails(KEY_REMOVE_ADDS) + "--" + bp.loadOwnedPurchasesFromGoogle() + " " + bp.listOwnedProducts());

                if (bp.isPurchased(KEY_REMOVE_ADDS)) {

                    opendialog_restore((getResources().getString(R.string.app_name)), "Restored successfully!", true, true);

                } else {
                    opendialog((getResources().getString(R.string.app_name)), "First purchase then only you can restore it!", true, true,"restore");
                }

            }
        });

        return rootView;
    }
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

        return MoveAnimation.create(MoveAnimation.LEFT, enter, 500);

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
    private void dialog() {
        // custom dialog

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.radiobutton_dialog);
        dialog.setCancelable(false);
        TextView_Regular ok = (TextView_Regular) dialog.findViewById(R.id.ok);
        TextView_Regular cancel = (TextView_Regular) dialog.findViewById(R.id.cancel);


        final RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        if (Pref.getValue(context, "event_day_before", 0) == 1) {
            // id = 2131493099;
            rg.check(R.id.radio0);

        } else if (Pref.getValue(context, "event_day_before", 0) == 2) {
            //   id = 2131493100;

            rg.check(R.id.radio1);
        } else if (Pref.getValue(context, "event_day_before", 0) == 8) {
            //  id = 2131493101;

            rg.check(R.id.radio2);
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                RadioButton rb = (RadioButton) radioGroup.findViewById(i);
                if (null != rb && i > -1) {

                    //   Toast.makeText(context, rb.getText(), Toast.LENGTH_SHORT).show();

                    is_radiobutton=1;

                }
                id = rb.getId();
                text = rb.getText().toString();
                Log.e("id", "" + rb.getText());


            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pref.setValue(context, "event_day_before", 0);
                is_radiobutton=0;
                dialog.dismiss();
                mBinding.holidaySwitch.setEnabled(true);
            }
        });
        ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_radiobutton==0){
                    Toast.makeText(context, "Please select any one", Toast.LENGTH_LONG).show();
                } else {
                    if (text.equalsIgnoreCase("On the day")) {

                        Pref.setValue(context, "event_day_before", 1);

                    } else if (text.equalsIgnoreCase("One day before")) {

                        Pref.setValue(context, "event_day_before", 2);

                    } else if (text.equalsIgnoreCase("One week before")) {

                        Pref.setValue(context, "event_day_before", 8);

                    }

                    isnotify = "1";

                    Pref.setValue(context, "is_notify", isnotify);
                    mBinding.holidaySwitch.setBackgroundResource(R.mipmap.switch_on);
                    dialog.dismiss();
                    mBinding.holidaySwitch.setEnabled(true);
                }
            }
        });
        dialog.show();

    }

    private void preview() {
       /* ((DashBoardActivity) context).mBinding.footer.imgCalendar.setImageResource(R.mipmap.calendar_deselect);
        ((DashBoardActivity) context).mBinding.footer.imgCountDown.setImageResource(R.mipmap.countdown_deselect);
        ((DashBoardActivity) context).mBinding.footer.imgFb.setImageResource(R.mipmap.user_profile);
        ((DashBoardActivity) context).mBinding.footer.imgSettings.setImageResource(R.mipmap.setting_select);
      */
        ((DashBoardActivity) context).mBinding.includeHeader.txtTitle.setText("Settings");
        ((DashBoardActivity) context).mBinding.includeHeader.imgBack.setVisibility(View.GONE);
        ((DashBoardActivity) context).mBinding.includeHeader.imgDrawer.setVisibility(View.VISIBLE);
        ((DashBoardActivity) context).mBinding.includeHeader.txtDone.setVisibility(View.GONE);
        ((DashBoardActivity) context).mBinding.includeHeader.imgOptionMenu.setVisibility(View.GONE);

        if (Pref.getValue(context, "add_display", "").equalsIgnoreCase("0")) {
            ((DashBoardActivity) context).mBinding.adView.setVisibility(View.GONE);

        } else {
            ((DashBoardActivity) context).mBinding.adView.setVisibility(View.VISIBLE);

        }

    }

    /**
     * set alarm
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

                eventArrayList.add(events1[cat]);
            }
        }

    }
    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.e("0","product_purchased");
        if (productId.equals(KEY_REMOVE_ADDS)) {
            isPurchase = bp.isPurchased(KEY_REMOVE_ADDS);
        }
        if(isPurchase)
        {
            Pref.setValue(context,"add_display","0");
            ((DashBoardActivity) context).mBinding.adView.setVisibility(View.GONE);

        }else{
            Pref.setValue(context,"add_display","1");
            ((DashBoardActivity) context).mBinding.adView.setVisibility(View.VISIBLE);
        }
        is_restore_purchase_click=1;
        mBinding.llRestore.setEnabled(true);
        mBinding.llRemove.setEnabled(true);
    }



    @Override
    public void onPurchaseHistoryRestored() {
        Log.e("0","product_historyRestored");


        bp = new BillingProcessor(getActivity(), null, this);
        bp.initialize();
        isPurchase = bp.isPurchased(KEY_REMOVE_ADDS);
        if(isPurchase)
        {
            Toast.makeText(context,"Your purchase is restore automatically,because you already purchase.",Toast.LENGTH_LONG).show();
            Pref.setValue(context,"add_display","0");
            ((DashBoardActivity) context).mBinding.adView.setVisibility(View.GONE);
        }else{
            Pref.setValue(context,"add_display","1");
            ((DashBoardActivity) context).mBinding.adView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.e("0","billing error"+errorCode+error);
        //   Toast.makeText(context,"Please first add google account !",Toast.LENGTH_LONG).show();
        is_restore_purchase_click=0;
        is_remove_click=0;
        mBinding.llRestore.setEnabled(true);
        mBinding.llRemove.setEnabled(true);
    }

    @Override
    public void onBillingInitialized() {
        is_restore_purchase_click=0;
        mBinding.llRemove.setEnabled(true);
        mBinding.llRestore.setEnabled(true);
        Log.e("0","billing initialize");
    }
    public void opendialog(String title, String msg, boolean isVisiblePositive, boolean isVisibleNagative, final String from) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //Uncomment the below code to Set the message and title from the strings.xml file
        //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        if(is_remove_click==1) {
            mBinding.llRemove.setEnabled(false);
            mBinding.llRestore.setEnabled(true);
        }
        if(is_restore_click==1)
        {
            mBinding.llRemove.setEnabled(true);
            mBinding.llRestore.setEnabled(false);
        }
        //Setting message manually and performing action on button click
        builder.setMessage(msg).setCancelable(false);
        if (isVisiblePositive) {
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    bp.purchase(getActivity(), KEY_REMOVE_ADDS);
                    dialog.cancel();
                    mBinding.llRemove.setEnabled(true);
                    mBinding.llRestore.setEnabled(true);
                }
            });
        }
        if (isVisibleNagative) {
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //  Action for 'NO' Button
                    dialog.cancel();
                }
            });
        }
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(is_remove_click==1) {
                    mBinding.llRemove.setEnabled(true);
                    mBinding.llRestore.setEnabled(true);
                }
                if(is_restore_click==1)
                {
                    mBinding.llRemove.setEnabled(true);
                    mBinding.llRestore.setEnabled(true);
                }
            }
        });


        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(title);
        alert.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialog!=null)
        {
            dialog.dismiss();
        }

    }

    public void opendialog_restore(String title, String msg, boolean isVisiblePositive, boolean isVisibleNagative) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        mBinding.llRestore.setEnabled(true);
        builder.setMessage(msg)
                .setCancelable(false);
        if (isVisiblePositive) {
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    is_restore_purchase_click=0;
                    Log.e("check_purchase",""+bp.isPurchased(KEY_REMOVE_ADDS));
                    isPurchase = bp.isPurchased(KEY_REMOVE_ADDS);
                    if(isPurchase)
                    {
                        Pref.setValue(context,"add_display","0");
                    }else{
                        Pref.setValue(context,"add_display","1");
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                    dialog.cancel();
                }
            });
        }
        if (isVisibleNagative) {
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //  Action for 'NO' Button
                    is_restore_purchase_click=0;
                    dialog.cancel();
                }
            });
        }
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(title);
        alert.show();
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
                            FragmentManager fragmentManager=getActivity().getSupportFragmentManager();

                            for(int i=0;i<fragmentManager.getBackStackEntryCount();i++){
                                fragmentManager.popBackStack();
                            }

                            ListEventFragment fragment = new ListEventFragment();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
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
