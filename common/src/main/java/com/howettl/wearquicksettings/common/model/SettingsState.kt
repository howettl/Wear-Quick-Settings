package com.howettl.wearquicksettings.common.model

import android.content.Context
import android.net.wifi.WifiManager
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.howettl.wearquicksettings.common.injection.component.DaggerCommonComponent
import com.howettl.wearquicksettings.common.injection.module.MoshiModule
import com.howettl.wearquicksettings.common.injection.module.SettingsModule
import com.howettl.wearquicksettings.common.util.Consts
import com.howettl.wearquicksettings.common.util.SettingsPath
import javax.inject.Inject

class SettingsState(private val wifi: Int? = null) {

    constructor(context: Context): this() {
        DaggerCommonComponent
            .builder()
            .context(context)
            .settingsModule(SettingsModule)
            .moshiModule(MoshiModule)
            .build()
            .inject(this)
    }

    private val wifiState: Int
        get() = wifi ?: wifiManager.wifiState ?: WifiManager.WIFI_STATE_DISABLED

    @Inject
    internal lateinit var wifiManager: WifiManager

    fun toPutDataRequest(path: SettingsPath): PutDataRequest {
        return PutDataMapRequest.create(path.path).run {
            dataMap.putInt(Consts.KEY_WIFI_STATE, wifiState)
            asPutDataRequest()
                .setUrgent()
        }
    }

    fun isWifiEnabled(): Boolean {
        return wifiState == WifiManager.WIFI_STATE_ENABLED || wifiState == WifiManager.WIFI_STATE_ENABLING
    }

    fun isWifiChanging(): Boolean {
        return wifiState == WifiManager.WIFI_STATE_ENABLING || wifiState == WifiManager.WIFI_STATE_DISABLING
    }

    override fun toString(): String {
        return "Wifi enabled: ${isWifiEnabled()} (changing: ${isWifiChanging()})"
    }
}

fun DataMap.toSettingsState(): SettingsState {
    return SettingsState(
        getInt(Consts.KEY_WIFI_STATE)
    )
}