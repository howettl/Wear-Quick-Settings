package com.howettl.wearquicksettings.common.model.payloads

import android.content.Context
import com.google.android.gms.wearable.MessageClient
import com.howettl.wearquicksettings.common.R
import com.howettl.wearquicksettings.common.injection.component.DaggerCommonComponent
import com.howettl.wearquicksettings.common.injection.module.MoshiModule
import com.howettl.wearquicksettings.common.injection.module.SettingsModule
import com.howettl.wearquicksettings.common.util.Result
import com.howettl.wearquicksettings.common.util.blockingAwait
import com.squareup.moshi.JsonAdapter
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.nio.charset.Charset
import javax.inject.Inject

abstract class SettingsPayload(
    val setting: Setting,
    val enabled: Boolean,
    @field:Transient val context: Context? = null
) {

    init {
        context?.let {
            val injector = DaggerCommonComponent
                .builder()
                .context(it)
                .settingsModule(SettingsModule)
                .moshiModule(MoshiModule)
                .build()

            when (this) {
                is WifiPayload -> injector.inject(this)
                is CellDataPayload -> injector.inject(this)
            }
        }
    }

    @Transient
    @Inject
    lateinit var wearableMessageClient: MessageClient

    abstract val adapter: JsonAdapter<out SettingsPayload>

    abstract fun executeChange()
    abstract fun onSendSuccess()
    abstract fun onSendFailed(error: Exception)

    suspend fun sendChange(settingsOwnerNodeId: String) {
        val sendMessageResult = wearableMessageClient.sendMessage(
            settingsOwnerNodeId, context?.getString(R.string.request_settings_change) ?: return,
            toByteArray()
        ).blockingAwait()

        when (sendMessageResult) {
            is Result.Successful -> onSendSuccess()
            is Result.Failed -> onSendFailed(sendMessageResult.error)
        }
    }

    private fun toByteArray(): ByteArray {
        return when (this) {
            is WifiPayload -> adapter.toJson(this).toByteArray(Charset.defaultCharset())
            is CellDataPayload -> adapter.toJson(this).toByteArray(Charset.defaultCharset())
            else -> throw RuntimeException("must implement conversion in ${SettingsPayload::class.java.simpleName} for this child class")
        }
    }
}

fun ByteArray.toSettingsPayload(context: Context): SettingsPayload? {
    val injector = DaggerCommonComponent
        .builder()
        .context(context)
        .settingsModule(SettingsModule)
        .moshiModule(MoshiModule)
        .build()

    val jsonString = String(this)
    // determine which type of payload it is before we can deserialize it
    val type: Setting? = try {
        val jsonObject = JSONObject(jsonString)
        val setting = jsonObject.getString("setting")
        when (setting) {
            Setting.WIFI.description -> Setting.WIFI
            Setting.CELL_DATA.description -> Setting.CELL_DATA
            else -> null
        }
    } catch (e: JSONException) {
        Timber.e(e, "Unable to deserialize settings payload")
        null
    }

    val adapter = when (type) {
        Setting.WIFI -> MoshiModule.providesWifiPayloadAdapter(MoshiModule.providesMoshiInstance())
        Setting.CELL_DATA -> MoshiModule.providesCellDataPayloadAdapter(MoshiModule.providesMoshiInstance())
        else -> return null
    }
    val payload = adapter.fromJson(jsonString) ?: return null

    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    when (payload.setting) {
        Setting.WIFI -> injector.inject(payload as WifiPayload)
        Setting.CELL_DATA -> injector.inject(payload as CellDataPayload)
        else -> throw RuntimeException("must implement conversion in ${SettingsPayload::class.java.simpleName} for this child class")
    }

    return payload
}

enum class Setting(val description: String) {
    WIFI("WIFI"), CELL_DATA("CELL_DATA");

    override fun toString(): String {
        return description
    }
}