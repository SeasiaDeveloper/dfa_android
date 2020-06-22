package com.dfa.ui.forgotpassword.presenter


import com.dfa.pojo.request.VerifyUserRequest
import com.dfa.pojo.response.VerifyUserResponse
import com.dfa.ui.forgotpassword.model.ForgotPasswordModel
import com.dfa.ui.forgotpassword.view.ForgotPasswordView

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