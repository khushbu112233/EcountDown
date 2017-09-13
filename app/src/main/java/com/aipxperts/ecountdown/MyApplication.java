package com.aipxperts.ecountdown;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by aipxperts on 15/3/17.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);



    }
}
