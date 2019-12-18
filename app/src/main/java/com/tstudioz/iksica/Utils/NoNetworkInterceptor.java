package com.tstudioz.iksica.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tstudioz.iksica.Utils.Exceptions.NoNetworkException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

import static com.tstudioz.iksica.Utils.NetworkMonitor.isConnected;

/**
 * Created by etino7 on 12/17/2019.
 */
public class NoNetworkInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Timber.d(originalRequest.url().toString());

        if (!isConnected())
            throw new NoNetworkException();

        return chain.proceed(originalRequest);
    }

    public NoNetworkInterceptor() {
    }


}