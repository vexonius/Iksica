package com.tstudioz.iksica.Utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by etino7 on 12/17/2019.
 */
class NetworkMonitor(private val context: Context) {

    val isConnected: Boolean
        get() {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting
        }

}