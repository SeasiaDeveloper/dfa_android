package com.ngo.ui.signup.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.DistResponse
import com.ngo.pojo.response.SignupResponse

interface SignupPresenter : BasePresenter {
    fun onValidationSuccess(signupRequest: SignupRequest)
    fun checkValidations( signupRequest: SignupRequest)
    fun saveSignUpDetails(signupRequest: SignupRequest)
    fun onSaveDetailsSuccess(response: SignupResponse)
    fun onSaveDetailsFailed(error: String)
    fun getDist()
    fun fetchDistList(responseObject: DistResponse)
}