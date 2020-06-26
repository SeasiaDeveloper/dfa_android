package com.dfa.ui.forgotpassword.view

import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.pojo.request.VerifyUserRequest
import com.dfa.pojo.response.VerifyUserResponse
import com.dfa.ui.OtpVerification.view.OtpVerificationActivity
import com.dfa.ui.forgotpassword.presenter.ForgotPassworPresenter
import com.dfa.ui.forgotpassword.presenter.ForgotPasswordPresenterImpl
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_public.toolbarLayout

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener, ForgotPasswordView {
    private lateinit var clicked_from: String
    private var presenter: ForgotPassworPresenter = ForgotPasswordPresenterImpl(this)

    override fun getLayout(): Int {
        return R.layout.activity_forgot_password
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)

        (toolbarLayout as CenteredToolbar).title = getString(R.string.forgot_password_title)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.BLACK)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        val intent = intent
        clicked_from = intent.getStringExtra("clicked_from")
        if (clicked_from.equals("signup", ignoreCase = true)) {
            (toolbarLayout as CenteredToolbar).title = getString(R.string.verify_mobileno)
            tvTitleDes.setText(R.string.enter_mobileno)
            edit_mobile_number.setHint(R.string.enter_mobile_number)
        } else if (clicked_from.equals("forgotPassword", ignoreCase = true)) {
            (toolbarLayout as CenteredToolbar).title = getString(R.string.forgot_password_title)
            tvTitleDes.setText(R.string.forgot_password_heading)
            edit_mobile_number.setHint(R.string.enter_registered_mobile_number)
        } else {
            // do something
        }
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
                    Utilities.showMessage(this, "Please enter mobile number")
                } else if (edit_mobile_number.text.toString().trim().length != 10) {
                    Utilities.showMessage(this, "Please enter valid mobile number")
                } else {
                    if (clicked_from.equals("signup", ignoreCase = true)) {

                        if (isInternetAvailable()) {
                            showProgress()
                            val request = VerifyUserRequest(
                                edit_mobile_number.text.toString()
                            )
                            presenter.verifyForMobileNumberExistsOrNot(request)
                        } else {
                            Utilities.showMessage(this, getString(R.string.no_internet_connection))
                        }

                    } else if (clicked_from.equals("forgotPassword", ignoreCase = true)) {

                        if (isInternetAvailable()) {
                            showProgress()
                            val request = VerifyUserRequest(
                                edit_mobile_number.text.toString()
                            )
                            presenter.hitVerifyUserApi(request)
                        } else {
                            Utilities.showMessage(this, getString(R.string.no_internet_connection))
                        }

                    } else {
                        // do something
                    }
                }
            }
        }
    }

    override fun onVerifyUserFailure(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }

    override fun onVerifyUserSuccess(verifyUserResponse: VerifyUserResponse) {
        dismissProgress()
        // Utilities.showMessage(this, verifyUserResponse.message)
        val mobile: String = edit_mobile_number.getText().toString().trim()
        val intent = Intent(this, OtpVerificationActivity::class.java)
        intent.putExtra("mobile", mobile)
        intent.putExtra("intent_from", clicked_from)
        intent.putExtra("userId", verifyUserResponse.data.toString())
        startActivity(intent)

    }

    override fun numberExistsAlready(verifyUserResponse: VerifyUserResponse) {
        dismissProgress()
        Utilities.showMessage(this, "Number Exists Already")
    }

    override fun numberDoesNotExistAlready(error: String) {
        dismissProgress()
        val mobile: String = edit_mobile_number.getText().toString().trim()
        val intent = Intent(this, OtpVerificationActivity::class.java)
        intent.putExtra("mobile", mobile)
        intent.putExtra("intent_from", clicked_from)
        intent.putExtra("phoneNo", edit_mobile_number.text.toString())
        startActivity(intent)
    }

    override fun showServerError(error: String) {
        dismissProgress()
        Utilities.showMessage(this, error)
    }
}