package com.howettl.wearquicksettings.common.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.loadBitmapFromResId(@DrawableRes resId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(this, resId)
    return when (drawable) {
        is BitmapDrawable -> BitmapFactory.decodeResource(resources, resId)
        is VectorDrawable -> {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
        else -> null
    }
}