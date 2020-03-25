package com.ngo.ui.login.presenter

import com.ngo.pojo.request.LoginRequest
import com.ngo.pojo.response.LoginResponse
import com.ngo.ui.login.model.LoginModel
import com.ngo.ui.login.view.LoginView

class LoginActivityPresenterImpl(private var loginView: LoginView):
    LoginPresenter {
    private var loginModel: LoginModel = LoginModel(this)
    override fun onEmptyPassword() {
        loginView.onEmptyPassword()
    }

    override fun onInvalidEmail() {
        loginView.onInvalidEmailId()
    }

    override fun onInvalidNumber() {
        loginView.onInvalidNumber()
    }

    override fun onEmptyEmailId() {
        loginView.onEmptyEmailId()
    }


    override fun onValidationSuccess() {
       loginView.validationSuccess()
    }

    override fun hitLoginWebService(loginRequest: LoginRequest) {
        loginModel.hitLoginWebService(loginRequest)
    }

    override fun checkValidations(emailId: String, password: String) {
        loginModel.checkValidations(emailId, password)
    }

    override fun onLoginSuccess(response: LoginResponse) {
        loginView.onLoginSuccess(response)
    }

    override fun onLoginFailure(error: String) {
        loginView.onLoginFailure(error)
    }

    override fun showError(error: String) {
        loginView.showError(error)
    }
}