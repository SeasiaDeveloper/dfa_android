package com.ngo.ui.login.view

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.request.LoginRequest
import com.ngo.pojo.response.LoginResponse
import com.ngo.ui.forgotpassword.view.ForgotPasswordActivity
import com.ngo.ui.login.presenter.LoginActivityPresenterImpl
import com.ngo.ui.login.presenter.LoginPresenter
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_login_activity.*
import kotlinx.android.synthetic.main.activity_public.toolbarLayout

class LoginActivity : BaseActivity(), View.OnClickListener, LoginView {
    private var presenter: LoginPresenter = LoginActivityPresenterImpl(this)

    override fun getLayout(): Int {
        return R.layout.activity_login_activity
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.login)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        setListeners()
    }

    private fun setListeners() {
        btnLogin.setOnClickListener(this)
        forgot_password.setOnClickListener(this)
    }

    override fun handleKeyboard(): View {
        return loginlayout
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                presenter.checkValidations(
                    email_mobile_number.text.toString(),
                    editPassword.text.toString()
                )
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

    override fun onEmptyEmailId() {
        Toast.makeText(
            this@LoginActivity,
            "Enter Email Id or Mobile Number",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onInvalidEmailId() {
        Toast.makeText(
            this@LoginActivity,
            "Enter valid email id",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onEmptyPassword() {
        Toast.makeText(this@LoginActivity, "Enter Password First", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onInvalidNumber() {
        Toast.makeText(
            this@LoginActivity,
            "Enter valid mobile number",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun validationSuccess() {
        if (isInternetAvailable()) {
            showProgress()
            val request = LoginRequest(
                email_mobile_number.text.toString(),
                editPassword.text.toString()
            )
            presenter.hitLoginWebService(request)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
    }

    override fun showError(error: String) {
        dismissProgress()
        Toast.makeText(this@LoginActivity, error, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onLoginFailure(error: String) {
        dismissProgress()
        Toast.makeText(this@LoginActivity, error, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onLoginSuccess(loginResponse: LoginResponse) {
        dismissProgress()
        if (loginResponse != null) {
            Toast.makeText(this@LoginActivity, "Login Success", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun showServerError(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}