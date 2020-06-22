package com.dfa.ui.OtpVerification.presenter

import com.google.firebase.auth.PhoneAuthProvider
import com.dfa.base.presenter.BasePresenter

interface OtpVerificationPresenter: BasePresenter {

    fun sendVerificationCode(mobile: String, mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks)
}