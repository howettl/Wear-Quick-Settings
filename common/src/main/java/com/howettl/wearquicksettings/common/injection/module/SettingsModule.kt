package com.howettl.wearquicksettings.common.injection.module

import android.bluetooth.BluetoothManager
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

    @Provides
    @Singleton
    @JvmStatic
    internal fun providesBluetoothManager(context: Context): BluetoothManager {
        return context.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

}