package com.dfa.ui.termsConditions.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.GetTermsConditionsResponse

interface TermsConditionsView :BaseView {
    fun onTermsConditionsSuccess(response: GetTermsConditionsResponse)
    fun onPoliceDetailsFailed(error: String)
}