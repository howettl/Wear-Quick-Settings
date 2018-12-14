package com.howettl.wearquicksettings.common

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException


suspend fun <TResult> Task<TResult>.blockingAwait(dispatcher: CoroutineDispatcher = Dispatchers.IO): Result<TResult> {
    return try {
        withContext(dispatcher) {
            Result.Successful(Tasks.await(this@blockingAwait))
        }
    } catch (e: ExecutionException) {
        Result.Failed(e)
    } catch (e: InterruptedException) {
        Result.Interrupted()
    }
}

sealed class Result<T> {
    data class Successful<T>(val result: T) : Result<T>()
    data class Failed<T>(val error: Exception) : Result<T>()
    class Interrupted<T> : Result<T>()
}