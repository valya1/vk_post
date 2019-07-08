package com.example.vktests

import android.content.Context
import android.util.AttributeSet
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import android.widget.ImageView
import com.almeros.android.multitouch.MoveGestureDetector
import com.almeros.android.multitouch.RotateGestureDetector
import com.example.vktests.touch_listeners.MoveListener
import com.example.vktests.touch_listeners.RotateListener

class StickerView : ImageView  {


    private val mRotateListener = RotateListener()
    private val mMoveListener = MoveListener()

    val mScaleDetector: ScaleGestureDetector =
        ScaleGestureDetector(context, ScaleGestureDetector.SimpleOnScaleGestureListener())
    val mMoveDetector: MoveGestureDetector =
        MoveGestureDetector(context, MoveGestureDetector.SimpleOnMoveGestureListener())
    val mRotateDetector: RotateGestureDetector =
        RotateGestureDetector(context, RotateGestureDetector.SimpleOnRotateGestureListener())

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {

        scaleType = ScaleType.MATRIX

        setOnTouchListener { v, event ->

            mScaleDetector.onTouchEvent(event)
//            mMoveDetector.onTouchEvent(event)
//            mRotateDetector.onTouchEvent(event)


            layoutParams = with(layoutParams as FrameLayout.LayoutParams) {

                val stickerHeight = v.height
                val stickerWidth = v.width
                val scaleFactor = mScaleDetector.scaleFactor

                val scaleCenterY = (stickerHeight * scaleFactor) / 2
                val scaleCenterX = (stickerWidth * scaleFactor) / 2

                marginStart += (mMoveDetector.focusDelta.x).toInt()
                topMargin += (mMoveDetector.focusDelta.y).toInt()
                this
            }

            scaleX *= mScaleDetector.scaleFactor
            scaleY *= mScaleDetector.scaleFactor


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