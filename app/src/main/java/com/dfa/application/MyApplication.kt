package com.dfa.application

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.dfa.utils.FontStyle


class MyApplication : MultiDexApplication() {
    private var customFontFamily: FontStyle? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        MultiDex.install(this)
        FirebaseApp.initializeApp(this)
        customFontFamily = FontStyle.instance
        customFontFamily!!.addFont("regular", "Montserrat-Regular_0.ttf")
        customFontFamily!!.addFont("semibold", "Montserrat-Medium_0.ttf")
        customFontFamily!!.addFont("bold", "Montserrat-SemiBold_0.ttf")
    }


    companion object {
        /**
         * @return ApplicationController singleton instance
         */
        @get:Synchronized
        lateinit var instance: MyApplication
    }


}