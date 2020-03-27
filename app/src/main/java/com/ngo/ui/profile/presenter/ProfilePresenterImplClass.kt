package com.ngo.ui.profile.presenter

import com.ngo.pojo.request.SignupRequest
import com.ngo.pojo.response.DistResponse
import com.ngo.pojo.response.SignupResponse
import com.ngo.ui.profile.model.ProfileModel
import com.ngo.ui.profile.view.ProfileView

class ProfilePresenterImplClass(private var profileView: ProfileView):ProfilePresenter {
    override fun usernameEmptyValidation() {
        profileView.usernameEmptyValidation()
    }

    override fun usernameValidationFailure() {
        profileView.usernameValidationFailure()
    }

    override fun firstNameValidationFailure() {
        profileView.firstNameValidationFailure()
    }

    override fun lastNameValidationFailure() {
        profileView.lastNameValidationFailure()
    }

    override fun Address1ValidationFailure() {
        profileView.Address1ValidationFailure()
    }

    override fun pinCodeValidationFailure() {
        profileView.pinCodeValidationFailure()
    }

    override fun mobileValidationFailure() {
        profileView.mobileValidationFailure()
    }

    override fun adhaarNoValidationFailure() {
        profileView.adhaarNoValidationFailure()
    }

    override fun emailValidationFailure() {
        profileView.emailValidationFailure()
    }

    private var profileModel: ProfileModel = ProfileModel(this)

    override fun checkValidations(signupRequest: SignupRequest) {
        profileModel.setValidation(signupRequest)
    }

    override fun getDist() {
        profileModel.getDist()
    }

    override fun fetchDistList(responseObject: DistResponse) {
        profileView.fetchDistList(responseObject)
    }

    override fun showError(error: String) {
        profileView.showServerError(error)
    }

   override fun onValidationSuccess(request: SignupRequest) {
        profileView.onValidationSuccess(request)
    }

    override fun updateProfile(request: SignupRequest, token: String?) {
        profileModel.updateProfile(request,token) //hit update profile api in model class
    }

    override fun onSuccessfulUpdation(responseObject: SignupResponse) {
        profileView.onSuccessfulUpdation(responseObject)
    }


}