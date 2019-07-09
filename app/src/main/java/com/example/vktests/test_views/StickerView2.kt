package com.example.vktests.test_views

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.atan2
import kotlin.math.sqrt


class StickerView2 : AppCompatImageView {

    var mode: Int = MODE_NONE

    var dx = 0f
    var dy = 0f
    var _x = 0f
    var _y = 0f

    var oldDist = .0f
    var mPrevRotation = .0f

    var mAngle = .0f

    var scaleDiff = .0f

    val mapToScreenMatrix = Matrix()


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        const val MODE_NONE = 0
        const val MODE_DRAG = 1
        const val MODE_ZOOM = 2
    }

    init {
        setOnTouchListener { v, event ->

            when (event.action and MotionEvent.ACTION_MASK) {

                MotionEvent.ACTION_DOWN -> {
                    mode = MODE_DRAG
//                    if (dx == 0f)
                        dx = event.rawX
//                    if (dy == 0f)
                        dy = event.rawY
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

                MotionEvent.ACTION_POINTER_UP -> mode = MODE_NONE

                MotionEvent.ACTION_MOVE -> {
                    if (mode == MODE_DRAG) {
                        _x = event.rawX
                        _y = event.rawY

//                        println("x-dx: ${_x-dx}" )
//                        println("y-dx: ${_x-dx}" )

                        v.x = _x - dx
                        v.y = _y - dy
//                        dx = v.x
//                        dy = v.y

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
                            if (scale > 0.6) {
                                scaleDiff = scale
                                scaleX = scale
                                scaleY = scale
                            }
                        }

                        _x = event.rawX
                        _y = event.rawY

                        v.x = ((_x - dx) + scaleDiff)
                        v.y = ((_y - dy) + scaleDiff)
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
}