package com.tstudioz.menze;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by amarthus on 10-Oct-17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}