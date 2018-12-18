package com.howettl.wearquicksettings.common.injection.module

import android.content.Context
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.Wearable
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object WearableModule {

    @Provides
    @Singleton
    @JvmStatic
    internal fun providesWearableDataClient(context: Context): DataClient {
        return Wearable.getDataClient(context)
    }

}