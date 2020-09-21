package com.findthat_android.util.other

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.ui.crimedetails.view.IncidentDetailActivity
import com.dfa.ui.home.fragments.home.view.HomeActivity
import com.dfa.utils.Constants
import com.dfa.utils.PreferenceHandler


import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * Created by AIM on 10/18/2018.
 */
abstract class CheckPlayStoreVersion : BaseActivity(){

      inner class GetVersionCode : AsyncTask<Void, String, String>() {

        override fun doInBackground(vararg voids: Void): String? {

            val currentVersion_PatternSeq = "<div[^>]*?>Current\\sVersion</div><span[^>]*?>(.*?)><div[^>]*?>(.*?)><span[^>]*?>(.*?)</span>"
            val appVersion_PatternSeq = "htlgb\">([^<]*)</s"
            var playStoreAppVersion: String? = null

            var inReader: BufferedReader? = null
            var uc: URLConnection? = null
            val urlData = StringBuilder()

            val url: URL
            try {
                url = URL("https://play.google.com/store/apps/details?id=com.dfango.android&hl=en")
                uc = url.openConnection()
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (uc == null) {
                return null
            }
            uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
            try {
                inReader = BufferedReader(InputStreamReader(uc.getInputStream()))
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (null != inReader) {
                try {
                    do {
                        val line = inReader.readLine()
                        urlData.append(line)
                    } while (line != null)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            // Get the current version pattern sequence
            val versionString = getAppVersion(currentVersion_PatternSeq, urlData.toString())
            if (null == versionString) {
                return null
            } else {
                // get version from "htlgb">X.X.X</span>
                playStoreAppVersion = getAppVersion(appVersion_PatternSeq, versionString)
            }

            return playStoreAppVersion

        }


        override fun onPostExecute(onlineVersion: String?) {
            super.onPostExecute(onlineVersion)
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                try {
                    val current_version = java.lang.Float.valueOf(packageManager.getPackageInfo(packageName, 0).versionName.replace(".", ""))
                    val online_version = java.lang.Float.valueOf(onlineVersion.replace(".", ""))!!
                    val diff = java.lang.Float.compare(current_version, online_version)
                    if (diff < 0) {
                        openUpdateDialog(onlineVersion)
                    } else {
                        gotoActivity()
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }else{
                gotoActivity()
            }
        }
    }

    private fun getAppVersion(patternString: String, inputString: String): String? {
        try {
            //Create a pattern
            val pattern = Pattern.compile(patternString) ?: return null
//Match the pattern string in provided string
            val matcher = pattern.matcher(inputString)
            if (null != matcher && matcher.find()) {
                return matcher.group(1)
            }
        } catch (ex: PatternSyntaxException) {
            ex.printStackTrace()
        }

        return null
    }


    private fun openUpdateDialog(new_version: String) {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.setContentView(R.layout.update_app_dialog)
        dialog.setCanceledOnTouchOutside(false)

        val update = dialog.findViewById<View>(R.id.update) as TextView
        val version_text = dialog.findViewById<View>(R.id.version_text) as TextView
        version_text.text = getString(R.string.youAreNotUpdatedMessage) + resources.getString(R.string.now_popup)

        update.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            val handler = Handler()
            handler.postDelayed({ finish() }, 400)
        }

        dialog.show()
    }


    fun gotoActivity() {

        var complaintId =""
        val auth = PreferenceHandler.readString(this@CheckPlayStoreVersion, PreferenceHandler.AUTHORIZATION, "")
        //if intent is not null && user is logged in
        if ((intent != null) && !(auth.equals(""))) {
            val data: Uri? = intent.data
            if (data != null) { //URL scheme...
                complaintId = data.getQueryParameter("id")!!
                Log.i("ID", ">>>>>>>>>>>>>>>>>>>>> ID" + data.getQueryParameter("id"))
                // helper.save(PreferenceKeys.SCHEME_PARKING_ID, complaintId)
            }
        }

        Handler().postDelayed({
            run {
                if (!(auth.equals(""))) {
                    if (!complaintId.equals("")) {
                        val intent = Intent(this@CheckPlayStoreVersion, IncidentDetailActivity::class.java)
                        intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintId)
                        intent.putExtra(Constants.FROM_WHERE, "tohit")
                        startActivity(intent)
                        finish()
                    } else {
                        startActivity(Intent(this@CheckPlayStoreVersion, HomeActivity::class.java))
                        finish()
                    }
                } else {
                    startActivity(Intent(this@CheckPlayStoreVersion, HomeActivity::class.java))
                    finish()
                }
            }
        }, 2000)
    }
}