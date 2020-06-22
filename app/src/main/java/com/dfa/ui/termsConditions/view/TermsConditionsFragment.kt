package com.dfa.ui.termsConditions.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dfa.R
import com.dfa.pojo.response.GetTermsConditionsResponse
import com.dfa.ui.termsConditions.presenter.TermsConditionsPresenter
import com.dfa.ui.termsConditions.presenter.TermsConditionsPresenterImpl
import com.dfa.utils.Utilities
import kotlinx.android.synthetic.main.fragment_terms_conditions.*


class TermsConditionsFragment :Fragment() ,TermsConditionsView {

    private var presenter: TermsConditionsPresenter = TermsConditionsPresenterImpl(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terms_conditions, container, false)!!
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utilities.showProgress(activity!!)
       // presenter.getTermsConditions()
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