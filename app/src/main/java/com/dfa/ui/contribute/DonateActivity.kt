package com.dfa.ui.contribute

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.dfa.R
import com.dfa.ui.home.fragments.home.view.HomeActivity

class DonateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)
        val browser: WebView = findViewById<View>(R.id.webview) as WebView
        browser.getSettings().setJavaScriptEnabled(true)
        var isfirst="0"
        browser.loadUrl("file:///android_asset/donate.html")



        browser.evaluateJavascript(
            "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
            object : ValueCallback<String?> {
              override  fun onReceiveValue(html: String?) {
                    Log.d("HTML", html)
                    // code here
                }
            })




        browser.setWebViewClient(object : WebViewClient() {
            override fun onPageStarted(
                view: WebView?,
                url: String,
                favicon: Bitmap?
            ) {
                super.onPageStarted(view, url, favicon)
                Log.d("WebView", "your current url when webpage loading..$url")


            }

            override fun onPageFinished(
                view: WebView?,
                url: String
            ) {
                Log.d("WebView", "your current url when webpage loading.. finish$url")
                super.onPageFinished(view, url)

                if(url.contains("https://razorpay.com/payment-button/pl_FbgQS0OKuv0Ing/view?")){
                    if(isfirst.equals("1")){
                        var intent= Intent(this@DonateActivity, HomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    isfirst="1"
                }
            }

            override  fun onLoadResource(
                view: WebView?,
                url: String?
            ) {
                super.onLoadResource(view, url)

            }

            override   fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String
            ): Boolean {

                println("when you click on any interlink on webview that time you got url :-$url")
                return super.shouldOverrideUrlLoading(view, url)
            }
        })

    }




}