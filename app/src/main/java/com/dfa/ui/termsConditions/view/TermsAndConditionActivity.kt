package com.dfa.ui.termsConditions.view

import android.graphics.Color
import android.view.View
import com.dfa.R
import com.dfa.base.BaseActivity
import com.dfa.customviews.CenteredToolbar
import com.dfa.pojo.response.GetTermsConditionsResponse
import com.dfa.ui.termsConditions.presenter.TermsConditionsPresenter
import com.dfa.ui.termsConditions.presenter.TermsConditionsPresenterImpl
import com.dfa.utils.PreferenceHandler
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.activity_profile.toolbarLayout
import kotlinx.android.synthetic.main.fragment_terms_conditions.wv_terms
import kotlinx.android.synthetic.main.terms_and_conditions_activity.*

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
        Utilities.showProgress(this)
        val authorizationToken = PreferenceHandler.readString(this, PreferenceHandler.AUTHORIZATION, "")
        presenter.getTermsConditions(authorizationToken)
    }

    override fun handleKeyboard(): View {
        return termsConditionsLayout
    }

    override fun onTermsConditionsSuccess(response: GetTermsConditionsResponse) {
        Utilities.dismissProgress()
        showWebView(response.post_content)
    }

    override fun onPoliceDetailsFailed(error: String) {
        Utilities.dismissProgress()
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
    }

    private fun showWebView(postContent: String?) {
        val mimeType = "text/html"
        val encoding = "UTF-8"
        val html = postContent
        wv_terms.loadDataWithBaseURL("", html, mimeType, encoding, "")
    }
}