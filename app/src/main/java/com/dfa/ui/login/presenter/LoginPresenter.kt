package com.dfa.ui.login.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.LoginRequest
import com.dfa.pojo.response.LoginResponse

interface LoginPresenter: BasePresenter {
    fun onEmptyPassword()
    fun onInvalidEmail()
    fun onInvalidNumber()
    fun onEmptyEmailId()
    fun onValidationSuccess()
    fun hitLoginWebService(loginRequest: LoginRequest);
    fun checkValidations(emailId:String,password:String)
    fun onLoginSuccess(response: LoginResponse)
    fun onLoginFailure(error: String)
}