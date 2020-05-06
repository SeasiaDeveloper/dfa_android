package com.ngo.ui.signup.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.DistResponse
import com.ngo.pojo.response.SignupResponse

interface SignupPresenter : BasePresenter {
    fun onValidationSuccess(signupRequest: SignupRequest)
    fun checkValidations( signupRequest: SignupRequest)
    fun saveSignUpDetails(signupRequest: SignupRequest,token:String?)
    fun onSaveDetailsSuccess(response: SignupResponse)
    fun onSaveDetailsFailed(error: String)
    fun getDist()
    fun fetchDistList(responseObject: DistResponse)

    //validations:-
    fun usernameEmptyValidation()
    fun usernameValidationFailure()
    fun firstNameValidationFailure()
    fun lastNameValidationFailure()
    fun Address1ValidationFailure()
    fun pinCodeValidationFailure()
    fun mobileValidationFailure()
    fun adhaarNoValidationFailure()
    fun emailValidationFailure()
    fun passwordEmptyValidation()
    fun passwordLengthValidation()
    fun confirmPasswordEmptyValidation()
    fun confirmPasswordLengthValidation()
    fun confirmPasswordMismatchValidation()

    fun firstNameAlphabetFailure()
    fun firstNameLengthFailure()
    fun middleNameAlphabetFailure()
    fun middleNameLengthFailure()
    fun lastNameAlphabetFailure()
    fun lastNameLengthFailure()
    fun addressLine1LengthFailure()
    fun addressLine2LengthFailure()
    fun pinCodeLengthFailure()

    fun passwordInvalidValidation()
    fun districtValidationFailure()
}