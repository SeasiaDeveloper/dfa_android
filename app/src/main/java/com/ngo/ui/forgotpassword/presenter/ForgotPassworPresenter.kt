package com.ngo.ui.forgotpassword.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.VerifyUserRequest
import com.ngo.pojo.response.VerifyUserResponse

interface ForgotPassworPresenter : BasePresenter {
    fun hitVerifyUserApi(verfiPasswordRequest: VerifyUserRequest);
    fun onVerfiyUserSuccess(verifyUserResponse: VerifyUserResponse)
    fun onVerifyUserFailure(error: String)
}