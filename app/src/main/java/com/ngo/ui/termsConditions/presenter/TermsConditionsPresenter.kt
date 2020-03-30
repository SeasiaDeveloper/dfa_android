package com.ngo.ui.termsConditions.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.response.GetTermsConditionsResponse

interface TermsConditionsPresenter :BasePresenter {
    fun getTermsConditions(token:String?)
    fun onTermsConditionsSuccess(response: GetTermsConditionsResponse)
    fun onTermsConditionsFailed(error: String)
}