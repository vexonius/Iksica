package com.tstudioz.iksica.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by etino7 on 12/17/2019.
 */

public class NetworkMonitor {

    private Context context;

    public NetworkMonitor(Context context) {
        this.context = context;
    }

    public boolean isConnected() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}