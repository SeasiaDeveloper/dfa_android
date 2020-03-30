package com.ngo.ui.termsConditions.view

import android.graphics.Color
import android.view.View
import com.ngo.R
import com.ngo.base.BaseActivity
import com.ngo.customviews.CenteredToolbar
import com.ngo.pojo.response.GetTermsConditionsResponse
import com.ngo.ui.termsConditions.presenter.TermsConditionsPresenter
import com.ngo.ui.termsConditions.presenter.TermsConditionsPresenterImpl
import com.ngo.utils.Utilities
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
        (toolbarLayout as CenteredToolbar).setNavigationIcon(R.drawable.back_arrow)
        (toolbarLayout as CenteredToolbar).setNavigationOnClickListener {
            onBackPressed()
        }
        Utilities.showProgress(this)
        presenter.getTermsConditions()
    }

    override fun handleKeyboard(): View {
        return termsConditionsLayout
    }

    override fun onTermsConditionsSuccess(response: GetTermsConditionsResponse) {
        Utilities.dismissProgress()
        // response.guid
        showWebView(response.post_content)
    }

    override fun onPoliceDetailsFailed(error: String) {
        Utilities.dismissProgress()
    }

    override fun showServerError(error: String) {
        Utilities.dismissProgress()
    }

    private fun showWebView(postContent: String?) {
        var mimeType = "text/html"
        val encoding = "UTF-8"
        val html = postContent
        wv_terms.loadDataWithBaseURL("", html, mimeType, encoding, "")
    }
}