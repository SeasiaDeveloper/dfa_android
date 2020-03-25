package com.ngo.ui.profile.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.DistResponse
import com.ngo.pojo.response.SignupResponse

interface ProfilePresenter : BasePresenter {
    fun getDist()
    fun fetchDistList(responseObject: DistResponse)
    fun checkValidations(signupRequest: SignupRequest)
    fun onValidationSuccess(request: SignupRequest)
    fun updateProfile(request: SignupRequest)
    fun onSuccessfulUpdation(responseObject: SignupResponse)
    fun usernameEmptyValidation()
    fun usernameValidationFailure()
    fun firstNameValidationFailure()
    fun lastNameValidationFailure()
    fun Address1ValidationFailure()
    fun pinCodeValidationFailure()
    fun mobileValidationFailure()
    fun adhaarNoValidationFailure()
    fun emailValidationFailure()

}