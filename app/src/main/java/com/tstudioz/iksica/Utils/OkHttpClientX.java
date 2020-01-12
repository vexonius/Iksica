package com.tstudioz.iksica.Utils;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;


public class OkHttpClientX {

    private static OkHttpClient okHttpClient;

    private static ClearableCookieJar cookieJar;

    private OkHttpClientX(){

    }

    public static OkHttpClient getInstance(){
        return okHttpClient;
    }

    public static void createInstance(Context context){

        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        okHttpClient = new OkHttpClient()
                .newBuilder()
                .callTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .followRedirects(true)
                .followSslRedirects(true)
                .addInterceptor(new NoNetworkInterceptor())
                .cookieJar(cookieJar)
                .build();
    }

    public static void clearCookies(){
        if (cookieJar != null)
            cookieJar.clear();
    }



}
