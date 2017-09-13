package com.aipxperts.ecountdown.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aipxperts.ecountdown.R;
import com.aipxperts.ecountdown.utils.Constants;
import com.aipxperts.ecountdown.utils.Pref;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends BaseActivity {


    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity


                    Intent intent = new Intent(MainActivity.this, DashBoardActivity.class);
                    startActivity(intent);
                    // close this activity
                    finish();


            }
        }, SPLASH_TIME_OUT);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
