package com.ngo.ui.login.view

import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.ui.forgotpassword.view.ForgotPasswordActivity
import com.ngo.ui.login.presenter.LoginActivityPresenterImpl
import com.ngo.ui.login.presenter.LoginPresenter
import com.ngo.ui.ngo.presenter.NgoPresenter
import com.ngo.ui.ngo.presenter.NgoPresenterImpl
import kotlinx.android.synthetic.main.activity_login_activity.*
import kotlinx.android.synthetic.main.activity_public.toolbarLayout


class LoginActivity : BaseActivity(), View.OnClickListener, LoginView {
    private var presenter: LoginPresenter = LoginActivityPresenterImpl(this)

    override fun getLayout(): Int {
        return R.layout.activity_login_activity
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.report_incident)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        setListeners()
    }

    private fun setListeners() {
        btnLogin.setOnClickListener(this)
    }

    override fun handleKeyboard(): View {
        return loginlayout
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                presenter.checkValidations(email_mobile_number.text.toString(),editPassword.text.toString())

            }
            R.id.btnSignUp -> {
                //open sign up screen
            }
            R.id.forgot_password -> {
                var intent = Intent(this, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}