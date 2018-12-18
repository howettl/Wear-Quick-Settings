package com.howettl.wearquicksettings.injection.component

import android.content.Context
import com.howettl.wearquicksettings.handlers.SettingsUpdateService
import com.howettl.wearquicksettings.common.injection.module.WearableModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [WearableModule::class])
interface MobileComponent {

    fun inject(service: SettingsUpdateService)

    @Component.Builder
    interface Builder {
        fun build(): MobileComponent
        fun wearableModule(module: WearableModule): Builder
        @BindsInstance
        fun context(context: Context): Builder
    }

}