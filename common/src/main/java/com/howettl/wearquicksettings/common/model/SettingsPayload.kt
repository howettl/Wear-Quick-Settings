package com.howettl.wearquicksettings.common.model

import android.bluetooth.BluetoothManager
import android.content.Context
import android.net.wifi.WifiManager
import com.howettl.wearquicksettings.common.injection.component.DaggerCommonComponent
import com.howettl.wearquicksettings.common.injection.module.MoshiModule
import com.howettl.wearquicksettings.common.injection.module.SettingsModule
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import java.nio.charset.Charset
import javax.inject.Inject

@JsonClass(generateAdapter = true)
data class SettingsPayload @JvmOverloads constructor(
    val setting: Setting,
    val enabled: Boolean,
    @field:Transient val context: Context? = null) {

    init {
        context?.let {
            DaggerCommonComponent
                .builder()
                .context(it)
                .settingsModule(SettingsModule)
                .moshiModule(MoshiModule)
                .build()
                .inject(this)
        }
    }

    @Inject
    @Transient
    lateinit var adapter: JsonAdapter<SettingsPayload>

    @Inject
    @Transient
    lateinit var wifiManager: WifiManager

    @Inject
    @Transient
    lateinit var bluetoothManager: BluetoothManager

    fun executeChange() {
        when (setting) {
            Setting.WIFI -> wifiManager.isWifiEnabled = enabled
            Setting.BLUETOOTH -> if (enabled) bluetoothManager.adapter.enable() else bluetoothManager.adapter.disable()
        }
    }

    fun toByteArray(): ByteArray {
        return adapter.toJson(this).toByteArray(Charset.defaultCharset())
    }
}

fun ByteArray.toSettingsPayload(context: Context): SettingsPayload? {
    val adapter = MoshiModule.providesSettingsPayloadAdapter(MoshiModule.providesMoshiInstance())
    val payload = adapter.fromJson(String(this)) ?: return null
    DaggerCommonComponent
        .builder()
        .context(context)
        .settingsModule(SettingsModule)
        .moshiModule(MoshiModule)
        .build()
        .inject(payload)
    return payload
}

enum class Setting {
    WIFI, BLUETOOTH
}