package com.dfa.firebase

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.dfa.R
import com.dfa.pojo.response.NotificationResponse
import com.dfa.ui.home.fragments.home.view.HomeActivity
import com.dfa.utils.PreferenceHandler

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "FirebaseMessagingService"
    private val CHANNEL_ID = "channel_01"

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("Mytoken", p0)
        PreferenceHandler.writeString(this, PreferenceHandler.FCM_DEVICE_TOKEN, p0)
    }

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Message: ${remoteMessage.from}")

        if (remoteMessage.data != null) {
            val notificationResponse = NotificationResponse()
            notificationResponse.complaint_id = remoteMessage.data.get("complaint_id")
            notificationResponse.description = remoteMessage.data.get("description")
            notificationResponse.report_data = remoteMessage.data.get("report_data")
            notificationResponse.report_time = remoteMessage.data.get("report_time")
            notificationResponse.username = remoteMessage.data.get("username")
            notificationResponse.is_notify = remoteMessage.data.get("is_notify")
            notificationResponse.crime_type = remoteMessage.data.get("crime_type")
            notificationResponse.latitude = remoteMessage.data.get("latitude")
            notificationResponse.longitude = remoteMessage.data.get("longitude")
            notificationResponse.urgency = remoteMessage.data.get("urgency")

            showNotification("DFA App", notificationResponse)
        }
    }

    private fun showNotification(title: String?, obj: NotificationResponse) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_ONE_SHOT
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("DFA ")
            .setContentText("New Complaint")
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())


        val refreshChatIntent = Intent("policeJsonReceiver")
        refreshChatIntent.putExtra("notificationResponse", obj)
        sendBroadcast(refreshChatIntent)
    }
}