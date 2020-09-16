package com.dfa.ui.contribute.payment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cdflynn.android.library.checkview.CheckView
import com.dfa.R
import com.dfa.customviews.CenteredToolbar
import com.dfa.pojo.request.PaymentInput
import com.dfa.ui.home.fragments.home.view.HomeActivity
import com.dfa.utils.Utilities
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(), PaymentResultListener,PaymentCallback {

    var payment = 0
    var paymentInRupiese = 0
    var contactNumber = ""
    var name = ""
    var email = ""
    var tickets = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        var toolbarLayout:CenteredToolbar= findViewById<View>(R.id.toolbarLayout) as CenteredToolbar

        (toolbarLayout as CenteredToolbar).title = getString(R.string.payment)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }

        Checkout.preload(getApplicationContext());


        val button: Button = findViewById<View>(R.id.btn_pay) as Button

        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                startPayment()
            }
        })
        val tvPayment: TextView = findViewById<View>(R.id.payment) as TextView
        val privacyPolicy: TextView = findViewById<View>(R.id.txt_privacy_policy) as TextView
        payment = intent.getStringExtra("totalCost")!!.toInt()
        paymentInRupiese=payment
        contactNumber = intent.getStringExtra("contactNumber")!!
        name = intent.getStringExtra("name")!!
        email = intent.getStringExtra("userEmail")!!
        tickets = intent.getStringExtra("ticketsId")!!
        tvPayment.setText("INR " + payment)
        payment = payment * 100

        privacyPolicy.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                    val httpIntent = Intent(Intent.ACTION_VIEW)
                    httpIntent.setData(Uri.parse("https://razorpay.com/sample-application/"))
                    startActivity(httpIntent)
            }
        })


    }

    override fun onPaymentError(code: Int, response: String?) {
        try {
            Toast.makeText(
                this,
                "Payment failed: " + code.toString() + " " + response,
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: java.lang.Exception) {
            Log.e(
                "PaymentActivity",
                "Exception in onPaymentError",
                e
            )
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        try {
//            Toast.makeText(
//                this,
//                "Payment Successful: $razorpayPaymentID",
//                Toast.LENGTH_SHORT
//            ).show()

            var presenter=PaymentPresenter(this)
            var input=PaymentInput()
            input.customer_C=contactNumber
            input.customer_E=email
            input.customer_N=name
            input.ticket_N=tickets
            Utilities.showProgress(this)
            presenter.paymentStatus(input)

        } catch (e: java.lang.Exception) {
            Log.e("PaymentActivity", "Exception in onPaymentSuccess", e)
        }
    }

    fun startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */

        val activity = this
        val co = Checkout()
        try {
            val options = JSONObject()
            options.put("name", "Drug Free Arunachal")
          //  options.put("description", "Demoing Charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency", "INR")
            options.put("amount", payment)
            val preFill = JSONObject()
            preFill.put("email", email)
            preFill.put("contact", contactNumber)
            preFill.put("name", name)
            options.put("prefill", preFill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }

    override fun updateStatusSuccess(message: String?) {
       // Toast.makeText(this,message,Toast.LENGTH_LONG).show()
        dueIncomePopup()
        Utilities.dismissProgress()
    }
    override fun failer(error: String) {
        Toast.makeText(this,error,Toast.LENGTH_LONG).show()
        Utilities.dismissProgress()
    }



    fun dueIncomePopup() {
        var dialog = Dialog(this!!) // Context, this, etc.
        dialog!!.setContentView(R.layout.payment_success)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

//        dialog.getWindow()!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT);

        var checkImage = dialog.findViewById<CheckView>(R.id.check1)
        var tvPayment = dialog.findViewById<TextView>(R.id.tvPayment)
        var btnDone = dialog.findViewById<Button>(R.id.btnDone)

        tvPayment.setText("₹ "+paymentInRupiese)


        btnDone.setOnClickListener {
            dialog.dismiss()
            var intent=Intent(this,HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        object : CountDownTimer(800, 800) {
           override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                checkImage.check()
            }
        }.start()

        dialog.show()
    }

}