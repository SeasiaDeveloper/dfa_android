package com.ngo.ui.updatepassword.presenter

import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.request.UpdatePasswordRequest
import com.ngo.pojo.response.ChangePasswordResponse
import com.ngo.ui.changepassword.view.ChangePasswordView
import com.ngo.ui.updatepassword.model.UpdatePasswordModel

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