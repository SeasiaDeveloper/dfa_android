package com.ngo.ui.changepassword.presenter

import com.ngo.pojo.request.ChangePasswordRequest
import com.ngo.pojo.request.LoginRequest
import com.ngo.pojo.response.ChangePasswordResponse
import com.ngo.pojo.response.LoginResponse
import com.ngo.ui.changepassword.model.ChangePasswordModel
import com.ngo.ui.changepassword.view.ChangePasswordView
import com.ngo.ui.login.model.LoginModel

class ChangePasswordPresenterImpl(private var changePasswordView: ChangePasswordView) :
    ChangePasswordPresenter {
    private var changeModel: ChangePasswordModel = ChangePasswordModel(this)

    override fun onEmptyPasswordField() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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