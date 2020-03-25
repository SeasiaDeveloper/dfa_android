package com.ngo.ui.signup.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.SignupResponse

interface SignupView :BaseView {
    fun onValidationSuccess(signupRequest: SignupRequest)
    fun showResponse(response: SignupResponse)


}