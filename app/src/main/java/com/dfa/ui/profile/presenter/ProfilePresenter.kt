package com.dfa.ui.profile.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.SignupRequest
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.GetProfileResponse
import com.dfa.pojo.response.SignupResponse

interface ProfilePresenter : BasePresenter {
    fun getDist()
    fun fetchDistList(responseObject: DistResponse)
    fun checkValidations(signupRequest: SignupRequest,isAdharCardNumberAdded:Boolean)
    fun onValidationSuccess(request: SignupRequest)
    fun updateProfile(request: SignupRequest,token:String?,isAdhaarNoAdded: Boolean)
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

    fun firstNameAlphabetFailure()
    fun firstNameLengthFailure()
    fun middleNameAlphabetFailure()
    fun middleNameLengthFailure()
    fun lastNameAlphabetFailure()
    fun lastNameLengthFailure()
    fun addressLine1LengthFailure()
    fun addressLine2LengthFailure()
    fun pinCodeLengthFailure()
    fun fetchUserInfo(userId: String)
    fun getUserProfileSuccess(responseObject: GetProfileResponse)
}