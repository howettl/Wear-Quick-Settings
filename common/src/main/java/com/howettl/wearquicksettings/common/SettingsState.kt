package com.howettl.wearquicksettings.common

import android.net.wifi.WifiManager
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest

data class SettingsState(
    val wifi: Int,
    val bluetooth: Boolean
) {
    fun toPutDataRequest(path: SettingsPath): PutDataRequest {
        return PutDataMapRequest.create(path.path).run {
            dataMap.putInt(Consts.KEY_WIFI_STATE, wifi)
            dataMap.putBoolean(Consts.KEY_BLUETOOTH_STATE, bluetooth)
            asPutDataRequest()
                .setUrgent()
        }
    }

    fun isWifiEnabled(): Boolean {
        return wifi == WifiManager.WIFI_STATE_ENABLED || wifi == WifiManager.WIFI_STATE_ENABLING
    }

    fun isWifiChanging(): Boolean {
        return wifi == WifiManager.WIFI_STATE_ENABLING || wifi == WifiManager.WIFI_STATE_DISABLING
    }

    override fun toString(): String {
        return "Wifi enabled: ${isWifiEnabled()} (changing: ${isWifiChanging()}); Bluetooth enabled: $bluetooth"
    }
}

fun DataMap.toSettingsState(): SettingsState {
    return SettingsState(
        getInt(Consts.KEY_WIFI_STATE),
        getBoolean(Consts.KEY_BLUETOOTH_STATE)
    )
}