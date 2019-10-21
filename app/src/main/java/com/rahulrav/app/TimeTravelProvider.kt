package com.rahulrav.app

@Suppress("unused")
@TimeTravelExperiment
class TimeTravelProvider {
    var timeInternal: Long = 0

    fun setTime(timestamp: Long) {
        timeInternal = timestamp
    }
}
