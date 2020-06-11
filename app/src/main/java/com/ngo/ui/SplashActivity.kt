package com.ngo.ui

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.View
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.ui.crimedetails.view.IncidentDetailActivity
import com.ngo.ui.home.fragments.home.view.HomeActivity
import com.ngo.ui.login.view.LoginActivity
import com.ngo.utils.Constants
import com.ngo.utils.PreferenceHandler
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : BaseActivity() {

    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    override fun setupUI() {
        var complaintId =""
        val auth = PreferenceHandler.readString(this@SplashActivity, PreferenceHandler.AUTHORIZATION, "")
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
                        val intent = Intent(this@SplashActivity, IncidentDetailActivity::class.java)
                        intent.putExtra(Constants.PUBLIC_COMPLAINT_DATA, complaintId)
                        intent.putExtra(Constants.FROM_WHERE, "tohit")
                        startActivity(intent)
                        finish()
                    } else {
                        startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                        finish()
                    }
                } else {
                    startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                    finish()
                }
            }
        }, 2000)
    }

    override fun handleKeyboard(): View {
        return splashParent
    }
}