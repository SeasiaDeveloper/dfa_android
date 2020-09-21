package com.dfa.application

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.dfa.R
import com.dfa.databinding.DialogUpdateVersionBinding
import com.dfa.ui.generalpublic.VideoPlayerActivity
import org.jsoup.Jsoup




 class GetVersionCode(context1: Context): AsyncTask<Void, String, String>() {
var context=context1
     protected override fun doInBackground(vararg voids: Void): String {
        var newVersion: String? = null

        try {
            newVersion =
                Jsoup.connect("https://play.google.com/store/apps/details?id=" + MyApplication.instance.getPackageName() + "&hl=it")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select(".hAyfc .htlgb")
                    .get(7)
                    .ownText()
            return newVersion
        } catch (e: Exception) {
            return newVersion.toString()
        }
    }

     protected override fun onPostExecute(onlineVersion: String) {
         var currentVersion = MyApplication.instance.getPackageManager().getPackageInfo(
             MyApplication.instance.getPackageName(), 0
         ).versionName;
         super.onPostExecute(onlineVersion)
         Log.d(
             "update",
             "Current version " + currentVersion + "playstore version " + onlineVersion
         )
         if (!onlineVersion.equals("null") && onlineVersion != null && !onlineVersion.isEmpty()) {
             if (java.lang.Float.valueOf(currentVersion) < java.lang.Float.valueOf(onlineVersion)) {

                 showVersionUpdateDialog(onlineVersion)


             }
         }
     }



     private fun showVersionUpdateDialog(newVersion:String) {
        lateinit var dialog: android.app.AlertDialog
        val builder = android.app.AlertDialog.Builder(context)
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(MyApplication.instance),
            R.layout.dialog_update_version,
            null,
            false
        ) as DialogUpdateVersionBinding

        binding.newVersion.text=context.getString(R.string.new_version)+" available"
        // Create the AlertDialog object and return it
        binding.btnDone.setOnClickListener {
            // mInterface.photoFromCamera(mKey)

            try {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + MyApplication.instance.packageName)
                    )
                );
            } catch (ex: ActivityNotFoundException) {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + MyApplication.instance.packageName)
                    )
                );
            }

            //dialog.dismiss()
        }


        builder.setView(binding.root)
        dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }

}

