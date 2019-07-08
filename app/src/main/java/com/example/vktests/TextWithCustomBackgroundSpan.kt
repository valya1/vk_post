package com.example.vktests

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt

class TextWithCustomBackgroundSpan(@ColorInt val backgroundColor: Int, @ColorInt val textColor: Int) :
    ReplacementSpan() {
    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        p: Paint
    ) {
        val paint = Paint(p)
        val rect = RectF(x, top.toFloat(), x + (text?.length ?: 0).toFloat(), bottom.toFloat())
        paint.color = backgroundColor
        canvas.drawRoundRect(rect, 10f, 10f, paint)

        paint.color = textColor
        canvas.drawText(text ?: "", start, end, x, y.toFloat(), paint)
    }


    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?) = 0


}