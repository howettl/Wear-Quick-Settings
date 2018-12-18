package com.howettl.wearquicksettings.handlers

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.howettl.wearquicksettings.R

class ActualSettingsUpdateRequestListener: WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        if (messageEvent.path != getString(R.string.request_settings_update)) {
            return
        }

        SettingsUpdateService.start(this)
    }
}

class ToggleSettingRequestListener: WearableListenerService() {
    override fun onMessageReceived(event: MessageEvent?) {
        super.onMessageReceived(event)

        if (event?.path != getString(R.string.request_settings_change)) {
            return
        }

        SettingsUpdateService.start(this, event?.data ?: return)
    }
}