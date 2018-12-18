package com.howettl.wearquicksettings.common.injection.module

import com.howettl.wearquicksettings.common.model.SettingsPayload
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object MoshiModule {

    @Provides
    @Singleton
    @JvmStatic
    internal fun providesSettingsPayloadAdapter(moshi: Moshi): JsonAdapter<SettingsPayload> {
        return moshi.adapter(SettingsPayload::class.java)
    }

    @Provides
    @Singleton
    @JvmStatic
    internal fun providesMoshiInstance(): Moshi {
        return Moshi.Builder()
            .build()
    }

}