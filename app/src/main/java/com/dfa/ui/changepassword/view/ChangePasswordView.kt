package com.dfa.ui.changepassword.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.ChangePasswordResponse

interface ChangePasswordView : BaseView {
    fun onEmptyPassword()
    fun onChangePasswordFailure(error: String)
    fun onChangePasswordSuccess(changePasswordResponse: ChangePasswordResponse)
}