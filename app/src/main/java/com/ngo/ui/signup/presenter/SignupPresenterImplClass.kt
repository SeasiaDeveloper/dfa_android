package com.ngo.ui.signup.presenter

import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.SignupResponse
import com.ngo.ui.signup.model.SignupModel
import com.ngo.ui.signup.view.SignupView

class SignupPresenterImplClass(private var signupView: SignupView) : SignupPresenter {

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