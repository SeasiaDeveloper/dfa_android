package com.dfa.ui.changepassword.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.ChangePasswordRequest
import com.dfa.pojo.response.ChangePasswordResponse

interface ChangePasswordPresenter: BasePresenter {
    fun onEmptyPasswordField()
    fun hitChangePasswordApi(changePasswordRequest: ChangePasswordRequest);
    fun onPasswordChangeSuccess(respone:ChangePasswordResponse)
    fun onPasswordChangeFailure(error: String)
}