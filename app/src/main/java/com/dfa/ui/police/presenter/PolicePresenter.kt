package com.dfa.ui.police.presenter

import com.dfa.base.presenter.BasePresenter
import com.dfa.pojo.request.PoliceStatusRequest
import com.dfa.pojo.response.GetPoliceFormData
import com.dfa.pojo.response.PoliceStatusResponse

interface PolicePresenter:BasePresenter {
    fun onPoliceDetailsSuccess(response: GetPoliceFormData)
    fun onPoliceDetailsFailed(error: String)
    fun getPoliceDetailsRequest()
    fun onPoliceStatusSuccess(response: PoliceStatusResponse)
    fun onPoliceStatusFailed(error: String)
    fun savePoliceStatusRequest(request: PoliceStatusRequest)
}