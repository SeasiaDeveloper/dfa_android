package com.ngo.ui.profile.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.DistResponse
import com.ngo.pojo.response.GetProfileResponse
import com.ngo.pojo.response.SignupResponse

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