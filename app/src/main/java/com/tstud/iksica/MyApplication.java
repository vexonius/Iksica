package com.tstud.iksica;

import android.app.Application;

import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.security.SecureRandom;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    public RealmConfiguration defaultRealmConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        defaultRealmConfig = new RealmConfiguration.Builder()
                .name("encrypted.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .encryptionKey(getRealmKey())
                .build();
        Realm.setDefaultConfiguration(defaultRealmConfig);

        chechIfOldRealmExists();

        //    MobileAds.initialize(this, "ca-app-pub-5944203368510130~6211911487");

    }

    private void chechIfOldRealmExists(){
        File newRealmFile = new File(defaultRealmConfig.getPath());
        if (!newRealmFile.exists()) {
            // Migrate old Realm and delete old
            RealmConfiguration old = new RealmConfiguration.Builder()
                    .name("myRealm.realm")
                    .schemaVersion(1)
                    .deleteRealmIfMigrationNeeded()
                    .build();

            Realm realm = Realm.getInstance(old);
            realm.writeEncryptedCopyTo(newRealmFile, getRealmKey());
            realm.close();
            Realm.deleteRealm(old);
        }
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