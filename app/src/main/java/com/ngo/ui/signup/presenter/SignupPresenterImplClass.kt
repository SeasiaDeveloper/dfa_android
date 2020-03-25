package com.ngo.ui.signup.presenter

import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.DistResponse
import com.ngo.pojo.response.SignupResponse
import com.ngo.ui.signup.model.SignupModel
import com.ngo.ui.signup.view.SignupView

class SignupPresenterImplClass(private var signupView: SignupView) : SignupPresenter {
    override fun passwordEmptyValidation() {
        signupView.passwordEmptyValidation()
    }

    override fun passwordLengthValidation() {
        signupView.passwordLengthValidation()
    }

    override fun confirmPasswordEmptyValidation() {
        signupView.confirmPasswordEmptyValidation()
    }

    override fun confirmPasswordLengthValidation() {
        signupView.confirmPasswordLengthValidation()
    }

    override fun confirmPasswordMismatchValidation() {
        signupView.confirmPasswordMismatchValidation()
    }

    override fun usernameEmptyValidation() {
        signupView.usernameEmptyValidation()
    }

    override fun usernameValidationFailure() {
        signupView.usernameValidationFailure()
    }

    override fun firstNameValidationFailure() {
        signupView.firstNameValidationFailure()
    }

    override fun lastNameValidationFailure() {
        signupView.lastNameValidationFailure()
    }

    override fun Address1ValidationFailure() {
        signupView.Address1ValidationFailure()
    }

    override fun pinCodeValidationFailure() {
        signupView.pinCodeValidationFailure()
    }

    override fun mobileValidationFailure() {
        signupView.mobileValidationFailure()
    }

    override fun adhaarNoValidationFailure() {
        signupView.adhaarNoValidationFailure()
    }

    override fun emailValidationFailure() {
        signupView.emailValidationFailure()
    }

    override fun fetchDistList(responseObject: DistResponse) {
        signupView.fetchDistList(responseObject)
    }

    override fun getDist() {
        signupModel.getDist()
    }

    override fun saveSignUpDetails(signupRequest: SignupRequest) {
        signupModel.userRegisteration(signupRequest) //hit register api in model class
    }

    private var signupModel: SignupModel = SignupModel(this)
    override fun onValidationSuccess(signupRequest: SignupRequest) {
        signupView.onValidationSuccess(signupRequest)
    }

    override fun checkValidations(signupRequest: SignupRequest) {
        signupModel.setValidation(signupRequest)
    }


    override fun showError(error: String) {
    signupView.showServerError(error)
    }

    override fun onSaveDetailsSuccess(response: SignupResponse) {
        signupView.showResponse(response)
    }

    override fun onSaveDetailsFailed(error: String) {
        signupView.showServerError(error)
    }

}