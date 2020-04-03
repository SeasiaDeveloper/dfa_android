package com.ngo.ui.policedetail.presenter

import com.ngo.base.presenter.BasePresenter

import com.ngo.pojo.request.PoliceDetailrequest
import com.ngo.pojo.response.GetCrimeDetailsResponse

interface PoliceDetailPresenter : BasePresenter {
    fun crimeDetailsSuccess(getComplaintsResponse: GetCrimeDetailsResponse)
    fun crimeDetailsFailure()
    fun hitCrimeDetailsApi(crimeDetailsRequest: PoliceDetailrequest, token: String?)
}