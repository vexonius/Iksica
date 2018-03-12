package com.tstudioz.iksica;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration defaultRealmConfig = new RealmConfiguration.Builder()
                .name("myRealm.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(defaultRealmConfig);

        MobileAds.initialize(this, "ca-app-pub-5944203368510130~6211911487");

    }
}