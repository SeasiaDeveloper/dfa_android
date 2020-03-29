package com.ngo.ui.updatepassword.view

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.response.ChangePasswordResponse
import com.ngo.ui.changepassword.view.ChangePasswordView
import com.ngo.ui.updatepassword.presenter.UpdatePasswordPresenter
import com.ngo.ui.updatepassword.presenter.UpdatePasswordPresenterImpl
import com.ngo.utils.PreferenceHandler
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.change_password_layout.toolbarLayout
import kotlinx.android.synthetic.main.update_password_layout.*
import kotlinx.android.synthetic.main.update_password_layout.changePassword
import kotlinx.android.synthetic.main.update_password_layout.new_password

class UpdatePasswordActivity : BaseActivity(), ChangePasswordView {
    private var updatePasswordPresenter: UpdatePasswordPresenter = UpdatePasswordPresenterImpl(this)
    private lateinit var userId: String

    override fun getLayout(): Int {
        return R.layout.update_password_layout
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.change_password)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        userId = PreferenceHandler.readString(this, PreferenceHandler.USER_ID, "")!!
        changePassword.setOnClickListener {
            if (TextUtils.isEmpty(old_password.text.toString())) {
                Utilities.showMessage(this, "Please enter old Password")
            } else if (TextUtils.isEmpty(new_password.text.toString())) {
                Utilities.showMessage(this, "Please enter new Password")
            } else if (!new_password.text.toString().equals(old_password.text.toString())) {
                Utilities.showMessage(this, "Mismatch Passwords")
            } else {
                if (isInternetAvailable()) {
                    showProgress()
                    var changePasswordRequest = ChangePasswordRequest(
                        old_password.text.toString(),
                        new_password.text.toString(),
                        userId
                    )
                    updatePasswordPresenter.hitUpdatePasswordApi(changePasswordRequest)
                } else {
                    Utilities.showMessage(this, getString(R.string.no_internet_connection))
                }
            }
        }
    }

    override fun handleKeyboard(): View {
        return updatePasswordLayout
    }

    override fun onEmptyPassword() {

    }

    override fun onChangePasswordFailure(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun onChangePasswordSuccess(changePasswordResponse: ChangePasswordResponse) {
        dismissProgress()
        Utilities.showMessage(this, changePasswordResponse.message)
        old_password.setText("")
        new_password.setText("")
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }
}