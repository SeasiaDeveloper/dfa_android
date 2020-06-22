package com.dfa.ui.OtpVerification.presenter

import com.google.firebase.auth.PhoneAuthProvider
import com.dfa.ui.OtpVerification.model.OtpVerificationModel
import com.dfa.ui.OtpVerification.view.OtpVerificationView


class OtpVerificationImpl (private var view: OtpVerificationView): OtpVerificationPresenter {

    private var model: OtpVerificationModel = OtpVerificationModel(this)
    override fun sendVerificationCode(
        mobile: String,
        mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        model.sendVerificationCode(mobile,mCallbacks)
    }


    override fun showError(error: String) {
    }

}