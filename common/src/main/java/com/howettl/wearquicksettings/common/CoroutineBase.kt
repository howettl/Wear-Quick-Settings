package com.howettl.wearquicksettings.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

interface CoroutineBase: CoroutineScope {

    val coroutineJob: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + coroutineJob

    fun cancelJob() {
        coroutineJob.cancel()
    }
}