package com.howettl.wearquicksettings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch

class MainActivity : AppCompatActivity() {

    private val serviceSwitch: Switch
        get() = findViewById(R.id.service_toggle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        serviceSwitch.setOnCheckedChangeListener(null)
        serviceSwitch.isChecked = SettingsUpdateService.isRunning(this)
        serviceSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) SettingsUpdateService.start(this)
            else SettingsUpdateService.stop(this)
        }
    }
}
