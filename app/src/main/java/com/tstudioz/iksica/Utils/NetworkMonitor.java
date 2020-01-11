package com.tstudioz.iksica.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by etino7 on 12/17/2019.
 */

public class NetworkMonitor {

    private static NetworkMonitor instance;
    private static ConnectivityManager connectivityManager;

    private NetworkMonitor(Context context) {
        connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static NetworkMonitor getInstance() {
        return instance;
    }

    public static NetworkMonitor createInstance(Context context) {
        return new NetworkMonitor(context);
    }

    public static boolean isConnected() {

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}