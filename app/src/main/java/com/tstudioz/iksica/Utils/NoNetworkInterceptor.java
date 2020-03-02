package com.tstudioz.iksica.Utils;

import androidx.annotation.NonNull;

import com.tstudioz.iksica.Utils.Exceptions.NoNetworkException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

import static org.koin.java.KoinJavaComponent.get;


/**
 * Created by etino7 on 12/17/2019.
 */
public class NoNetworkInterceptor implements Interceptor {

    private NetworkMonitor monitor = get(NetworkMonitor.class);

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Timber.d(String.valueOf(monitor.isConnected()));

        if (!monitor.isConnected())
            throw new NoNetworkException();

        return chain.proceed(originalRequest);
    }

    public NoNetworkInterceptor() {
    }


}