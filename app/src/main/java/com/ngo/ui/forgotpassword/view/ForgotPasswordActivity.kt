package com.ngo.ui.forgotpassword.view

import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.ui.changepassword.view.ChangePasswordActivity
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_public.toolbarLayout

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {

    override fun getLayout(): Int {
        return R.layout.activity_forgot_password
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.forgot_password)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        setListeners()
    }

    private fun setListeners() {
        btnSubmit.setOnClickListener(this)
    }

    override fun handleKeyboard(): View {
        return forgotPasswordLayout
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSubmit -> {
                if (TextUtils.isEmpty(edit_mobile_number.text.toString())) {
                    Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show()
                } else if (edit_mobile_number.text.toString().length != 10) {
                    Toast.makeText(this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
                }else{
                    //validate mobile number and send OTP
                    var intent = Intent(this, ChangePasswordActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}