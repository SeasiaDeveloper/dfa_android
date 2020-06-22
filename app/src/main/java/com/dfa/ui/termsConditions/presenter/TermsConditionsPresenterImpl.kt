package com.dfa.ui.termsConditions.presenter

import com.dfa.pojo.response.GetTermsConditionsResponse
import com.dfa.ui.termsConditions.model.TermsConditionsModel
import com.dfa.ui.termsConditions.view.TermsConditionsView

class TermsConditionsPresenterImpl(private var view: TermsConditionsView): TermsConditionsPresenter {
    private var model: TermsConditionsModel = TermsConditionsModel(this)

    override fun getTermsConditions(token:String?) {
        model.getTermsConditions(token)
    }

    override fun onTermsConditionsSuccess(response: GetTermsConditionsResponse) {
        view.onTermsConditionsSuccess(response)
    }

    override fun onTermsConditionsFailed(error: String) {
        view.onPoliceDetailsFailed(error)

    }

    override fun showError(error: String) {
        view.showServerError(error)
    }

  }