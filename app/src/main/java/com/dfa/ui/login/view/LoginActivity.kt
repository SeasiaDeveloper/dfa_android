package com.dfa.ui.login.view

import android.content.Intent
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.pojo.request.LoginRequest
import com.dfa.pojo.response.LoginResponse
import com.dfa.ui.forgotpassword.view.ForgotPasswordActivity
import com.dfa.ui.home.fragments.home.view.HomeActivity
import com.dfa.ui.login.presenter.LoginActivityPresenterImpl
import com.dfa.ui.login.presenter.LoginPresenter
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_login_activity.*

class LoginActivity : BaseActivity(), View.OnClickListener, LoginView {
    private var presenter: LoginPresenter = LoginActivityPresenterImpl(this)
    private var token: String = ""

    override fun getLayout(): Int {
        return R.layout.activity_login_activity
    }

    override fun setupUI() {
        /* (toolbarLayout as CenteredToolbar).title = getString(R.string.login)
         (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)*/
        setListeners()
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(
            this,
            OnSuccessListener<InstanceIdResult> { instanceIdResult ->
                token = instanceIdResult.token
                Log.i("FCM Token", token)
                // saveToken(token)
            })
    }

    private fun setListeners() {
        btnLogin.setOnClickListener(this)
        btnGuestUser.setOnClickListener(this)
        forgot_password.setOnClickListener(this)

        btnSignUp.setOnClickListener(this)
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

            R.id.btnGuestUser -> {
                val intent = Intent(this, HomeActivity::class.java) //GeneralPublicActivity
                startActivity(intent)
                finish()
            }
            R.id.btnSignUp -> {
                val intent = Intent(this, ForgotPasswordActivity::class.java) //
                intent.putExtra("clicked_from", "signup")
                startActivity(intent)
            }
            R.id.forgot_password -> {
                val intent = Intent(this, ForgotPasswordActivity::class.java)
                intent.putExtra("clicked_from", "forgotPassword")
                startActivity(intent)
            }
        }
    }

    override fun onEmptyEmailId() {
        Utilities.showMessage(this, "Enter Email Id or Mobile Number")
    }

    override fun onInvalidEmailId() {
        Utilities.showMessage(this, "Enter valid email id")
    }

    override fun onEmptyPassword() {
        Utilities.showMessage(this, "Enter Password First")
    }

    override fun onInvalidNumber() {
        Utilities.showMessage(this, "Enter valid mobile number")
    }

    override fun validationSuccess() {
        if (isInternetAvailable()) {
            showProgress()
            val request = LoginRequest(
                email_mobile_number.text.toString(),
                editPassword.text.toString(), token
            )
            presenter.hitLoginWebService(request)
        } else {
            Utilities.showMessage(this, getString(R.string.no_internet_connection))
        }
    }

    override fun onLoginFailure(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun onLoginSuccess(loginResponse: LoginResponse) {
        dismissProgress()

        if (loginResponse.token != null) {
            PreferenceHandler.writeString(
                this,
                PreferenceHandler.AUTHORIZATION,
                "Bearer " + loginResponse.token
            )

            PreferenceHandler.writeString(
                this,
                PreferenceHandler.USER_ROLE,
                loginResponse?.user_role.toString()
            )
            Utilities.showMessage(this, getString(R.string.login_message))
            finish()
            val intent = Intent(this, HomeActivity::class.java) //GeneralPublicActivity
            startActivity(intent)
        } else {
            Utilities.showMessage(this, loginResponse.message/*"Username or password is incorrect"*/)
        }
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }
}