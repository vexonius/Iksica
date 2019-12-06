package com.tstudioz.iksica.Utils;

import android.content.Context;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;


public class OkHttpClientX {

    private static OkHttpClient okHttpClient;

    private static CookieJar cookieJar;

    private OkHttpClientX(){

    }

    public static OkHttpClient getInstance(){
        return okHttpClient;
    }

    public static void createInstance(Context context){

        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        okHttpClient = new OkHttpClient().newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .cookieJar(cookieJar)
                .build();
    }



}
