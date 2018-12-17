package com.howettl.wearquicksettings

import android.app.Application
import timber.log.Timber

class WearApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}