package com.howettl.wearquicksettings.common.model.payloads

import android.content.Context
import android.net.ConnectivityManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import timber.log.Timber
import javax.inject.Inject

@JsonClass(generateAdapter = true)
class CellDataPayload(enabled: Boolean, context: Context? = null, setting: Setting = Setting.CELL_DATA): SettingsPayload(Setting.CELL_DATA, enabled, context) {

    @Inject
    @Transient
    lateinit var connectivityManager: ConnectivityManager

    @Inject
    @Transient
    override lateinit var adapter: JsonAdapter<CellDataPayload>

    override fun executeChange() {
        val `class` = Class.forName(connectivityManager::class.java.name)
        val field = `class`.getDeclaredField("mService")
        field.isAccessible = true
        val manager = field.get(connectivityManager)
        val managerClass = Class.forName(manager::class.java.name)
        val enableMethod = managerClass.getDeclaredMethod("setMobileDataEnabled", Boolean::class.java.componentType)
        enableMethod.isAccessible = true
        enableMethod.invoke(manager, enabled)
    }

    override fun onSendSuccess() {
        Timber.i("Successfully send cell data change request")
    }

    override fun onSendFailed(error: Exception) {
        Timber.e(error, "Error requesting cell data change")
    }
}