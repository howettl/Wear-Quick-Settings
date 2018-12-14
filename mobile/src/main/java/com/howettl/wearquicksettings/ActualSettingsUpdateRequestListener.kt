package com.howettl.wearquicksettings

import android.bluetooth.BluetoothManager
import android.content.Context
import android.net.wifi.WifiManager
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.howettl.wearquicksettings.common.CoroutineBase
import com.howettl.wearquicksettings.common.Result
import com.howettl.wearquicksettings.common.SettingsPath
import com.howettl.wearquicksettings.common.SettingsState
import com.howettl.wearquicksettings.common.blockingAwait
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class ActualSettingsUpdateRequestListener : WearableListenerService(), CoroutineBase {

    override val coroutineJob: Job
        get() = Job()

    private val wearableDataClient: DataClient by lazy {
        Wearable.getDataClient(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        if (messageEvent.path != getString(R.string.request_settings_update)) {
            return
        }

        val wifiManager: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val btManager: BluetoothManager =
            applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        val actualState = SettingsState(wifiManager.wifiState, btManager.adapter.isEnabled)
        launch {
            val putResult = wearableDataClient.putDataItem(actualState.toPutDataRequest(SettingsPath.ACTUAL)).blockingAwait()
            when (putResult) {
                is Result.Successful -> Timber.i("Settings update successfully sent")
                is Result.Failed -> Timber.e(putResult.error, "Settings update error")
                is Result.Interrupted -> {}
            }
            cancelJob()
        }
    }
}