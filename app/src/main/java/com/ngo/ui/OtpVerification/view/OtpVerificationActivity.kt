package com.ngo.ui.OtpVerification.view

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.ui.OtpVerification.presenter.OtpVerificationImpl
import com.ngo.ui.OtpVerification.presenter.OtpVerificationPresenter
import com.ngo.ui.changepassword.view.ChangePasswordActivity
import kotlinx.android.synthetic.main.activity_forgot_password.toolbarLayout
import kotlinx.android.synthetic.main.activity_otp_verification.*
import java.util.concurrent.TimeUnit


class OtpVerificationActivity : BaseActivity(), View.OnClickListener,OtpVerificationView {
    private lateinit var mAuth: FirebaseAuth
   private lateinit var mVerificationId: String
    private var presenter: OtpVerificationPresenter = OtpVerificationImpl(this)

    override fun getLayout(): Int {
        return R.layout.activity_otp_verification
    }

    override fun setupUI() {
        mAuth = FirebaseAuth.getInstance()
        (toolbarLayout as CenteredToolbar).title = getString(R.string.otp_verification)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        val intent = intent
        val mobile = intent.getStringExtra("mobile")
        setListeners()
        presenter.sendVerificationCode(mobile,mCallbacks)

    }

    //the callback to detect the verification status
    private val mCallbacks: OnVerificationStateChangedCallbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    et_otp.setText(code)
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@OtpVerificationActivity, e.message, Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                //storing the verification id that is sent to the user
                mVerificationId = s
            }
        }

    private fun setListeners() {
        btnOtpSubmit.setOnClickListener(this)
    }

    override fun handleKeyboard(): View {
        return OtpVerificationLayout
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnOtpSubmit -> {
                val code: String = et_otp.getText().toString().trim()
                if (code.isEmpty() || code.length < 6) {
                    et_otp.setError("Enter valid code")
                    et_otp.requestFocus()
                    return
                }
                //verifying the code entered manually
                verifyVerificationCode(code)
            }

        }
    }

    private fun verifyVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(mVerificationId, code)
        //signing the user
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this@OtpVerificationActivity,
            OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    //verification successful we will start the Change Password activity
                    val intent = Intent(this, ChangePasswordActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {

                    Toast.makeText(this@OtpVerificationActivity, task.exception?.message, Toast.LENGTH_LONG).show()

                }
            })
    }

    override fun showServerError(error: String) {

    }
}
