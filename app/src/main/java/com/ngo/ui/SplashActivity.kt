package com.ngo.ui

import android.content.Intent
import android.os.Handler
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
        Handler().postDelayed({
            run {
                if (!PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "").equals("")) {
                    val mIntent: Intent = Intent(this@SplashActivity, HomeActivity::class.java)
                    startActivity(mIntent)
                    finish()
                } else {
                    val mIntent: Intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(mIntent)
                    finish()
                }
            }
        }, 2000)
    }

    override fun handleKeyboard(): View {
        return splashParent
    }
}