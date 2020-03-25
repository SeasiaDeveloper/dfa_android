package com.ngo.ui.forgotpassword.presenter


import com.ngo.pojo.request.VerifyUserRequest
import com.ngo.pojo.response.VerifyUserResponse
import com.ngo.ui.forgotpassword.model.ForgotPasswordModel
import com.ngo.ui.forgotpassword.view.ForgotPasswordView

class ForgotPasswordPresenterImpl(private var forgotPasswordView: ForgotPasswordView) :
    ForgotPassworPresenter {
    private var forgotPasswordModel: ForgotPasswordModel = ForgotPasswordModel(this)
    override fun hitVerifyUserApi(verfiPasswordRequest: VerifyUserRequest) {
        forgotPasswordModel.hitVerifyUserApi(verfiPasswordRequest)
    }

    override fun onVerfiyUserSuccess(verifyUserResponse: VerifyUserResponse) {
        forgotPasswordView.onVerifyUserSuccess(verifyUserResponse)
    }

    override fun onVerifyUserFailure(error: String) {
        forgotPasswordView.onVerifyUserFailure(error)
    }

    override fun showError(error: String) {
        forgotPasswordView.showServerError(error)
    }
}