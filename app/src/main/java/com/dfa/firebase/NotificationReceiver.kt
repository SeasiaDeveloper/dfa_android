package com.dfa.firebase

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.legacy.content.WakefulBroadcastReceiver
import com.dfa.R
import com.dfa.application.MyApplication

class NotificationReceiver : WakefulBroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        playNotificationSound()
    }

//    fun playNotificationSound(context: Context?) {
//        try {
//            val notification =
//                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            val r =
//                RingtoneManager.getRingtone(context, notification)
//            r.play()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    fun playNotificationSound() {
        try {

            val mp: MediaPlayer = MediaPlayer.create(MyApplication.instance, R.raw.siren)
            //mp.setVolume(0.8F, 0.8F);

            mp.start()



            val th = Thread(Runnable {
                try {
                    Thread.sleep(5000) //30000 is for 30 seconds, 1 sec =1000
                    if (mp.isPlaying())
                        mp.setLooping(false)
                    mp.stop() // for stopping the ringtone
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    mp.stop()
                }
            })
            th.start()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}