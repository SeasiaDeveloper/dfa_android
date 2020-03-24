package com.ngo.ui.login.presenter

import com.ngo.pojo.response.ComplaintResponse
import com.ngo.ui.generalpublic.model.PublicModel
import com.ngo.ui.login.model.LoginModel
import com.ngo.ui.login.view.LoginView

class LoginActivityPresenterImpl(private var loginView: LoginView) :
    LoginPresenter {
    private var loginModel: LoginModel = LoginModel(this)
    override fun onEmptyLevel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEmptyImage() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEmptyDescription() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onValidationSuccess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkValidations(emailId: String, password: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoginSuccess(response: ComplaintResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoginFailure(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}