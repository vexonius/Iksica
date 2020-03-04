package com.tstudioz.iksica.Utils

import com.tstudioz.iksica.Utils.Exceptions.NoNetworkException
import okhttp3.Interceptor
import okhttp3.Response
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.io.IOException

/**
 * Created by etino7 on 12/17/2019.
 */
class NoNetworkInterceptor : Interceptor {

    private val monitor = KoinJavaComponent.get(NetworkMonitor::class.java)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        Timber.d(monitor.isConnected.toString())

        if (!monitor.isConnected)
            throw NoNetworkException()

        return chain.proceed(originalRequest)
    }
}