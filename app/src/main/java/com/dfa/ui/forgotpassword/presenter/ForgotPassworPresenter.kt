package com.dfa.ui.forgotpassword.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.VerifyUserRequest
import com.dfa.pojo.response.VerifyUserResponse

interface ForgotPassworPresenter : BasePresenter {
    fun hitVerifyUserApi(verfiPasswordRequest: VerifyUserRequest);
    fun onVerfiyUserSuccess(verifyUserResponse: VerifyUserResponse)
    fun onVerifyUserFailure(error: String)
}