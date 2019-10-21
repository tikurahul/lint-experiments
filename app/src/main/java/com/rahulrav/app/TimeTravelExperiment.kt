package com.rahulrav.app

/**
 * Marker for the experimental Time Travel feature set.
 * <p>
 * Use with caution! May be removed in a future (or past) release.
 */
@Experimental
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class TimeTravelExperiment
