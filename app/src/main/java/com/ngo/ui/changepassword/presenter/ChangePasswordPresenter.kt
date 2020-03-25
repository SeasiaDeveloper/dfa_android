package com.ngo.ui.changepassword.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.response.ChangePasswordResponse
import com.ngo.pojo.response.LoginResponse

interface ChangePasswordPresenter: BasePresenter {
    fun onEmptyPasswordField()
    fun hitChangePasswordApi(changePasswordRequest: ChangePasswordRequest);
    fun onPasswordChangeSuccess(respone:ChangePasswordResponse)
    fun onPasswordChangeFailure(error: String)
}