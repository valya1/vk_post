package com.example.vktests.test_views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.atan2

class StickerView2 : AppCompatImageView {

    var mode: Int = MODE_NONE

    var dx = 0f
    var dy = 0f
    var _x = 0f
    var _y = 0f

    lateinit var lp: FrameLayout.LayoutParams

    var oldDist = .0f
    var d = .0f

    var newRot = .0f
    var angle = .0f

    var scaleDiff = .0f


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    companion object {```
        const val MODE_NONE = 0
        const val MODE_DRAG = 1
        const val MODE_ZOOM = 2
    }

    init {
        setOnTouchListener { v, event ->
            lp = layoutParams as FrameLayout.LayoutParams

            when (event.action and MotionEvent.ACTION_MASK) {

                MotionEvent.ACTION_DOWN -> {
                    mode = MODE_DRAG
                    dx = event.rawX - lp.leftMargin
                    dy = event.rawY - lp.topMargin
                }

                MotionEvent.ACTION_POINTER_DOWN -> {
                    oldDist = getSpacingBetweenPointers(event)
                    if (oldDist > 10f) {
                        mode = MODE_ZOOM
                    }
                    d = getRotation(event)
                }

                MotionEvent.ACTION_POINTER_UP -> mode = MODE_NONE

                MotionEvent.ACTION_MOVE -> {
                    if (mode == MODE_DRAG) {
                        _x = event.rawX
                        _y = event.rawY

                        layoutParams = with(lp) {
                            leftMargin = (_x - dx).toInt()
                            topMargin = (_y - dy).toInt()
                            this
                        }
                    } else if (mode == MODE_ZOOM && event.pointerCount == 2) {
                        newRot = getRotation(event)
                        val r = newRot - d // todo определить смысл переменных
                        angle = r
                        _x = event.rawX
                        _y = event.rawY

                        val newDist = getSpacingBetweenPointers(event)
                        if (newDist > 10f) {
                            val scale = newDist / oldDist * scaleX
                            if (scale > 0.6) {
                                scaleDiff = scale
                                scaleX = scale
                                scaleY = scale
                            }
                        }

                        animate()
                            .rotationBy(angle)
                            .setDuration(6)
                            .setInterpolator(LinearInterpolator())
                            .start()

                        _x = event.rawX
                        _y = event.rawY

                        layoutParams = with(lp) {
                            leftMargin = ((_x - dx) + scaleDiff).toInt()
                            topMargin = ((_y - dy) + scaleDiff).toInt()
                            this
                        }
                    }
                }
            }
            true
        }
    }

    private fun getSpacingBetweenPointers(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }


    private fun getRotation(event: MotionEvent): Float {
        val deltaX = (event.getX(0) - event.getX(1))
        val deltaY = (event.getY(0) - event.getY(1))
        val rads = atan2(deltaX, deltaY)
        return Math.toDegrees(rads.toDouble()).toFloat()
    }
}