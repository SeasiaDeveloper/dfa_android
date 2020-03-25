package com.ngo.ui.login.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.LoginRequest
import com.ngo.pojo.response.LoginResponse

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