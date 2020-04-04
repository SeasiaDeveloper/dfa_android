package com.ngo.ui.OtpVerification.view

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
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
import com.ngo.ui.signup.SignupActivity
import com.ngo.utils.Utilities
import kotlinx.android.synthetic.main.activity_forgot_password.toolbarLayout
import kotlinx.android.synthetic.main.activity_otp_verification.*


class OtpVerificationActivity : BaseActivity(), View.OnClickListener, OtpVerificationView {
    private lateinit var mobile: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var intent_from: String
    private lateinit var mVerificationId: String
    private var presenter: OtpVerificationPresenter = OtpVerificationImpl(this)
    private lateinit var userId: String
    private lateinit var phoneNo: String

    override fun getLayout(): Int {
        return R.layout.activity_otp_verification
    }

    override fun setupUI() {
        mAuth = FirebaseAuth.getInstance()
        (toolbarLayout as CenteredToolbar).title = getString(R.string.otp_verification)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        val intent = intent
        mobile = intent.getStringExtra("mobile")
        intent_from = intent.getStringExtra("intent_from")

        if (intent_from.equals("forgotPassword", ignoreCase = true)) {
            userId = intent.getStringExtra("userId")

        } else {
            phoneNo = intent.getStringExtra("phoneNo")
        }


        setListeners()
        showProgress()
        presenter.sendVerificationCode(mobile, mCallbacks)

    }

    //the callback to detect the verification status
    private val mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                dismissProgress()
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    et_otp.setText(code)
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                dismissProgress()
                Toast.makeText(this@OtpVerificationActivity, e.message, Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                dismissProgress()
                //storing the verification id that is sent to the user
                mVerificationId = s
            }
        }

    private fun setListeners() {
        btnOtpSubmit.setOnClickListener(this)
        btnOtpResend.setOnClickListener(this)
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
                } else {
                    Utilities.showProgress(this)
                    verifyVerificationCode(code)
                }
                //verifying the code entered manually
            }

            R.id.btnOtpResend -> {
                val code: String = et_otp.getText().toString().trim()
                /*  if (code.isEmpty() || code.length < 6) {
                      et_otp.setError("Enter valid code")
                      et_otp.requestFocus()
                      return
                  } else {*/
                Utilities.showProgress(this)
                presenter.sendVerificationCode(mobile, mCallbacks)
                //   }
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
                    Utilities.dismissProgress()

                    //verification successful we will start the Change Password activity
                    if (intent_from.equals("signup", ignoreCase = true)) {
                        val intent = Intent(this, SignupActivity::class.java)
                        intent.putExtra("phoneNo", phoneNo)
                        startActivity(intent)
                        finish()
                    } else if (intent_from.equals("forgotPassword", ignoreCase = true)) {
                        val intent = Intent(this, ChangePasswordActivity::class.java)
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                        finish()
                    }

                } else {
                    Utilities.dismissProgress()
                    Toast.makeText(
                        this@OtpVerificationActivity,
                        task.exception?.message,
                        Toast.LENGTH_LONG
                    ).show()
                    finish()

                }
            })
    }

    override fun showServerError(error: String) {

    }
}
