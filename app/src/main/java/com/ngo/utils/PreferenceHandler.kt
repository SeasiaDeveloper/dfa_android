package com.ngo.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceHandler {
    const val FCM_DEVICE_TOKEN = "fcm_token"
    private const val PREF_NAME = "ngo_demo_app"
    private const val MODE = Context.MODE_PRIVATE
    const val DEVICE_ID = "device_id"
    const val LAT = "lat"
    const val LNG = "lng"
    const val AUTHORIZATION = "authorization"
    const val DEVICE_TOKEN = "token"

    fun writeBoolean(context: Context, key: String, value: Boolean) {
        getEditor(context)
            .putBoolean(key, value).commit()
    }

    fun readBoolean(
        context: Context, key: String,
        defValue: Boolean
    ): Boolean {
        return getPreferences(context)
            .getBoolean(key, defValue)
    }

    fun writeString(context: Context, key: String, value: String) {
        getEditor(context).putString(key, value).commit()
    }

    fun readString(context: Context, key: String, defValue: String): String? {
        return getPreferences(context)
            .getString(key, defValue)
    }
    fun writeInteger(context: Context, key: String, value: Int) {
        getEditor(context).putInt(key, value).commit()
    }

    fun readInteger(context: Context, key: String, defValue: Int): Int {
        return getPreferences(context).getInt(key, defValue)
    }
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, MODE)
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        return getPreferences(context).edit()
    }

    fun clearPreferences(context: Context) {
        getEditor(context).clear().apply()

    }
}