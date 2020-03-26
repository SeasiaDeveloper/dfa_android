package com.ngo.ui.changepassword.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.ChangePasswordResponse
import com.ngo.pojo.response.LoginResponse

interface ChangePasswordView : BaseView {
    fun onEmptyPassword()
    fun onChangePasswordFailure(error: String)
    fun onChangePasswordSuccess(changePasswordResponse: ChangePasswordResponse)
}