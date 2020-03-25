package com.ngo.ui.OtpVerification.model

import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.auth.PhoneAuthProvider
import com.ngo.ui.OtpVerification.presenter.OtpVerificationPresenter
import java.util.concurrent.TimeUnit


class OtpVerificationModel(private var loginPresenter: OtpVerificationPresenter) {

    //the method is sending verification code
//the country id is concatenated
//you can take the country id as user input as well
    /* fun sendVerificationCode(mobile: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91$mobile",
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallbacks
        )
    }
*/

}