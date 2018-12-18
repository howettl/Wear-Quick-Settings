package com.howettl.wearquicksettings.injection.component

import android.content.Context
import com.howettl.wearquicksettings.common.injection.module.WearableModule
import com.howettl.wearquicksettings.ui.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [WearableModule::class])
interface WearComponent {

    fun inject(activity: MainActivity)

    @Component.Builder
    interface Builder {
        fun build(): WearComponent
        fun wearableModule(module: WearableModule): Builder
        @BindsInstance
        fun context(context: Context): Builder
    }

}