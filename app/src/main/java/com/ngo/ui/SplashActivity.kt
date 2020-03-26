package com.ngo.ui

import android.content.Intent
import android.os.Handler
import android.view.View
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.ui.login.view.LoginActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    override fun getLayout(): Int {
        return R.layout.activity_splash
    }

    override fun setupUI() {
        Handler().postDelayed({
            run {
                val mIntent: Intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(mIntent)
                finish()
            }
        }, 2000)
    }

    override fun handleKeyboard(): View {
        return splashParent
    }
}