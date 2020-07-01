package com.dfa.application

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.dfa.R
import com.dfa.utils.FontStyle
import com.google.firebase.FirebaseApp
//import org.acra.ACRA
//import org.acra.ReportField
//import org.acra.ReportingInteractionMode
//import org.acra.annotation.ReportsCrashes
//import org.acra.sender.HttpSender

//@ReportsCrashes(
//    formUri = "",
//    mailTo = "ansarisaira@seasia.in",
//    customReportContent = [ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.BRAND, ReportField.CUSTOM_DATA, ReportField.INITIAL_CONFIGURATION, ReportField.CRASH_CONFIGURATION, ReportField.USER_CRASH_DATE, ReportField.STACK_TRACE, ReportField.LOGCAT],
//    resToastText = R.string.crash_toast_text,
//    mode = ReportingInteractionMode.TOAST)

class MyApplication : MultiDexApplication() {
    private var customFontFamily: FontStyle? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        MultiDex.install(this)
        //ACRA.init(this);
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