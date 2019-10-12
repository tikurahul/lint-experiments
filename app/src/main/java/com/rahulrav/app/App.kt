package com.rahulrav.app

import android.app.Application
import androidx.work.Configuration

class App : Application(), Configuration.Provider {
    override fun getWorkManagerConfiguration() = Configuration.Builder().build()
}
