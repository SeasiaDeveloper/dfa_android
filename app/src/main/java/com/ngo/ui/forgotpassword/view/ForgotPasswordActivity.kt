package com.ngo.ui.forgotpassword.view

import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.request.VerifyUserRequest
import com.ngo.pojo.response.VerifyUserResponse
import com.ngo.ui.OtpVerification.view.OtpVerificationActivity
import com.ngo.ui.forgotpassword.presenter.ForgotPassworPresenter
import com.ngo.ui.forgotpassword.presenter.ForgotPasswordPresenterImpl
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_public.toolbarLayout

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener, ForgotPasswordView {
    private var presenter: ForgotPassworPresenter = ForgotPasswordPresenterImpl(this)

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
                }
                else {
                    if (isInternetAvailable()) {
                        showProgress()
                       // var myNewInt: String = edit_mobile_number.text.toString()
                        val request = VerifyUserRequest(
                            edit_mobile_number.text.toString()
                        )
                        presenter.hitVerifyUserApi(request)
                    } else {
                        Utilities.showMessage(this, getString(R.string.no_internet_connection))
                    }
                }
            }
        }
    }

    override fun onVerifyUserFailure(error: String) {
        dismissProgress()
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onVerifyUserSuccess(verifyUserResponse: VerifyUserResponse) {
        dismissProgress()
        Toast.makeText(this, verifyUserResponse.message, Toast.LENGTH_SHORT).show()
        val mobile: String = edit_mobile_number.getText().toString().trim()
        val intent = Intent(this, OtpVerificationActivity::class.java)
        intent.putExtra("mobile", mobile)
        intent.putExtra("intent_from", "forgotPass")
        intent.putExtra("userId", verifyUserResponse.data.toString())
        startActivity(intent)

    }

    override fun showServerError(error: String) {
        dismissProgress()
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }