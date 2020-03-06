package com.tstudioz.iksica.DI

import android.content.Context
import com.tstudioz.iksica.Data.DatabaseHelper
import com.tstudioz.iksica.Data.Repository
import com.tstudioz.iksica.Utils.DataParser
import com.tstudioz.iksica.Utils.NetworkMonitor
import com.tstudioz.iksica.Utils.NetworkService
import com.tstudioz.iksica.Utils.OkHttpClientX
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Created by etino7 on 1.3.2020..
 */


val defaultModule = module {

    single { NetworkMonitor(androidContext()) }
    single<OkHttpClient> { provideOkHttp(androidContext()) }
    single { NetworkService(get()) }
    single { DataParser() }
    single { DatabaseHelper(androidContext()) }
    single { Repository(get(), get(), get()) }

}

fun provideOkHttp(context: Context) : OkHttpClient = OkHttpClientX.provideClientInstance(context)