package com.tstudioz.iksica.Utils;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class OkHttpClientX {

    private static ClearableCookieJar cookieJar;

    public static OkHttpClient provideClientInstance(Context context) {

        cookieJar = new PersistentCookieJar(new SetCookieCache(),
                new SharedPrefsCookiePersistor(context));

        return new OkHttpClient()
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

    public static void clearCookies() {
        if (cookieJar != null)
            cookieJar.clear();
    }

}
