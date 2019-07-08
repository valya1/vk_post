package com.example.vktests.touch_listeners

import android.util.Log
import android.view.ScaleGestureDetector
import kotlin.math.max
import kotlin.math.min

class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    companion object {
        const val MIN_SCALE = 0.1f
    }

    var scaleFactor = 1f
        private set

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {

        Log.i(this.toString() + "scale started", detector.scaleFactor.toString())
        return super.onScaleBegin(detector)
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        Log.i(this.toString() + "scale end", detector.scaleFactor.toString())
        super.onScaleEnd(detector)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {

        Log.i(this.toString() + "scale progress", detector.scaleFactor.toString())
        scaleFactor *= detector.scaleFactor
//        scaleFactor = max(MIN_SCALE, min(scaleFactor, 1.0f))
        scaleFactor = max(MIN_SCALE, Math.min(scaleFactor, 5.0f))
        return super.onScale(detector)
    }
}