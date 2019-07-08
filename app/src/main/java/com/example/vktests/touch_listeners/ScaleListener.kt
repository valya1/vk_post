package com.example.vktests.touch_listeners

import android.view.ScaleGestureDetector
import kotlin.math.max
import kotlin.math.min

class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    companion object {
        const val MIN_SCALE = 0.1f
    }

    var scaleFactor = 0.5f
    private set

    override fun onScale(detector: ScaleGestureDetector): Boolean {

        scaleFactor *= detector.scaleFactor
        scaleFactor = max(MIN_SCALE, min(scaleFactor, 1.0f))
        return true
    }
}