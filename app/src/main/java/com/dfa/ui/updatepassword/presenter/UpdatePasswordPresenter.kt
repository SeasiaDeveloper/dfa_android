package com.dfa.ui.updatepassword.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.UpdatePasswordRequest
import com.dfa.pojo.response.ChangePasswordResponse

interface UpdatePasswordPresenter : BasePresenter {
    fun updatePasswordSucccess(changePasswordResponse: ChangePasswordResponse)
    fun updatePasswordFailure(error: String)
    fun hitUpdatePasswordApi(updateChangePasswordRequest: UpdatePasswordRequest)
}