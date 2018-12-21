package com.howettl.wearquicksettings.common.model.payloads

import android.content.Context
import android.net.wifi.WifiManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import timber.log.Timber
import javax.inject.Inject

@JsonClass(generateAdapter = true)
class WifiPayload(enabled: Boolean, context: Context? = null, setting: Setting = Setting.WIFI): SettingsPayload(Setting.WIFI, enabled, context) {

    @Inject
    @Transient
    lateinit var wifiManager: WifiManager

    @Inject
    @Transient
    override lateinit var adapter: JsonAdapter<WifiPayload>

    override fun executeChange() {
        wifiManager.isWifiEnabled = enabled
    }

    override fun onSendSuccess() {
        Timber.i("Successfully sent wifi state change request")
    }

    override fun onSendFailed(error: Exception) {
        Timber.e(error, "Error requesting wifi state change")
    }
}