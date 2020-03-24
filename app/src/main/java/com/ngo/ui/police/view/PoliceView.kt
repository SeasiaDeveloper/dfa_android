package com.ngo.ui.police.view

import com.ngo.base.view.BaseView
import com.ngo.pojo.response.GetPoliceFormData
import com.ngo.pojo.response.PoliceStatusResponse

interface PoliceView:BaseView {
    fun showPoliceDetailsResponse(response: GetPoliceFormData)
    fun showPoliceStatusResponse(response: PoliceStatusResponse)

}