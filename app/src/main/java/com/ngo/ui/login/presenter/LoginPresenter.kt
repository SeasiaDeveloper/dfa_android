package com.ngo.ui.login.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.response.ComplaintResponse
import com.ngo.pojo.response.LoginResponse

interface LoginPresenter: BasePresenter {
    fun onEmptyPassword()
    fun onInvalidEmail()
    fun onEmptyEmailId()
    fun onValidationSuccess()
    fun checkValidations(emailId:String,password:String)
    fun onLoginSuccess(response: LoginResponse)
    fun onLoginFailure(error: String)
}