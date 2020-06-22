package com.dfa.ui.profile.presenter

import com.dfa.pojo.request.SignupRequest
import com.dfa.pojo.response.DistResponse
import com.dfa.pojo.response.GetProfileResponse
import com.dfa.pojo.response.SignupResponse
import com.dfa.ui.profile.model.ProfileModel
import com.dfa.ui.profile.view.ProfileView

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

    override fun checkValidations(signupRequest: SignupRequest,isAdhaarNoAdded: Boolean) {
        profileModel.setValidation(signupRequest,isAdhaarNoAdded)
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

    override fun updateProfile(request: SignupRequest, token: String?,isAdhaarNoAdded: Boolean) {
        profileModel.updateProfile(request,token,isAdhaarNoAdded) //hit update profile api in model class
    }

    override fun onSuccessfulUpdation(responseObject: SignupResponse) {
        profileView.onSuccessfulUpdation(responseObject)
    }

    override fun firstNameAlphabetFailure() {
        profileView.firstNameAlphabetFailure()
    }

    override fun firstNameLengthFailure() {
        profileView.firstNameLengthFailure()
    }

    override fun middleNameAlphabetFailure() {
        profileView.middleNameAlphabetFailure()
    }

    override fun middleNameLengthFailure() {
        profileView.middleNameLengthFailure()
    }

    override fun lastNameAlphabetFailure() {
        profileView.lastNameAlphabetFailure()
    }

    override fun lastNameLengthFailure() {
        profileView.lastNameLengthFailure()
    }

    override fun addressLine1LengthFailure() {
        profileView.addressLine1LengthFailure()
    }

    override fun addressLine2LengthFailure() {
        profileView.addressLine2LengthFailure()
    }

    override fun pinCodeLengthFailure() {
        profileView.pinCodeLengthFailure()
    }

    override fun fetchUserInfo(userId: String) {
        profileModel.fetchUserInfo(userId)
    }

    override fun getUserProfileSuccess(responseObject: GetProfileResponse) {
        profileView.getUserProfileSuccess(responseObject)
    }

}