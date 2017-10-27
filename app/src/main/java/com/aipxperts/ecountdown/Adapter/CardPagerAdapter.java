package com.aipxperts.ecountdown.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aipxperts.ecountdown.Fragment.EditEventFragment;
import com.aipxperts.ecountdown.Fragment.EventDeatilViewpagerFragment;
import com.aipxperts.ecountdown.Fragment.EventDeatilViewpagerFutureFragment;
import com.aipxperts.ecountdown.Fragment.EventDeatilViewpagerpastFragment;
import com.aipxperts.ecountdown.Fragment.ListEventFragment;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.Widget.TextView_Regular;
import com.aipxperts.ecountdown.utils.Constants;
import com.aipxperts.ecountdown.utils.Pref;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

import static com.aipxperts.ecountdown.Activity.DashBoardActivity.realm;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private float mBaseElevation;
    ArrayList<Event> eventArrayList;
    String mDataImage, mDataName, mDataDate, mDataId;
    Context context;
    String str_share;
    static final int READ_BLOCK_SIZE = 100;
    String from;

    public CardPagerAdapter(Context context, ArrayList<Event> eventArrayList,String from) {
        this.context = context;
        this.eventArrayList = eventArrayList;
        this.from=from;
        mViews = new ArrayList<>();
    }
    public void addCardItem(String item) {
        mViews.add(null);
        mDataImage = item;
    }



    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return eventArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.pager_adapter_layout, container, false);
        container.addView(view);
        bind(position, view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }


        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(final int item, View view) {


        final ImageView img = (ImageView) view.findViewById(R.id.img1);
        final LinearLayout ll_option_menu = (LinearLayout) view.findViewById(R.id.ll_option_menu);
        final LinearLayout ll_edit = (LinearLayout) view.findViewById(R.id.ll_edit);
        final LinearLayout ll_share = (LinearLayout) view.findViewById(R.id.ll_share);
        final LinearLayout ll_delete = (LinearLayout) view.findViewById(R.id.ll_delete);
        View v1 = (View)view.findViewById(R.id.v1);
        final ImageView img_option_menu = (ImageView) view.findViewById(R.id.img_option_menu);
        TextView_Regular txt_date = (TextView_Regular) view.findViewById(R.id.txt_date);
        TextView_Regular txt_ecountdown = (TextView_Regular) view.findViewById(R.id.txt_ecountdown);
        CardView  cardView= (CardView)view.findViewById(R.id.cardView);
        if(eventArrayList.get(item).getEvent_image()!=null)
        {
            if(!eventArrayList.get(item).getEvent_image().equalsIgnoreCase(""))
            {
                Glide.with(context)
                        .load(eventArrayList.get(item).getEvent_image())
                        .centerCrop()
                        .into(img);


            }else
            {

                v1.setBackgroundColor(Color.parseColor(eventArrayList.get(item).getCategoryColor()));
            }

        }else{
            v1.setBackgroundColor(Color.parseColor(eventArrayList.get(item).getCategoryColor()));
        }


        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ll_option_menu.setVisibility(View.GONE);
                img_option_menu.setVisibility(View.VISIBLE);
                return false;
            }
        });



        final String str_date = eventArrayList.get(item).getDate();
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");


        Date date = null;
        Date date_cur = null;
        try {
            date = (Date) formatter.parse(str_date);
            Date date1 = new Date();
            String str_cur = formatter.format(date1.getTime());

            date_cur = formatter.parse(str_cur);
            Long diff = (date_cur.getTime()) - (date.getTime());
            int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
            if (numOfDays < 0 ) {
                txt_date.setVisibility(View.VISIBLE);
                txt_date.setText(Constants.parseDateDatabaseToDisplay1(eventArrayList.get(item).getDate()));
                if (Math.abs(numOfDays) == 1) {
                    txt_ecountdown.setText(Math.abs(numOfDays) + " Day After");
                } else {
                    txt_ecountdown.setText(Math.abs(numOfDays) + " Days After");
                }

            } else if (numOfDays > 0) {
                txt_date.setVisibility(View.VISIBLE);
                txt_date.setText(Constants.parseDateDatabaseToDisplay1(eventArrayList.get(item).getDate()));
                if (numOfDays == 1) {
                    txt_ecountdown.setText(numOfDays + " Day Before");
                } else {
                    txt_ecountdown.setText(numOfDays + " Days Before");
                }

            } else if (numOfDays== 0) {

                txt_date.setVisibility(View.GONE);
                txt_ecountdown.setText("Today");

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        img_option_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_option_menu.setVisibility(View.VISIBLE);
                img_option_menu.setVisibility(View.GONE);
            }
        });


        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_share.setClickable(false);
                str_share = "";
                Constants.showProgress(context);


