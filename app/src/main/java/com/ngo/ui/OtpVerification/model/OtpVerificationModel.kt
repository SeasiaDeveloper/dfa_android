package com.ngo.ui.OtpVerification.model

import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.auth.PhoneAuthProvider
import com.ngo.ui.OtpVerification.presenter.OtpVerificationPresenter
import java.util.concurrent.TimeUnit


class OtpVerificationModel(private var presenter: OtpVerificationPresenter) {

     fun sendVerificationCode(mobile: String ,mCallbacks  : PhoneAuthProvider.OnVerificationStateChangedCallbacks ) {

         PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91" + mobile,
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallbacks)
    }

}