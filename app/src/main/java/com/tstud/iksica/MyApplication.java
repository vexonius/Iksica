package com.tstud.iksica;

import android.app.Application;

import com.orhanobut.hawk.Hawk;

import java.security.SecureRandom;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);


        RealmConfiguration defaultRealmConfig = new RealmConfiguration.Builder()
                .name("myRealm.realm")
                .schemaVersion(2)
                .deleteRealmIfMigrationNeeded()
                .encryptionKey(getRealmKey())
                .build();
        Realm.setDefaultConfiguration(defaultRealmConfig);

        //    MobileAds.initialize(this, "ca-app-pub-5944203368510130~6211911487");

    }

    private byte[] getRealmKey(){

        Hawk.init(getApplicationContext()).build();

        if(Hawk.contains("masterKey")){
            byte[] array = Hawk.get("masterKey");
            return array;
        }

        byte[] bytes = new byte[64];

        new SecureRandom().nextBytes(bytes);

        Hawk.put("masterKey", bytes);

        return bytes;
    }
}