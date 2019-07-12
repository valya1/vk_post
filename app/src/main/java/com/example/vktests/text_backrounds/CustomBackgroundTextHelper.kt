package com.example.vktests.text_backrounds

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.Spanned
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

class CustomBackgroundTextHelper(
    val context: Context,
    val horizontalPadding: Int,
    val verticalPadding: Int,
    @ColorRes val backgroundColor: Int
) {
    private val renderer: MultilineTextRenderer by lazy {
        MultilineTextRenderer(
            context = context,
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
            backgroundColor = backgroundColor
        )
    }

    fun draw(canvas: Canvas, paint: Paint, text: Spanned, layout: Layout) {
        // ideally the calculations here should be cached since they are not cheap. However, proper
        // invalidation of the cache is required whenever anything related to text has changed.
        val spanStart = 0
        val spanEnd = text.length
        val startLine = layout.getLineForOffset(spanStart)
        val endLine = layout.getLineForOffset(spanEnd)

        val startOffset = (layout.getPrimaryHorizontal(spanStart) - horizontalPadding).toInt()

        val endOffset = (layout.getPrimaryHorizontal(spanEnd)
                + layout.getParagraphDirection(endLine) * horizontalPadding).toInt()

//        renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
        renderer.draw(canvas,  paint, layout, startLine, endLine, startOffset, endOffset)

    }
}