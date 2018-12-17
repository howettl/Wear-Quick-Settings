package com.howettl.wearquicksettings

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class ActualSettingsUpdateRequestListener: WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        if (messageEvent.path != getString(R.string.request_settings_update)) {
            return
        }

        SettingsUpdateService.start(this)
    }
}