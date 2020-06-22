package com.dfa.ui.OtpVerification.view

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
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.ui.OtpVerification.presenter.OtpVerificationImpl
import com.dfa.ui.OtpVerification.presenter.OtpVerificationPresenter
import com.dfa.ui.changepassword.view.ChangePasswordActivity
import com.dfa.ui.signup.SignupActivity
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_forgot_password.toolbarLayout
import kotlinx.android.synthetic.main.activity_otp_verification.*
import java.util.concurrent.TimeUnit


class OtpVerificationActivity : BaseActivity(), View.OnClickListener, OtpVerificationView {
    private lateinit var mobile: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var intent_from: String
    private var mVerificationId: String = ""
    private var presenter: OtpVerificationPresenter = OtpVerificationImpl(this)
    private lateinit var userId: String
    private lateinit var phoneNo: String
    private var isResend:Boolean=false

    override fun getLayout(): Int {
        return R.layout.activity_otp_verification
    }

    override fun setupUI() {
        mAuth = FirebaseAuth.getInstance()
        (toolbarLayout as CenteredToolbar).title = getString(R.string.otp_verification)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.BLACK)
        val intent = intent
        mobile = intent.getStringExtra("mobile")
        intent_from = intent.getStringExtra("intent_from")

        if (intent_from.equals("forgotPassword", ignoreCase = true)) {
            userId = intent.getStringExtra("userId")

        } else {
            phoneNo = intent.getStringExtra("phoneNo")
        }


        setListeners()
        Utilities.showProgress(this)
        sendVerificationCode(mobile, mCallbacks)

    }

    //the callback to detect the verification status
    private val mCallbacks: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Utilities.dismissProgress()
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    et_otp.setText(code)
                    verifyVerificationCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Utilities.dismissProgress()
                Toast.makeText(this@OtpVerificationActivity, e.message, Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
               // super.onCodeSent(s, forceResendingToken)
                Utilities.dismissProgress()
                //storing the verification id that is sent to the user
                mVerificationId = s
                if(isResend)
                {
                 Utilities.showMessage(this@OtpVerificationActivity,"Code Resend Successfully")
                }
                isResend=false

            }
        }

    private fun setListeners() {
        btnOtpSubmit.setOnClickListener(this)
        btnOtpResend.setOnClickListener(this)
    }

    override fun handleKeyboard(): View {
        return OtpVerificationLayout
    }

    fun sendVerificationCode(mobile: String ,mCallbacks  : PhoneAuthProvider.OnVerificationStateChangedCallbacks ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91" + mobile,
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallbacks)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnOtpSubmit -> {
                val code: String = et_otp.getText().toString().trim()
                if(code.trim().isEmpty() ){
                    et_otp.setError("Enter Verification Code")
                    et_otp.requestFocus()
                    return
                }
               else if ( code.length < 6) {
                    et_otp.setError("Incorrect verification code")
                    et_otp.requestFocus()
                    return
                } else {
                    Utilities.showProgress(this)
                    if (mVerificationId != null && !mVerificationId.equals("")) {
                        verifyVerificationCode(code)
                    }
                }
                //verifying the code entered manually
            }

            R.id.btnOtpResend -> {
                isResend=true
                val code: String = et_otp.getText().toString().trim()
                /*  if (code.isEmpty() || code.length < 6) {
                      et_otp.setError("Enter valid code")
                      et_otp.requestFocus()
                      return
                  } else {*/
              Utilities.showProgress(this)
               sendVerificationCode(mobile, mCallbacks)
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
