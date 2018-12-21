package com.howettl.wearquicksettings.ui

import android.net.Uri
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.Button
import com.google.android.gms.wearable.*
import com.howettl.wearquicksettings.R
import com.howettl.wearquicksettings.common.base.CoroutineBase
import com.howettl.wearquicksettings.common.injection.module.WearableModule
import com.howettl.wearquicksettings.common.model.SettingsState
import com.howettl.wearquicksettings.common.model.payloads.WifiPayload
import com.howettl.wearquicksettings.common.model.toSettingsState
import com.howettl.wearquicksettings.common.util.Consts
import com.howettl.wearquicksettings.common.util.Result
import com.howettl.wearquicksettings.common.util.blockingAwait
import com.howettl.wearquicksettings.injection.component.DaggerWearComponent
import com.howettl.wearquicksettings.widget.CircleToggle
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class WearActivity: WearableActivity(), DataClient.OnDataChangedListener,
    CoroutineBase {

    override val coroutineJob: Job
        get() = Job()

    private val wifiToggle: CircleToggle
        get() = findViewById(R.id.wifi_toggle)

    private val cellDataToggle: CircleToggle
        get() = findViewById(R.id.cell_data_toggle)

    @Inject
    internal lateinit var wearableDataClient: DataClient

    private var settingsOwnerNodeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerWearComponent
            .builder()
            .context(this)
            .wearableModule(WearableModule)
            .build()
            .inject(this)

        setContentView(R.layout.activity_wear)

        // Enables Always-on
        setAmbientEnabled()

        findViewById<Button>(R.id.request_update_button).setOnClickListener {
            launch { requestActualSettingsUpdate() }
        }
    }

    override fun onResume() {
        super.onResume()

        wearableDataClient.addListener(this)

        launch {
            updateSettingsOwnerNodeId()

            val fetchResult = wearableDataClient.getDataItem(
                Uri.Builder()
                    .scheme(PutDataRequest.WEAR_URI_SCHEME)
                    .path(Consts.ACTUAL_SETTINGS_PATH)
                    .authority(settingsOwnerNodeId)
                    .build()
            ).blockingAwait()
            when (fetchResult) {
                is Result.Successful -> {
                    Timber.d("Successfully polled actual state")
                    fetchResult.result?.let { updateActualState(DataMapItem.fromDataItem(it).dataMap.toSettingsState()) }
                }
                is Result.Failed -> Timber.e(fetchResult.error, "Failed polling actual state")
            }
            requestActualSettingsUpdate()
        }
    }

    override fun onPause() {
        super.onPause()

        wearableDataClient.removeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        cancelJob()
    }

    private fun updateActualState(state: SettingsState) {
        unregisterControlEvents()

        wifiToggle.isChecked = state.isWifiEnabled()
        wifiToggle.isEnabled = !state.isWifiChanging()

        registerControlEvents()
    }

    private fun registerControlEvents() {
        wifiToggle.setOnClickListener {
            launch { WifiPayload(wifiToggle.isChecked, this@WearActivity)
                .sendChange(settingsOwnerNodeId ?: return@launch) }
        }
    }

    private fun unregisterControlEvents() {
        wifiToggle.setOnClickListener(null)
    }

    override fun onDataChanged(buffer: DataEventBuffer) {
        buffer.forEach { event: DataEvent ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                Timber.i("Received event")
                event.dataItem.also { item ->
                    if (item.uri?.path?.compareTo(Consts.ACTUAL_SETTINGS_PATH) == 0) {
                        Timber.i("Updating actual state. URI: ${item.uri}")
                        updateActualState(DataMapItem.fromDataItem(item).dataMap.toSettingsState())
                    }
                }
            } else if (event.type == DataEvent.TYPE_DELETED) {
                Timber.i("DataMap ${event.dataItem.uri?.path} deleted")
            }
        }
    }

    private suspend fun updateSettingsOwnerNodeId() {
        val capabilityInfo = Wearable.getCapabilityClient(this@WearActivity)
            .getCapability(getString(R.string.settings_owner), CapabilityClient.FILTER_REACHABLE).blockingAwait()
        when (capabilityInfo) {
            is Result.Successful -> {
                val node =
                    capabilityInfo.result.nodes.firstOrNull { it.isNearby } ?: capabilityInfo.result.nodes.firstOrNull()
                node?.let {
                    Timber.i("got node: $it")
                    settingsOwnerNodeId = it.id
                }
            }
            is Result.Failed -> Timber.e(capabilityInfo.error, "Error updating settings owner node id")
            is Result.Interrupted -> return
        }
    }

    private suspend fun requestActualSettingsUpdate() {
        Timber.i("requesting settings update")
        settingsOwnerNodeId?.let { nodeId ->
            Timber.i("Requesting actual settings from node ID $nodeId")
            val sendMessageResult = Wearable.getMessageClient(this@WearActivity).sendMessage(
                nodeId, getString(R.string.request_settings_update), null
            ).blockingAwait()

            when (sendMessageResult) {
                is Result.Successful -> Timber.i("Successfully sent request for actual settings update")
                is Result.Failed -> Timber.e(sendMessageResult.error, "Failed requesting actual settings update")
            }
        } ?: Timber.i("No settings owner nodes connected")
    }
}
