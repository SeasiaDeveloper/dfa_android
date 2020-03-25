package com.ngo.ui.forgotpassword.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.VerifyUserResponse

interface ForgotPasswordView : BaseView {
    fun onVerifyUserFailure(error: String)
    fun onVerifyUserSuccess(verifyUserResponse: VerifyUserResponse)
}