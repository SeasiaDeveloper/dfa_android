package com.ngo.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ngo.utils.PreferenceHandler

class MyInstanceIDListenerService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("token",s)
        PreferenceHandler.writeString(this, PreferenceHandler.FCM_DEVICE_TOKEN, s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
    }
}