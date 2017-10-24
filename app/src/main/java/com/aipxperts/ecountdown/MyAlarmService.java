package com.aipxperts.ecountdown;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.aipxperts.ecountdown.Activity.DashBoardActivity;
import com.aipxperts.ecountdown.Model.Event;
import com.aipxperts.ecountdown.utils.Pref;
import com.aipxperts.ecountdown.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;


public class MyAlarmService extends Service
{

    ArrayList<Event> eventArrayList=new ArrayList<>();
    private NotificationManager mManager;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    MyReceiver receiver;

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful


        Calendar cal = Calendar.getInstance();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        cal.add(Calendar.SECOND, 10);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);

        //  Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_LONG).show();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Utils.mBroadcastStationFix);
        filter.addAction(Utils.alarmRemoveReceiver);
        receiver= new MyReceiver();
       // registerReceiver(receiver, filter);
        /*if (intent != null) {
            MyReceiver.completeWakefulIntent(intent);
        }*/
        shownotification();

        return Service.START_STICKY;
    }

    private void shownotification() {


        Intent notificationIntent = new Intent(this, DashBoardActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(DashBoardActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


        Notification notification = builder.setContentTitle(Pref.getValue(this,"notify_title",""))
                .setContentText(Pref.getValue(this,"notify_des",""))
                .setTicker("Event Alert!")
                .setSmallIcon(R.mipmap.app_icon)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }


   /* @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(),DashBoardActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Firebase Push Notification")
                .setContentText("sdfsd")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        Notification notification = new Notification(R.mipmap.app_icon,"This is a test message!", System.currentTimeMillis());
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB) {

         //   notification.setLatestEventInfo(this.getApplicationContext(), "AlarmManagerDemo", "This is a test message!", pendingIntent);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            mManager.notify(0,notification);
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    this);
            notification = builder.setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.app_icon).setTicker("This is a test message!").setWhen(System.currentTimeMillis())
                    .setAutoCancel(true).setContentTitle("This is a test message!")
                    .setContentText("This is a test message!").build();

            mManager.notify(0,notification);
        }


    }*/



    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}