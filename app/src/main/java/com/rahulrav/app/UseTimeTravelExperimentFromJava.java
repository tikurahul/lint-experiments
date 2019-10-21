package com.rahulrav.app;

import kotlin.UseExperimental;

@SuppressWarnings("unused")
class UseTimeTravelExperimentFromJava {
    @TimeTravelExperiment
    void setTimeToNow() {
        new TimeTravelProvider().setTime(System.currentTimeMillis());
    }

    @UseExperimental(markerClass = TimeTravelExperiment.class)
    void setTimeToEpoch() {
        new TimeTravelProvider().setTime(0);
    }

    public void violateTimeTravelAccords() {
        new TimeTravelProvider().setTime(-1);
    }
}
