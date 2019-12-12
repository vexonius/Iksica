package com.tstudioz.iksica;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.orhanobut.hawk.Hawk;
import com.tstudioz.iksica.Data.DatabaseHelper;
import com.tstudioz.iksica.Utils.OkHttpClientX;

import io.paperdb.Paper;
import io.realm.Realm;
import timber.log.Timber;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        Paper.init(getApplicationContext());
        super.onCreate();

        Realm.init(this);
        Hawk.init(getApplicationContext()).build();
        DatabaseHelper.Companion.createInstance(getApplicationContext());

        OkHttpClientX.createInstance(getApplicationContext());

        // MobileAds.initialize(this, "ca-app-pub-5944203368510130~6211911487");

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}