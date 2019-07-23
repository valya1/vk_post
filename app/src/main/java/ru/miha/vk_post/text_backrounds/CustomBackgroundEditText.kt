package ru.miha.vk_post.text_backrounds

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.graphics.withTranslation
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Layout
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.withSave
import ru.miha.vk_post.R


class CustomBackgroundEditText : AppCompatEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @ColorInt
    private var mBgColor: Int? = null

    lateinit var mAllRoundedCornersDrawable: Drawable
    lateinit var mTopRoundedCornersDrawable: Drawable
    lateinit var mBottomRoundedCornersDrawable: Drawable
    lateinit var mNotRoundedCornersDrawable: Drawable

    private var mPath = Path()

    var mPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        if (mBgColor != null) {

            val verticalPadding = paddingBottom
            val horizontalPadding = paddingLeft

            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat()) {
                val startLine = 0
                val endLine = layout.getLineForOffset(text?.length ?: 0)

                for (line in startLine..endLine) {

                    if (layout.getLineWidth(line) == 0.0f) continue

                    val isFirst = line == startLine
                    val isLast = line == endLine
                    val biggerOrEqualThanNext = isLast || layout.getLineRight(line) >= layout.getLineRight(line + 1)
                    val biggerOrEqualThanPrevious =
                        isFirst || layout.getLineRight(line) >= layout.getLineRight(line - 1)

                    val lineTop = getLineTop(layout, line)
                    val lineBottom = getLineBottom(layout, line)

                    val lineLeft = layout.getLineLeft(line) - horizontalPadding
                    val lineRight = layout.getLineRight(line).toInt() + horizontalPadding

                    val halfHorizontalPadding = horizontalPadding / 2.0f
                    val halfVerticalPadding = verticalPadding / 2.0f

                    when {
                        biggerOrEqualThanNext && biggerOrEqualThanPrevious -> {
                            mAllRoundedCornersDrawable.drawOnLine(
                                canvas,
                                lineLeft.toInt(),
                                lineTop + halfVerticalPadding.toInt(),
                                lineRight,
                                lineBottom - halfVerticalPadding.toInt()
                            )
                        }

                        biggerOrEqualThanNext && !biggerOrEqualThanPrevious -> {
                            mBottomRoundedCornersDrawable.drawOnLine(
                                canvas,
                                lineLeft.toInt(),
                                lineTop + Math.ceil(halfVerticalPadding.toDouble()).toInt(),
                                lineRight,
                                lineBottom - halfVerticalPadding.toInt()
                            )
                        }

                        !biggerOrEqualThanNext && biggerOrEqualThanPrevious -> {
                            mTopRoundedCornersDrawable.drawOnLine(
                                canvas,
                                lineLeft.toInt(),
                                lineTop + halfVerticalPadding.toInt(),
                                lineRight,
                                lineBottom - Math.ceil(halfVerticalPadding.toDouble()).toInt()
                            )
                        }

                        !biggerOrEqualThanNext && !biggerOrEqualThanPrevious -> {
                            mNotRoundedCornersDrawable.drawOnLine(
                                canvas,
                                lineLeft.toInt(),
                                lineTop + Math.ceil(halfVerticalPadding.toDouble()).toInt(),
                                lineRight,
                                lineBottom -  Math.ceil(halfVerticalPadding.toDouble()).toInt()
                            )
                        }
                    }

                    //скругленные края вниз
                    if (!biggerOrEqualThanNext) {
                        with(mPath) {

                            var startPointX = lineRight.toFloat()
                            var startPointY = lineBottom.toFloat() - verticalPadding * 1.5f

                            var endPointX =
                                Math.min(
                                    startPointX + halfHorizontalPadding,
                                    layout.getLineRight(line + 1) + halfHorizontalPadding
                                )
                            var endPointY = lineBottom - halfVerticalPadding.toInt().toFloat()

                            if (endPointX > startPointX) {
                                canvas.withSave {
                                    reset()
                                    clipRect(
                                        startPointX.toInt(), startPointY.toInt(), endPointX.toInt(),
                                        (layout.getLineTop(line + 1) + halfHorizontalPadding).toInt()
                                    )

                                    moveTo(startPointX, startPointY)
                                    quadTo(lineRight.toFloat(), lineBottom.toFloat(), endPointX, endPointY)
                                    lineTo(startPointX, endPointY)
                                    close()
                                    canvas.drawPath(mPath, mPaint)
                                }
                            }


                            startPointX = lineLeft
                            startPointY = lineBottom.toFloat() - verticalPadding * 1.5f

                            endPointX = Math.max(
                                startPointX - halfHorizontalPadding,
                                layout.getLineLeft(line + 1) - halfHorizontalPadding
                            )
                            endPointY = lineBottom - halfVerticalPadding.toInt().toFloat()

                            if (endPointX < startPointX) {
                                canvas.withSave {
                                    clipRect(
                                        startPointX, startPointY,
                                        endPointX, layout.getLineTop(line + 1) + halfHorizontalPadding
                                    )
                                    reset()
                                    moveTo(startPointX, startPointY)
                                    quadTo(lineLeft, lineBottom.toFloat(), endPointX, endPointY)
                                    lineTo(startPointX, endPointY)
                                    close()
                                    drawPath(mPath, mPaint)
                                }

                            }
                        }
                    }

                    //скругленные края наверх
                    if (!biggerOrEqualThanPrevious) {
                        with(mPath) {

                            var startPointX = lineLeft
                            var startPointY = lineTop.toFloat() + verticalPadding * 1.5f

                            var endPointX = Math.max(
                                startPointX - halfHorizontalPadding,
                                layout.getLineLeft(line - 1) - halfHorizontalPadding
                            )
                            var endPointY = lineTop + halfVerticalPadding

                            if (endPointX < startPointX) {
                                canvas.withSave {
                                    clipRect(
                                        endPointX, layout.getLineBottom(line - 1) + halfVerticalPadding,
                                        startPointX, startPointY
                                    )
                                    reset()
                                    moveTo(startPointX, startPointY)
                                    quadTo(lineLeft, lineTop.toFloat(), endPointX, endPointY)
                                    lineTo(startPointX, endPointY)
                                    close()
                                    canvas.drawPath(mPath, mPaint)
                                }
                            }

                            startPointX = lineRight.toFloat()
                            startPointY = lineTop.toFloat() + verticalPadding * 1.5f
                            endPointX = Math.min(
                                startPointX + halfHorizontalPadding,
                                layout.getLineRight(line - 1) + halfHorizontalPadding
                            )
                            endPointY = lineTop + halfVerticalPadding

                            if (endPointX > startPointX) {
                                canvas.withSave {
                                    clipRect(
                                        endPointX, layout.getLineBottom(line - 1) + halfVerticalPadding,
                                        startPointX, startPointY
                                    )
                                    reset()
                                    moveTo(startPointX, startPointY)
                                    quadTo(lineRight.toFloat(), lineTop.toFloat(), endPointX, endPointY)
                                    lineTo(startPointX, endPointY)
                                    close()
                                    canvas.drawPath(mPath, mPaint)
                                }
                            }
                        }
                    }
                }
            }
        }
        super.onDraw(canvas)
    }

    protected fun getLineTop(layout: Layout, line: Int): Int {
        return layout.getLineTopWithoutPadding(line)
    }

    protected fun getLineBottom(layout: Layout, line: Int): Int {
        return layout.getLineBottomWithoutPadding(line) + paddingBottom
    }

    fun setBackgroundColorFotText(@ColorRes color: Int?) {
        if (color != null) {
            mBgColor = ContextCompat.getColor(context, color).also {
                invalidateColorStates(it)
            }
        } else {
            mBgColor = null
        }
        invalidate()
    }

    fun invalidateColorStates(@ColorInt color: Int) {
        mAllRoundedCornersDrawable =
            DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.all_rounded_rect)!!)
                .apply {
                    DrawableCompat.setTint(this, color)
                }
        mTopRoundedCornersDrawable =
            DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.top_rounded_rect)!!)
                .apply {
                    DrawableCompat.setTint(this, color)
                }

        mBottomRoundedCornersDrawable =
            DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.bottom_rounded_rect)!!)
                .apply {
                    DrawableCompat.setTint(this, color)
                }

        mNotRoundedCornersDrawable =
            DrawableCompat.wrap(ContextCompat.getDrawable(context, R.drawable.not_rounded_rect)!!)
                .apply {
                    DrawableCompat.setTint(this, color)
                }

        mPaint.color = color
    }

    fun Drawable.drawOnLine(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) {
        setBounds(left, top, right, bottom)
        draw(canvas)
    }


}