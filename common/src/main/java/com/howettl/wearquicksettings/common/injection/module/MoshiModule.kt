package com.howettl.wearquicksettings.common.injection.module

import com.howettl.wearquicksettings.common.model.payloads.CellDataPayload
import com.howettl.wearquicksettings.common.model.payloads.SettingsPayload
import com.howettl.wearquicksettings.common.model.payloads.WifiPayload
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
    internal fun providesWifiPayloadAdapter(moshi: Moshi): JsonAdapter<WifiPayload> {
        return moshi.adapter(WifiPayload::class.java)
    }

    @Provides
    @Singleton
    @JvmStatic
    internal fun providesCellDataPayloadAdapter(moshi: Moshi): JsonAdapter<CellDataPayload> {
        return moshi.adapter(CellDataPayload::class.java)
    }

    @Provides
    @Singleton
    @JvmStatic
    internal fun providesMoshiInstance(): Moshi {
        return Moshi.Builder()
            .build()
    }

}