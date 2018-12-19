package com.howettl.wearquicksettings.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.howettl.wearquicksettings.R
import com.howettl.wearquicksettings.common.util.dpToPx
import com.howettl.wearquicksettings.common.util.loadBitmapFromResId

class CircleToggle @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private val iconDisabled: Bitmap
    private val iconEnabled: Bitmap
    private val iconPaint: Paint = Paint()
    private val bgPaintDisabled: Paint
    private val bgPaintEnabled: Paint
    private val radius: Float

    var isChecked: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    init {
        val bgColourDisabled: Int
        val bgColourEnabled: Int
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CircleToggle, 0, 0)
            try {
                iconDisabled = context.loadBitmapFromResId(ta.getResourceId(R.styleable.CircleToggle_iconDisabled, R.drawable.wifi_off)) ?: throw IllegalArgumentException("Unable to load disabled icon")
                iconEnabled = context.loadBitmapFromResId(ta.getResourceId(R.styleable.CircleToggle_iconEnabled, R.drawable.wifi_on)) ?: throw IllegalArgumentException("unable to load enabled icon")
                bgColourDisabled = ta.getColor(R.styleable.CircleToggle_bgDisabled, Color.BLACK)
                bgColourEnabled = ta.getColor(R.styleable.CircleToggle_bgEnabled, Color.CYAN)
                radius = ta.getDimension(R.styleable.CircleToggle_radius, 24.dpToPx(context))
            } finally {
                ta.recycle()
            }
        } else {
            iconDisabled = context.loadBitmapFromResId(R.drawable.wifi_off) ?: throw IllegalArgumentException("unable to load disabled icon")
            iconEnabled = context.loadBitmapFromResId(R.drawable.wifi_on) ?: throw IllegalArgumentException("unable to load enabled icon")
            bgColourDisabled = Color.BLACK
            bgColourEnabled = Color.CYAN
            radius = 24.dpToPx(context)
        }

        bgPaintDisabled = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColourDisabled
        }
        bgPaintEnabled = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColourEnabled
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener { v ->
            listener?.let {
                isChecked = !isChecked
                it.onClick(v)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension((radius * 2).toInt(), (radius * 2).toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            it.drawCircle(radius, radius, radius, if (isChecked) bgPaintEnabled else bgPaintDisabled)
            val icon = if (isChecked) iconEnabled else iconDisabled
            it.drawBitmap(icon, radius - (icon.width / 2f), radius - (icon.height / 2f), iconPaint)
        }
    }
}