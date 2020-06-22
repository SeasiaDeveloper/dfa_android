package com.dfa.ui.police.view

import com.dfa.base.view.BaseView
import com.dfa.pojo.response.GetPoliceFormData
import com.dfa.pojo.response.PoliceStatusResponse

interface PoliceView:BaseView {
    fun showPoliceDetailsResponse(response: GetPoliceFormData)
    fun showPoliceStatusResponse(response: PoliceStatusResponse)

}