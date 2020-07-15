package com.dfa.ui.termsConditions.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.webkit.*
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.pojo.response.GetTermsConditionsResponse
import com.dfa.ui.termsConditions.presenter.TermsConditionsPresenter
import com.dfa.ui.termsConditions.presenter.TermsConditionsPresenterImpl
import com.dfa.utils.Constants
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_profile.toolbarLayout
import kotlinx.android.synthetic.main.terms_and_conditions_activity.*

@SuppressLint("SetJavaScriptEnabled")
class TermsAndConditionActivity : BaseActivity(), TermsConditionsView {
    private var presenter: TermsConditionsPresenter = TermsConditionsPresenterImpl(this)

    override fun getLayout(): Int {
        return R.layout.terms_and_conditions_activity
    }

    override fun setupUI() {
        (toolbarLayout as CenteredToolbar).title = getString(R.string.terms_and_condition)
        (toolbarLayout as CenteredToolbar).setTitleTextColor(Color.WHITE)
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_button)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        showWebView()
        //  Utilities.showProgress(this)
        //  val authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        //  presenter.getTermsConditions(authorizationToken)
    }

    override fun handleKeyboard(): View {
        return termsConditionsLayout
    }

    override fun onTermsConditionsSuccess(response: GetTermsConditionsResponse) {
        Utilities.dismissProgress()
        // showWebView(response.post_content)
    }

    override fun onPoliceDetailsFailed(error: String) {
        Utilities.dismissProgress()
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
    }

    private fun showWebView() {
        val html = Constants.BASE_URL_FOR_TERMS_AND_CONDITIONS
        val mimeType = "text/html"
        val encoding = "UTF-8"
        var header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
        // wv_terms_activity.loadData(html, mimeType, encoding)
        // wv_terms.loadDataWithBaseURL("", html, mimeType, encoding, "")
        wv_terms_activity.setWebChromeClient(WebChromeClient())
        wv_terms_activity.getSettings().setJavaScriptEnabled(true)
        wv_terms_activity.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Utilities.showProgress(this@TermsAndConditionActivity)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Utilities.dismissProgress()
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Utilities.dismissProgress()
               // Utilities.showMessage(this@TermsAndConditionActivity,error.toString())
            }

        })
        wv_terms_activity.loadUrl(html)
    }
}