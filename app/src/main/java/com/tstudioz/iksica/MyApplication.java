package com.tstudioz.iksica;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.google.android.gms.ads.MobileAds;
import com.orhanobut.hawk.Hawk;
import com.tstudioz.iksica.Data.DatabaseHelper;
import com.tstudioz.iksica.Utils.NetworkMonitor;
import com.tstudioz.iksica.Utils.OkHttpClientX;


import io.realm.Realm;
import timber.log.Timber;

public class MyApplication extends Application {

    @Override
    public void onCreate() {

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        super.onCreate();

        NetworkMonitor.createInstance(getApplicationContext());
        OkHttpClientX.createInstance(getApplicationContext());

        Realm.init(this);
        Hawk.init(getApplicationContext()).build();
        DatabaseHelper.Companion.createInstance(getApplicationContext());

        // MobileAds.initialize(this, "ca-app-pub-5944203368510130~6211911487");

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}