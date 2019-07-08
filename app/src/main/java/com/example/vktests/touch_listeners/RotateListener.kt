package com.example.vktests.touch_listeners

import com.almeros.android.multitouch.RotateGestureDetector

class RotateListener : RotateGestureDetector.SimpleOnRotateGestureListener() {

    var rotationDegree = 0.0f
        private set

    override fun onRotate(detector: RotateGestureDetector): Boolean {
        rotationDegree -= detector.rotationDegreesDelta
        return true
    }
}