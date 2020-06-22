package com.dfa.ui.changepassword.presenter

import com.dfa.pojo.request.ChangePasswordRequest
import com.dfa.pojo.response.ChangePasswordResponse
import com.dfa.ui.changepassword.model.ChangePasswordModel
import com.dfa.ui.changepassword.view.ChangePasswordView

class ChangePasswordPresenterImpl(private var changePasswordView: ChangePasswordView) :
    ChangePasswordPresenter {
    private var changeModel: ChangePasswordModel = ChangePasswordModel(this)

    override fun onEmptyPasswordField() {

    }

    override fun hitChangePasswordApi(changePasswordRequest: ChangePasswordRequest) {
        changeModel.changePasswordApi(changePasswordRequest)
    }

    override fun onPasswordChangeSuccess(respone: ChangePasswordResponse) {
        changePasswordView.onChangePasswordSuccess(respone)
    }

    override fun onPasswordChangeFailure(error: String) {
        changePasswordView.onChangePasswordFailure(error)
    }

    override fun showError(error: String) {
        changePasswordView.showServerError(error)
    }

}