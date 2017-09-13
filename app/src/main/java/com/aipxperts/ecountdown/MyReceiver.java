package com.aipxperts.ecountdown;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.aipxperts.ecountdown.Activity.MainActivity;
import com.aipxperts.ecountdown.utils.Pref;

import java.util.Random;

public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("111", "onReceive called    " + intent.getStringExtra("Event_name"));
        String action = intent.getAction();


        String Event_name = intent.getStringExtra("Event_name");
        String Event_des=intent.getStringExtra("Event_des");
        // String Event_image=intent.getStringExtra("Event_image");
            displayNotification1(Event_name,Event_des,"", context);



 /*       Intent service1 = new Intent(context, MyAlarmService.class);
        context.startService(service1);
*/

    }

    private void displayNotification1(String result,String result1,String result2, Context context) {

        Random random = new Random();
        int id = random.nextInt(9999 - 1000) + 1000;

        NotificationManager manager;
        Notification myNotication;

        manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);



        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, 0);

        Notification.Builder builder = new Notification.Builder(context);

        builder.setTicker("Event Alert!");
        builder.setContentTitle(result);
        builder.setContentText(result1);
        builder.setSmallIcon(R.mipmap.app_icon);
        builder.setContentIntent(pendingIntent);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        builder.setOngoing(false);
        builder.setAutoCancel(true);


        builder.build();

        myNotication = builder.getNotification();
        manager.notify(id, myNotication);

    }
}