package com.dfa.ui.forgotpassword.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.VerifyUserResponse

interface ForgotPasswordView : BaseView {
    fun onVerifyUserFailure(error: String)
    fun onVerifyUserSuccess(verifyUserResponse: VerifyUserResponse)
    fun numberExistsAlready(verifyUserResponse: VerifyUserResponse)
    fun numberDoesNotExistAlready(error: String)
}