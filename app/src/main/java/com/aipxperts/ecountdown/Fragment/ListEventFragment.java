package com.aipxperts.ecountdown.Fragment;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.Adapter.EventPagerAdapter;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.databinding.FragmentDashboardBinding;
import com.aipxperts.ecountdown.utils.ConnectionDetector;
import com.aipxperts.ecountdown.utils.Constants;
import com.aipxperts.ecountdown.utils.Pref;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

/**
 * Created by aipxperts-ubuntu-01 on 5/7/17.
 */

public class ListEventFragment extends Fragment {

    FragmentDashboardBinding mBinding;
    View rootView;
    Context context;
    private InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    Event[] events1;
    ConnectionDetector connectionDetector;
    ArrayList<Event> eventArrayList=new ArrayList<>();

    File storeDirectory;
    String get_file_path = null;
    int PICK_PDF_REQUEST = 100;
    boolean is_category_add=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_dashboard, container, false);
        rootView = mBinding.getRoot();
        context = getActivity();
        connectionDetector = new ConnectionDetector(context);
        preview();
        mInterstitialAd = new InterstitialAd(context);
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);

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

                Pref.setValue(context,"url","");
                CreateNewEventFragment fragment = new CreateNewEventFragment();
                //getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left).addToBackStack(null).commit();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

            }
        });
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                // intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select"), PICK_PDF_REQUEST);

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

    public void getdata( Uri selectedFileURI , File f) throws URISyntaxException {


        FileInputStream fi;
        try {
            fi = (FileInputStream)context. getContentResolver().openInputStream(selectedFileURI);

            ObjectInputStream oi = new ObjectInputStream(fi);
            final Event ev1 = (Event) oi.readObject();

            /**
             * this is for store encodedstring in file path
             */
            if (ev1.getEvent_image()!=null) {
                if (!ev1.getEvent_image().equalsIgnoreCase("")) {

                    storeDirectory = new File("/sdcard/Android/data/" + context.getPackageName() + "/");
                    if (!storeDirectory.exists()) {
                        storeDirectory.mkdirs();
                    }
                    String timeStamp = "/" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + ".jpg";

                    final File file = new File(storeDirectory + timeStamp);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Bitmap bitmap = decodeBase64(ev1.getEvent_image());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100 , bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    get_file_path = file.getAbsolutePath();
                }
            }
            /**
             * generate new share event database
             */
            boolean is_Match=false;
            Event event = realm.where(Event.class).equalTo("event_uuid", ev1.getEvent_uuid()).findFirst();
            if(event!=null) {
                is_Match = false;
              //  Constants.dismissProgress();

                Toast.makeText(context,"Event already imported.",Toast.LENGTH_SHORT).show();
                Log.e("is_Match", "" + is_Match);

            }else
            {
                is_Match = true;
               // importFile(is_Match,ev1,get_file_path);
                if(connectionDetector.isConnectingToInternet()) {
                    if (!Pref.getValue(context, "add_display", "").equalsIgnoreCase("0")) {


                        showInterstitial();
                        final boolean finalIs_Match = is_Match;
                        mInterstitialAd.setAdListener(new AdListener() {


                            @Override
                            public void onAdClosed() {
                                super.onAdClosed();

                                Constants.dismissProgress();
                                importFile(finalIs_Match,ev1,get_file_path);


                            }
                            @Override
                            public void onAdOpened() {
                                super.onAdOpened();
                            }
                        });

                    }else
                    {
                        importFile(is_Match,ev1,get_file_path);

                    }
                }else
                {
                    importFile(is_Match,ev1,get_file_path);

                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("test","111");
           //Constants.dismissProgress();

        } catch (StreamCorruptedException e)
        {
           // Constants.dismissProgress();

            Log.e("test","222");
            Toast.makeText(context,"Inappropriate file contain.",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
           // Constants.dismissProgress();

            Log.e("test","333");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
          //  Constants.dismissProgress();

            Log.e("test","444");
        }

    }
    public void importFile(boolean is_Match, final Event ev1, final String get_file_path)
    {

        if(is_Match) {

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts = tsLong.toString();
                    // Add a Category
                    Event event = realm.createObject(Event.class);

                    event.set_token(Pref.getValue(context, Constants.Token_tag, ""));
                    event.setEvent_uuid(ev1.getEvent_uuid());
                    event.setEvent_name(ev1.getEvent_name());
                    event.setEvent_description(ev1.getEvent_description());
                    event.setEvent_image(get_file_path);
                    event.setIs_cover("1");
                    event.setDate(ev1.getDate());
                    // event.setEnd_date(Constants.parseDateToAddDatabase(mBinding.edtEndsDate.getText().toString()));
                    event.setIsComplete("0");
                    if(ev1.getCategory().equalsIgnoreCase("Anniversary")||ev1.getCategory().equalsIgnoreCase("Birthday")||ev1.getCategory().equalsIgnoreCase("Holiday")
                            ||ev1.getCategory().equalsIgnoreCase("School")||ev1.getCategory().equalsIgnoreCase("Life")||ev1.getCategory().equalsIgnoreCase("Trip"))
                    {
                        event.setCategory(ev1.getCategory());
                        event.setCategoryColor(ev1.getCategoryColor());
                    }else
                    {
                        event.setCategory("General");
                        event.setCategoryColor("#911414");
                    }

                    event.setCreated_date(ts);
                    event.setModified_date(ts);
                    event.setOperation("1");
                }
            });


            Toast.makeText(context,"Event imported successfully.",Toast.LENGTH_SHORT).show();
        }

        //  file_delete.delete();
        mBinding.fab.setClickable(true);
//       ((FragmentActivity) context).getSupportFragmentManager().popBackStack();
        ListEventFragment fragment = new ListEventFragment();
        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commitAllowingStateLoss();



    }
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();

            //AddDatabase();
        }

    }
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                //return "/storage/extSdCard" + "/" + split[1];

                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        ((DashBoardActivity)context).slidingMenu.setSlidingEnabled(true);
        Pref.setValue(context,"last_open","list");

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
                        getActivity().finish();
                      /*  AlertDialog.Builder builder;

                        builder = new AlertDialog.Builder(context);

                        builder.setTitle("Alert")
                                .setMessage("Do you want to exit?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(((DashBoardActivity)context).slidingMenu.isMenuShowing()){
                                            ((DashBoardActivity)context).slidingMenu.toggle();
                                        }
                                        else{
                                            //  getActivity().getSupportFragmentManager().popBackStack();
                                            getActivity().finish();
                                        }
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })

                                .show();*/


                        return true;
                    }
                }
                return false;
            }
        });
    }
}
