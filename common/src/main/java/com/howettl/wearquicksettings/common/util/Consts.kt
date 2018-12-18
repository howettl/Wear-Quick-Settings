package com.howettl.wearquicksettings.common.util

object Consts {
    const val DESIRED_SETTINGS_PATH = "/desired-settings"
    const val ACTUAL_SETTINGS_PATH = "/actual-settings"

    const val KEY_WIFI_STATE = "key-wifi-state"
    const val KEY_BLUETOOTH_STATE = "key-bt-state"
}

enum class SettingsPath(path: String) {
    ACTUAL(Consts.ACTUAL_SETTINGS_PATH), DESIRED(Consts.DESIRED_SETTINGS_PATH);

    var path: String = path
        private set

}