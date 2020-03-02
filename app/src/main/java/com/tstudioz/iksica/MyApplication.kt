package com.tstudioz.iksica

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.google.android.gms.ads.MobileAds
import com.orhanobut.hawk.Hawk
import com.tstudioz.iksica.DI.defaultModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()


        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(defaultModule)
        }

        Hawk.init(applicationContext).build()
        MobileAds.initialize(this, "ca-app-pub-5944203368510130~6211911487")

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}