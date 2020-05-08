package com.ngo.ui

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.View
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.ui.home.fragments.home.view.HomeActivity
import com.ngo.ui.login.view.LoginActivity
import com.ngo.utils.PreferenceHandler
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : BaseActivity() {

    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    override fun setupUI() {

        if (intent != null) {
            val data: Uri? = intent.data
            if (data != null) { //URL scheme...
                val mParkingId = data.getQueryParameter("id")
                Log.i("ID", ">>>>>>>>>>>>>>>>>>>>> ID" + data.getQueryParameter("id"))
                // helper.save(PreferenceKeys.SCHEME_PARKING_ID, mParkingId)
            }
        }

        Handler().postDelayed({
            run {
                if (!PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "").equals(
                        ""
                    )
                ) {
                    startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }, 2000)
    }

    override fun handleKeyboard(): View {
        return splashParent
    }
}