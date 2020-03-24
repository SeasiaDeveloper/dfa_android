package com.ngo.ui.login.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.ComplaintRequest
import com.ngo.pojo.response.ComplaintResponse

interface LoginPresenter: BasePresenter {
    fun onEmptyLevel()
    fun onEmptyImage()
    fun onEmptyDescription()
    fun onValidationSuccess()
    fun checkValidations(emailId:String,password:String)
    fun onLoginSuccess(response: ComplaintResponse)
    fun onLoginFailure(error: String)
}