/*
                str_share = str_share + "Event Title:" + eventArrayList.get(item).getEvent_name() + "\n";
                str_share = str_share + "Event Description:" + eventArrayList.get(item).getEvent_description() + "\n";
                str_share = str_share + "Event Date:" + eventArrayList.get(item).getDate() + "\n";
                str_share = str_share + "Event Category:" +eventArrayList.get(item).getCategory() +"\n";
                */
                str_share = eventArrayList.get(item).getEvent_image();

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        Constants.showProgress(context);
                        // Looper.prepare();
                        /* Bitmap theBitmap = Glide.with(context).
                                 load(str_share).asBitmap().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).
                                 into(400, 400).get();*/
                        if(str_share.length()<200) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            // downsizing image as it throws OutOfMemory Exception for larger
                            // images
                            options.inSampleSize = 2;
                            Bitmap temp = BitmapFactory.decodeFile(str_share,
                                    options);

                            Log.e("width",temp.getWidth()+" "+temp.getHeight());
                            Bitmap theBitmap=null;
                            if(temp.getWidth()>450 || temp.getHeight()>450) {
                                theBitmap = getResizedBitmap(temp, 450, 450);
                            }
                            else {
                                theBitmap = temp;
                            }

                            if (theBitmap != null) {
                                //  Log.e("theBitmap", "bitmap:" + theBitmap);

                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                theBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                byte[] byteArray = byteArrayOutputStream.toByteArray();

                                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                                eventArrayList.get(item).setEvent_image("");
                                if (!encoded.equalsIgnoreCase("")) {
                                    eventArrayList.get(item).setEvent_image(encoded);
                                }
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        File file =  new File("/sdcard/Android/data/" + context.getPackageName() + "/");
                        File gpxfile=null;
                        if(!file.exists()){
                            file.mkdir();
                        }

                        try{
                            gpxfile = new File(file, "eCountDown_"+eventArrayList.get(item).getEvent_name());

                            FileOutputStream f = new FileOutputStream(gpxfile);
                            ObjectOutputStream oos = new ObjectOutputStream(f);

                            oos.writeObject(eventArrayList.get(item));

                            oos.flush();
                            oos.close();
                            f.close();

                        }catch (Exception e){

                        }
                        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        emailIntent.setType("*/*");
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                                "Test Subject");
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                                "go on read the emails");
                        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(gpxfile.getPath())));
                        ((FragmentActivity)context).startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        ll_share.setClickable(true);
                        Constants.dismissProgress();

                    }
                }.execute();



            }
        });
        ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pref.setValue(context, "edit_from", "detail");
                Pref.setValue(context, "EditEventId", eventArrayList.get(item).getEvent_uuid());
                Pref.setValue(context,"url","");
                EditEventFragment fragment = new EditEventFragment();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pref.setValue(context, "edit_from", "detail");
                Pref.setValue(context, "EditEventId", eventArrayList.get(item).getEvent_uuid());

                Pref.setValue(context,"url","");
                EditEventFragment fragment = new EditEventFragment();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).addToBackStack(null).commit();

            }
        });

        ll_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(item);

            }
        });

    }
    private Bitmap decodeFile(String imgPath)
    {
        Bitmap b = null;
        int max_size = 1000;
        File f = new File(imgPath);
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            int scale = 1;
            if (o.outHeight > max_size || o.outWidth > max_size)
            {
                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(max_size / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        }
        catch (Exception e)
        {
        }
        return b;
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

                realm.executeTransaction(
                        new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                Event event = realm.where(Event.class).equalTo("event_uuid", eventArrayList.get(i).getEvent_uuid()).findFirst();
                                event.deleteFromRealm();
                                eventArrayList.remove(i);
                                notifyDataSetChanged();
                                if(eventArrayList.size()>0) {
                                    if (from.equalsIgnoreCase("All")) {
                                        EventDeatilViewpagerFragment fragment = new EventDeatilViewpagerFragment();
                                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
                                    } else if (from.equalsIgnoreCase("Future")) {
                                        EventDeatilViewpagerFutureFragment fragment = new EventDeatilViewpagerFutureFragment();
                                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
                                    } else if (from.equalsIgnoreCase("Past")) {
                                        EventDeatilViewpagerpastFragment fragment = new EventDeatilViewpagerpastFragment();
                                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commit();
                                    }
                                }else
                                {
                                    ListEventFragment fragment = new ListEventFragment();
                                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main_container, fragment).commitAllowingStateLoss();

                                }

                            }
                        });



                dialog.dismiss();
                Toast.makeText(context, "Event deleted successfully.", Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();

    }
    public Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(resizedBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return resizedBitmap;
    }

}
