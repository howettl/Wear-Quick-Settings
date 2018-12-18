package com.howettl.wearquicksettings.common

import com.howettl.wearquicksettings.common.PayloadSerializer.adapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset

@JsonClass(generateAdapter = true)
data class SettingsPayload(val setting: Setting, val enabled: Boolean) {
    fun toByteArray(): ByteArray {
        return adapter.toJson(this).toByteArray(Charset.defaultCharset())
    }
}

fun ByteArray.toSettingsPayload(): SettingsPayload? {
    return adapter.fromJson(String(this))
}

private object PayloadSerializer {
    private val moshi: Moshi by lazy {
        Moshi.Builder().build()
    }
    val adapter: JsonAdapter<SettingsPayload> by lazy {
        moshi.adapter(SettingsPayload::class.java)
    }
}

enum class Setting {
    WIFI, BLUETOOTH
}