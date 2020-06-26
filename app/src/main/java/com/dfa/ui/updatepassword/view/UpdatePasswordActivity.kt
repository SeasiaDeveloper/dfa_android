package com.dfa.ui.updatepassword.view

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.pojo.request.UpdatePasswordRequest
import com.dfa.pojo.response.ChangePasswordResponse
import com.dfa.ui.changepassword.view.ChangePasswordView
import com.dfa.ui.updatepassword.presenter.UpdatePasswordPresenter
import com.dfa.ui.updatepassword.presenter.UpdatePasswordPresenterImpl
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
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
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.BLACK)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        userId = PreferenceHandler.readString(this, PreferenceHandler.USER_ID, "")!!
        changePassword.setOnClickListener {
            if (TextUtils.isEmpty(old_password.text.toString())) {
                Utilities.showMessage(this, "Please enter old Password")
            } else if (TextUtils.isEmpty(new_password.text.toString())) {
                Utilities.showMessage(this, "Please enter new Password")
            } /*else if (!(Utilities.isValidPassword(new_password.text.toString()))) {
                Utilities.showMessage(this, getString(R.string.password_invalid))
            } */else if (new_password.text.toString().length < 6) {
                Utilities.showMessage(this, "Password should of minimum 6 characters")
            } else if (new_password.text.toString().equals(old_password.text.toString())) {
                Utilities.showMessage(this, "New Password is same as old password")
            } else if (TextUtils.isEmpty(confirm_password.text.toString())) {
                Utilities.showMessage(this, "Please confirm Password")
            } else if (!new_password.text.toString().equals(confirm_password.text.toString())) {
                Utilities.showMessage(this, "New and Confirm Password should be same")
            } else {
                if (isInternetAvailable()) {
                    showProgress()
                    val changePasswordRequest = UpdatePasswordRequest(
                        old_password.text.toString(),
                        new_password.text.toString(),
                        confirm_password.text.toString(),
                        userId
                    )
                    updatePasswordPresenter.hitUpdatePasswordApi(changePasswordRequest)
                } else {
                    Utilities.showMessage(this, getString(R.string.no_internet_connection))
                }
            }
        }

        cancel_update_password.setOnClickListener {
            onBackPressed()
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
        finish()
        /*   old_password.setText("")
           new_password.setText("")
           confirm_password.setText("")*/
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }
}