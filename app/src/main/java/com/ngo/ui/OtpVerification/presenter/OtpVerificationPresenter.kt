package com.ngo.ui.OtpVerification.presenter

import com.google.firebase.auth.PhoneAuthProvider
import com.ngo.base.presenter.BasePresenter

interface OtpVerificationPresenter: BasePresenter {

    fun sendVerificationCode(mobile: String, mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks)
}