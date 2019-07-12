package ru.miha.vk_post.test_views

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.atan2
import kotlin.math.sqrt


class StickerView : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        const val MODE_NONE = 0
        const val MODE_DRAG = 1
        const val MODE_ZOOM = 2

        const val MIN_SCALE = 0.4F
    }


    private var mode: Int = MODE_NONE

    private var prevX = 0f
    private var prevY = 0f
    private var oldDist = .0f
    private var mPrevRotation = .0f
    private var mAngle = .0f
    private var scaleDiff = .0f

    val mapToScreenMatrix = Matrix()

    var onMoveListener: OnMoveListener? = null

    init {
        setOnTouchListener { v, event ->

            when (event.action and MotionEvent.ACTION_MASK) {

                MotionEvent.ACTION_DOWN -> {
                    mode = MODE_DRAG
                    prevX = event.rawX
                    prevY = event.rawY


                    var parent = parent
                    while (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true)
                        parent = parent.parent
                    }
                }

                MotionEvent.ACTION_POINTER_DOWN -> {

                    oldDist = getSpacingBetweenPointers(event)
                    if (oldDist > 10f) {
                        mode = MODE_ZOOM
                    }

                    mPrevRotation = getRotation(
                        event.getX(0), event.getY(0),
                        event.getX(1), event.getY(1)
                    )
                }

                MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> {
                    mode = MODE_NONE

                    var parent = parent
                    while (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(false)
                        parent = parent.parent
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    if (mode == MODE_DRAG) {

                        val dx = event.rawX - prevX
                        val dy = event.rawY - prevY

                        v.x += dx
                        v.y += dy
                        prevX = event.rawX
                        prevY = event.rawY
                        onMoveListener?.onStickerMove(this, dx, dy)

                    } else if (mode == MODE_ZOOM && event.pointerCount == 2) {

                        mapToScreenMatrix.run {
                            setRotate(rotation)
                            postTranslate(v.x, v.y)
                        }

                        val viewToScreenCoords1 = floatArrayOf(event.getX(0), event.getY(0))
                        val viewToScreenCoords2 = floatArrayOf(event.getX(1), event.getY(1))

                        mapToScreenMatrix.mapPoints(viewToScreenCoords1)
                        mapToScreenMatrix.mapPoints(viewToScreenCoords2)


                        mAngle = getRotation(
                            viewToScreenCoords1[0], viewToScreenCoords1[1],
                            viewToScreenCoords2[0], viewToScreenCoords2[1]
                        ) - mPrevRotation

                        rotation = -mAngle

                        val newDist = getSpacingBetweenPointers(event)
                        if (newDist > 10f) {
                            val scale = newDist / oldDist * scaleX
                            if (scale >= MIN_SCALE) {
                                scaleDiff = scale
                                scaleX = scale
                                scaleY = scale
                            }
                        }

                        v.x += ((event.rawX - prevX) + scaleDiff)
                        v.y += ((event.rawY - prevY) + scaleDiff)
                        prevX = event.rawX
                        prevY = event.rawY
                    }
                }
            }
            true
        }
    }


    private fun getSpacingBetweenPointers(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun getRotation(x1: Float, y1: Float, x2: Float, y2: Float): Float {

        val deltaX = (x1 - x2)
        val deltaY = (y1 - y2)

        val rads = atan2(deltaX, deltaY)
        return Math.toDegrees(rads.toDouble()).toFloat()
    }

    interface OnMoveListener {
        fun onStickerMove(sticker: StickerView, dx: Float, dy: Float)
    }
}