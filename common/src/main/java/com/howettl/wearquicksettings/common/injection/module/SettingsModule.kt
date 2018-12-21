package com.howettl.wearquicksettings.common.injection.module

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object SettingsModule {

    @Provides
    @Singleton
    @JvmStatic
    internal fun providesWearableMessageClient(context: Context): MessageClient {
        return Wearable.getMessageClient(context)
    }

    @Provides
    @Singleton
    @JvmStatic
    internal fun providesWifiManager(context: Context): WifiManager {
        return context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    @Provides
    @Singleton
    @JvmStatic
    internal fun providesConnectivityManager(context: Context): ConnectivityManager {
        return context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

}