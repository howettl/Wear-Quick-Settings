package com.howettl.wearquicksettings

import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.IBinder
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.Wearable
import com.howettl.wearquicksettings.common.Result
import com.howettl.wearquicksettings.common.SettingsPath
import com.howettl.wearquicksettings.common.SettingsState
import com.howettl.wearquicksettings.common.blockingAwait
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class SettingsUpdateService: Service(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    private val coroutineJob = Job()

    private val wearableDataClient: DataClient by lazy {
        Wearable.getDataClient(this)
    }

    private val wifiBtStateChangeReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            launch { sendActualStateUpdate() }
        }
    }

    override fun onCreate() {
        super.onCreate()

        registerReceiver(wifiBtStateChangeReceiver, IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION))
        registerReceiver(wifiBtStateChangeReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_REDELIVER_INTENT

        val notificationChannel = NotificationChannel(
            "ongoing",
            getString(R.string.ongoing_settings_listener),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationChannel.description = getString(R.string.channel_description)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val launchActivity = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }

        startForeground(
            NOTIFICATION_ID, Notification.Builder(this, notificationChannel.id)
                .setContentTitle(getString(R.string.ongoing_notification_title))
                .setContentText(getString(R.string.ongoing_notification_message))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(launchActivity)
                .build()
        )

        launch {
            sendActualStateUpdate()
        }

        return START_REDELIVER_INTENT
    }

    private suspend fun sendActualStateUpdate() {
        val wifiManager: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val btManager: BluetoothManager =
            applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        val actualState = SettingsState(wifiManager.wifiState, btManager.adapter.isEnabled)
        val putResult =
            wearableDataClient.putDataItem(actualState.toPutDataRequest(SettingsPath.ACTUAL)).blockingAwait()
        when (putResult) {
            is Result.Successful -> Timber.i("Settings update successfully sent")
            is Result.Failed -> Timber.e(putResult.error, "Settings update error")
            is Result.Interrupted -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(wifiBtStateChangeReceiver)
        coroutineJob.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val NOTIFICATION_ID = 42

        fun start(c: Context) {
            c.startForegroundService(Intent(c, SettingsUpdateService::class.java))
        }

        fun stop(c: Context) {
            c.stopService(Intent(c, SettingsUpdateService::class.java))
        }

        fun isRunning(c: Context): Boolean {
            val activityManager = c.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            @Suppress("DEPRECATION")
            return activityManager.getRunningServices(Int.MAX_VALUE).firstOrNull { it.service.className == SettingsUpdateService::class.java.name }?.started
                ?: false
        }
    }

}