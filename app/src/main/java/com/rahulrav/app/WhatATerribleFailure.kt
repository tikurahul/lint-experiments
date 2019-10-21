package com.rahulrav.app

import android.util.Log

@Suppress("unused")
class WhatATerribleFailure {
    fun <T> logAsWtf(clazz: Class<T>, message: String) {
        Log.wtf(clazz.name, message)

        wtf(message)
    }

    fun wtf(message: String) {
        Log.d("TAG", message)
    }
}
