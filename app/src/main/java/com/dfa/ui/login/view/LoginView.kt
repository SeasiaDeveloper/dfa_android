package com.dfa.ui.login.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.LoginResponse

interface LoginView : BaseView {
    fun onEmptyEmailId()
    fun onInvalidEmailId()
    fun onEmptyPassword()
    fun onInvalidNumber()
    fun validationSuccess()
    fun onLoginFailure(error:String)
    fun onLoginSuccess(loginResponse: LoginResponse)
}