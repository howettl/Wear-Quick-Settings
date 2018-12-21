package com.howettl.wearquicksettings.common.injection.component

import android.content.Context
import com.howettl.wearquicksettings.common.injection.module.MoshiModule
import com.howettl.wearquicksettings.common.injection.module.SettingsModule
import com.howettl.wearquicksettings.common.model.SettingsState
import com.howettl.wearquicksettings.common.model.payloads.CellDataPayload
import com.howettl.wearquicksettings.common.model.payloads.WifiPayload
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [SettingsModule::class, MoshiModule::class])
interface CommonComponent {

    fun inject(payload: WifiPayload)
    fun inject(payload: CellDataPayload)
    fun inject(state: SettingsState)

    @Component.Builder
    interface Builder {
        fun build(): CommonComponent
        fun settingsModule(module: SettingsModule): Builder
        fun moshiModule(module: MoshiModule): Builder
        @BindsInstance
        fun context(context: Context): Builder
    }

}