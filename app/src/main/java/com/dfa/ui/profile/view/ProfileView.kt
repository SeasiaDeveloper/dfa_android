package com.dfa.ui.profile.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.request.SignupRequest
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.GetProfileResponse
import com.dfa.pojo.response.SignupResponse

interface ProfileView:BaseView {
    fun fetchDistList(responseObject: DistResponse)
    fun onValidationSuccess(request: SignupRequest)
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
    fun getUserProfileSuccess(responseObject: GetProfileResponse)
}