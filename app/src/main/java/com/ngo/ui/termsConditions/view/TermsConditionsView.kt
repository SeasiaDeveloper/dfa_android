package com.ngo.ui.termsConditions.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.GetTermsConditionsResponse

interface TermsConditionsView :BaseView {
    fun onTermsConditionsSuccess(response: GetTermsConditionsResponse)
    fun onPoliceDetailsFailed(error: String)
}