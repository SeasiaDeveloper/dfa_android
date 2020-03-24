package com.ngo.ui.police.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.PoliceDataRequest
import com.ngo.pojo.request.PoliceStatusRequest
import com.ngo.pojo.response.GetPoliceFormData
import com.ngo.pojo.response.PoliceStatusResponse

interface PolicePresenter:BasePresenter {
    fun onPoliceDetailsSuccess(response: GetPoliceFormData)
    fun onPoliceDetailsFailed(error: String)
    fun getPoliceDetailsRequest()
    fun onPoliceStatusSuccess(response: PoliceStatusResponse)
    fun onPoliceStatusFailed(error: String)
    fun savePoliceStatusRequest(request: PoliceStatusRequest)
}