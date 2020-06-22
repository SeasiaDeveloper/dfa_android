package com.dfa.ui.signup.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.SignupRequest
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.SignupResponse

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