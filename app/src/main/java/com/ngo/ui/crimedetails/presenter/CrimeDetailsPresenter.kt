package com.ngo.ui.crimedetails.presenter

import com.ngo.base.presenter.BasePresenter
import com.ngo.pojo.request.CrimeDetailsRequest
import com.ngo.pojo.response.GetCrimeDetailsResponse

interface CrimeDetailsPresenter : BasePresenter {
    fun crimeDetailsSuccess(getComplaintsResponse: GetCrimeDetailsResponse)
    fun crimeDetailsFailure()
    fun hiCrimeDetailsApi(crimeDetailsRequest: CrimeDetailsRequest,token:String?)
}