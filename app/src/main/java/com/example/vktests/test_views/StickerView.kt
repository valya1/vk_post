package com.example.vktests.test_views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import android.widget.ImageView
import com.almeros.android.multitouch.MoveGestureDetector
import com.almeros.android.multitouch.RotateGestureDetector
import com.example.vktests.touch_listeners.MoveListener
import com.example.vktests.touch_listeners.RotateListener
import com.example.vktests.touch_listeners.ScaleListener

class StickerView : ImageView {


    private val mRotateListener = RotateListener()
    private val mMoveListener = MoveListener()
    private val mScaleListener = ScaleListener()

    val mScaleDetector: ScaleGestureDetector = ScaleGestureDetector(context, mScaleListener)
    val mMoveDetector: MoveGestureDetector = MoveGestureDetector(context, mMoveListener)
    val mRotateDetector: RotateGestureDetector = RotateGestureDetector(context, mRotateListener)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {

        scaleType = ScaleType.MATRIX

        setOnTouchListener { v, event ->

            mScaleDetector.onTouchEvent(event)
//            mScaleDetector.isQuickScaleEnabled
            mMoveDetector.onTouchEvent(event)
//            mRotateDetector.onTouchEvent(event)

            layoutParams = with(layoutParams as FrameLayout.LayoutParams) {

                val stickerHeight = v.height
                val stickerWidth = v.width
                val scaleFactor = mScaleDetector.scaleFactor

                val scaleCenterY = (stickerHeight * scaleFactor) / 2
                val scaleCenterX = (stickerWidth * scaleFactor) / 2

//                marginStart = (mMoveListener.focusX).toInt()
//                topMargin = (mMoveListener.focusY).toInt()
                this
            }

            scaleX = mScaleListener.scaleFactor
            scaleY = mScaleListener.scaleFactor

//            imageMatrix = with(matrix) {
//                val scaleFactor = mScaleDetector.scaleFactor
//                postScale(scaleFactor, scaleFactor)
//                postRotate(mRotateDetector.rotationDegreesDelta)
//                this
//            }
            true
        }
    }
}