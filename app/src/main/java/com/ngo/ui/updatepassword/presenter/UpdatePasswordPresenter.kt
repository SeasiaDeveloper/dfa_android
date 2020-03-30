package com.ngo.ui.updatepassword.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.request.UpdatePasswordRequest
import com.ngo.pojo.response.ChangePasswordResponse

interface UpdatePasswordPresenter : BasePresenter {
    fun updatePasswordSucccess(changePasswordResponse: ChangePasswordResponse)
    fun updatePasswordFailure(error: String)
    fun hitUpdatePasswordApi(updateChangePasswordRequest: UpdatePasswordRequest)
}