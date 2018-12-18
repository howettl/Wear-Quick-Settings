package com.howettl.wearquicksettings.common.injection.module

import android.content.Context
import android.net.wifi.WifiManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object SettingsModule {

    @Provides
    @Singleton
    @JvmStatic
    internal fun providesWifiManager(context: Context): WifiManager {
        return context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

}