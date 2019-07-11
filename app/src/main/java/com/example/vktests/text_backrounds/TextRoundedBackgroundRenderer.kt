package com.example.vktests.text_backrounds

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Layout
import androidx.core.content.ContextCompat
import com.example.vktests.R
import kotlin.math.max
import kotlin.math.min


internal abstract class TextRoundedBgRenderer(
    val horizontalPadding: Int,
    val verticalPadding: Int
) {

    protected fun getLineTop(layout: Layout, line: Int): Int {
        return layout.getLineTopWithoutPadding(line)
    }

    protected fun getLineBottom(layout: Layout, line: Int): Int {
        return layout.getLineBottomWithoutPadding(line) + verticalPadding
    }


    internal class MultilineTextRenderer(
        horizontalPadding: Int,
        verticalPadding: Int,
        val backgroundDrawable: Drawable
    ) : TextRoundedBgRenderer(horizontalPadding, verticalPadding) {


        private val transparentPaint = Paint().apply {
            color = Color.TRANSPARENT
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN)
        }

        companion object {

            const val ROUNDED_CORNER_RX = 5.0f
            const val ROUNDED_CORNER_RY = 5.0f

            const val CIRCLE_RADIUS = 5.0f
        }


        fun draw(
            canvas: Canvas, context: Context, paint: Paint, layout: Layout, startLine: Int, endLine: Int,
            startOffset: Int, endOffset: Int
        ) {

            for (line in startLine..endLine) {

                val lineTop = getLineTop(layout, line)
                val lineBottom = getLineBottom(layout, line)
                val lineLeft = max(layout.getLineLeft(line).toInt() - horizontalPadding, 0)
                val lineRight = min(layout.getLineRight(line).toInt() + horizontalPadding, layout.width)
//                val lineLeft = layout.getLineLeft(line)
//                val lineRight = layout.getLineRight(line)

                backgroundDrawable.setBounds(
                    max(layout.getLineLeft(line).toInt() - horizontalPadding, 0),
                    lineTop + verticalPadding,
//                    lineTop + if (line in startLine + 1 until endLine) horizontalPadding else 0,
                    min(layout.getLineRight(line).toInt() + horizontalPadding, layout.width),
                    lineBottom
                )
                Path().
//                backgroundDrawable.setColorFilter(
//                    ContextCompat.getColor(context, R.color.white_semitransparent),
//                    PorterDuff.Mode.SCREEN
//                )
                backgroundDrawable.draw(canvas)

//                canvas.drawRoundRect(
//                    RectF(lineLeft.toFloat(), lineTop.toFloat(), lineRight.toFloat(), lineBottom.toFloat()),
//                    ROUNDED_CORNER_RX,
//                    ROUNDED_CORNER_RY,
//                    paint
//                )
//
//                canvas.drawRect(
//                    RectF(
//                        lineLeft - CIRCLE_RADIUS,
//                        lineBottom.toFloat(),
//                        lineRight + CIRCLE_RADIUS,
//                        lineBottom + CIRCLE_RADIUS
//                    ),
//                    paint
//                )

//                canvas.drawCircle(lineLeft.toFloat(), lineBottom.toFloat(), CIRCLE_RADIUS, paint)
//                canvas.drawCircle(lineRight.toFloat(), lineBottom.toFloat(), CIRCLE_RADIUS, paint)

//                backgroundDrawable.setBounds(
//                    max(layout.getLineLeft(line).toInt() - horizontalPadding, 0),
//                    lineTop + if (line in (startLine + 1)..endLine) verticalPadding else 0,
//                    min(layout.getLineRight(line).toInt() + horizontalPadding, layout.getLineRight(line).toInt()),
//                    lineBottom
//                )
//                backgroundDrawable.draw(canvas)
//                canvas.drawCircle()
            }
        }
    }
}