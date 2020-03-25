package com.ngo.ui.changepassword.view

import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.request.LoginRequest
import com.ngo.pojo.response.ChangePasswordResponse
import com.ngo.pojo.response.LoginResponse
import com.ngo.ui.changepassword.presenter.ChangePasswordPresenter
import com.ngo.ui.changepassword.presenter.ChangePasswordPresenterImpl
import com.ngo.ui.login.presenter.LoginActivityPresenterImpl
import com.ngo.ui.login.presenter.LoginPresenter
import com.ngo.ui.login.view.LoginActivity
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_login_activity.*
import kotlinx.android.synthetic.main.change_password_layout.*
import kotlinx.android.synthetic.main.change_password_layout.toolbarLayout

class ChangePasswordActivity : BaseActivity(), View.OnClickListener, ChangePasswordView {
    private var presenter: ChangePasswordPresenter = ChangePasswordPresenterImpl(this)

    override fun getLayout(): Int {
        return R.layout.change_password_layout
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.change_password)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        setListeners()
    }

    fun setListeners() {
        changePassword.setOnClickListener(this)
    }

    override fun handleKeyboard(): View {
        return changePasswordLayout
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.changePassword -> {
                if (TextUtils.isEmpty(new_password.text.toString())) {
                    Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(confirm_password.text.toString())) {
                    Toast.makeText(this, "Please enter confirm password", Toast.LENGTH_SHORT).show()
                } else if (!confirm_password.text.toString().equals(new_password.text.toString())) {
                    Toast.makeText(this, "Entered passwords are not same", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (isInternetAvailable()) {
                        showProgress()
                        val request = ChangePasswordRequest(
                            new_password.text.toString(),
                            confirm_password.text.toString(),
                            "13"
                        )
                        presenter.hitChangePasswordApi(request)
                    } else {
                        Utilities.showMessage(this, getString(R.string.no_internet_connection))
                    }
                }
            }
        }
    }

    override fun onEmptyPassword() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: String) {
        dismissProgress()
        Toast.makeText(this, error, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onChangePasswordFailure(error: String) {
        dismissProgress()
        Toast.makeText(this, error, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onChangePasswordSuccess(changePasswordResponse: ChangePasswordResponse) {
        dismissProgress()
        Toast.makeText(this, changePasswordResponse.message, Toast.LENGTH_SHORT).show()
        this.finish()
        var intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }

    override fun showServerError(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}