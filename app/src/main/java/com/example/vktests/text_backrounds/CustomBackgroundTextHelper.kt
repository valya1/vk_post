package com.example.vktests.text_backrounds

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.Spanned

class CustomBackgroundTextHelper(
    val horizontalPadding: Int,
    verticalPadding: Int,
    backgroundDrawable: Drawable
) {
    private val renderer: TextRoundedBgRenderer.MultilineTextRenderer by lazy {
        TextRoundedBgRenderer.MultilineTextRenderer(
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
            backgroundDrawable = backgroundDrawable
        )
    }

    fun draw(canvas: Canvas, paint: Paint, text: Spanned, layout: Layout) {
        // ideally the calculations here should be cached since they are not cheap. However, proper
        // invalidation of the cache is required whenever anything related to text has changed.
        val spanStart = 0
        val spanEnd = text.length
        val startLine = layout.getLineForOffset(spanStart)
        val endLine = layout.getLineForOffset(spanEnd)

//        val startOffset = (layout.getPrimaryHorizontal(spanStart)
//                + -1 * layout.getParagraphDirection(startLine) * horizontalPadding).toInt()
//        val endOffset = (layout.getPrimaryHorizontal(spanEnd)
//
//                + layout.getParagraphDirection(endLine) * horizontalPadding).toInt()

//        renderer.draw(canvas, layout, startLine, endLine, startOffset, endOffset)
        renderer.draw(canvas, paint, layout, startLine, endLine)

    }
}