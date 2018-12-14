package com.howettl.wearquicksettings

import android.net.Uri
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.Button
import android.widget.Switch
import com.google.android.gms.wearable.*
import com.howettl.wearquicksettings.common.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : WearableActivity(), DataClient.OnDataChangedListener, CoroutineBase {

    override val coroutineJob: Job
        get() = Job()

    private val wifiSwitch: Switch
        get() = findViewById(R.id.wifi_switch)

    private val bluetoothSwitch: Switch
        get() = findViewById(R.id.bluetooth_switch)

    private val wearableDataClient: DataClient by lazy {
        Wearable.getDataClient(this)
    }

    private var settingsOwnerNodeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        wifiSwitch.isChecked = state.isWifiEnabled()
        wifiSwitch.isEnabled = !state.isWifiChanging()

        bluetoothSwitch.isChecked = state.bluetooth
        bluetoothSwitch.isEnabled = true
    }

    override fun onDataChanged(buffer: DataEventBuffer) {
        Timber.i("onDataChanged")
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
        val capabilityInfo = Wearable.getCapabilityClient(this@MainActivity)
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
            val sendMessageResult = Wearable.getMessageClient(this@MainActivity).sendMessage(
                nodeId, getString(R.string.request_settings_update), null
            ).blockingAwait()

            when (sendMessageResult) {
                is Result.Successful -> Timber.i("Successfully sent request for actual settings update")
                is Result.Failed -> Timber.e(sendMessageResult.error, "Failed requesting actual settings update")
                is Result.Interrupted -> return
            }
        } ?: Timber.i("No settings owner nodes connected")
    }
}