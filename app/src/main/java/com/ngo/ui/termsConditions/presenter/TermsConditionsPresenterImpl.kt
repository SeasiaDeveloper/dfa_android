package com.ngo.ui.termsConditions.presenter

import com.ngo.pojo.response.GetTermsConditionsResponse
import com.ngo.ui.termsConditions.model.TermsConditionsModel
import com.ngo.ui.termsConditions.view.TermsConditionsView

class TermsConditionsPresenterImpl(private var view: TermsConditionsView): TermsConditionsPresenter {
    private var model: TermsConditionsModel = TermsConditionsModel(this)

    override fun getTermsConditions() {
        model.getTermsConditions()
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