package com.dfa.ui.termsConditions.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.response.GetTermsConditionsResponse

interface TermsConditionsPresenter :BasePresenter {
    fun getTermsConditions(token:String?)
    fun onTermsConditionsSuccess(response: GetTermsConditionsResponse)
    fun onTermsConditionsFailed(error: String)
}