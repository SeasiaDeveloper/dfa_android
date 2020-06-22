package com.dfa.ui.updatepassword.presenter

import com.dfa.pojo.request.UpdatePasswordRequest
import com.dfa.pojo.response.ChangePasswordResponse
import com.dfa.ui.changepassword.view.ChangePasswordView
import com.dfa.ui.updatepassword.model.UpdatePasswordModel

class UpdatePasswordPresenterImpl(private var updatePassordView: ChangePasswordView) :
    UpdatePasswordPresenter {
    private var updatePasswordModel: UpdatePasswordModel = UpdatePasswordModel(this)

    override fun updatePasswordSucccess(changePasswordResponse: ChangePasswordResponse) {
        updatePassordView.onChangePasswordSuccess(changePasswordResponse)
    }

    override fun updatePasswordFailure(error: String) {
        updatePassordView.onChangePasswordFailure(error)
    }

    override fun hitUpdatePasswordApi(updateChangePasswordRequest: UpdatePasswordRequest) {
        updatePasswordModel.changePasswordApi(updateChangePasswordRequest)
    }

    override fun showError(error: String) {
        updatePassordView.showServerError(error)
    }

}