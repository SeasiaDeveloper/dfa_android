package com.ngo.ui.login.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.LoginResponse

interface LoginView : BaseView {
    fun onEmptyEmailId()
    fun onInvalidEmailId()
    fun onEmptyPassword()
    fun onInvalidNumber()
    fun validationSuccess()
    fun showError(error:String)
    fun onLoginFailure(error:String)
    fun onLoginSuccess(loginResponse: LoginResponse)
}