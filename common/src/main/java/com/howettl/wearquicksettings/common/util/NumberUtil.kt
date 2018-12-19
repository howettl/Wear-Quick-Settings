package com.howettl.wearquicksettings.common.util

import android.content.Context
import android.util.TypedValue

fun Float.dpToPx(c: Context): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), c.resources.displayMetrics)

fun Int.dpToPx(c: Context): Float =
        this.toFloat().dpToPx(c)