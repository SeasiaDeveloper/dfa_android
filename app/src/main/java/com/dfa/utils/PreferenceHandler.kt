package com.dfa.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceHandler {
    const val FCM_DEVICE_TOKEN = "fcm_token"
    private const val PREF_NAME = "ngo_demo_app"
    private const val MODE = Context.MODE_PRIVATE
    const val DEVICE_ID = "device_id"
    const val LAT = "lat"
    const val LNG = "lng"
    const val PROFILE_JSON = "profileData"
    const val PROFILE_IMAGE="image"
    const val USER_ROLE = "userRole"
    const val CONTACT_NUMBER = "contact_number"
    const val USER_ID = "userId"
    const val AUTHORIZATION = "authorization"
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"
    const val APP_URL = "app_url"
    const val USER_FULLNAME = "user_fullname"

    const val NGO_CONTACT_NO = "ngo_contactNumber"
    const val NGO_NAME = "ngo_name"
    const val NGO_DIST = "ngo_dist"
    const val NGO_STATE = "ngo_state"
    const val NGO_PIN = "ngo_pinCode"
    const val NGO_EMAIL = "ngo_email"
    const val NGO_ADDRESS = "ngo_addressLine1"
    const val NGO_LATITUDE = "ngo_latitude"
    const val NGO_LONGITUDE = "ngo_longitude"

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