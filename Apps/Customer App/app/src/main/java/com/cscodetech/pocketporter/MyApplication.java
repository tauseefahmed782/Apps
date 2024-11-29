package com.cscodetech.pocketporter;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;

public class MyApplication extends Application {

    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
        FirebaseApp.initializeApp(this);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId("*******************************");
    }

}