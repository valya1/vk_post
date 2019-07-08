package com.example.vktests.touch_listeners

import com.almeros.android.multitouch.MoveGestureDetector

class MoveListener : MoveGestureDetector.SimpleOnMoveGestureListener() {

    var focusX = 0.0f
    private set
    var focusY = 0.0f
    private set

    override fun onMove(detector: MoveGestureDetector): Boolean {
        val d = detector.focusDelta
        focusX = d.x
        focusY = d.y
        return true
    }
}