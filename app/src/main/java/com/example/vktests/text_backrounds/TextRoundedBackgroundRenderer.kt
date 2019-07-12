package com.example.vktests.text_backrounds

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Layout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.vktests.R
import kotlin.math.max
import kotlin.math.min


class MultilineTextRenderer(
    val context: Context,
    val horizontalPadding: Int,
    val verticalPadding: Int,
    @ColorRes val backgroundColor: Int
) {

    private var bgColor = ContextCompat.getColor(context, backgroundColor)

    private var paint = Paint().apply {
        color = bgColor
        isAntiAlias = true
    }

    val allRoundedCornersDrawable =
        DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.all_rounded_rect)!!)
            .apply {
                DrawableCompat.setTint(this, bgColor)
            }
    val topRoundedCornersDrawable =
        DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.top_rounded_rect)!!)
            .apply {
                DrawableCompat.setTint(this, bgColor)
            }

    val bottomRoundedCornersDrawable =
        DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.bottom_rounded_rect)!!)
            .apply {
                DrawableCompat.setTint(this, bgColor)
            }

    val notRoundedCornersDrawable =
        DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.not_rounded_rect)!!)
            .apply {
                DrawableCompat.setTint(this, bgColor)
            }


    protected fun getLineTop(layout: Layout, line: Int): Int {
        return layout.getLineTopWithoutPadding(line)
    }

    protected fun getLineBottom(layout: Layout, line: Int): Int {
        return layout.getLineBottomWithoutPadding(line) + verticalPadding
    }


    fun draw(
        canvas: Canvas, paint: Paint, layout: Layout, startLine: Int, endLine: Int, startOffset: Int, endOffset: Int
    ) {

        for (line in startLine..endLine) {
            if (layout.getLineWidth(line) == 0.0f) continue
            val isFirst = line == startLine
            val isLast = line == endLine
            val biggerOrEqualThanNext = isLast || layout.getLineWidth(line) >= layout.getLineWidth(line + 1)
            val biggerOrEqualThanPrevious = isFirst || layout.getLineWidth(line) >= layout.getLineWidth(line - 1)


            val lineTop = getLineTop(layout, line)
            val lineBottom = getLineBottom(layout, line)

            val lineLeft = layout.getLineLeft(line).toInt()
            val lineRight = min(layout.getLineRight(line).toInt() + horizontalPadding, layout.width)

            when {

                biggerOrEqualThanNext && biggerOrEqualThanPrevious -> {

                    allRoundedCornersDrawable?.drawOnLine(
                        canvas,
                        lineLeft - horizontalPadding,
                        lineTop + (verticalPadding / 2.0).toInt(),
                        lineRight,
                        lineBottom - (verticalPadding / 2.0).toInt()
                    )
                }

                biggerOrEqualThanNext && !biggerOrEqualThanPrevious -> {
                    bottomRoundedCornersDrawable?.drawOnLine(
                        canvas,
                        lineLeft - horizontalPadding,
                        lineTop + (verticalPadding / 2.0).toInt(),
                        lineRight,
                        lineBottom - (verticalPadding / 2.0).toInt()
                    )
                }

                !biggerOrEqualThanNext && biggerOrEqualThanPrevious -> {
                    topRoundedCornersDrawable?.drawOnLine(
                        canvas,
                        lineLeft - horizontalPadding,
                        lineTop + (verticalPadding / 2.0).toInt(),
                        lineRight,
                        lineBottom - (verticalPadding / 2.0).toInt()
                    )
                    Path().apply {

                        moveTo(lineRight.toFloat(), lineBottom.toFloat() - 20)
                        quadTo(
                            lineRight.toFloat(), lineBottom.toFloat(), lineRight.toFloat() + 10,
                            lineBottom - (verticalPadding / 2.0f)
                        )
                        paint.apply {
                            style = Paint.Style.STROKE
                        }
                        canvas.drawPath(this, paint)
//                        canvas.drawLine(
//                            lineRight.toFloat(), lineBottom.toFloat() - 20,
//                            lineRight.toFloat(), lineBottom - (verticalPadding / 2.0f),
//                            paint
//                        )
//                        canvas.drawLine(
//                            lineRight.toFloat(), lineBottom - (verticalPadding / 2.0f),
//                            lineRight.toFloat() + 10, lineBottom - (verticalPadding / 2.0f),
//                            paint
//                        )
                    }
                }

                !biggerOrEqualThanNext && !biggerOrEqualThanPrevious -> {
                    notRoundedCornersDrawable?.drawOnLine(
                        canvas,
                        lineLeft - horizontalPadding,
                        lineTop + (verticalPadding / 2.0).toInt(),
                        lineRight,
                        lineBottom - (verticalPadding / 2.0).toInt()
                    )

                }
            }
        }
    }
}

fun Drawable.drawOnLine(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) {
    setBounds(left, top, right, bottom)
    draw(canvas)
}

val smoothCornerPaint = Paint().apply {
    color = Color.RED
    isAntiAlias = true
    style = Paint.Style.FILL
}

fun drawFilledSmoothCorner(radiusPoint: PointF, targetPoint: PointF) {

    val path = Path().apply {
        quadTo(radiusPoint.x, radiusPoint.y, targetPoint.x, targetPoint.y)
    }

}